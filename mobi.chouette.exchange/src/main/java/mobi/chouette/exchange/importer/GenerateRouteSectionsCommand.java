package mobi.chouette.exchange.importer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.dao.JourneyPatternDAO;
import mobi.chouette.dao.RouteSectionDAO;
import mobi.chouette.exchange.importer.geometry.RouteSectionGenerator;
import mobi.chouette.exchange.parameters.AbstractImportParameter;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.RouteSection;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.type.SectionStatusEnum;
import mobi.chouette.model.type.TransportModeNameEnum;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
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

		log.info("Generating route sections for all journeyPatterns without route sections for " + configuration.getReferentialName() + " with transport modes: " + configuration.getGenerateMissingRouteSectionsForModes());
		RouteSectionRepository routeSectionRepository = new RouteSectionRepository(routeSectionGenerator);
		try {
			journeyPatternDAO.findAll().stream().filter(jp -> CollectionUtils.isEmpty(jp.getRouteSections()))
					.filter(jp -> configuration.getGenerateMissingRouteSectionsForModes().contains(jp.getRoute().getLine().getTransportModeName())).forEach(jp -> generateRouteSectionsForJourneyPattern(jp, routeSectionRepository));
		} catch (Exception e) {
			log.warn("Route section generation failed with exception for " + configuration.getReferentialName() + " : " + e.getMessage(), e);
		} finally {
			log.info(Color.YELLOW + monitor.stop() + Color.NORMAL);
		}

		return SUCCESS;
	}

	private void generateRouteSectionsForJourneyPattern(JourneyPattern jp, RouteSectionRepository routeSectionRepository) {
		log.debug("Generating route sections for JourneyPattern: " + jp.getObjectId());
		TransportModeNameEnum transportMode = jp.getRoute().getLine().getTransportModeName();
		List<RouteSection> routeSections = new ArrayList<>();

		StopPoint previousStopPoint = null;
		for (StopPoint currentStopPoint : jp.getStopPoints()) {
			if (previousStopPoint != null) {
				routeSections.add(routeSectionRepository.getRouteSection(jp, previousStopPoint, currentStopPoint, transportMode));
			}
			previousStopPoint = currentStopPoint;
		}

		for (RouteSection routeSection : routeSections) {
			routeSectionDAO.create(routeSection);
			jp.getRouteSections().add(routeSection);
		}

		if (jp.hasCompleteRouteSections()) {
			jp.setSectionStatus(SectionStatusEnum.Completed);
		}

		journeyPatternDAO.update(jp);
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
