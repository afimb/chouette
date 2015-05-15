/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mobi.chouette.model.factory;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import lombok.Getter;
import lombok.Setter;
import mobi.chouette.model.AccessLink;
import mobi.chouette.model.AccessPoint;
import mobi.chouette.model.CalendarDay;
import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.GroupOfLine;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.Period;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.VehicleJourneyAtStop;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.type.DayTypeEnum;

import com.tobedevoured.modelcitizen.CreateModelException;
import com.tobedevoured.modelcitizen.ModelFactory;
import com.tobedevoured.modelcitizen.RegisterBlueprintException;

/**
 * 
 * @author marc
 */
public class ComplexModelFactory
{
   private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger
         .getLogger(ComplexModelFactory.class);
   @Getter
   @Setter
   private ModelFactory modelFactory;

   @Getter
   @Setter
   private List<StopArea> quays;
   @Getter
   @Setter
   private List<StopArea> stopPlaces;
   @Getter
   @Setter
   private List<AccessLink> accessLinks;
   @Getter
   @Setter
   private List<AccessPoint> accessPoints;
   @Getter
   @Setter
   private List<ConnectionLink> connectionLinks;
   @Getter
   @Setter
   int quayCount = 21;
   @Getter
   @Setter
   int routeStopCount = 21;
   @Getter
   @Setter
   int journeyPatternCount = 7;
   @Getter
   @Setter
   int vehicleCount = 2;
   @Getter
   @Setter
   int routeCount = 2;
   @Getter
   @Setter
   int timetablesCount = 5;
   @Getter
   @Setter
   int groupOfLinesCount = 2;
   @Getter
   @Setter
   int routingConstraintCount = 2;
   @Getter
   @Setter
   List<Timetable> timetables;

   public void init()
   {
      modelFactory = new ModelFactory();
      try
      {
         modelFactory
               .setRegisterBlueprintsByPackage("mobi.chouette.model.blueprint");
      } catch (RegisterBlueprintException ex)
      {
         throw new RuntimeException(ex.getMessage(), ex);
      }
      quays = new ArrayList<>(quayCount);
      stopPlaces = new ArrayList<>();
      timetables = new ArrayList<>(timetablesCount);
      connectionLinks = new ArrayList<>();
      accessLinks = new ArrayList<>();
      accessPoints = new ArrayList<>();

      createStopAreas();
      createTimetables();
      createConnectionLinks();
      createAccessLinks();
   }

   private void createStopAreas() throws RuntimeException
   {
      try
      {
         for (int i = 0; i < quayCount; i++)
         {
            StopArea stopAreaCommercial = modelFactory
                  .createModel(StopArea.class);
            stopAreaCommercial.setObjectId("T:StopArea:CSP-" + i);
            stopAreaCommercial
                  .setAreaType(ChouetteAreaEnum.CommercialStopPoint);
            stopAreaCommercial.setFareCode(i % 5);

            if (i % 2 == 0)
            {
               StopArea stopAreaPlace = modelFactory
                     .createModel(StopArea.class);
               stopAreaPlace.setObjectId("T:StopArea:PLC-" + i);
               stopAreaPlace.setAreaType(ChouetteAreaEnum.StopPlace);
               stopPlaces.add(stopAreaPlace);
               stopAreaCommercial.setParent(stopAreaPlace);
            }

            StopArea stopArea = modelFactory.createModel(StopArea.class);
            stopArea.setObjectId("T:StopArea:" + i);
            stopArea.setAreaType(ChouetteAreaEnum.Quay);
            stopArea.setParent(stopAreaCommercial);

            quays.add(stopArea);

            for (StopArea sa : quays)
            {
               if (!sa.getAreaType().equals(ChouetteAreaEnum.Quay))
                  logger.info("error: " + sa.getObjectId());
            }
         }
      } catch (CreateModelException ex)
      {
         throw new RuntimeException(ex.getMessage(), ex);
      }
   }

   private void createConnectionLinks() throws RuntimeException
   {
      try
      {
         if (quays.size() > 2)
         {
            StopArea startOfLink = quays.get(0);
            StopArea endOfLink = quays.get(1);

            ConnectionLink connectionLink = modelFactory
                  .createModel(ConnectionLink.class);
            connectionLinks.add(connectionLink);
            connectionLink.setStartOfLink(startOfLink);
            connectionLink.setEndOfLink(endOfLink);

         }
      } catch (CreateModelException ex)
      {
         throw new RuntimeException(ex.getMessage(), ex);
      }
   }

   private void createAccessLinks() throws RuntimeException
   {
      try
      {
         if (stopPlaces.size() > 1)
         {
        	 for (int i = 0; i < 2; i++)
        	 {
            StopArea stopArea = stopPlaces.get(i);

            AccessLink accessLink = modelFactory.createModel(AccessLink.class);
            accessLinks.add(accessLink);
            AccessPoint accessPoint = modelFactory
                  .createModel(AccessPoint.class);
            accessPoints.add(accessPoint);
            accessLink.setAccessPoint(accessPoint);
            accessPoint.setContainedIn(stopArea);
            accessLink.setStopArea(stopArea);
        	 }

         }
      } catch (CreateModelException ex)
      {
         throw new RuntimeException(ex.getMessage(), ex);
      }
   }

