package fr.certu.chouette.exchange.xml.neptune.exporter.producer;

import org.trident.schema.trident.AddressType;
import org.trident.schema.trident.LongLatTypeType;
import org.trident.schema.trident.ProjectedPointType;
import org.trident.schema.trident.ChouettePTNetworkType;

import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.type.Address;
import fr.certu.chouette.model.neptune.type.LongLatTypeEnum;
import fr.certu.chouette.model.neptune.type.ProjectedPoint;

public class StopPointProducer extends AbstractJaxbNeptuneProducer<ChouettePTNetworkType.ChouetteLineDescription.StopPoint, StopPoint> {

	@Override
	public ChouettePTNetworkType.ChouetteLineDescription.StopPoint produce(StopPoint stopPoint) {
		ChouettePTNetworkType.ChouetteLineDescription.StopPoint jaxbStopPoint = tridentFactory.createChouettePTNetworkTypeChouetteLineDescriptionStopPoint();
		
		//
		populateFromModel(jaxbStopPoint, stopPoint);
		
		jaxbStopPoint.setComment(stopPoint.getComment());
		jaxbStopPoint.setName(stopPoint.getName());
		jaxbStopPoint.setLineIdShortcut(stopPoint.getLineIdShortcut());
		
		Address address = stopPoint.getAddress();
		if(stopPoint.getAddress() != null){
			AddressType jaxbAddress = tridentFactory.createAddressType();
			jaxbAddress.setCountryCode(address.getCountryCode());
			jaxbAddress.setStreetName(address.getStreetName());
			jaxbStopPoint.setAddress(jaxbAddress);
		}
		
		jaxbStopPoint.setContainedIn(getNonEmptyObjectId(stopPoint.getContainedInStopArea()));
		jaxbStopPoint.setLatitude(stopPoint.getLatitude());
		jaxbStopPoint.setLongitude(stopPoint.getLongitude());
		
		if(stopPoint.getLongLatType() != null){
			LongLatTypeEnum longLatType = stopPoint.getLongLatType();
			try {
				jaxbStopPoint.setLongLatType(LongLatTypeType.fromValue(longLatType.value()));
			} catch (IllegalArgumentException e) {
				// TODO generate report
			}
		}
		
		ProjectedPoint projectedPoint = stopPoint.getProjectedPoint();
		if(projectedPoint != null){
			ProjectedPointType jaxbProjectedPoint = tridentFactory.createProjectedPointType();
			jaxbProjectedPoint.setProjectionType(projectedPoint.getProjectionType());
			jaxbProjectedPoint.setX(projectedPoint.getX());
			jaxbProjectedPoint.setY(projectedPoint.getY());
			jaxbStopPoint.setProjectedPoint(jaxbProjectedPoint);
		}
		jaxbStopPoint.setPtNetworkIdShortcut(stopPoint.getPtNetworkIdShortcut());
						
		return jaxbStopPoint;
	}

}
