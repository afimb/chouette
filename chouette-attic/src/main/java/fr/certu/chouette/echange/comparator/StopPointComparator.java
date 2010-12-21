package fr.certu.chouette.echange.comparator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.modele.ArretItineraire;
import fr.certu.chouette.modele.Itineraire;
import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.service.commun.CodeIncident;

/**
 * @author michel
 *
 */
public class StopPointComparator extends AbstractChouetteDataComparator 
{
   private static final Log logger = LogFactory.getLog(StopPointComparator.class);
   
   /*
   // map locale des routes
   private Map<String, String> sourceTargetIdMap = new HashMap<String, String>();   
   private Map<String, String> targetSourceIdMap = new HashMap<String, String>();

   private void addMappingIds(String sourceId, String targetId) 
   {
      sourceTargetIdMap.put(sourceId, targetId);
      targetSourceIdMap.put(targetId, sourceId);
   }

   private String getSourceId(String targetId)
   {
      return targetSourceIdMap.get(targetId);
   }

   private String getTargetId(String sourceId)
   {
      return sourceTargetIdMap.get(sourceId);
   }  

   */
   
   private boolean compareDataImpl() throws Exception
   {
      ILectureEchange source = master.getSource();
      ILectureEchange target = master.getTarget();

      ExchangeableLineObjectIdMapper sourceMapper = master.getSourceExchangeMap();
      ExchangeableLineObjectIdMapper targetMapper = master.getTargetExchangeMap();

      List<ArretItineraire> sourceDataList = source.getArrets();
      List<ArretItineraire> targetDataList = target.getArrets();

      if (sourceDataList == null || sourceDataList.size() == 0) 
      {
         throw new ComparatorException(ComparatorException.TYPE.UnbuildResource,"ILectureEchange source getArrets");
      }
      if (targetDataList == null || targetDataList.size() == 0) 
      {
         throw new ComparatorException(ComparatorException.TYPE.UnbuildResource,"ILectureEchange target getArrets");
      }

      boolean validList = true;

      Map<String,ArretItineraire> targetDataMap = new HashMap<String, ArretItineraire>();

      for (ArretItineraire targetData : targetDataList) 
      {
         String key = buildKey(targetData, targetMapper,true);
         if (targetDataMap.containsKey(key))
         {
            Integer i = 1;
            String dupKey = key ;
            while (targetDataMap.containsKey(dupKey))
            {
               dupKey = key + "-"+i.toString(); 
               i++;
            }

            key = dupKey;
         }
         targetDataMap.put(key, targetData);

         /*
         ArretItineraire oldTargetData = targetDataMap.put(key, targetData);
         if (oldTargetData != null)
         {
            throw new ComparatorException(ComparatorException.TYPE.DuplicateKey, "target key = " + key + " ids = " + targetData.getObjectId() + "," + oldTargetData.getObjectId());
         }
          */
      }


      // check all the elements of the source list, 
      // if found in the target map, remove it from the target map
      // at the end of processing, the target map contains only unknown objects in the source list
      for (ArretItineraire sourceData : sourceDataList) 
      {
         // String parentId = sourceData.getContainedIn();
         // PositionGeographique sourceParent = sourceMapper.getStopArea(parentId);		    
         ArretItineraire targetData = targetDataMap.remove(buildKey(sourceData, sourceMapper,false));
         ChouetteObjectState objectState = null;
         if (targetData == null)
         {
            //throw new ServiceException(CodeIncident.COMPARATOR_UNVAILABLE_RESOURCE,"No target stoppoint");
            objectState= new ChouetteObjectState(getMappingKey(), sourceData.getObjectId(), null);		    
         }
         else
         {
            // String targetParentId = targetData.getContainedIn();		        
            // PositionGeographique targetParent = targetMapper.getStopArea(targetParentId);

            objectState = new ChouetteObjectState(getMappingKey(),sourceData.getObjectId(),targetData.getObjectId());
            objectState.addAttributeState("Name", sourceData.getName(), targetData.getName());

            // objectState.addAttributeState("ParentName", sourceParent.getName(), targetParent.getName());
            // objectState.addAttributeState("ParentLatitude", sourceParent.getLatitude(), targetParent.getLatitude());
            // objectState.addAttributeState("ParentLongitude", sourceParent.getLongitude(), targetParent.getLongitude());
            // objectState.addAttributeState("Position", sourceData.getPosition(), targetData.getPosition());

            master.addMappingIds(sourceData.getObjectId(), targetData.getObjectId());
            /*
            // ajout du mapping des routes pour dédouanner les doublons
            Itineraire sourceRoute = sourceMapper.getRouteOfStopPoint(sourceData.getObjectId());
            Itineraire targetRoute = targetMapper.getRouteOfStopPoint(targetData.getObjectId());

            addMappingIds(sourceRoute.getObjectId(), targetRoute.getObjectId());
            */
         }
         validList = validList && objectState.isIdentical();
         master.addObjectState(objectState);
      }

      // advise for target object not found in source list
      for (ArretItineraire targetData : targetDataMap.values().toArray(new ArretItineraire[0]))
      {
         ChouetteObjectState objectState = new ChouetteObjectState(getMappingKey(),null,targetData.getObjectId());
         master.addObjectState(objectState);
         validList = false;
      }
      return validList;
   }

