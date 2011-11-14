package fr.certu.chouette.exchange.csv.exporter.producer;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import fr.certu.chouette.model.neptune.AreaCentroid;
import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.neptune.VehicleJourneyAtStop;
import fr.certu.chouette.model.neptune.type.Address;
import fr.certu.chouette.model.neptune.type.ChouetteAreaEnum;
import fr.certu.chouette.model.neptune.type.ProjectedPoint;

public class LineProducer extends AbstractCSVNeptuneProducer<Line>
{

   public static final String    LINE_NAME_TITLE               = "Nom de la ligne";
   
   private static final SimpleDateFormat  dfHM = new SimpleDateFormat("HH:mm") ;
   private static final SimpleDateFormat dfHMS = new SimpleDateFormat("HH:mm:ss") ;

   private static final String   PUBLISHED_LINE_NAME_TITLE     = "Nom public";
   private static final String   NUMBER_TITLE                  = "Numero de la ligne";
   private static final String   COMMENT_TITLE                 = "Commentaire de la ligne";
   private static final String   TRANSPORT_MODE_NAME_TITLE     = "Mode de Transport (BUS,METRO,RER,TRAIN ou TRAMWAY)";

   private static final String   DIRECTION_TITLE               = "Direction (ALLER/RETOUR)";
   private static final String   TIMETABLE_TITLE               = "Calendriers d'application";
   private static final String   SPECIFIC_TITLE                = "Particularités";

   private static final String[] BOARDING_POSITION_LINE_TITLES = { "X", "Y", "Latitude", "Longitude", "Adresse",
         "Code Postal", "Zone", "Liste des arrêts"            };

   private static final int      X_COLUMN                      = 0;
   private static final int      Y_COLUMN                      = 1;
   private static final int      LATITUDE_COLUMN               = 2;
   private static final int      LONGITUDE_COLUMN              = 3;
   private static final int      ADDRESS_COLUMN                = 4;
   private static final int      ZIPCODE_COLUMN                = 5;
   private static final int      AREAZONE_COLUMN               = 6;
   private static final int      STOPNAME_COLUMN               = 7;


