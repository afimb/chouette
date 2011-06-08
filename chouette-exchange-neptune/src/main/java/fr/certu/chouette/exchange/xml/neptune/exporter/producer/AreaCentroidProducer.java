package fr.certu.chouette.exchange.xml.neptune.exporter.producer;

import chouette.schema.types.LongLatTypeType;
import fr.certu.chouette.model.neptune.AreaCentroid;
import fr.certu.chouette.model.neptune.type.Address;
import fr.certu.chouette.model.neptune.type.LongLatTypeEnum;
import fr.certu.chouette.model.neptune.type.ProjectedPoint;

public class AreaCentroidProducer extends AbstractCastorNeptuneProducer<chouette.schema.AreaCentroid, AreaCentroid> {

	@Override
	public chouette.schema.AreaCentroid produce(AreaCentroid areaCentroid) {
		chouette.schema.AreaCentroid castorAreaCentroid = new chouette.schema.AreaCentroid();
		
		//
		populateFromModel(castorAreaCentroid, areaCentroid);
		
		castorAreaCentroid.setComment(getNotEmptyString(areaCentroid.getComment()));
		castorAreaCentroid.setName(areaCentroid.getName());
		
		Address address = areaCentroid.getAddress();
		if(areaCentroid.getAddress() != null){
			chouette.schema.Address castorAddress = new chouette.schema.Address();
			castorAddress.setCountryCode(getNotEmptyString(address.getCountryCode()));
			castorAddress.setStreetName(getNotEmptyString(address.getStreetName()));
			castorAreaCentroid.setAddress(castorAddress);
		}
		
		castorAreaCentroid.setContainedIn(getNonEmptyObjectId(areaCentroid.getContainedInStopArea()));
		castorAreaCentroid.setLatitude(areaCentroid.getLatitude());
		castorAreaCentroid.setLongitude(areaCentroid.getLongitude());
		
		if(areaCentroid.getLongLatType() != null){
			LongLatTypeEnum longLatType = areaCentroid.getLongLatType();
			try {
				castorAreaCentroid.setLongLatType(LongLatTypeType.fromValue(longLatType.value()));
			} catch (IllegalArgumentException e) {
				// TODO generate report
			}
		}
		
		ProjectedPoint projectedPoint = areaCentroid.getProjectedPoint();
		if(projectedPoint != null){
			chouette.schema.ProjectedPoint castorProjectedPoint = new chouette.schema.ProjectedPoint();
			castorProjectedPoint.setProjectionType(projectedPoint.getProjectionType());
			castorProjectedPoint.setX(projectedPoint.getX());
			castorProjectedPoint.setY(projectedPoint.getY());
			castorAreaCentroid.setProjectedPoint(castorProjectedPoint);
		}
						
		return castorAreaCentroid;
	}

}
