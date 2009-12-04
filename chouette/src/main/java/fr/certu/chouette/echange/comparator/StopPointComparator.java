package fr.certu.chouette.echange.comparator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.modele.ArretItineraire;
import fr.certu.chouette.modele.Itineraire;
import fr.certu.chouette.modele.PositionGeographique;

/**
 * @author michel
 *
 */
public class StopPointComparator extends AbstractChouetteDataComparator 
{

   public boolean compareData(IExchangeableLineComparator master) throws Exception
   {
      this.master = master;
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
         String key = buildKey(targetData, targetMapper);
         ArretItineraire oldTargetData = targetDataMap.put(key, targetData);
         if (oldTargetData != null)
         {
            throw new ComparatorException(ComparatorException.TYPE.DuplicateKey, "target key = " + key + " ids = " + targetData.getObjectId() + "," + oldTargetData.getObjectId());
         }
      }


      // check all the elements of the source list, 
      // if found in the target map, remove it from the target map
      // at the end of processing, the target map contains only unknown objects in the source list
      for (ArretItineraire sourceData : sourceDataList) 
      {
         String parentId = sourceData.getContainedIn();
         PositionGeographique sourceParent = sourceMapper.getStopArea(parentId);		    
         ArretItineraire targetData = targetDataMap.remove(buildKey(sourceData, sourceMapper));
         ChouetteObjectState objectState = null;
         if (targetData == null)
         {
            //throw new ServiceException(CodeIncident.COMPARATOR_UNVAILABLE_RESOURCE,"No target stoppoint");
            objectState= new ChouetteObjectState(getMappingKey(), sourceData.getObjectId(), null);		    
         }
         else
         {
            String targetParentId = targetData.getContainedIn();		        
            PositionGeographique targetParent = targetMapper.getStopArea(targetParentId);

            objectState = new ChouetteObjectState(getMappingKey(),sourceData.getObjectId(),targetData.getObjectId());
            objectState.addAttributeState("Name", sourceData.getName(), targetData.getName());

            objectState.addAttributeState("ParentName", sourceParent.getName(), targetParent.getName());
            objectState.addAttributeState("ParentLatitude", sourceParent.getLatitude(), targetParent.getLatitude());
            objectState.addAttributeState("ParentLongitude", sourceParent.getLongitude(), targetParent.getLongitude());
            objectState.addAttributeState("Position", sourceData.getPosition(), targetData.getPosition());

            master.addMappingIds(sourceData.getObjectId(), targetData.getObjectId());
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

   private String buildKey(ArretItineraire data, ExchangeableLineObjectIdMapper mapper)
   {
      String parentId = data.getContainedIn();
      PositionGeographique parent = mapper.getStopArea(parentId);
      Itineraire route = mapper.getRouteOfStopPoint(data.getObjectId());
      String dir = "U";
      if (route != null)
      {
         List<ArretItineraire> arrets = mapper.getStopPointsOfRoute(route.getObjectId());
         if (arrets != null)
         {
            PositionGeographique start = mapper.getStopArea(arrets.get(0).getContainedIn());
            PositionGeographique end = mapper.getStopArea(arrets.get(arrets.size() - 1).getContainedIn());
            if ((start != null) && (end != null))
               dir = start.getName() + "-" + end.getName();
         }
      }
      return data.getPosition() + "-" + parent.getName() + "-" + parent.getLatitude() + "-" + parent.getLongitude() + "-" + dir;
   }

   @Override
   public Map<String, ChouetteObjectState> getStateMap()
   {
      // TODO Auto-generated method stub
      return null;
   }
}
