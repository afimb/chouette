package mobi.chouette.exchange.neptune.exporter.producer;

import java.math.BigDecimal;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.neptune.JsonExtension;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.type.LongLatTypeEnum;

import org.codehaus.jettison.json.JSONObject;
import org.trident.schema.trident.ChouettePTNetworkType;
import org.trident.schema.trident.LongLatTypeType;
import org.trident.schema.trident.ProjectedPointType;

@Log4j
public class StopPointProducer extends
		AbstractJaxbNeptuneProducer<ChouettePTNetworkType.ChouetteLineDescription.StopPoint, StopPoint> implements
		JsonExtension {

	// @Override
	public ChouettePTNetworkType.ChouetteLineDescription.StopPoint produce(StopPoint stopPoint, boolean addExtension) {
		ChouettePTNetworkType.ChouetteLineDescription.StopPoint jaxbStopPoint = tridentFactory
				.createChouettePTNetworkTypeChouetteLineDescriptionStopPoint();

		//
		populateFromModel(jaxbStopPoint, stopPoint);

		jaxbStopPoint.setComment(buildComment(stopPoint, addExtension));
		if (stopPoint.getScheduledStopPoint().getContainedInStopArea() != null) {
			StopArea area = stopPoint.getScheduledStopPoint().getContainedInStopArea();
			jaxbStopPoint.setName(area.getName());
			// jaxbStopPoint.setLineIdShortcut(stopPoint.getLineIdShortcut());


			jaxbStopPoint.setContainedIn(getNonEmptyObjectId(stopPoint.getScheduledStopPoint().getContainedInStopArea()));
			if (area.hasCoordinates())
			{
			jaxbStopPoint.setLatitude(area.getLatitude());
			jaxbStopPoint.setLongitude(area.getLongitude());
				LongLatTypeEnum longLatType = area.getLongLatType();
				try {
					jaxbStopPoint.setLongLatType(LongLatTypeType.fromValue(longLatType.name()));
				} catch (IllegalArgumentException e) {
					// TODO generate report
				}
			}
			else
			{
				log.error("missing coordinates for StopArea "+area.getObjectId()+" "+area.getName());
				jaxbStopPoint.setLatitude(BigDecimal.ZERO);
				jaxbStopPoint.setLongitude(BigDecimal.ZERO);
				jaxbStopPoint.setLongLatType(LongLatTypeType.WGS_84);
			}

			if (area.hasProjection()) {
				ProjectedPointType jaxbProjectedPoint = tridentFactory.createProjectedPointType();
				jaxbProjectedPoint.setProjectionType(area.getProjectionType());
				jaxbProjectedPoint.setX(area.getX());
				jaxbProjectedPoint.setY(area.getY());
				jaxbStopPoint.setProjectedPoint(jaxbProjectedPoint);
			}
		}
		// jaxbStopPoint.setPtNetworkIdShortcut(stopPoint.getPtNetworkIdShortcut());

		return jaxbStopPoint;
	}

	protected String buildComment(StopPoint stopPoint, boolean addExtension) {
		if (!addExtension)
			return null;
		try {
			JSONObject jsonComment = new JSONObject();
			JSONObject jsonRC = new JSONObject();
			if (stopPoint.getForBoarding() != null) {
				jsonRC.put(BOARDING, stopPoint.getForBoarding().name());
			}
			if (stopPoint.getForAlighting() != null) {
				jsonRC.put(ALIGHTING, stopPoint.getForAlighting().name());
			}
			if (jsonRC.length() == 0) {
				return null;
			}
			jsonComment.put(ROUTING_CONSTRAINTS, jsonRC);
			return jsonComment.toString();
		} catch (Exception e) {
			return null;
		}
	}

}
