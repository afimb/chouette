package fr.certu.chouette.echange.comparator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.modele.Correspondance;
import fr.certu.chouette.modele.PositionGeographique;

/**
 * @author michel
 */
public class ConnectionLinkComparator extends AbstractChouetteDataComparator
{
   public boolean compareData(IExchangeableLineComparator master) throws ComparatorException
   {
      this.master = master;
      ILectureEchange source = master.getSource();
      ILectureEchange target = master.getTarget();

      ExchangeableLineObjectIdMapper sourceMapper = master.getSourceExchangeMap();
      ExchangeableLineObjectIdMapper targetMapper = master.getTargetExchangeMap();

      List<Correspondance> sourceDataList = source.getCorrespondances();
      List<Correspondance> targetDataList = target.getCorrespondances();

      boolean validList = true;
      Map<String,Correspondance> sourceDataMap = new HashMap<String, Correspondance>();
      Map<String,Correspondance> targetDataMap = new HashMap<String, Correspondance>();

      // the natural key is the name of the start stopArea + the name of the end StopArea : checking for unique key
      for (Correspondance sourceData : sourceDataList) 
      {
         String key = buildKey(sourceData, sourceMapper);
         if (key == null)
         {
            ChouetteObjectState objectState = null;
            objectState = new ChouetteObjectState(getMappingKey(), sourceData.getObjectId(), null);       
            master.addObjectState(objectState);
         }
         else
         {
            if (sourceDataMap.put(key,sourceData) != null)
            {
               // duplicate key : cannot check correctly this data            		
               continue;
               //throw new ComparatorException(ComparatorException.TYPE.DuplicateKey,"StartStopName+EndStopName in source ConnectionLink");
            }
         }
      }
      for (Correspondance targetData : targetDataList) 
      {
         String key = buildKey(targetData, targetMapper);
         if (key == null)
         {
            ChouetteObjectState objectState = new ChouetteObjectState(getMappingKey(), null, targetData.getObjectId());       
            master.addObjectState(objectState);
         }
         else
         {
            if (targetDataMap.put(key,targetData) != null)
            {
               continue;
               // duplicate key : cannot check correctly this data
               //throw new ComparatorException(ComparatorException.TYPE.DuplicateKey,"StopStopName+EndStopName in target ConnectionLink");
            }
         }
      }

      // check all the elements of the source list, 
      // if found in the target map, remove it from the target map
      // at the end of processing, the target map contains only unknown objects in the source list
      for (Correspondance sourceData : sourceDataList) 
      {
         String key = buildKey(sourceData, sourceMapper);
         if (key == null)
         {
            continue;
         }
         Correspondance targetData = targetDataMap.remove(key);
         ChouetteObjectState objectState = null;
         if (targetData == null)
         {
            objectState = new ChouetteObjectState(getMappingKey(), sourceData.getObjectId(), null);       
         }
         else
         {
            objectState = new ChouetteObjectState(getMappingKey(),sourceData.getObjectId(),targetData.getObjectId());
            objectState.addAttributeState("Name", sourceData.getName(), targetData.getName());
            objectState.addAttributeState("Comment", sourceData.getComment(), targetData.getComment());
            objectState.addAttributeState("DefaultDuration", sourceData.getDefaultDuration(), targetData.getDefaultDuration());
            objectState.addAttributeState("FrequentTravellerDuration", sourceData.getFrequentTravellerDuration(), targetData.getFrequentTravellerDuration());
            objectState.addAttributeState("LinkDistance", sourceData.getLinkDistance(), targetData.getLinkDistance());
            objectState.addAttributeState("LinkType", sourceData.getLinkType(), targetData.getLinkType());
            objectState.addAttributeState("OccasionalTravellerDuration", sourceData.getOccasionalTravellerDuration(), targetData.getOccasionalTravellerDuration());
            objectState.addAttributeState("MobilityRestrictedTravellerDuration", sourceData.getMobilityRestrictedTravellerDuration(), targetData.getMobilityRestrictedTravellerDuration());
            objectState.addAttributeState("LiftAvailability", sourceData.getLiftAvailability(), targetData.getLiftAvailability());
            objectState.addAttributeState("MobilityRestrictedSuitability", sourceData.getMobilityRestrictedSuitability(), targetData.getMobilityRestrictedSuitability());
            objectState.addAttributeState("StairsAvailability", sourceData.getStairsAvailability(), targetData.getStairsAvailability());

            master.addMappingIds(sourceData.getObjectId(), targetData.getObjectId());
         }
         validList = validList && objectState.isIdentical();
         master.addObjectState(objectState);
      }

      // advise for target object not found in source list
      for (Correspondance targetData : targetDataMap.values().toArray(new Correspondance[0]))
      {
         ChouetteObjectState objectState = new ChouetteObjectState("ConnectionLink", null, targetData.getObjectId());
         master.addObjectState(objectState);
         validList = false;
      }
      //p.close();
      return validList;
   }

   private String buildKey(Correspondance data, ExchangeableLineObjectIdMapper mapper)
   {    	    	    	
      String key = data.getDefaultDuration().toString();

      if (data.getLinkDistance() != null)
      {    		
         key += "-" + data.getLinkDistance().toString();
      }
      if (data.getLinkType() != null)
      {    		
         key += "-" + data.getLinkType().toString();
      }
      String startId = data.getStartOfLink();
      String endId = data.getEndOfLink();

      PositionGeographique start = mapper.getStopArea(startId);
      PositionGeographique end = mapper.getStopArea(endId);

      if (start == null) 
      {
         if (end == null)
         {
             return null;
         }
         return key += "-" + end.getName();
      }
      return key += "-" + start.getName() + "-" + end.getName();
   }

   @Override
   public Map<String, ChouetteObjectState> getStateMap()
   {
      // TODO Auto-generated method stub
      return null;
   }
}
