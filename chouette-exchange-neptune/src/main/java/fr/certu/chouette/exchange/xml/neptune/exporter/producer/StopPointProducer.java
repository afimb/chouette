package fr.certu.chouette.exchange.xml.neptune.exporter.producer;

import chouette.schema.types.LongLatTypeType;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.type.Address;
import fr.certu.chouette.model.neptune.type.LongLatTypeEnum;
import fr.certu.chouette.model.neptune.type.ProjectedPoint;

public class StopPointProducer extends AbstractCastorNeptuneProducer<chouette.schema.StopPoint, StopPoint> {

	@Override
	public chouette.schema.StopPoint produce(StopPoint stopPoint) {
		chouette.schema.StopPoint castorStopPoint = new chouette.schema.StopPoint();
		
		//
		populateFromModel(castorStopPoint, stopPoint);
		
		castorStopPoint.setComment(stopPoint.getComment());
		castorStopPoint.setName(stopPoint.getName());
		castorStopPoint.setLineIdShortcut(stopPoint.getLineIdShortcut());
		
		Address address = stopPoint.getAddress();
		if(stopPoint.getAddress() != null){
			chouette.schema.Address castorAddress = new chouette.schema.Address();
			castorAddress.setCountryCode(address.getCountryCode());
			castorAddress.setStreetName(address.getStreetName());
			castorStopPoint.setAddress(castorAddress);
		}
		
		castorStopPoint.setContainedIn(getNonEmptyObjectId(stopPoint.getContainedInStopArea()));
		castorStopPoint.setLatitude(stopPoint.getLatitude());
		castorStopPoint.setLongitude(stopPoint.getLongitude());
		
		if(stopPoint.getLongLatType() != null){
			LongLatTypeEnum longLatType = stopPoint.getLongLatType();
			try {
				castorStopPoint.setLongLatType(LongLatTypeType.fromValue(longLatType.value()));
			} catch (IllegalArgumentException e) {
				// TODO generate report
			}
		}
		
		ProjectedPoint projectedPoint = stopPoint.getProjectedPoint();
		if(projectedPoint != null){
			chouette.schema.ProjectedPoint castorProjectedPoint = new chouette.schema.ProjectedPoint();
			castorProjectedPoint.setProjectionType(projectedPoint.getProjectionType());
			castorProjectedPoint.setX(projectedPoint.getX());
			castorProjectedPoint.setY(projectedPoint.getY());
			castorStopPoint.setProjectedPoint(castorProjectedPoint);
		}
		castorStopPoint.setPtNetworkIdShortcut(stopPoint.getPtNetworkIdShortcut());
						
		return castorStopPoint;
	}

}