   private String buildKey(ArretItineraire data, ExchangeableLineObjectIdMapper mapper,boolean withConversion)
   {
      String parentId = data.getContainedIn();
      PositionGeographique parent = mapper.getStopArea(parentId);
      Itineraire route = mapper.getRouteOfStopPoint(data.getObjectId());
      
      String dir = "U";
      if (route != null)
      {
         /*
         boolean isMapped = false;
         String routeId = route.getObjectId();
         if (withConversion)
         {
            isMapped = (getSourceId(routeId) != null);
            if (isMapped) routeId = getSourceId(routeId);
         }
         else
         {
            isMapped = (getSourceId(routeId) != null);
         }
         if (isMapped)
         {
            dir = "-"+routeId;
         }
         else
         */
         {
            {
            List<ArretItineraire> arrets = mapper.getStopPointsOfRoute(route.getObjectId());
            if (arrets != null)
            {
               PositionGeographique start = mapper.getStopArea(arrets.get(0).getContainedIn());
               PositionGeographique end = mapper.getStopArea(arrets.get(arrets.size() - 1).getContainedIn());
               if ((start != null) && (end != null))
                  dir = start.getName() + "-" + end.getName();
            }
            dir += "-"+mapper.getJourneyPatternList(route.getObjectId()).size();
            }
            String retourId = route.getChouetteRoute().getWayBackRouteId();
            if (retourId != null)
            {
               List<ArretItineraire> arrets = mapper.getStopPointsOfRoute(retourId);
               if (arrets != null)
               {
                  PositionGeographique start = mapper.getStopArea(arrets.get(0).getContainedIn());
                  PositionGeographique end = mapper.getStopArea(arrets.get(arrets.size() - 1).getContainedIn());
                  if ((start != null) && (end != null))
                     dir = start.getName() + "-" + end.getName();
               }
               dir += "-"+mapper.getJourneyPatternList(retourId).size();
            }
            
         }
      }
      String key = data.getPosition() + "-" + parent.getName() + "-" + parent.getLatitude() + "-" + parent.getLongitude() + "-" + dir;
      // logger.debug(data.getObjectId() +": key = "+key);
      return key ;
   }

   @Override
   public Map<String, ChouetteObjectState> getStateMap()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public boolean compareData(IExchangeableLineComparator master)
   throws Exception
   {
      this.master = master;
      ILectureEchange source = master.getSource();

      int routeCount = 1; //source.getItineraires().size();

      boolean result = false;
      for (int i = 0; i < routeCount && !result; i++)
      {
         if (i > 0) logger.debug("maybe dupplicate key : try again ");
         result = compareDataImpl();
      }

      return result;
   }
}
