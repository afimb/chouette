package fr.certu.chouette.exchange.xml.neptune.exporter.producer;

import chouette.schema.types.LongLatTypeType;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.type.LongLatTypeEnum;

public class AreaCentroidProducer extends AbstractCastorNeptuneProducer<chouette.schema.AreaCentroid, StopArea> {

	@Override
	public chouette.schema.AreaCentroid produce(StopArea area) 
	{
		chouette.schema.AreaCentroid castorAreaCentroid = new chouette.schema.AreaCentroid();
		
		//
		populateFromModel(castorAreaCentroid, area);
		
		castorAreaCentroid.setObjectId(castorAreaCentroid.getObjectId().replace(":StopArea:", ":AreaCentroid:"));
		castorAreaCentroid.setComment(getNotEmptyString(area.getComment()));
		castorAreaCentroid.setName(area.getName());
		
		if(area.hasAddress())
		{
			chouette.schema.Address castorAddress = new chouette.schema.Address();
			castorAddress.setCountryCode(getNotEmptyString(area.getCountryCode()));
			castorAddress.setStreetName(getNotEmptyString(area.getStreetName()));
			castorAreaCentroid.setAddress(castorAddress);
		}
		
		castorAreaCentroid.setContainedIn(getNonEmptyObjectId(area));
		
		if(area.hasCoordinates())
		{
			LongLatTypeEnum longLatType = area.getLongLatType();
			castorAreaCentroid.setLatitude(area.getLatitude());
			castorAreaCentroid.setLongitude(area.getLongitude());
			try {
				castorAreaCentroid.setLongLatType(LongLatTypeType.fromValue(longLatType.value()));
			} catch (IllegalArgumentException e) {
				// TODO generate report
			}
		}
		
		if(area.hasProjection())
		{
			chouette.schema.ProjectedPoint castorProjectedPoint = new chouette.schema.ProjectedPoint();
			castorProjectedPoint.setProjectionType(area.getProjectionType());
			castorProjectedPoint.setX(area.getX());
			castorProjectedPoint.setY(area.getY());
			castorAreaCentroid.setProjectedPoint(castorProjectedPoint);
		}
						
		return castorAreaCentroid;
	}

}
