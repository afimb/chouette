package fr.certu.chouette.echange.comparator;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.modele.Periode;
import fr.certu.chouette.modele.TableauMarche;

/**
 * @author Dryade, Evelyne Zahn
 * 
 */
public class TimetableComparator extends AbstractChouetteDataComparator 
{
   private static final Log logger = LogFactory.getLog(TimetableComparator.class);

   public boolean compareData(IExchangeableLineComparator master) throws ComparatorException{

      boolean sameTimetables = true;

      this.master = master;

      ILectureEchange source = master.getSource();
      ILectureEchange target = master.getTarget();

      List<TableauMarche> sourceDataList = source.getTableauxMarche();
      List<TableauMarche> targetDataList = target.getTableauxMarche();

      HashMap<String, String> sourceObjectIdBytargetNaturalKey = new HashMap<String, String>();
      // natural key ? dayTypes, periods, calendar days
      Integer dupplicates = 1;
      for (TableauMarche sourceTM : sourceDataList) 
      {
         int dayTypeMask = sourceTM.getIntDayTypes();
         List<Periode> TMPeriods = sourceTM.getPeriodes();
         List<Date> TMDates = sourceTM.getDates();
         String key = buildNaturalKey(sourceTM,dayTypeMask, TMPeriods, TMDates);
         key = key + sourceTM.getVehicleJourneyIdCount();
         // sourceObjectIdBytargetNaturalKey.put(key, sourceTM.getObjectId());
         if (sourceObjectIdBytargetNaturalKey.containsKey(key))
         {
            Integer i = 1;
            String dupKey = key ;
            while (sourceObjectIdBytargetNaturalKey.containsKey(dupKey))
            {
               dupKey = key + "-"+i.toString(); 
               i++;
            }
            if (dupplicates < i) dupplicates = i;
            key = dupKey;
         }
         sourceObjectIdBytargetNaturalKey.put(key, sourceTM.getObjectId());
      }

      for (TableauMarche targetTM : targetDataList) 
      {
         int dayTypeMask = targetTM.getIntDayTypes();
         List<Periode> TMPeriods = targetTM.getPeriodes();
         List<Date> TMDates = targetTM.getDates();
         String key = buildNaturalKey(targetTM,dayTypeMask, TMPeriods, TMDates);
         key = key + targetTM.getVehicleJourneyIdCount();
         String sourceTMId = sourceObjectIdBytargetNaturalKey.remove(key);
         ChouetteObjectState objectState = null;
         Integer i = 1;
         while (sourceTMId == null && i <= dupplicates)
         {
            String dupKey = key + "-"+i.toString(); 
            sourceTMId = sourceObjectIdBytargetNaturalKey.remove(dupKey);
            i++;
         }
            
         if (sourceTMId == null)
         {
            objectState = new ChouetteObjectState(getMappingKey(), null, targetTM.getObjectId());
            sameTimetables = false;
         }
         else 
         {
            objectState = new ChouetteObjectState(getMappingKey(), sourceTMId, targetTM.getObjectId());
            master.addMappingIds(sourceTMId, targetTM.getObjectId());
         }
         master.addObjectState(objectState);
      }

      // Unmapped source vehicle journeys
      Collection<String> unmappedSourceObjects = sourceObjectIdBytargetNaturalKey.values();
      if (unmappedSourceObjects.size() != 0) 
      {
         sameTimetables = false;
         for (String sourceObjectId : unmappedSourceObjects) 
         {
            ChouetteObjectState objectState = new ChouetteObjectState(getMappingKey(), sourceObjectId, null);
            master.addObjectState(objectState);
         }
      }
      return sameTimetables;
   }


   private String buildNaturalKey(TableauMarche timetable, int dayTypesMask, List<Periode> TMPeriods,List<Date> TMDates) 
   {
      String key = ""+timetable.getComment();
      key += "-" +((Integer) dayTypesMask).toString();
      SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
      Periode[] periods = (Periode[])TMPeriods.toArray(new Periode[0]);
      Comparator<Periode> comparor = new PeriodeComparator();
      Arrays.sort(periods, comparor);
      for (Periode TMPeriod : TMPeriods) 
      {
         key = key + "-" + format.format(TMPeriod.getDebut());
         key = key + "-" + format.format(TMPeriod.getFin());
      }

      Date[] dates = (Date[])TMDates.toArray(new Date[0]);
      Arrays.sort(dates);

      for (Date TMDate : dates)
      {
         key = key + "-" + format.format(TMDate);
      }

      return key;
   }

   @Override
   public Map<String, ChouetteObjectState> getStateMap()
   {
      // TODO Auto-generated method stub
      return null;
   }

   class PeriodeComparator implements Comparator<Periode>
   {
      public int compare(Periode o1, Periode o2)
      {
         if (o1.debut.equals(o2.debut))
         {
            return o1.fin.compareTo(o2.fin);
         }
         return o1.debut.compareTo(o2.debut);
      }
   }

}