   @Override
   public List<String[]> produce(Line line)
   {
      List<String[]> csvLinesList = new ArrayList<String[]>();
      csvLinesList.add(createCSVLine(LINE_NAME_TITLE, line.getName()));
      csvLinesList.add(createCSVLine(PUBLISHED_LINE_NAME_TITLE, line.getPublishedName()));
      csvLinesList.add(createCSVLine(NUMBER_TITLE, line.getNumber()));
      csvLinesList.add(createCSVLine(COMMENT_TITLE, line.getComment()));
      csvLinesList.add(createCSVLine(TRANSPORT_MODE_NAME_TITLE, line.getTransportModeName().toString().toUpperCase()));

      // make a copy of line routes for sorting
      List<Route> routes = new ArrayList<Route>(line.getRoutes());
      if (routes.size() > 2)
      {
         // TODO report problem
         return null;
      }
      // sort routes (A before R)
      Collections.sort(routes, new WaybackRouteComparator());

      int vehicleJourneysCount = routes.size();//add one dummy vehicle journey for each route
      for (Route route : routes)
      {
         List<JourneyPattern> journeyPatterns = route.getJourneyPatterns();
         for (JourneyPattern journeyPattern : journeyPatterns)
         {
            vehicleJourneysCount += journeyPattern.getVehicleJourneys().size();
         }
      }

      String[] vehicleJourneyDirectionCSVLine = new String[TITLE_COLUMN + 1 + vehicleJourneysCount];
      vehicleJourneyDirectionCSVLine[TITLE_COLUMN] = DIRECTION_TITLE;
      csvLinesList.add(vehicleJourneyDirectionCSVLine);

      String[] vehicleJourneyTimetableCSVLine = new String[TITLE_COLUMN + 1 + vehicleJourneysCount];
      vehicleJourneyTimetableCSVLine[TITLE_COLUMN] = TIMETABLE_TITLE;
      csvLinesList.add(vehicleJourneyTimetableCSVLine);

      String[] vehicleJourneySpecificCSVLine = new String[TITLE_COLUMN + 1 + vehicleJourneysCount];
      vehicleJourneySpecificCSVLine[TITLE_COLUMN] = SPECIFIC_TITLE;
      csvLinesList.add(vehicleJourneySpecificCSVLine);

      csvLinesList.add(BOARDING_POSITION_LINE_TITLES);

      HashMap<StopPoint, String[]> csvLinesByStopPoint = new HashMap<StopPoint, String[]>();
      int vehicleJourneyColumn = TITLE_COLUMN + 1; // must not be reseted for second route ! 
      for (Route route : routes)
      {
         for (StopPoint stopPointOnRoute : route.getStopPoints())
         {
            StopArea boardingPosition = stopPointOnRoute.getContainedInStopArea();
            String[] csvLine = createBoardingPositionCsvLine(boardingPosition, vehicleJourneysCount);
            csvLine[vehicleJourneyColumn] = "00:00";
            csvLinesByStopPoint.put(stopPointOnRoute, csvLine);
            csvLinesList.add(csvLine);
         }
         List<VehicleJourney> vehicleJourneys = new ArrayList<VehicleJourney>();
         for (JourneyPattern journeyPattern : route.getJourneyPatterns())
         {
            vehicleJourneys.addAll(journeyPattern.getVehicleJourneys());
         }

         //dummy vehicleJourney global informations are filled with the ones of the first vehicleJourney of the route
         VehicleJourney dummyVJ = vehicleJourneys.get(0);
         vehicleJourneyDirectionCSVLine[vehicleJourneyColumn] = ("R".equals(dummyVJ.getRoute().getWayBack()) ? "RETOUR"
                 : "ALLER");
         List<Timetable> dummyVJTimetables = dummyVJ.getTimetables();
         if (dummyVJTimetables != null && dummyVJTimetables.size() > 0){
            vehicleJourneyTimetableCSVLine[vehicleJourneyColumn] = dummyVJTimetables.get(0).getComment();
         }
         else{
            // TODO add report item
         }
         vehicleJourneySpecificCSVLine[vehicleJourneyColumn] = dummyVJ.getVehicleTypeIdentifier();
         
         vehicleJourneyColumn++;
         
         for (VehicleJourney vehicleJourney : vehicleJourneys)
         {
            vehicleJourneyDirectionCSVLine[vehicleJourneyColumn] = ("R".equals(vehicleJourney.getRoute().getWayBack()) ? "RETOUR"
                  : "ALLER");
            List<Timetable> timetables = vehicleJourney.getTimetables();
            if (timetables != null && timetables.size() > 0)
            {
               vehicleJourneyTimetableCSVLine[vehicleJourneyColumn] = timetables.get(0).getComment();
            }
            else
            {
               // TODO add report item
            }
            vehicleJourneySpecificCSVLine[vehicleJourneyColumn] = vehicleJourney.getVehicleTypeIdentifier();

            for (VehicleJourneyAtStop vehicleJourneyAtStop : vehicleJourney.getVehicleJourneyAtStops())
            {
               csvLinesByStopPoint.get(vehicleJourneyAtStop.getStopPoint())[vehicleJourneyColumn] = convertTimeToString(vehicleJourneyAtStop
                     .getDepartureTime());
            }
            vehicleJourneyColumn++;
         }
      }

      return csvLinesList;
   }

   private String[] createBoardingPositionCsvLine(StopArea boardingPosition, int vehicleJourneysCount)
   {
      String[] csvLine = new String[TITLE_COLUMN + 1 + vehicleJourneysCount];
      AreaCentroid areaCentroid = boardingPosition.getAreaCentroid();
      if (areaCentroid != null)
      {
         ProjectedPoint projectedPoint = areaCentroid.getProjectedPoint();
         if (projectedPoint != null)
         {
            csvLine[X_COLUMN] = asString(projectedPoint.getX());
            csvLine[Y_COLUMN] = asString(projectedPoint.getY());
         }
         csvLine[LATITUDE_COLUMN] = asString(areaCentroid.getLatitude());
         csvLine[LONGITUDE_COLUMN] = asString(areaCentroid.getLongitude());
         Address address = areaCentroid.getAddress();
         if (address != null)
         {
            csvLine[ADDRESS_COLUMN] = address.getStreetName();
            csvLine[ZIPCODE_COLUMN] = address.getCountryCode();
         }
      }
      StopArea parent = getParentStopArea(boardingPosition.getParents());
      if (parent != null)
      {
         csvLine[AREAZONE_COLUMN] = getParentStopArea(boardingPosition.getParents()).getName();
      }
      csvLine[STOPNAME_COLUMN] = boardingPosition.getName();

      return csvLine;
   }

   private StopArea getParentStopArea(List<StopArea> parents)
   {
      for (StopArea parent : parents)
      {
         if (!parent.getAreaType().equals(ChouetteAreaEnum.ITL))
         {
            return parent;
         }
      }
      return null;
   }

   public String convertTimeToString(Time time)
   {
      long h = time.getTime()/1000;
      long s = h%60;
      if (s > 0)
      {
         return dfHMS.format(time);
      }
      else
      {
         return dfHM.format(time);
      }
   }

   private class WaybackRouteComparator implements Comparator<Route>
   {

      @Override
      public int compare(Route o1, Route o2)
      {
         return o1.getWayBack().compareTo(o2.getWayBack());
      }

   }
}
