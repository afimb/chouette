/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.exchange.geoportail.exporter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import fr.certu.chouette.exchange.geoportail.exporter.report.GeoportailReport;
import fr.certu.chouette.model.neptune.AccessLink;
import fr.certu.chouette.model.neptune.AccessPoint;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.type.ChouetteAreaEnum;

/**
 *
 */
public class NeptuneData
{
   @Getter String source;
   @Getter Set<StopArea> stopAreas = new HashSet<StopArea>();
   @Getter Set<AccessPoint> accessPoints = new HashSet<AccessPoint>();

   public void populate(PTNetwork ptnetwork, GeoportailReport report)
   {
      source = ptnetwork.getObjectId().split(":")[0];
      List<Line> lines = ptnetwork.getLines();
      for (Line line : lines)
      {
         line.complete();
         if (line.getRoutes() != null)
         {
            for (Route route : line.getRoutes())
            {
               for (StopPoint point : route.getStopPoints())
               {
                  StopArea stop = point.getContainedInStopArea();
                  if (stopAreas.contains(stop)) continue;
                  if (stop.getObjectId().startsWith(source))
                  {
                     stopAreas.add(stop);
                     addParents(stop);
                     addAccessPoints(stop);
                  }
               }
            }
         }
      }
   }
   private void addAccessPoints(StopArea stop)
   {
      if (stop.getAccessLinks() != null && !stop.getAccessLinks().isEmpty())
      {
         for (AccessLink link : stop.getAccessLinks())
         {
            AccessPoint access = link.getAccessPoint();
            if (accessPoints.contains(access)) continue;
            if (access.getObjectId().startsWith(source))
            {
               accessPoints.add(access);
            }
         }
      }

   }

   private void addParents(StopArea stop)
   {
      if (stop.getParents() != null && !stop.getParents().isEmpty())
      {
         for (StopArea parent : stop.getParents())
         {
            if (parent.getAreaType().equals(ChouetteAreaEnum.ITL)) continue;
            if (stopAreas.contains(parent)) continue;
            if (parent.getObjectId().startsWith(source))
            {
               stopAreas.add(parent);
               addParents(parent);
               addAccessPoints(parent);
            }
         }
      }

   }

}
