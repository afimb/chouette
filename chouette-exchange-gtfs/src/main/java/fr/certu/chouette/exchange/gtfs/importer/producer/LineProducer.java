package fr.certu.chouette.exchange.gtfs.importer.producer;

import java.awt.Color;

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

      line.setObjectId(composeObjectId(Line.LINE_KEY, gtfsLine.getRouteId(), logger));

      // Name optional
      line.setName(getNonEmptyTrimedString(gtfsLine.getRouteLongName()));
      if (line.getName() == null)
         line.setName(getNonEmptyTrimedString(gtfsLine.getRouteShortName()));

      // Number optional
      line.setNumber(getNonEmptyTrimedString(gtfsLine.getRouteShortName()));

      // PublishedName optional
      line.setPublishedName(getNonEmptyTrimedString(gtfsLine.getRouteLongName()));
      
      // Name = route_long_name oder route_short_name
      if (line.getPublishedName() != null)
      {
         line.setName(line.getPublishedName()); 
      }
      else
      {
         line.setName(line.getNumber()); 
      }
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
      case Funicular:
         line.setTransportModeName(TransportModeNameEnum.Other);
         break;
      default:
         line.setTransportModeName(TransportModeNameEnum.Other);
         break;

      }

      // Registration optional
      String[] token = line.getObjectId().split(":");
      line.setRegistrationNumber(token[2]);

      // Comment optional
      line.setComment(gtfsLine.getRouteDesc());

      // Company optional
      if (gtfsLine.getAgencyId() != null)
      {
         String cid = getNonEmptyTrimedString(composeObjectId(Company.COMPANY_KEY, gtfsLine.getAgencyId(), logger));
         if (cid != null)
         {
            line.getCompanyIds().add(cid);
         }
      }

      line.setColor(toHexa(gtfsLine.getRouteColor()));
      line.setTextColor(toHexa(gtfsLine.getRouteTextColor()));
      line.setUrl(toString(gtfsLine.getRouteUrl()));

      return line;
   }

   private String toHexa(Color color)
   {
      // TODO : check alpha !!!
      if (color == null)
         return null;
      String ret = Integer.toHexString(color.getRGB());
      if (ret.length() == 8)
         ret = ret.substring(2);
      while (ret.length() < 6)
         ret = "0" + ret;
      return ret;

   }

}
