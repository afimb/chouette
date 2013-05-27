package fr.certu.chouette.exchange.gtfs.importer;

import java.math.BigDecimal;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import fr.certu.chouette.model.neptune.ConnectionLink;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.type.ConnectionLinkTypeEnum;

public class ConnectionLinkGenerator extends AbstractGenerator
{
   private static final Logger logger = Logger.getLogger(ConnectionLinkGenerator.class);
   public List<ConnectionLink> createConnectionLinks(List<StopArea> commercials,double distanceMax, List<ConnectionLink> fixedLinks, List<ConnectionLink> excludedLinks) 
   {
      double minDistanceRejected = 1000000;
      Map<String,ConnectionLink> fixedLinkMap = ConnectionLink.mapOnObjectIds(fixedLinks);
      Map<String,ConnectionLink> excludedLinkMap = ConnectionLink.mapOnObjectIds(excludedLinks);

      // build a map for CL for routes
      Map<String, List<String>> routesForStopArea = new HashMap<String, List<String>>();
      List<ConnectionLink> links = new ArrayList<ConnectionLink>();
      if (distanceMax <=0 ) return links; // nothing to do

      for (int i = 0; i < commercials.size() -1; i++)
      {
         StopArea source = commercials.get(i);
         for (int j = i+1; j < commercials.size() ; j++)
         {
            StopArea target =  commercials.get(j);
            double distance = distance(source.getLongitude().doubleValue(),
                  source.getLatitude().doubleValue(),
                  target.getLongitude().doubleValue(),
                  target.getLatitude().doubleValue());
            if (distance < distanceMax)
            {
               // eligible, check route
               List<String> sourceRoutes = routesForStopArea.get(source.getObjectId());
               if (sourceRoutes == null)
               {
                  sourceRoutes = computeRoutes(source);
                  routesForStopArea.put(source.getObjectId(), sourceRoutes);
               }
               List<String> targetRoutes = routesForStopArea.get(target.getObjectId());
               if (targetRoutes == null)
               {
                  targetRoutes = computeRoutes(target);
                  routesForStopArea.put(target.getObjectId(), targetRoutes);
               }
               boolean ok = true;
               for (String route : targetRoutes) 
               {
                  if (sourceRoutes.contains(route))
                  {
                     ok = false;
                     break;
                  }
               }
               if (ok)
               {
                  // create connectionLink
                  String[] sourceToken = source.getObjectId().split(":");
                  String[] targetToken = target.getObjectId().split(":");
                  String objectId = sourceToken[0]+":"+ConnectionLink.CONNECTIONLINK_KEY+":"+sourceToken[2]+"_"+targetToken[2];
                  String reverseId = sourceToken[0]+":"+ConnectionLink.CONNECTIONLINK_KEY+":"+targetToken[2]+"_"+sourceToken[2];

                  if (excludedLinkMap.containsKey(objectId)) continue;
                  if (excludedLinkMap.containsKey(reverseId)) continue;
                  double durationInMillis = distance * 900; // speed of 4 km/h 
                  Time defaultDuration = getTime((long) durationInMillis);

                  if (fixedLinkMap.containsKey(objectId) || fixedLinkMap.containsKey(reverseId))
                  {
                     ConnectionLink link = fixedLinkMap.get(objectId);
                     if (link != null)
                     {
                        if (link.getDefaultDuration() == null) link.setDefaultDuration(defaultDuration);
                        link.setLinkDistance(BigDecimal.valueOf(distance));
                        source.addConnectionLink(link);
                        target.addConnectionLink(link);
                        logger.info("ConnectionLink "+link.getName()+" updated"); 
                     }
                     link = fixedLinkMap.get(reverseId);
                     if (link != null)
                     {
                        if (link.getDefaultDuration() == null) link.setDefaultDuration(defaultDuration);
                        link.setLinkDistance(BigDecimal.valueOf(distance));
                        source.addConnectionLink(link);
                        target.addConnectionLink(link);   
                        logger.info("ConnectionLink "+link.getName()+" updated"); 

                     }
                  }
                  else
                  {
                     ConnectionLink link = new ConnectionLink();
                     link.setObjectId(objectId);
                     link.setDefaultDuration(defaultDuration);
                     link.setCreationTime(Calendar.getInstance().getTime());
                     link.setStartOfLink(source);
                     link.setEndOfLink(target);
                     link.setStartOfLinkId(source.getObjectId());
                     link.setEndOfLinkId(target.getObjectId());
                     link.setLinkDistance(BigDecimal.valueOf(distance));
                     link.setLinkType(ConnectionLinkTypeEnum.OVERGROUND);
                     link.setName("from "+source.getName()+" to "+target.getName());
                     // logger.info("ConnectionLink "+link.getName()+" added"); 
                     links.add(link);
                     source.addConnectionLink(link);
                     target.addConnectionLink(link);
                     ConnectionLink reverseLink = new ConnectionLink();
                     reverseLink.setDefaultDuration(defaultDuration);
                     objectId = sourceToken[0]+":"+ConnectionLink.CONNECTIONLINK_KEY+":"+targetToken[2]+"_"+sourceToken[2];
                     reverseLink.setObjectId(objectId);
                     reverseLink.setCreationTime(Calendar.getInstance().getTime());
                     reverseLink.setStartOfLink(target);
                     reverseLink.setEndOfLink(source);
                     reverseLink.setStartOfLinkId(target.getObjectId());
                     reverseLink.setEndOfLinkId(source.getObjectId());
                     reverseLink.setLinkDistance(BigDecimal.valueOf(distance));
                     reverseLink.setLinkType(ConnectionLinkTypeEnum.OVERGROUND);
                     reverseLink.setName("from "+target.getName()+" to "+source.getName());
                     // logger.info("ConnectionLink "+reverseLink.getName()+" added"); 
                     links.add(reverseLink);
                     source.addConnectionLink(reverseLink);
                     target.addConnectionLink(reverseLink);
                  }
               }
            }
            else if (distance < minDistanceRejected)
            {
               minDistanceRejected = distance;
            }

         }
      }

      if (links.isEmpty())
      {
         logger.info("ConnectionLink : no links builded , minimal distance found = "+minDistanceRejected+" > "+distanceMax); 
      }
      return links;
   }

   private List<String> computeRoutes(StopArea stop)
   {
      List<String> routes = new ArrayList<String>();
      if (stop.getContainedStopAreas() != null)
      {
         for (StopArea bp : stop.getContainedStopAreas()) 
         {
            if (bp.getContainedStopPoints() != null)
            {
               for (StopPoint point : bp.getContainedStopPoints())
               {
                  String route = point.getRoute().getObjectId(); 
                  if (!routes.contains(route)) routes.add(route);
               }
            }
            else
            {
               logger.warn("Stop without trip : "+bp.getObjectId());
            }
         }
      }
      else
      {
         logger.warn("Stop without child : "+stop.getObjectId());
      }
      return routes;
   }
   protected Time getTime(long timeInMillis) 
   {
      long timeInSec = timeInMillis / 1000;
      Calendar c = Calendar.getInstance();
      int d = c.get(Calendar.DATE);
      int M = c.get(Calendar.MONTH);
      int y = c.get(Calendar.YEAR);
      int s = (int) (timeInSec % 3600);
      timeInSec /=60;
      int m = (int) (timeInSec % 60);
      timeInSec /=60;
      int h = (int) (timeInSec);
      c.set(y, M, d, h, m, s);
      Time time = new Time(c.getTimeInMillis());
      return time;
   }

}
