package fr.certu.chouette.exchange.xml.neptune.importer.producer;

import java.util.List;

import org.trident.schema.trident.ChouettePTNetworkType.ChouetteLineDescription.ChouetteRoute;

import fr.certu.chouette.exchange.xml.neptune.importer.Context;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.type.PTDirectionEnum;
import fr.certu.chouette.plugin.exchange.report.ExchangeReportItem;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportItem;

public class RouteProducer extends AbstractModelProducer<Route, ChouetteRoute>
{
   public Route produce(Context context, ChouetteRoute xmlRoute)
   {
      Route route = new Route();

      // objectId, objectVersion, creatorId, creationTime
      populateFromCastorNeptune(context, route, xmlRoute);

      // Name optional
      route.setName(getNonEmptyTrimedString(xmlRoute.getName()));

      // Direction optional
      if (xmlRoute.getDirection() != null)
      {
         try
         {
            route.setDirection(PTDirectionEnum.valueOf(xmlRoute.getDirection()
                  .value()));
         } catch (IllegalArgumentException e)
         {
            // TODO: traiter le cas de non correspondance

         }
      }

      // JourneyPatternIds [1..w]
      List<String> castorJourneyPatternIds = xmlRoute.getJourneyPatternId();
      for (String castorJourneyPatternId : castorJourneyPatternIds)
      {
         String journeyPatternId = getNonEmptyTrimedString(castorJourneyPatternId);
         if (journeyPatternId == null)
         {
            ReportItem item = new ExchangeReportItem(
                  ExchangeReportItem.KEY.EMPTY_ROUTE, Report.STATE.WARNING,
                  route.getObjectId());
            context.getImportReport().addItem(item);
         } else
         {
            route.addJourneyPatternId(journeyPatternId);
         }
      }

      // Number optional
      route.setNumber(getNonEmptyTrimedString(xmlRoute.getNumber()));

      // PTLinkIds [1..w]
      List<String> castorPTLinkIds = xmlRoute.getPtLinkId();
      for (String castorPTLinkId : castorPTLinkIds)
      {
         String ptLinkId = getNonEmptyTrimedString(castorPTLinkId);
         if (ptLinkId == null)
         {
            ReportItem item = new ExchangeReportItem(
                  ExchangeReportItem.KEY.EMPTY_ROUTE, Report.STATE.WARNING,
                  route.getObjectId());
            context.getImportReport().addItem(item);
         } else
         {
            route.addPTLinkId(ptLinkId);
         }
      }

      // PublishedName optional
      route.setPublishedName(getNonEmptyTrimedString(xmlRoute
            .getPublishedName()));

      // WayBack optional

      if (xmlRoute.getRouteExtension() != null)
      {
         String wb = getNonEmptyTrimedString(xmlRoute.getRouteExtension()
               .getWayBack());
         route.setWayBack(wb.toLowerCase().startsWith("a") ? "A" : "R");
      }

      // WayBackRouteId optional
      route.setWayBackRouteId(getNonEmptyTrimedString(xmlRoute
            .getWayBackRouteId()));

      // Comment optional
      route.setComment(getNonEmptyTrimedString(xmlRoute.getComment()));

      // return null if in conflict with other files, else return object
      return checkUnsharedData(context, route, xmlRoute);
   }
}
