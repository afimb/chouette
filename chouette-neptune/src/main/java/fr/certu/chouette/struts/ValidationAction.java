package fr.certu.chouette.struts;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.manager.INeptuneManager;
import fr.certu.chouette.model.neptune.AreaCentroid;
import fr.certu.chouette.model.neptune.Period;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.neptune.VehicleJourneyAtStop;
import fr.certu.chouette.model.neptune.type.ChouetteAreaEnum;
import fr.certu.chouette.model.neptune.type.DayTypeEnum;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportItem;
import fr.certu.chouette.service.cleaning.ICleanService;
import fr.certu.chouette.service.geographic.IGeographicService;

public class ValidationAction extends GeneriqueAction
{
   private static final long serialVersionUID = -4184571575287686925L;
   private static final Logger logger = Logger.getLogger(ValidationAction.class);
   private static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
   private static final SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");

   @Setter private INeptuneManager<VehicleJourney> vehicleJourneyManager;
   @Setter private INeptuneManager<StopArea> stopAreaManager;
   @Setter private INeptuneManager<Timetable> timetableManager;
   @Setter private IGeographicService geographicService;

   @Setter private ICleanService cleanService; 

   private boolean withErrors = false;
   @Getter @Setter private String inclusif;
   @Getter @Setter private Date purgeBoundaryDate;
   @Getter @Setter private boolean beforeDatePurge;
   @Getter @Setter private String useGeometry;
   @Getter @Setter private String useValidation = "false";

   public ValidationAction()
   {
      super();
   }

   @Override
   public String execute() throws Exception
   {
      return SUCCESS;
   }

   @Override
   public String input() throws Exception
   {
      return INPUT;
   }

   public String valider()
   {

      // Les horaires sont croissants et toute course a un calendrier
      validerHoraires();
      addActionMessage(getText("message.validate.vehicleJourney"));

      // Tout Calendrier est non vide 
      validerCalendriers();
      addActionMessage(getText("message.validate.timetable"));

      // Tout arret (zone ou physique) possède des coordonnées
      validerCoordonnees();
      addActionMessage(getText("message.validate.stoppoint.coordinates"));

      // Tout arret physique est contenue dans une zone d'arrêts.
      validerArrets();
      addActionMessage(getText("message.validate.boardingPosition"));


      return SUCCESS;
   }

