package mobi.chouette.exchange.importer;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.GeometryUtil;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.dao.JourneyPatternDAO;
import mobi.chouette.dao.RouteSectionDAO;
import mobi.chouette.exchange.importer.geometry.RouteSectionGenerator;
import mobi.chouette.exchange.parameters.AbstractImportParameter;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.RouteSection;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.type.TransportModeNameEnum;
import mobi.chouette.model.util.ObjectIdTypes;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import org.apache.commons.collections.CollectionUtils;

@Stateless(name = GenerateRouteSectionsCommand.COMMAND)
@Log4j
public class GenerateRouteSectionsCommand implements Command, Constant {

	public static final String COMMAND = "GenerateRouteSectionsCommand";

	@EJB
	private RouteSectionGenerator routeSectionGenerator;

	@EJB
	private JourneyPatternDAO journeyPatternDAO;

	@EJB
	private RouteSectionDAO routeSectionDAO;

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean execute(Context context) throws Exception {
		Monitor monitor = MonitorFactory.start(COMMAND);
		AbstractImportParameter configuration = (AbstractImportParameter) context.get(CONFIGURATION);

		log.info("Generating route sections for all journeyPatterns without route sections for " + configuration.getReferentialName());

		try {
			journeyPatternDAO.findAll().stream().filter(jp -> CollectionUtils.isEmpty(jp.getRouteSections())).forEach(jp -> generateRouteSectionsForJourneyPattern(jp));
		} catch (Exception e) {
			log.warn("Route section generation failed with exception for " + configuration.getReferentialName() + " : " + e.getMessage(), e);
		} finally {
			log.info(Color.YELLOW + monitor.stop() + Color.NORMAL);
		}

		return SUCCESS;
	}

	private void generateRouteSectionsForJourneyPattern(JourneyPattern jp) {
		log.debug("Generating route sections for JourneyPattern: " + jp.getObjectId());
		TransportModeNameEnum transportMode = jp.getRoute().getLine().getTransportModeName();

		StopPoint prev = null;
		List<RouteSection> routeSections = new ArrayList<>();
		for (StopPoint sp : jp.getStopPoints()) {

			Coordinate from = getCoordinateFromStopPoint(prev);
			Coordinate to = getCoordinateFromStopPoint(sp);
			if (from != null && to != null) {
				LineString lineString = routeSectionGenerator.getRouteSection(from, to, transportMode);
				if (lineString != null) {
					routeSections.add(createRouteSection(prev, sp, lineString));
				}
			}
			prev = sp;
		}

		for (RouteSection routeSection : routeSections) {
			routeSectionDAO.create(routeSection);
			jp.getRouteSections().add(routeSection);
		}
		journeyPatternDAO.update(jp);
	}

	private RouteSection createRouteSection(StopPoint from, StopPoint to, LineString lineString) {
		RouteSection routeSection = new RouteSection();
		routeSection.setObjectId(from.objectIdPrefix() + ":" + ObjectIdTypes.ROUTE_SECTION_KEY + ":" + UUID.randomUUID().toString());

		routeSection.setFromScheduledStopPoint(from.getScheduledStopPoint());
		routeSection.setToScheduledStopPoint(to.getScheduledStopPoint());
		routeSection.setInputGeometry(lineString);
		routeSection.setProcessedGeometry(lineString);
		routeSection.setNoProcessing(true);
		routeSection.setFilled(true);
		routeSection.setDetached(true);
		routeSection.setDistance(BigDecimal.valueOf(GeometryUtil.convertFromAngleDegreesToMeters(lineString.getLength())));
		return routeSection;
	}

	private Coordinate getCoordinateFromStopPoint(StopPoint stopPoint) {
		if (stopPoint == null || stopPoint.getScheduledStopPoint() == null || stopPoint.getScheduledStopPoint().getContainedInStopAreaRef() == null) {
			return null;
		}
		StopArea stopArea = stopPoint.getScheduledStopPoint().getContainedInStopAreaRef().getObject();
		if (stopArea == null || stopArea.getLongitude() == null || stopArea.getLatitude() == null) {
			return null;
		}
		return new Coordinate(stopArea.getLongitude().doubleValue(), stopArea.getLatitude().doubleValue());
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = null;
			try {
				String name = "java:app/mobi.chouette.exchange/" + COMMAND;
				result = (Command) context.lookup(name);
			} catch (NamingException e) {
				// try another way on test context
				String name = "java:module/" + COMMAND;
				try {
					result = (Command) context.lookup(name);
				} catch (NamingException e1) {
					log.error(e);
				}
			}
			return result;
		}
	}

	static {
		CommandFactory.factories.put(GenerateRouteSectionsCommand.class.getName(), new GenerateRouteSectionsCommand.DefaultCommandFactory());
	}
}
