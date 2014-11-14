package fr.certu.chouette.exchange.gtfs.importer.producer;

import org.apache.log4j.Logger;

import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsStop;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.type.ChouetteAreaEnum;
import fr.certu.chouette.model.neptune.type.LongLatTypeEnum;
import fr.certu.chouette.plugin.exchange.report.ExchangeReportItem;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportItem;

public class StopAreaProducer extends AbstractModelProducer<StopArea, GtfsStop>
{
   private static Logger logger = Logger.getLogger(StopAreaProducer.class);

   @Override
   public StopArea produce(GtfsStop gtfsStop, Report report)
   {
      StopArea stopArea = new StopArea();

      // objectId, objectVersion, creatorId, creationTime
      stopArea.setObjectId(composeObjectId(StopArea.STOPAREA_KEY,
            gtfsStop.getStopId(), logger));

      stopArea.setLatitude(gtfsStop.getStopLat());
      stopArea.setLongitude(gtfsStop.getStopLon());
      stopArea.setLongLatType(LongLatTypeEnum.WGS84);

      // Name optional
      stopArea.setName(getNonEmptyTrimedString(gtfsStop.getStopName()));

      // URL optional
      stopArea.setUrl(toString(gtfsStop.getStopUrl()));

      // Comment optional
      stopArea.setComment(getNonEmptyTrimedString(gtfsStop.getStopDesc()));
      if (stopArea.getComment() != null && stopArea.getComment().length() > 255)
         stopArea.setComment(stopArea.getComment().substring(0, 255));

      // farecode
      stopArea.setFareCode(0);

      if (gtfsStop.getLocationType() == GtfsStop.LocationType.Station)
      {
         stopArea.setAreaType(ChouetteAreaEnum.CommercialStopPoint);
         if (getNonEmptyTrimedString(gtfsStop.getParentStation()) != null)
         {
            ReportItem item = new ExchangeReportItem(
                  ExchangeReportItem.KEY.IGNORED_DATA, Report.STATE.WARNING,
                  "stops.txt", gtfsStop.getId(), "parent_station",
                  gtfsStop.getParentStation());
            report.addItem(item);
            logger.warn("station " + stopArea.getName() + " has parent "
                  + getNonEmptyTrimedString(gtfsStop.getParentStation()));
         }
      } else if (gtfsStop.getLocationType() == GtfsStop.LocationType.Access)
      {
         return null;
      } else
      {
         stopArea.setAreaType(ChouetteAreaEnum.BoardingPosition);
         stopArea.setParentObjectId(getNonEmptyTrimedString(gtfsStop
               .getParentStation()));
      }

      // RegistrationNumber optional
      //      String[] token = stopArea.getObjectId().split(":");
      //      stopArea.setRegistrationNumber(token[2]);
      // TODO : code

      // extension
       stopArea.setStreetName(gtfsStop.getAddressLine());
       stopArea.setCityName(gtfsStop.getLocality());
       stopArea.setZipCode(gtfsStop.getPostalCode());

      return stopArea;
   }

}
