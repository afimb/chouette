package fr.certu.chouette.echange.comparator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.service.commun.ServiceException;

/**
 * @author michel
 *
 */
public class StopAreaComparator extends AbstractChouetteDataComparator 
{
   private static final Log logger = LogFactory.getLog(StopAreaComparator.class);

   Map<String,List<String>> sourceChildsOfStopArea;
   Map<String,List<String>> targetChildsOfStopArea;


   public boolean compareData(IExchangeableLineComparator master) throws Exception
   {
      this.master = master;
      ILectureEchange source = master.getSource();
      ILectureEchange target = master.getTarget();

      sourceChildsOfStopArea = new HashMap<String, List<String>>();
      targetChildsOfStopArea = new HashMap<String, List<String>>();

      populateChildrenMap(source, sourceChildsOfStopArea);
      populateChildrenMap(target, targetChildsOfStopArea);

      List<PositionGeographique> sourceDataList = source.getArretsPhysiques();
      List<PositionGeographique> targetDataList = target.getArretsPhysiques();

      boolean validList1 = compare("BoardingPosition", sourceDataList, targetDataList);

      populateChildrenMap(source, sourceChildsOfStopArea);
      populateChildrenMap(target, targetChildsOfStopArea);

      sourceDataList = source.getZonesPlaces();
      targetDataList = target.getZonesPlaces();

      boolean validList2 = compare("StopPlace", sourceDataList, targetDataList);

      populateChildrenMap(source, sourceChildsOfStopArea);
      populateChildrenMap(target, targetChildsOfStopArea);

      sourceDataList = source.getZonesCommerciales();
      targetDataList = target.getZonesCommerciales();

      boolean validList3 = compare("Commercial", sourceDataList, targetDataList);

      return validList1 && validList2 && validList3;

   }

   private void populateChildrenMap(ILectureEchange data, Map<String, List<String>> childsOfStopArea) 
   {

      childsOfStopArea.clear();
      Map<String, String> reverseMap = data.getZoneParenteParObjectId();

      for (String key : reverseMap.keySet().toArray(new String[0])) 
      {
         String value = reverseMap.get(key);
         // exclude non mapped children (stoparea may refer stoppoints not in file)
         String sourceID = master.getSourceId(key);
         String targetID = master.getTargetId(key);
         if (sourceID != null || targetID != null)
         {
            List<String> children = childsOfStopArea.get(value);
            if (children == null) 
            {
               children = new ArrayList<String>();
               childsOfStopArea.put(value, children);
            }
            children.add(key);
         }
      }

      for (Object childrenObj : childsOfStopArea.values().toArray()) {
         List<String> children = (List<String>) childrenObj;
         sortList(children);
      }

   }

   private List<String> sortList(List<String> list)
   {
      String[] childrenArray = list.toArray(new String[0]);
      Arrays.sort(childrenArray);
      list.clear();
      list.addAll(Arrays.asList(childrenArray));
      return list;
   }