   /**
    * controle la croissance entre arrivée départ et arrivée au suivant
    * et la course a au moins un calendrier
    */
   private void validerHoraires()
   {
      withErrors = false;
      String erreurs = "";
      Date maxDate = null;
      Date minDate = null;
      try
      {
         maxDate = sdf.parse("23:00:00");
         minDate = sdf.parse("01:00:00");
      }
      catch (ParseException e)
      {
      }

      try 
      {
         List<VehicleJourney> vehicleJourneys = vehicleJourneyManager.getAll(null);
         for (Iterator<VehicleJourney> iterator = vehicleJourneys.iterator(); iterator.hasNext();) 
         {
            VehicleJourney vehicleJourney = iterator.next();
            Time previousArrival = null;
            for (VehicleJourneyAtStop vjas : vehicleJourney.getVehicleJourneyAtStops()) 
            {
               Time arrival = vjas.getArrivalTime();
               Time departure = vjas.getDepartureTime();
               // horaires non renseigné
               if (arrival == null && departure == null)
               {
                  if (!withErrors)
                  {
                     erreurs = getText("message.validate.vehicleJourneyAtStop") + "<UL TYPE=\"DISC\">";
                  }
                  erreurs += "<LI>" + getText("message.validate.vehicleJourneyAtStop.time") + "</LI>";
                  withErrors = true;
                  continue;
               }
               // check bet
               if ((arrival != null) && (departure != null) && (arrival.after(departure)))
               {
                  if (maxDate.before(arrival) && minDate.after(departure)); // Tout va bien
                  else
                  {
                     if (!withErrors)
                     {
                        erreurs = getText("message.validate.vehicleJourneyAtStop") + "<UL TYPE=\"DISC\">";
                     }
                     String[] args = new String[5];
                     args[0] = sdf.format(arrival);
                     args[1] = sdf.format(departure);
                     args[2] = vehicleJourney.getId().toString();
                     args[3] = vehicleJourney.getRoute().getId().toString();
                     erreurs += "<LI><a href=\"vehicleJourneyAtStop/list?idLigne=" + vehicleJourney.getRoute().getLine().getId().toString() + "&idItineraire=" + vehicleJourney.getRoute().getId().toString() + "\">" + getText("message.validate.vehicleJourneyAtStop.stoppoint", args) + "</a>" + "</LI>";
                     withErrors = true;
                  }
               }
               if (previousArrival != null && (departure != null) && departure.after(previousArrival))
               {
                  if (maxDate.before(departure) && minDate.after(previousArrival)); // Tout va bien
                  else
                  {
                     if (!withErrors)
                     {
                        erreurs = getText("message.validate.vehicleJourneyAtStop") + "<UL TYPE=\"DISC\">";
                     }
                     String[] args = new String[5];
                     args[0] = sdf.format(previousArrival);
                     args[1] = sdf.format(departure);
                     args[2] = vehicleJourney.getId().toString();
                     args[3] = vehicleJourney.getRoute().getId().toString();
                     args[4] = Long.toString(vjas.getOrder());
                     erreurs += "<LI><a href=\"vehicleJourneyAtStop/list?idLigne=" + vehicleJourney.getRoute().getLine().getId().toString() + "&idItineraire=" + vehicleJourney.getRoute().getId().toString() + "\">" + getText("message.validate.vehicleJourneyAtStop.previous.stoppoint", args) + "</a></LI>";
                     withErrors = true;
                  }                    	
               }
            }
            if (vehicleJourney.getTimetables() == null || vehicleJourney.getTimetables().isEmpty())
            {
               withErrors = true;
               String[] args = new String[2];
               args[0] = vehicleJourney.getId().toString();
               String errMsg = getText("message.validate.vehicleJourney.noTimetable", args);
               if (vehicleJourney.getNumber() != 0)
               {
                  errMsg = errMsg + getText("message.validate.vehicleJourney.id") + Long.toString(vehicleJourney.getNumber());
               }
               if (vehicleJourney.getComment() != null)
               {
                  errMsg = errMsg + getText("message.validate.vehicleJourney.comment") + vehicleJourney.getComment();
               }
               if (vehicleJourney.getRoute() != null && vehicleJourney.getRoute().getName() != null)
               {
                  errMsg = errMsg + getText("message.validate.vehicleJourney.route") + vehicleJourney.getRoute().getName();
               }
               addActionError(errMsg);
            }
            iterator.remove();
         }
      } 
      catch (ChouetteException e) 
      {
         if (!withErrors)
         {
            erreurs = getText("message.validate.vehicleJourneyAtStop") + "<UL TYPE=\"DISC\">";
         }
         erreurs += "<LI>" + getText("message.validate.vehicleJourneyAtStop.times") +  getExceptionMessage(e) + "</LI>";
         withErrors = true;
         logger.error("erreur SQL", e);
      }


      if (erreurs.length() != 0)
      {
         erreurs += "</UL>";
         logger.debug("erreurs : " + erreurs);
         addActionError(erreurs);
      }
      else
      {
         addActionMessage(getText("message.validate.vehicleJourneyAtStop.increase"));
      }
   }

