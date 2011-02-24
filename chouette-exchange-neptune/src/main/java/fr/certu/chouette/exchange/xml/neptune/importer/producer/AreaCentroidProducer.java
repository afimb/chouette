package fr.certu.chouette.exchange.xml.neptune.importer.producer;

import fr.certu.chouette.model.neptune.AreaCentroid;
import fr.certu.chouette.model.neptune.type.Address;
import fr.certu.chouette.model.neptune.type.LongLatTypeEnum;
import fr.certu.chouette.model.neptune.type.ProjectedPoint;
import fr.certu.chouette.plugin.report.ReportItem;

public class AreaCentroidProducer extends AbstractModelProducer<AreaCentroid,chouette.schema.AreaCentroid>
{
	@Override
	public AreaCentroid produce(chouette.schema.AreaCentroid xmlAreaCentroid,ReportItem report)
	{
		AreaCentroid areaCentroid = new AreaCentroid();
		// objectId, objectVersion, creatorId, creationTime
		populateFromCastorNeptune(areaCentroid, xmlAreaCentroid,report);

		// Name mandatory
		areaCentroid.setName(getNonEmptyTrimedString(xmlAreaCentroid.getName()));

		// Comment optional
		areaCentroid.setComment(getNonEmptyTrimedString(xmlAreaCentroid.getComment()));
		
		// LongLatType mandatory
		if(xmlAreaCentroid.getLongLatType() != null){
			try {
				areaCentroid.setLongLatType(LongLatTypeEnum.fromValue(xmlAreaCentroid.getLongLatType().value()));
			}
			catch (IllegalArgumentException e) 
			{
				// TODO: traiter le cas de non correspondance
				e.printStackTrace();
			}
		}
		
		// Latitude mandatory
		areaCentroid.setLatitude(xmlAreaCentroid.getLatitude());
		
		// Longitude mandatory
		areaCentroid.setLongitude(xmlAreaCentroid.getLongitude());
		
		// ContainedInStopAreaId 
		areaCentroid.setContainedInStopAreaId(getNonEmptyTrimedString(xmlAreaCentroid.getContainedIn()));
		
		// Address optional
		chouette.schema.Address xmlAddress = xmlAreaCentroid.getAddress();		
		if(xmlAddress != null){
			Address address = new Address();
			address.setCountryCode(getNonEmptyTrimedString(xmlAddress.getCountryCode()));
			address.setStreetName(getNonEmptyTrimedString(xmlAddress.getStreetName()));
		}
		
		// ProjectedPoint optional
		chouette.schema.ProjectedPoint xmlProjectedPoint = xmlAreaCentroid.getProjectedPoint();
		if(xmlProjectedPoint != null){
			ProjectedPoint projectedPoint = new ProjectedPoint();
			projectedPoint.setX(xmlProjectedPoint.getX());
			projectedPoint.setY(xmlProjectedPoint.getY());
			projectedPoint.setProjectionType(xmlProjectedPoint.getProjectionType());
		}
		
		return areaCentroid;
	}

}