   private boolean compare(String type,List<PositionGeographique> sourceDataList,List<PositionGeographique> targetDataList) throws ServiceException
   {
      boolean validList = true;
      Map<String,PositionGeographique> targetDataMap = new HashMap<String, PositionGeographique>();

      // the natural key is the name : checking for unique key
      int rank=0;
      for (PositionGeographique targetData : targetDataList) 
      {
         List<String> targetChildren = targetChildsOfStopArea.get(targetData.getObjectId());
         String key = buildKey(type,rank++,targetData,targetChildren,true);
         PositionGeographique oldTarget = targetDataMap.put(key, targetData);
         if (oldTarget != null)
         {
            // duplicate key : cannot check correctly this data
            logger.error("DuplicateKey " + key+" in target StopArea "+type+" "+targetData.getObjectId()+" and "+oldTarget.getObjectId());
            throw new ComparatorException(ComparatorException.TYPE.DuplicateKey,key+" in target StopArea "+type);
         }
      }

      // check all the elements of the source list, 
      // if found in the target map, remove it from the target map
      // at the end of processing, the target map contains only unknown objects in the source list
      rank=0;
      for (PositionGeographique sourceData : sourceDataList) 
      {
         List<String> sourceChildren = sourceChildsOfStopArea.get(sourceData.getObjectId());
         String key = buildKey(type,rank++,sourceData,sourceChildren,false);
         PositionGeographique targetData = targetDataMap.remove(key);
         ChouetteObjectState objectState = null;
         if (targetData == null)
         {
            objectState= new ChouetteObjectState(getMappingKey(), sourceData.getObjectId(),null);      
            logger.debug("no target "+type+" for key = "+key+" source id = "+sourceData.getObjectId());
         }
         else
         {
            objectState = new ChouetteObjectState(getMappingKey(), sourceData.getObjectId(), targetData.getObjectId());


            List<String> targetChildren = targetChildsOfStopArea.get(targetData.getObjectId());

            //No children if stoppoint is a boarding position 
            if (sourceChildren != null && targetChildren == null)
            {
               // first check the size of the lists and if matches, check the content
               if (objectState.addAttributeState("children count", sourceChildren, targetChildren))
               {
                  // check equivalence of childrenList
                  List<String> targetMappingChildren = sortList(convertToSourceId(targetChildren));
                  String sourceList = Arrays.toString(sourceChildren.toArray(new String[0]));
                  String targetList = Arrays.toString(targetMappingChildren.toArray(new String[0]));
                  objectState.addAttributeState("children list", sourceList, targetList);
               }
            }
            objectState.addAttributeState("Comment", sourceData.getComment(), targetData.getComment());
            objectState.addAttributeState("Latitude", sourceData.getLatitude(), targetData.getLatitude());
            objectState.addAttributeState("Longitude", sourceData.getLongitude(), targetData.getLongitude());
            objectState.addAttributeState("LongLatType", sourceData.getLongLatType(), targetData.getLongLatType());
            objectState.addAttributeState("StreetName", sourceData.getStreetName(), targetData.getStreetName());
            objectState.addAttributeState("CountryCode", sourceData.getCountryCode(), targetData.getCountryCode());
            objectState.addAttributeState("X", sourceData.getX(), targetData.getX());
            objectState.addAttributeState("Y", sourceData.getY(), targetData.getY());
            objectState.addAttributeState("ProjectionType", sourceData.getProjectionType(), targetData.getProjectionType());
            objectState.addAttributeState("FareCode", sourceData.getFareCode(), targetData.getFareCode());
            objectState.addAttributeState("FullName", sourceData.getFullName(), targetData.getFullName());
            objectState.addAttributeState("RegistrationNumber", sourceData.getRegistrationNumber(), targetData.getRegistrationNumber());

            master.addMappingIds(sourceData.getObjectId(), targetData.getObjectId());
         }
         validList = validList && objectState.isIdentical();
         master.addObjectState(objectState);
      }

      // advise for target object not found in source list
      for (PositionGeographique targetData : targetDataMap.values().toArray(new PositionGeographique[0]))
      {

         List<String> targetChildren = targetChildsOfStopArea.get(targetData.getObjectId());
         String key = buildKey(type,rank++,targetData,targetChildren,true);
         logger.debug("no source "+type+" for key = "+key+" target id = "+targetData.getObjectId());

         ChouetteObjectState objectState = new ChouetteObjectState(getMappingKey(),null,targetData.getObjectId());
         master.addObjectState(objectState);
         validList = false;
      }

      return validList;
   }

   private String buildKey(String type,int rank,PositionGeographique data,List<String> children,boolean withConversion)
   {
      if (type.equals("BoardingPosition"))
         return ""+rank+"-"+data.getName()+"-"+data.getLatitude()+"-"+data.getLongitude();
      List<String> dataChildren =  children;
      if (withConversion) 
      {
         dataChildren = convertToSourceId(children);
         sortList(dataChildren);
      }

      return data.getName()+"-"+Arrays.toString(dataChildren.toArray());
   }

   @Override
   public Map<String, ChouetteObjectState> getStateMap()
   {
      // TODO Auto-generated method stub
      return null;
   }

}