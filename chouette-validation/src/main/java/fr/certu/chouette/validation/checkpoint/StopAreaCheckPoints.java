package fr.certu.chouette.validation.checkpoint;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.extern.log4j.Log4j;

import org.json.JSONObject;

import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.type.ChouetteAreaEnum;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.validation.ICheckPointPlugin;
import fr.certu.chouette.plugin.validation.report.CheckPointReportItem;
import fr.certu.chouette.plugin.validation.report.DetailReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem;
import fr.certu.chouette.plugin.validation.report.ReportLocation;

@Log4j
public class StopAreaCheckPoints extends AbstractValidation<StopArea> implements
ICheckPointPlugin<StopArea>
{

   @Override
   public void check(List<StopArea> beans, JSONObject parameters,
         PhaseReportItem report, Map<String, Object> context)
   {
      if (isEmpty(beans))
         return;
      // init checkPoints : add here all defined check points for this kind of
      // object
      // 3-StopArea-1 : check if all non ITL stopArea has geolocalization
      // 3-StopArea-2 : check distance of stop areas with different name
      // 3-StopArea-3 : check multiple occurrence of a stopArea
      // 3-StopArea-4 : check localization in a region
      // 3-StopArea-5 : check distance with parent
      initCheckPoint(report, STOP_AREA_1, CheckPointReportItem.SEVERITY.ERROR);
      initCheckPoint(report, STOP_AREA_2, CheckPointReportItem.SEVERITY.WARNING);
      initCheckPoint(report, STOP_AREA_3, CheckPointReportItem.SEVERITY.WARNING);
      initCheckPoint(report, STOP_AREA_4, CheckPointReportItem.SEVERITY.WARNING);
      initCheckPoint(report, STOP_AREA_5, CheckPointReportItem.SEVERITY.WARNING);
      prepareCheckPoint(report, STOP_AREA_1);
      prepareCheckPoint(report, STOP_AREA_2);
      prepareCheckPoint(report, STOP_AREA_3);
      prepareCheckPoint(report, STOP_AREA_4);
      prepareCheckPoint(report, STOP_AREA_5);

      boolean test4_1 = (parameters.optInt(CHECK_OBJECT+OBJECT_KEY.stop_area.name(),0) != 0);
      if (test4_1)
      {
         initCheckPoint(report, L4_STOP_AREA_1, CheckPointReportItem.SEVERITY.ERROR);
         prepareCheckPoint(report, L4_STOP_AREA_1);
      }
      boolean test4_2 = parameters.optInt(CHECK_STOP_PARENT,0) == 1;
      if (test4_2)
      {
         initCheckPoint(report, L4_STOP_AREA_2, CheckPointReportItem.SEVERITY.ERROR);
         prepareCheckPoint(report, L4_STOP_AREA_2);
      }

      Polygon enveloppe = getEnveloppe(parameters);

      for (int i = 0; i < beans.size(); i++)
      {
         StopArea stopArea = beans.get(i);
         // no test for ITL
         if (stopArea.getAreaType().equals(ChouetteAreaEnum.ITL))
            continue;
         check3StopArea1(report, stopArea);
         check3StopArea4(report, stopArea, enveloppe);
         check3StopArea5(report, stopArea, parameters);
         // 4-StopArea-1 : check columns constraints
         if (test4_1)
            check4Generic1(report,stopArea,L4_STOP_AREA_1,OBJECT_KEY.stop_area,parameters,context,log );
         // 4-StopArea-2 : check parent
         if (test4_2)
            check4StopArea2(report, stopArea);
         // 4-StopArea-3 : check country code and Cityname coherence
         check4StopArea3(report, stopArea, context);

         for (int j = i + 1; j < beans.size(); j++)
         {
            check3StopArea2(report, i, stopArea, j, beans.get(j), parameters);
            check3StopArea3(report, i, stopArea, j, beans.get(j));
         }

      }
   }



   private void check3StopArea1(PhaseReportItem report, StopArea stopArea)
   {
      // 3-StopArea-1 : check if all non ITL stopArea has geolocalization
      if (!hasCoordinates(stopArea))
      {
         ReportLocation location = new ReportLocation(stopArea);
         Map<String, Object> map = new HashMap<String, Object>();
         map.put("name", stopArea.getName());
         DetailReportItem detail = new DetailReportItem(STOP_AREA_1,
               stopArea.getObjectId(), Report.STATE.ERROR, location, map);
         addValidationError(report, STOP_AREA_1, detail);
      }
   }

   private void check3StopArea2(PhaseReportItem report, int rank,
         StopArea stopArea, int rank2, StopArea stopArea2, JSONObject parameters)
   {
      // 3-StopArea-2 : check distance of stop areas with different name
      if (!hasCoordinates(stopArea))
         return;
      long distanceMin = parameters.optLong(INTER_STOP_AREA_DISTANCE_MIN, 20);
      ChouetteAreaEnum type = stopArea.getAreaType();
      if (type.equals(ChouetteAreaEnum.BoardingPosition)
            || type.equals(ChouetteAreaEnum.Quay))
      {
         if (!stopArea2.getAreaType().equals(type))
            return;
         if (!hasCoordinates(stopArea2))
            return;
         if (stopArea.getName().equals(stopArea2.getName()))
            return;
         double distance = distance(stopArea, stopArea2);
         if (distance < distanceMin)
         {
            ReportLocation location = new ReportLocation(stopArea);

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("name", stopArea.getName());
            map.put("areaId", stopArea2.getObjectId());
            map.put("areaName", stopArea2.getName());
            map.put("distance", Integer.valueOf((int) distance));
            map.put("distanceLimit", Integer.valueOf((int) distanceMin));

            DetailReportItem detail = new DetailReportItem(STOP_AREA_2,
                  stopArea.getObjectId(), Report.STATE.WARNING, location, map);
            addValidationError(report, STOP_AREA_2, detail);
         }

      }

   }

   private void check3StopArea3(PhaseReportItem report, int rank,
         StopArea stopArea, int rank2, StopArea stopArea2)
   {
      // 3-StopArea-3 : check multiple occurrence of a stopArea of same type
      if (!stopArea2.getAreaType().equals(stopArea.getAreaType()))
         return;
      // same name; same code; same address ...
      if (!stopArea.getName().equals(stopArea2.getName()))
         return;
      if (stopArea.getStreetName() != null
            && !stopArea.getStreetName().equals(stopArea2.getStreetName()))
         return;
      if (stopArea.getCountryCode() != null
            && !stopArea.getCountryCode().equals(stopArea2.getCountryCode()))
         return;
      Collection<Line> lines = getLines(stopArea);
      Collection<Line> lines2 = getLines(stopArea2);
      if (lines.containsAll(lines2) && lines2.containsAll(lines))
      {
         ReportLocation location = new ReportLocation(stopArea);

         Map<String, Object> map = new HashMap<String, Object>();
         map.put("name", stopArea.getName());
         map.put("areaId", stopArea2.getObjectId());

         DetailReportItem detail = new DetailReportItem(STOP_AREA_3,
               stopArea.getObjectId(), Report.STATE.WARNING, location, map);
         addValidationError(report, STOP_AREA_3, detail);
      }

   }

   private void check3StopArea4(PhaseReportItem report, StopArea stopArea,
         Polygon enveloppe)
   {
      // 3-StopArea-4 : check localization in a region
      if (!hasCoordinates(stopArea))
         return;
      Point p = buildPoint(stopArea);
      if (!enveloppe.contains(p))
      {
         ReportLocation location = new ReportLocation(stopArea);

         Map<String, Object> map = new HashMap<String, Object>();
         map.put("name", stopArea.getName());

         DetailReportItem detail = new DetailReportItem(STOP_AREA_4,
               stopArea.getObjectId(), Report.STATE.WARNING, location, map);
         addValidationError(report, STOP_AREA_4, detail);
      }

   }

   private void check3StopArea5(PhaseReportItem report, StopArea stopArea,
         JSONObject parameters)
   {
      // 3-StopArea-5 : check distance with parents
      if (!hasCoordinates(stopArea))
         return;
      long distanceMax = parameters.optLong(PARENT_STOP_AREA_DISTANCE_MAX, 300);
      StopArea stopArea2 = stopArea.getParent();
      if (stopArea2 == null)
         return; // no parent
      if (!hasCoordinates(stopArea2))
         return;
      double distance = distance(stopArea, stopArea2);
      if (distance > distanceMax)
      {
         ReportLocation location = new ReportLocation(stopArea);

         Map<String, Object> map = new HashMap<String, Object>();
         map.put("name", stopArea.getName());
         map.put("parentId", stopArea2.getObjectId());
         map.put("parentName", stopArea2.getName());
         map.put("distance", Integer.valueOf((int) distance));
         map.put("distanceLimit", Integer.valueOf((int) distanceMax));

         DetailReportItem detail = new DetailReportItem(STOP_AREA_5,
               stopArea.getObjectId(), Report.STATE.WARNING, location, map);
         addValidationError(report, STOP_AREA_5, detail);
      }
   }

   private void check4StopArea2(PhaseReportItem report, StopArea stopArea)
   {
      // 4-StopArea-2 : check if all physical stopArea has parent
      if (stopArea.getAreaType().equals(ChouetteAreaEnum.BoardingPosition) || stopArea.getAreaType().equals(ChouetteAreaEnum.Quay))
      {
         if (stopArea.getParent() == null)
         {
            ReportLocation location = new ReportLocation(stopArea);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("name", stopArea.getName());
            DetailReportItem detail = new DetailReportItem(L4_STOP_AREA_2,
                  stopArea.getObjectId(), Report.STATE.ERROR, location, map);
            addValidationError(report, L4_STOP_AREA_2, detail);
         }
      }
   }

   @SuppressWarnings("unchecked")
   private void check4StopArea3(PhaseReportItem report, StopArea stopArea, Map<String, Object> context)
   {
      // 4-StopArea-3 : check if city name and code are unique in area set
      if (!isEmpty(stopArea.getCityName()) && !isEmpty(stopArea.getCountryCode()) && stopArea.getCountryCode().length() == 5)
      {
         Map<String,Object> check4StopArea3Context = (Map<String, Object>) context.get("test4StopArea3");
         if (check4StopArea3Context == null) 
         {
            // first call with data to be checked, initialize also checkpoint container
            initCheckPoint(report, L4_STOP_AREA_3, CheckPointReportItem.SEVERITY.WARNING);
            prepareCheckPoint(report, L4_STOP_AREA_3);

            check4StopArea3Context = new HashMap<>();
            context.put("test4StopArea3",check4StopArea3Context);
         }
         String departmentCode = stopArea.getCountryCode().substring(0, 2);
         String cityKey = departmentCode+"_"+stopArea.getCityName();
         Map<String, City> countryCodeMap = (Map<String, City>) check4StopArea3Context.get("countryCodeMap");
         if (countryCodeMap == null) 
         {
            countryCodeMap = new HashMap<>();
            check4StopArea3Context.put("countryCodeMap",countryCodeMap);
         }
         Map<String, City> cityMap = (Map<String, City>) check4StopArea3Context.get("cityMap");
         if (cityMap == null) 
         {
            cityMap = new HashMap<>();
            check4StopArea3Context.put("cityMap",cityMap);
         }
         City localCity = new City();
         localCity.cityName = stopArea.getCityName();
         localCity.countryCode = stopArea.getCountryCode();
         localCity.stopOwner = stopArea;
         City cityByCountryCode = countryCodeMap.get(stopArea.getCountryCode());
         if (cityByCountryCode == null)
         {
            countryCodeMap.put(stopArea.getCountryCode(), localCity);
         }
         else
         {
            if (!cityByCountryCode.cityName.equals(localCity.cityName))
            {
               // error conflict between city names for same code
               ReportLocation location = new ReportLocation(stopArea);
               Map<String, Object> map = new HashMap<String, Object>();
               map.put("name", stopArea.getName());
               map.put("countryCode", stopArea.getCountryCode());
               map.put("cityName", stopArea.getCityName());
               map.put("alternateCityName",cityByCountryCode.cityName);
               map.put("alternateStopareaName",cityByCountryCode.stopOwner.getName());
               map.put("alternateStopareaId",cityByCountryCode.stopOwner.getObjectId());
               DetailReportItem detail = new DetailReportItem(L4_STOP_AREA_3+"_1",
                     stopArea.getObjectId(), Report.STATE.WARNING, location, map);
               addValidationError(report, L4_STOP_AREA_3, detail);
            }
         }
         City cityByName = cityMap.get(cityKey);
         if (cityByName == null)
         {
            cityMap.put(cityKey, localCity);
         }
         else
         {
            if (!cityByName.countryCode.equals(localCity.countryCode))
            {
               // error conflict between city codes for same name in same department
               ReportLocation location = new ReportLocation(stopArea);
               Map<String, Object> map = new HashMap<String, Object>();
               map.put("name", stopArea.getName());
               map.put("cityName", stopArea.getCityName());
               map.put("countryCode", stopArea.getCountryCode());
               map.put("alternateCountryCode",cityByName.countryCode);
               map.put("alternateStopareaName",cityByName.stopOwner.getName());
               map.put("alternateStopareaId",cityByName.stopOwner.getObjectId());
               DetailReportItem detail = new DetailReportItem(L4_STOP_AREA_3+"_2",
                     stopArea.getObjectId(), Report.STATE.WARNING, location, map);
               addValidationError(report, L4_STOP_AREA_3, detail);
            }
         }
      }

   }



   private Collection<Line> getLines(StopArea area)
   {
      Set<Line> lines = new HashSet<Line>();
      if (area.getAreaType().equals(ChouetteAreaEnum.BoardingPosition)
            || area.getAreaType().equals(ChouetteAreaEnum.Quay))
      {
         for (StopPoint point : area.getContainedStopPoints())
         {
            lines.add(point.getRoute().getLine());
         }
      } else
      {
         for (StopArea child : area.getContainedStopAreas())
         {
            lines.addAll(getLines(child));
         }
      }
      return lines;
   }

   private class City 
   {
      private String countryCode;
      private String cityName;
      private StopArea stopOwner;
   }

}
