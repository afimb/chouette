package fr.certu.chouette.exchange.gtfs.importer.producer;

import org.apache.log4j.Logger;

import fr.certu.chouette.exchange.gtfs.model.GtfsStop;
import fr.certu.chouette.model.neptune.AreaCentroid;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.type.ChouetteAreaEnum;
import fr.certu.chouette.model.neptune.type.LongLatTypeEnum;
import fr.certu.chouette.plugin.report.ReportItem;

public class StopAreaProducer extends AbstractModelProducer<StopArea,GtfsStop>
{
	private static Logger logger = Logger.getLogger(StopAreaProducer.class);
	@Override
	public StopArea produce(GtfsStop gtfsStop,ReportItem report) 
	{
		StopArea stopArea = new StopArea();

		// objectId, objectVersion, creatorId, creationTime
		stopArea.setObjectId(composeObjectId( StopArea.STOPAREA_KEY, gtfsStop.getStopId(),logger));

		// AreaCentroid optional
		AreaCentroid centroid = new AreaCentroid();
		centroid.setObjectId(composeObjectId( AreaCentroid.AREACENTROID_KEY, gtfsStop.getStopId(),logger));
		centroid.setName(getNonEmptyTrimedString(gtfsStop.getStopName()));
		centroid.setLatitude(gtfsStop.getStopLat());
		centroid.setLongitude(gtfsStop.getStopLon());
		centroid.setLongLatType(LongLatTypeEnum.WGS84);

		stopArea.setAreaCentroid(centroid);
		centroid.setContainedInStopArea(stopArea);

		// Name optional
		stopArea.setName(getNonEmptyTrimedString(gtfsStop.getStopName()));

		// Comment optional
		stopArea.setComment(getNonEmptyTrimedString(gtfsStop.getStopDesc()));
		if (stopArea.getComment() != null && stopArea.getComment().length() > 255) 
			stopArea.setComment(stopArea.getComment().substring(0,255));

		// farecode 
		stopArea.setFareCode(0);

		if (gtfsStop.getLocationType() == GtfsStop.STATION)
		{
			stopArea.setAreaType(ChouetteAreaEnum.COMMERCIALSTOPPOINT);
			if (getNonEmptyTrimedString(gtfsStop.getParentStation()) != null)
			{
				logger.warn("station "+stopArea.getName()+" has parent "+ getNonEmptyTrimedString(gtfsStop.getParentStation()));  
			}
		}
		else
		{
			stopArea.setAreaType(ChouetteAreaEnum.BOARDINGPOSITION);
			stopArea.setParentObjectId(getNonEmptyTrimedString(gtfsStop.getParentStation()));
		}

		// RegistrationNumber optional
		String[] token = stopArea.getObjectId().split(":");
		stopArea.setRegistrationNumber(token[2]);

		return stopArea;
	}

}