   /**
    * verification calendrier couvre au moins une journee réelle 
    */
   private void validerCalendriers()
   {
      withErrors = false;

      try 
      {
         List<Timetable> timetables = timetableManager.getAll(null);
         for (Iterator<Timetable> iterator = timetables.iterator(); iterator.hasNext();) 
         {
            Timetable timetable = iterator.next();
            int intDayTypes = (timetable.getIntDayTypes() != null)? timetable.getIntDayTypes().intValue() : 0;

            Set<String> tmDates = new HashSet<String>();
            if (timetable.getCalendarDays() != null)
            {
               for (Date day : timetable.getCalendarDays()) 
               {
                  tmDates.add(sdf2.format(day));
               }
            }
            // check period chronology
            if (timetable.getPeriods() != null)
            {

               for (Period period : timetable.getPeriods()) 
               {
                  Date debut = period.getStartDate();
                  Date fin = period.getEndDate();
                  if (debut.after(fin))
                  {
                     if (!withErrors)
                     {
                        addActionError("<TMS>");
                        withErrors = true;
                     }
                     String[] args = new String[2];
                     args[0] = debut.toString();
                     args[1] = fin.toString();
                     addActionError("<TM>" + getText("message.validate.timetable.interval", args) + "</TM>");
                     continue;
                  }
                  Date date = debut;
                  while (date.before(fin))
                  {
                     Calendar calendar = Calendar.getInstance();
                     calendar.setTime(date);
                     switch (calendar.get(Calendar.DAY_OF_WEEK))
                     {
                     case Calendar.MONDAY:
                        int monday = (int) Math.pow(2, DayTypeEnum.MONDAY.ordinal());
                        if ((intDayTypes & monday) == monday)
                        {
                           tmDates.add(sdf2.format(date));
                        }
                        else if (inclusif.equals("true"))
                        {
                           tmDates.remove(sdf2.format(date));
                        }
                        break;
                     case Calendar.TUESDAY:
                        int tuesday = (int) Math.pow(2, DayTypeEnum.TUESDAY.ordinal());
                        if ((intDayTypes & tuesday) == tuesday)
                        {
                           tmDates.add(sdf2.format(date));
                        }
                        else if (inclusif.equals("true"))
                        {
                           tmDates.remove(sdf2.format(date));
                        }
                        break;
                     case Calendar.WEDNESDAY:
                        int wednesday = (int) Math.pow(2, DayTypeEnum.WEDNESDAY.ordinal());
                        if ((intDayTypes & wednesday) == wednesday)
                        {
                           tmDates.add(sdf2.format(date));
                        }
                        else if (inclusif.equals("true"))
                        {
                           tmDates.remove(sdf2.format(date));
                        }
                        break;
                     case Calendar.THURSDAY:
                        int thursday = (int) Math.pow(2, DayTypeEnum.THURSDAY.ordinal());
                        if ((intDayTypes & thursday) == thursday)
                        {
                           tmDates.add(sdf2.format(date));
                        }
                        else if (inclusif.equals("true"))
                        {
                           tmDates.remove(sdf2.format(date));
                        }
                        break;
                     case Calendar.FRIDAY:
                        int friday = (int) Math.pow(2, DayTypeEnum.FRIDAY.ordinal());
                        if ((intDayTypes & friday) == friday)
                        {
                           tmDates.add(sdf2.format(date));
                        }
                        else if (inclusif.equals("true"))
                        {
                           tmDates.remove(sdf2.format(date));
                        }
                        break;
                     case Calendar.SATURDAY:
                        int saturday = (int) Math.pow(2, DayTypeEnum.SATURDAY.ordinal());
                        if ((intDayTypes & saturday) == saturday)
                        {
                           tmDates.add(sdf2.format(date));
                        }
                        else if (inclusif.equals("true"))
                        {
                           tmDates.remove(sdf2.format(date));
                        }
                        break;
                     case Calendar.SUNDAY:
                        int sunday = (int) Math.pow(2, DayTypeEnum.SUNDAY.ordinal());
                        if ((intDayTypes & sunday) == sunday)
                        {
                           tmDates.add(sdf2.format(date));
                        }
                        else if (inclusif.equals("true"))
                        {
                           tmDates.remove(sdf2.format(date));
                        }
                        break;
                     }
                     long time = date.getTime() + 24l*60l*60l*1000l; // ?

                     date = new Date(time);
                  }


               }
               if (tmDates.size() == 0)
               {
                  if (!withErrors)
                  {
                     addActionError("<TMS>");
                     withErrors = true;
                  }

                  String[] args = new String[3];
                  args[0] = timetable.getObjectId();
                  args[1] = timetable.getId().toString();
                  String errMsg = "<TM>" + getText("message.validate.timetable.empty", args) + "</TM>";
                  if (timetable.getComment() != null)
                  {
                     args[0] = timetable.getComment();
                     args[1] = timetable.getObjectId();
                     args[2] = timetable.getId().toString();
                     errMsg = "<TM>" + getText("message.validate.timetable.empty.args", args) + "</TM>";
                  }
                  addActionError(errMsg);
               }
            }
            if (withErrors)
            {
               addActionError("</TMS>");
            }

            iterator.remove();
         }


      } 
      catch (ChouetteException e) 
      {
         withErrors = true;
         addActionError(getText("message.validate.timetable.error") +  getExceptionMessage(e));
      }
   }


