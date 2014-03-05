package fr.certu.chouette.exchange.xml.neptune.exporter.producer;

import org.trident.schema.trident.ChouettePTNetworkType;
import org.trident.schema.trident.LongLatTypeType;
import org.trident.schema.trident.ProjectedPointType;

import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.type.LongLatTypeEnum;

public class StopPointProducer extends AbstractJaxbNeptuneProducer<ChouettePTNetworkType.ChouetteLineDescription.StopPoint, StopPoint> {

	@Override
	public ChouettePTNetworkType.ChouetteLineDescription.StopPoint produce(StopPoint stopPoint) {
		ChouettePTNetworkType.ChouetteLineDescription.StopPoint jaxbStopPoint = tridentFactory.createChouettePTNetworkTypeChouetteLineDescriptionStopPoint();
		
		//
		populateFromModel(jaxbStopPoint, stopPoint);
		
		//jaxbStopPoint.setComment(stopPoint.getComment());
		jaxbStopPoint.setName(stopPoint.getName());
		jaxbStopPoint.setLineIdShortcut(stopPoint.getLineIdShortcut());
		
		StopArea area = stopPoint.getContainedInStopArea();
		// address is optional and useless
//		if(area.hasAddress())
//		{
//			AddressType jaxbAddress = tridentFactory.createAddressType();
//			jaxbAddress.setCountryCode(area.getCountryCode());
//			jaxbAddress.setStreetName(area.getStreetName());
//			jaxbStopPoint.setAddress(jaxbAddress);
//		}
		
		jaxbStopPoint.setContainedIn(getNonEmptyObjectId(stopPoint.getContainedInStopArea()));
		jaxbStopPoint.setLatitude(area.getLatitude());
		jaxbStopPoint.setLongitude(area.getLongitude());
		
		if(area.getLongLatType() != null){
			LongLatTypeEnum longLatType = area.getLongLatType();
			try {
				jaxbStopPoint.setLongLatType(LongLatTypeType.fromValue(longLatType.name()));
			} catch (IllegalArgumentException e) {
				// TODO generate report
			}
		}
		
		if(area.hasProjection())
		{
			ProjectedPointType jaxbProjectedPoint = tridentFactory.createProjectedPointType();
			jaxbProjectedPoint.setProjectionType(area.getProjectionType());
			jaxbProjectedPoint.setX(area.getX());
			jaxbProjectedPoint.setY(area.getY());
			jaxbStopPoint.setProjectedPoint(jaxbProjectedPoint);
		}
		jaxbStopPoint.setPtNetworkIdShortcut(stopPoint.getPtNetworkIdShortcut());
						
		return jaxbStopPoint;
	}

}
