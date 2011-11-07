/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.exchange.xml.neptune.importer;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;

import fr.certu.chouette.model.neptune.AccessLink;
import fr.certu.chouette.model.neptune.AccessPoint;
import fr.certu.chouette.model.neptune.AreaCentroid;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.model.neptune.ConnectionLink;
import fr.certu.chouette.model.neptune.Facility;
import fr.certu.chouette.model.neptune.GroupOfLine;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.TimeSlot;
import fr.certu.chouette.model.neptune.Timetable;

/**
 *
 */
public class SharedImportedData
{
   private Map<Class<? extends NeptuneIdentifiedObject>,Map<String,? extends NeptuneIdentifiedObject>> mapMap = new HashMap<Class<? extends NeptuneIdentifiedObject>, Map<String,? extends NeptuneIdentifiedObject>>();

   @Getter private Map<String,PTNetwork> networks = new HashMap<String, PTNetwork>();
   @Getter private Map<String,Company> companies = new HashMap<String, Company>();
   @Getter private Map<String,GroupOfLine> groupOfLines = new HashMap<String, GroupOfLine>();
   @Getter private Map<String,StopArea> areas = new HashMap<String, StopArea>();
   @Getter private Map<String,AreaCentroid> centroids = new HashMap<String, AreaCentroid>();
   @Getter private Map<String,ConnectionLink> conectionLinks = new HashMap<String, ConnectionLink>();
   @Getter private Map<String,AccessPoint> accessPoints = new HashMap<String, AccessPoint>();
   @Getter private Map<String,AccessLink> accessLinks = new HashMap<String, AccessLink>();
   @Getter private Map<String,Timetable> timetables = new HashMap<String, Timetable>();
   @Getter private Map<String,TimeSlot> timeSlots = new HashMap<String, TimeSlot>();
   @Getter private Map<String,Facility> facilities = new HashMap<String, Facility>();
   
   public SharedImportedData()
   {
      mapMap.put(PTNetwork.class, networks);
      mapMap.put(Company.class, companies);
      mapMap.put(GroupOfLine.class, groupOfLines);
      mapMap.put(StopArea.class, areas);
      mapMap.put(AreaCentroid.class, centroids);
      mapMap.put(ConnectionLink.class, conectionLinks);
      mapMap.put(AccessPoint.class, accessPoints);
      mapMap.put(AccessLink.class, accessLinks);
      mapMap.put(Timetable.class, timetables);
      mapMap.put(TimeSlot.class, timeSlots);
      mapMap.put(Facility.class, facilities);

   }
   
   @SuppressWarnings("unchecked")
   public <T extends NeptuneIdentifiedObject> void add(T bean)
   {
      Map<String,T> map = (Map<String, T>) mapMap.get(bean.getClass());
      add(bean,map);
   }
      
   private <T extends NeptuneIdentifiedObject> void add(T bean, Map<String,T> map)
   {
      map.put(bean.getObjectId(),bean);
   }
   
   @SuppressWarnings("unchecked")
   public <T extends NeptuneIdentifiedObject> T get(T bean)
   {
      Map<String,T> map = (Map<String, T>) mapMap.get(bean.getClass());
      return get(bean.getObjectId(),map);
   }
   
   private <T extends NeptuneIdentifiedObject> T get(String objectId, Map<String,T> map)
   {
      return map.get(objectId);
   }

}