   private void validerCoordonnees()
   {
      withErrors = false;
      try 
      {
         Filter latFilter = Filter.getNewIsNullFilter(StopArea.AREACENTROID+"."+AreaCentroid.LATITUDE);
         Filter lonFilter = Filter.getNewIsNullFilter(StopArea.AREACENTROID+"."+AreaCentroid.LONGITUDE);
         Filter filter = Filter.getNewOrFilter(latFilter,lonFilter);
         List<StopArea> areas = stopAreaManager.getAll(null,filter);
         if (!areas.isEmpty())
         {
            withErrors = true;
            String[] args = new String[2];
            for (StopArea stopArea: areas) 
            {
               if (stopArea.getName() == null)
               {
                  args[0] = stopArea.getObjectId();
                  addActionError(getText("message.validate.coordinates.noCoordinates", args));
               }
               else
               {
                  args[0] = stopArea.getName();
                  args[1] = stopArea.getObjectId();
                  addActionError(getText("message.validate.coordinates.noCoordinates.args", args));
               }
            }


         }

      } 
      catch (ChouetteException e) 
      {
         withErrors = true;
         addActionError(getText("message.validate.coordinates.error") +  getExceptionMessage(e));
      }
   }

   private void validerArrets() 
   {
      withErrors = false;

      Filter typeFilter = Filter.getNewInFilter("areaType", new String[]{ChouetteAreaEnum.BOARDINGPOSITION.toString(), ChouetteAreaEnum.QUAY.toString()}); 
      // Filter parentFilter = Filter.getNewIsNullFilter("parentStopArea"); 
      Filter filter = Filter.getNewAndFilter(typeFilter);

      try 
      {
         List<StopArea> areas = stopAreaManager.getAll(null,filter);
         if (!areas.isEmpty())
         {
            String[] args = new String[2];
            for (StopArea stopArea: areas) 
            {
               if (stopArea.getParents() == null || stopArea.getParents().isEmpty())
               {
                  withErrors = true;
                  if (stopArea.getName() == null)
                  {
                     args[0] = stopArea.getObjectId();
                     addActionError(getText("message.validate.boardingPosition.into.stopPlace", args));
                  }
                  else
                  {
                     args[0] = stopArea.getName();
                     args[1] = stopArea.getObjectId();
                     addActionError(getText("message.validate.boardingPosition.into.stopPlace.args", args));
                  }
               }
            }
         }
      } 
      catch (ChouetteException e) 
      {
         withErrors = true;
         addActionError(getText("message.validate.boardingPosition.error") +  getExceptionMessage(e));
      }

   }



   /**
    * purge les données de la base 
    * 
    * @return
    */
   public String purger()
   {
      try{
         Report report = cleanService.purgeAllItems(new java.sql.Date(purgeBoundaryDate.getTime()), beforeDatePurge);
         if (report.getStatus().equals(Report.STATE.OK))
         {
            addActionMessage(getText("message.validate.purge.success"));
            for (ReportItem item : report.getItems()) 
            {
               addActionMessage(item.getLocalizedMessage());
            }
         }
         else
         {
            for (ReportItem item : report.getItems()) 
            {
               addActionError(item.getLocalizedMessage());
            }
         }
      }
      catch(Exception e)
      {
         logger.error("purge failed "+e.getMessage(),e);
         addActionError(getText("message.validate.purge.error")+ e.getMessage());
      }

      return SUCCESS;
   }

   public String barycentre()
   {
      try
      {
         geographicService.propagateBarycentre();
         addActionMessage(getText("message.validate.barycentre.calculation"));
      }
      catch (RuntimeException e)
      {
         addActionError(getText("message.validate.barycentre.error") +  getExceptionMessage(e));
      }
      return INPUT;
   }

   public String convertir()
   {
      try
      {
         geographicService.convertToWGS84();
         addActionMessage(getText("message.validate.convert"));
      }
      catch (RuntimeException e)
      {
         e.printStackTrace();
         addActionError(getText("message.validate.convert.error") +  getExceptionMessage(e));
      }
      return INPUT;
   }


}
