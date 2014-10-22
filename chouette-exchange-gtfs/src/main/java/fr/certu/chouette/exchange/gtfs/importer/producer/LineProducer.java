package fr.certu.chouette.exchange.gtfs.importer.producer;

import org.apache.log4j.Logger;

import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsRoute;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.type.TransportModeNameEnum;
import fr.certu.chouette.plugin.report.Report;

public class LineProducer extends AbstractModelProducer<Line, GtfsRoute>
{
   private static Logger logger = Logger.getLogger(LineProducer.class);

   public Line produce(GtfsRoute gtfsLine, Report report)
   {
      Line line = new Line();

      line.setObjectId(composeObjectId(Line.LINE_KEY, gtfsLine.getRouteId(),
            logger));

      // Name optional
      line.setName(getNonEmptyTrimedString(gtfsLine.getRouteShortName()));
      if (line.getName() == null)
         line.setName(getNonEmptyTrimedString(gtfsLine.getRouteLongName()));

      // Number optional
      line.setNumber(getNonEmptyTrimedString(gtfsLine.getRouteShortName()));

      // PublishedName optional
      line.setPublishedName(getNonEmptyTrimedString(gtfsLine.getRouteLongName()));

      // TransportModeName optional
      switch (gtfsLine.getRouteType())
      {
      case Tram:
         line.setTransportModeName(TransportModeNameEnum.Tramway);
         break;
      case Subway:
         line.setTransportModeName(TransportModeNameEnum.Metro);
         break;
      case Rail:
         line.setTransportModeName(TransportModeNameEnum.Train);
         break;
      case Bus:
         line.setTransportModeName(TransportModeNameEnum.Bus);
         break;
      case Ferry:
         line.setTransportModeName(TransportModeNameEnum.Ferry);
         break;
      case Cable:
         line.setTransportModeName(TransportModeNameEnum.Other);
         break;
      case Gondola:
         line.setTransportModeName(TransportModeNameEnum.Other);
         break;
      case Funicular : 
         line.setTransportModeName(TransportModeNameEnum.Other);
         break;
      default:
         line.setTransportModeName(TransportModeNameEnum.Other);
         break;

      }

      // Registration optional
      String[] token = line.getObjectId().split(":");
      line.setRegistrationNumber(token[2]);

      // Comment optional : refers to company
      if (gtfsLine.getAgencyId() != null)
      {
         line.setComment(getNonEmptyTrimedString(composeObjectId(
               Company.COMPANY_KEY, gtfsLine.getAgencyId(), logger)));
         if (line.getComment() != null && line.getComment().length() > 255)
            line.setComment(line.getComment().substring(0, 255));
      } else
      {
         // if missing, ModelAssembler will take first agency
         line.setComment(null);
      }
      return line;
   }

}