   private void createTimetables()
   {
      for (int i = 0; i < timetablesCount; i++)
      {
         timetables.add(nominalTimetable(i));
      }
   }

   private Timetable nominalTimetable(int index)
   {
      Timetable tm = null;
      try
      {
         tm = new Timetable();
         tm.setObjectId("T:Timetable:" + index);
         tm.setComment("nom " + index);
         tm.setVersion("version " + index);

         tm.addDayType(DayTypeEnum.values()[index % 7]);
         tm.addDayType(DayTypeEnum.values()[(index + 1) % 7]);

         Calendar cal = Calendar.getInstance();
         cal.add(Calendar.MONTH, index);

         tm.setCalendarDays(new ArrayList<CalendarDay>(5));
         for (int i = 0; i < 5; i++)
         {
            tm.addCalendarDay(new CalendarDay(new Date(cal.getTimeInMillis()),
                  true));
            cal.add(Calendar.DAY_OF_MONTH, 1);
         }
         tm.setPeriods(new ArrayList<Period>(5));
         for (int i = 0; i < 5; i++)
         {
            Period period = new Period();
            period.setStartDate(new Date(cal.getTimeInMillis()));
            cal.add(Calendar.DAY_OF_MONTH, 4);
            period.setEndDate(new Date(cal.getTimeInMillis()));

            tm.addPeriod(period);

            cal.add(Calendar.MONTH, 1);
         }
      } catch (Exception ex)
      {
         throw new RuntimeException(ex.getMessage(), ex);
      }
      return tm;
   }

   private List<StopPoint> stopPointList(Route route)
   {
      List<StopPoint> stopPoints = new ArrayList<StopPoint>();
      try
      {
         for (int i = 0; i < routeStopCount; i++)
         {
            StopPoint stopPoint = new StopPoint();
            stopPoint.setContainedInStopArea(quays.get(i % quayCount));
            stopPoint.setObjectId("T:StopPoint:" + route.objectIdSuffix() + "-"
                  + i);
            stopPoint = modelFactory.createModel(stopPoint);
            stopPoint.setRoute(route);

            stopPoints.add(stopPoint);
         }
      } catch (CreateModelException ex)
      {
         Logger.getLogger(ComplexModelFactory.class.getName()).log(
               Level.SEVERE, null, ex);
      }
      return stopPoints;
   }

   private List<JourneyPattern> journeyPatternList(List<StopPoint> stopPoints,
         Route route) throws CreateModelException
   {
      List<JourneyPattern> journeyPatterns = new ArrayList<JourneyPattern>(
            journeyPatternCount);
      try
      {
         for (int i = 0; i < journeyPatternCount; i++)
         {
            JourneyPattern journeyPattern = modelFactory
                  .createModel(JourneyPattern.class);
            journeyPattern.setName(i + " modulo " + journeyPatternCount);
            journeyPattern.setObjectId("T:JourneyPattern:"
                  + route.objectIdSuffix() + "-" + i);
            journeyPattern.setStopPoints(new ArrayList<StopPoint>());
            journeyPattern.setRoute(route);
            journeyPatterns.add(journeyPattern);
         }

         for (int i = 0; i < stopPoints.size(); i++)
         {
            JourneyPattern journeyPattern = null;
            journeyPattern = journeyPatterns.get(i % journeyPatternCount);
            journeyPattern.addStopPoint(stopPoints.get(i));

            if (journeyPattern.getStopPoints() == null)
               throw new RuntimeException("echec");
         }

      } catch (Exception ex)
      {
         Logger.getLogger(ComplexModelFactory.class.getName()).log(
               Level.SEVERE, null, ex);
      }
      for (int i = 0; i < journeyPatternCount; i++)
      {
         JourneyPattern journeyPattern = journeyPatterns.get(i);
         journeyPattern.setVehicleJourneys(vehicleJourneyList(journeyPattern));

         assert journeyPattern.getVehicleJourneys().get(0).getJourneyPattern() != null;
      }

      return journeyPatterns;
   }

   public StopArea createITL(List<Line> lines, List<StopArea> stopAreas)
   {
      try
      {
         StopArea itl = modelFactory.createModel(StopArea.class);
         itl.setAreaType(ChouetteAreaEnum.ITL);
         itl.setRoutingConstraintLines(lines);
         itl.setRoutingConstraintAreas(stopAreas);

         return itl;
      } catch (CreateModelException ex)
      {
         Logger.getLogger(ComplexModelFactory.class.getName()).log(
               Level.SEVERE, null, ex);
         return null;
      }
   }

