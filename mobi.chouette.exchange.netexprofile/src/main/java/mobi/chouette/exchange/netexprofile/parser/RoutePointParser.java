package mobi.chouette.exchange.netexprofile.parser;

import javax.xml.bind.JAXBElement;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.ConversionUtil;
import mobi.chouette.model.ScheduledStopPoint;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

import org.rutebanken.netex.model.PointProjection;
import org.rutebanken.netex.model.RoutePoint;
import org.rutebanken.netex.model.RoutePointsInFrame_RelStructure;

@Log4j
public class RoutePointParser extends NetexParser implements Parser, Constant {

	@Override
	public void parse(Context context) throws Exception {
		RoutePointsInFrame_RelStructure routePointStruct = (RoutePointsInFrame_RelStructure) context.get(NETEX_LINE_DATA_CONTEXT);

		for (RoutePoint netexRoutePoint : routePointStruct.getRoutePoint()) {
			String scheduledStopPointId = getScheduledStopPointId(netexRoutePoint);

			Referential referential = (Referential) context.get(REFERENTIAL);

			mobi.chouette.model.RoutePoint neptuneRoutePoint = ObjectFactory.getRoutePoint(referential, netexRoutePoint.getId());

			ScheduledStopPoint scheduledStopPoint = ObjectFactory.getScheduledStopPoint(referential, scheduledStopPointId);
			neptuneRoutePoint.setScheduledStopPoint(scheduledStopPoint);

			neptuneRoutePoint.setObjectVersion(NetexParserUtils.getVersion(netexRoutePoint));
			neptuneRoutePoint.setName(ConversionUtil.getValue(netexRoutePoint.getName()));
			neptuneRoutePoint.setBoarderCrossing(netexRoutePoint.isBorderCrossing());
		}
	}

// TODO RoutePoint - make sure ref is to ssp
	private String getScheduledStopPointId(RoutePoint netexRoutePoint) {
		String scheduledStopPointId=null;
		if (netexRoutePoint.getProjections() != null) {

			for (JAXBElement<?> projectionRefElement : netexRoutePoint.getProjections().getProjectionRefOrProjection()) {
				PointProjection pointProjection = (PointProjection) projectionRefElement.getValue();
				if (pointProjection.getProjectedPointRef() != null) {
					scheduledStopPointId = pointProjection.getProjectedPointRef().getRef();
				} else if (pointProjection.getProjectToPointRef() != null) {
					scheduledStopPointId = pointProjection.getProjectToPointRef().getRef();
				} else {
					log.error("Could not find point reference for projection with id : " + pointProjection.getId());
					throw new RuntimeException("missing point reference");
				}
			}
		}

		if (scheduledStopPointId == null) {
			throw new RuntimeException("Could not parse RoutePoints without projection to scheduledStopPoint");
		}
		return scheduledStopPointId;
	}


	static {
		ParserFactory.register(RoutePointParser.class.getName(), new ParserFactory() {
			private RoutePointParser instance = new RoutePointParser();

			@Override
			protected Parser create() {
				return instance;
			}
		});
	}

}
