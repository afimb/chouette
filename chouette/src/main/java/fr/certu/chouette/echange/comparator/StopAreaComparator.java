package fr.certu.chouette.echange.comparator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.service.commun.CodeIncident;
import fr.certu.chouette.service.commun.ServiceException;

/**
 * @author michel
 *
 */
public class StopAreaComparator extends AbstractChouetteDataComparator 
{

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

        sourceDataList = source.getZonesPlaces();
        targetDataList = target.getZonesPlaces();

        boolean validList2 = compare("StopPlace", sourceDataList, targetDataList);

        sourceDataList = source.getZonesCommerciales();
        targetDataList = target.getZonesCommerciales();

        boolean validList3 = compare("Commercial", sourceDataList, targetDataList);

        return validList1 && validList2 && validList3;

    }

    private void populateChildrenMap(ILectureEchange data, Map<String, List<String>> childsOfStopArea) 
    {
        //    	PrintWriter p = null;
        //        try
        //        {
        //            p = new PrintWriter(new File("/home/evelyne/Tmp/chouette-debug-traces.log"));
        //        }
        //        catch (FileNotFoundException e)
        //        {
        //            // TODO Auto-generated catch block
        //            e.printStackTrace();
        //        }

        Map<String, String> reverseMap = data.getZoneParenteParObjectId();
        //        p.println("Reverse Map  : " + data.getZoneParenteParObjectId());
        for (String key : reverseMap.keySet().toArray(new String[0])) {
            String value = reverseMap.get(key);
            List<String> children = childsOfStopArea.get(value);
            if (children == null) {
                children = new ArrayList<String>();
                childsOfStopArea.put(value, children);
            }
            children.add(key);
        }
        //		p.println("childsOfStopArea : " + childsOfStopArea);
        for (Object childrenObj : childsOfStopArea.values().toArray()) {
            List<String> children = (List<String>) childrenObj;
            sortList(children);
        }
        //		p.close();
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
        for (PositionGeographique targetData : targetDataList) 
        {
            if (targetDataMap.put(buildKey(targetData), targetData) != null)
            {
                // duplicate key : cannot check correctly this data
                throw new ComparatorException(ComparatorException.TYPE.DuplicateKey,"Name+Lat+Long in target StopArea "+type);
            }
        }

        // check all the elements of the source list, 
        // if found in the target map, remove it from the target map
        // at the end of processing, the target map contains only unknown objects in the source list
        for (PositionGeographique sourceData : sourceDataList) 
        {
            PositionGeographique targetData = targetDataMap.remove(buildKey(sourceData));
            ChouetteObjectState objectState = null;
            if (targetData == null)
            {
                objectState= new ChouetteObjectState(getMappingKey(), sourceData.getObjectId(),null);       
            }
            else
            {
                objectState = new ChouetteObjectState(getMappingKey(), sourceData.getObjectId(), targetData.getObjectId());


                List<String> sourceChildren = sourceChildsOfStopArea.get(sourceData.getObjectId());
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
            ChouetteObjectState objectState = new ChouetteObjectState(getMappingKey(),null,targetData.getObjectId());
            master.addObjectState(objectState);
            validList = false;
        }

        return validList;
    }

    private String buildKey(PositionGeographique data)
    {
        return data.getName()+"-"+data.getLatitude()+"-"+data.getLongitude();
    }

    @Override
    public Map<String, ChouetteObjectState> getStateMap()
    {
        // TODO Auto-generated method stub
        return null;
    }

}