   private StopArea createLineITL(Line line)
   {
      List<Line> lines = new ArrayList<Line>();
      lines.add(line);
      try
      {
         lines.add(modelFactory.createModel(Line.class));
      } catch (CreateModelException ex)
      {
         Logger.getLogger(ComplexModelFactory.class.getName()).log(
               Level.SEVERE, null, ex);
      }
      List<StopArea> stop_areas = new ArrayList<StopArea>();
      int index = (new Random()).nextInt(line.getRoutes().size());

      stop_areas.add(line.getRoutes().get(index).getStopPoints().get(0)
            .getContainedInStopArea());
      try
      {
         stop_areas.add(modelFactory.createModel(StopArea.class));
      } catch (CreateModelException ex)
      {
         Logger.getLogger(ComplexModelFactory.class.getName()).log(
               Level.SEVERE, null, ex);
      }
      return createITL(lines, stop_areas);
   }

   public Line nominalLine(String lineId)
   {
      Line line = null;
      try
      {
         line = modelFactory.createModel(Line.class);
      } catch (Exception ex)
      {
         throw new RuntimeException(ex.getMessage(), ex);
      }
      line.setObjectId("T:Line:" + lineId);

      line.setRoutes(new ArrayList<Route>(routeCount));
      for (int i = 0; i < routeCount; i++)
      {
         line.getRoutes().add(nominalRoute(i, line));
      }

      line.setRoutingConstraints(new ArrayList<StopArea>());
      for (int i = 0; i < routingConstraintCount; i++)
      {

         line.getRoutingConstraints().add(createLineITL(line));
      }

      line.setGroupOfLines(new ArrayList<GroupOfLine>(groupOfLinesCount));
      for (int i = 0; i < groupOfLinesCount; i++)
      {
         try
         {
            GroupOfLine g = modelFactory.createModel(GroupOfLine.class);
            g.addLine(line);
         } catch (CreateModelException ex)
         {
            throw new RuntimeException(ex.getMessage(), ex);
         }
      }
     
      return line;
   }

   private Route nominalRoute(int index, Line line)
   {
      try
      {
          Route route = new Route();
         route.setObjectId("T:Route:" + line.objectIdSuffix() + "-" + index);

         List<StopPoint> stopPoints = stopPointList(route);
         List<JourneyPattern> journeyPatterns = journeyPatternList(stopPoints,
               route);

         route = modelFactory.createModel(route);
         route.setJourneyPatterns(journeyPatterns);
         route.setStopPoints(stopPoints);

         assert route.getJourneyPatterns().get(0).getVehicleJourneys().get(0)
         .getJourneyPattern() != null;

          return route;
      } catch (Exception ex)
      {
         throw new RuntimeException(ex.getMessage(), ex);
      }
   }

   private List<VehicleJourney> vehicleJourneyList(JourneyPattern journeyPattern)
         throws CreateModelException
   {
      List<VehicleJourney> vehicles = new ArrayList<VehicleJourney>(
            vehicleCount);
      Calendar calendar = Calendar.getInstance();
      calendar.set(Calendar.HOUR_OF_DAY, 13);
      calendar.set(Calendar.MINUTE, 5);
      calendar.set(Calendar.SECOND, 0);

      for (int i = 0; i < vehicleCount; i++)
      {
         VehicleJourney vehicle = vehicleJourney(calendar, journeyPattern);
         vehicle.setObjectId("T:VehicleJourney:"
               + journeyPattern.objectIdSuffix() + "-" + i);
         vehicle.getTimetables().clear();
         
         Timetable tm1 = timetables.get(i % timetablesCount);
         Timetable tm2 = timetables.get((i + 1) % timetablesCount);
         vehicle.getTimetables().add(tm1);
         vehicle.getTimetables().add(tm2);
         calendar.add(Calendar.MINUTE, 12);

         vehicles.add(vehicle);
      }

      return vehicles;
   }

   private VehicleJourney vehicleJourney(Calendar calendar,
         JourneyPattern journeyPattern) throws CreateModelException
   {
      VehicleJourney vehicle = modelFactory.createModel(VehicleJourney.class);
      vehicle.setNumber((new Random()).nextLong());
      vehicle.setJourneyPattern(journeyPattern);
      vehicle.setRoute(journeyPattern.getRoute());
      try
      {

         for (int i = 0; i < journeyPattern.getStopPoints().size(); i++)
         {
            VehicleJourneyAtStop vjas = new VehicleJourneyAtStop();
            vjas.setStopPoint(journeyPattern.getStopPoints().get(i));
            vjas.setVehicleJourney(vehicle);

            vjas.setArrivalTime(new Time(calendar.getTime().getTime()));
            vjas.setDepartureTime(new Time(calendar.getTime().getTime()));
            calendar.add(Calendar.MINUTE, 3);
            vjas = modelFactory.createModel(vjas);

            vjas.setVehicleJourney(vehicle);
         }
      } catch (CreateModelException ex)
      {
         Logger.getLogger(ComplexModelFactory.class.getName()).log(
               Level.SEVERE, null, ex);
      }
      assert vehicle.getJourneyPattern() != null;
      return vehicle;
   }

}
