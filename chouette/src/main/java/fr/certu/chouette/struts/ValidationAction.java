package fr.certu.chouette.struts;

import fr.certu.chouette.manager.SingletonManager;
import fr.certu.chouette.service.geographie.IConvertisseur;
import fr.certu.chouette.service.geographie.ICoordonnees;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import fr.certu.chouette.service.database.ChouetteDriverManagerDataSource;

@SuppressWarnings("serial")
public class ValidationAction extends GeneriqueAction
{

  private static final Logger logger = Logger.getLogger(ValidationAction.class);
  private ChouetteDriverManagerDataSource managerDataSource;
  private Connection connexion = null;
  private static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
  private static final SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
  private boolean withErrors = false;
  private String inclusif;
  private String decalage;
  private String purge;
  private String useGeometry;

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
    try
    {
      // TODO. gestion des messages de validation...
      Class.forName(managerDataSource.getDriverClassName());
      Properties props = new Properties();
      props.setProperty("user", managerDataSource.getUsername());
      props.setProperty("password", managerDataSource.getPassword());
      props.setProperty("allowEncodingChanges", "true");
      connexion = DriverManager.getConnection(managerDataSource.getUrl(), props);
      connexion.setAutoCommit(false);
      // Les horaires sont croissants.
      String errHoraire = validerHoraires();
      if (errHoraire.length() == 0)
      {
        addActionMessage(getText("message.validate.vehicleJourneyAtStop.increase"));
      }
      else
      {
        addActionError(errHoraire);
      }

      // Tout Calendrier est non vide
      validerCalendriers();
      addActionMessage(getText("message.validate.timetable"));
      validerCourses();
      addActionMessage(getText("message.validate.vehicleJourney"));

      // Tout arret (zone ou physique) possède des coordonnées
      validerCoordonnees();
      addActionMessage(getText("message.validate.stoppoint.coordinates"));

      // Tout arret physique est contenue dans une zone d'arrêts.
      validerArrets();
      addActionMessage(getText("message.validate.boardingPosition"));

      // Toutes les contraintes cles etrangers sont valides.
      validerContraintes();
      addActionMessage(getText("message.validate.referential.integrity"));

      connexion.commit();
    }
    catch (Exception e)
    {
      try
      {
        logger.debug("annuler :" + e.getMessage(), e);
        if (connexion != null)
        {
          connexion.rollback();
        }
      }
      catch (Exception ex)
      {
        logger.error("Echec de la tentative de rollback de la transaction " + ex.getMessage(), ex);
      }
      throw new RuntimeException(e);
    } finally
    {
      try
      {
        if (connexion != null)
        {
          connexion.close();
        }
      }
      catch (Exception e)
      {
        logger.error("Echec de la tentative de fermeture de la connexion " + e.getMessage(), e);
      }
    }

    return SUCCESS;
  }

  private String validerHoraires()
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
      Statement statement = connexion.createStatement();
      String selectStatement = "SELECT vjas.vehicleJourneyId, vjas.arrivaltime, vjas.departuretime, vj.routeId, r.lineId, s.position FROM " + managerDataSource.getDatabaseSchema() + ".vehiclejourneyatstop vjas, vehiclejourney vj, route r, stoppoint s WHERE vj.id = vjas.vehicleJourneyId AND r.id = vj.routeId AND s.id = vjas.stopPointId ORDER BY vjas.vehicleJourneyId, s.position;";
      ResultSet rs = statement.executeQuery(selectStatement);
      long idCourse = (long) -1;
      Date date = null;
      while (rs.next())
      {
        Object obj1 = rs.getObject(1);
        Object obj2 = rs.getObject(2);
        Object obj3 = rs.getObject(3);
        Date tmpDate2 = null;
        Date tmpDate3 = null;
        if (obj1 == null)
        {
          if (!withErrors)
          {
            erreurs = getText("message.validate.vehicleJourneyAtStop") + "<UL TYPE=DISC>";
          }
          erreurs += "<LI>" + getText("message.validate.vehicleJourneyAtStop.link");
          withErrors = true;
          continue;
        }
        if ((obj2 == null) && (obj3 == null))
        {
          if (!withErrors)
          {
            erreurs = getText("message.validate.vehicleJourneyAtStop") + "<UL TYPE=DISC>";
          }
          erreurs += "<LI>" + getText("message.validate.vehicleJourneyAtStop.time");
          withErrors = true;
          continue;
        }
        long tmpIdCourse = Long.parseLong(obj1.toString());
        if (obj2 != null)
        {
          try
          {
            tmpDate2 = sdf.parse(obj2.toString());
          }
          catch (ParseException ex)
          {
            if (!withErrors)
            {
              erreurs = getText("message.validate.vehicleJourneyAtStop") + "<UL TYPE=DISC>";
            }
            erreurs += "<LI>" + getText("message.validate.vehicleJourneyAtStop.arrival") + ex.getMessage();
            withErrors = true;
            continue;
          }
        }
        if (obj3 != null)
        {
          try
          {
            tmpDate3 = sdf.parse(obj3.toString());
          }
          catch (ParseException ex)
          {
            if (!withErrors)
            {
              erreurs = getText("message.validate.vehicleJourneyAtStop") + "<UL TYPE=DISC>";
            }
            erreurs += "<LI>" + getText("message.validate.vehicleJourneyAtStop.departure") + ex.getMessage();
            withErrors = true;
            continue;
          }
        }
        if ((tmpDate2 != null) && (tmpDate3 != null) && (tmpDate2.after(tmpDate3)))
        {
          if (maxDate.before(tmpDate2) && minDate.after(tmpDate3)); // Tout va bien
          else
          {
            if (!withErrors)
            {
              erreurs = getText("message.validate.vehicleJourneyAtStop") + "<UL TYPE=DISC>";
            }
            String[] args = new String[5];
            args[0] = sdf.format(tmpDate2);
            args[1] = sdf.format(tmpDate3);
            args[2] = obj1.toString();
            args[3] = rs.getObject(4).toString();
            erreurs += "<LI><a href=\"liste_HorairesDePassage.action?idLigne=" + rs.getObject(5).toString() + "&amp;idItineraire=" + rs.getObject(4).toString() + "\">" + getText("message.validate.vehicleJourneyAtStop.stoppoint", args) + "</a>";
            withErrors = true;
          }
        }
        if ((tmpIdCourse == idCourse) && (date.after(tmpDate2)))
        {
          if (maxDate.before(date) && minDate.after(tmpDate2)); // Tout va bien
          else
          {
            if (!withErrors)
            {
              erreurs = getText("message.validate.vehicleJourneyAtStop") + "<UL TYPE=DISC>";
            }
            String[] args = new String[5];
            args[0] = sdf.format(date);
            args[1] = sdf.format(tmpDate2);
            args[2] = obj1.toString();
            args[3] = rs.getObject(4).toString();
            args[4] = rs.getObject(6).toString();
            erreurs += "<LI><a href=\"liste_HorairesDePassage.action?idLigne=" + rs.getObject(5).toString() + "&amp;idItineraire=" + rs.getObject(4).toString() + "\">" + getText("message.validate.vehicleJourneyAtStop.previous.stoppoint", args) + "</a>";
            withErrors = true;
          }
        }
        date = tmpDate3;
        idCourse = tmpIdCourse;
      }
    }
    catch (SQLException e)
    {
      if (!withErrors)
      {
        erreurs = getText("message.validate.vehicleJourneyAtStop") + "<UL TYPE=DISC>";
      }
      erreurs += "<LI>" + getText("message.validate.vehicleJourneyAtStop.times") + e.getMessage();
      withErrors = true;
    }
    if (erreurs.length() != 0)
    {
      erreurs += "</UL>";
    }
    return erreurs;
  }

  private void validerCalendriers()
  {
    String erreurs = "";
    withErrors = false;
    try
    {
      String selectTM = "SELECT id, objectid, objectversion, creationtime, creatorid, \"version\",  \"comment\", intdaytypes FROM " + managerDataSource.getDatabaseSchema() + ".timetable;";
      Statement statementTM = connexion.createStatement();
      ResultSet rsTM = statementTM.executeQuery(selectTM);
      List<Object[]> tms = new ArrayList<Object[]>();
      while (rsTM.next())
      {
        Object[] tmp = new Object[8];
        for (int i = 1; i <= 8; i++)
        {
          tmp[i - 1] = rsTM.getObject(i);
        }
        tms.add(tmp);
      }
      String selectDates = "SELECT timetableid, date, \"position\" FROM " + managerDataSource.getDatabaseSchema() + ".timetable_date;";
      Statement statementDates = connexion.createStatement();
      ResultSet rsDates = statementDates.executeQuery(selectDates);
      List<Object[]> dates = new ArrayList<Object[]>();
      while (rsDates.next())
      {
        Object[] tmp = new Object[3];
        for (int i = 1; i <= 3; i++)
        {
          tmp[i - 1] = rsDates.getObject(i);
        }
        dates.add(tmp);
      }
      String selectPeriodes = "SELECT timetableid, periodStart, periodEnd, \"position\" FROM " + managerDataSource.getDatabaseSchema() + ".timetable_period;";
      Statement statementPeriodes = connexion.createStatement();
      ResultSet rsPeriodes = statementPeriodes.executeQuery(selectPeriodes);
      List<Object[]> periodes = new ArrayList<Object[]>();
      while (rsPeriodes.next())
      {
        Object[] tmp = new Object[4];
        for (int i = 1; i <= 4; i++)
        {
          tmp[i - 1] = rsPeriodes.getObject(i);
        }
        periodes.add(tmp);
      }
      for (Object[] tm : tms)
      {
        int intDayTypes = 0;
        if (tm[7] != null)
        {
          intDayTypes = Integer.parseInt(tm[7].toString());
        }
        Set<String> tmDates = new HashSet<String>();
        for (Object[] date : dates)
        {
          if (tm[0].toString().equals(date[0].toString()))
          {
            tmDates.add(date[1].toString());
          }
        }
        for (Object[] periode : periodes)
        {
          if (tm[0].toString().equals(periode[0].toString()))
          {
            try
            {
              Date debut = sdf2.parse(periode[1].toString());
              Date fin = sdf2.parse(periode[2].toString());
              if (debut.after(fin))
              {
                if (!withErrors)
                {
                  addActionError("<TMS>");
                  withErrors = true;
                }
                String[] args = new String[2];
                args[0] = periode[1].toString();
                args[1] = periode[2].toString();
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
                    int monday = (int) Math.pow(2, chouette.schema.types.DayTypeType.MONDAY_TYPE);
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
                    int tuesday = (int) Math.pow(2, chouette.schema.types.DayTypeType.TUESDAY_TYPE);
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
                    int wednesday = (int) Math.pow(2, chouette.schema.types.DayTypeType.WEDNESDAY_TYPE);
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
                    int thursday = (int) Math.pow(2, chouette.schema.types.DayTypeType.THURSDAY_TYPE);
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
                    int friday = (int) Math.pow(2, chouette.schema.types.DayTypeType.FRIDAY_TYPE);
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
                    int saturday = (int) Math.pow(2, chouette.schema.types.DayTypeType.SATURDAY_TYPE);
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
                    int sunday = (int) Math.pow(2, chouette.schema.types.DayTypeType.SUNDAY_TYPE);
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
                long time = date.getTime() + ((long) 24) * ((long) 60) * ((long) 60) * ((long) 1000);
                date = new Date(time);
              }
            }
            catch (ParseException e)
            {
              withErrors = true;
              addActionError(getText("message.validate.timetable.invalid.format") + e.getMessage());
            }
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
          args[0] = tm[1].toString();
          args[1] = tm[0].toString();
          String errMsg = "<TM>" + getText("message.validate.timetable.empty", args) + "</TM>";
          if (tm[6] != null)
          {
            args[0] = tm[6].toString();
            args[1] = tm[1].toString();
            args[2] = tm[0].toString();
            errMsg = "<TM>" + getText("message.validate.timetable.empty.args", args) + "</TM>";
          }
          addActionError(errMsg);
        }
      }
      if (withErrors)
      {
        addActionError("</TMS>");
      }
    }
    catch (SQLException e)
    {
      withErrors = true;
      addActionError(getText("message.validate.timetable.error") + e.getMessage());
    }
  }

  private void validerCourses()
  {
    withErrors = false;
    try
    {
      String selectCourse = "SELECT id, \"number\", \"comment\", (SELECT \"name\" FROM " + managerDataSource.getDatabaseSchema() + ".route WHERE id=routeid) FROM " + managerDataSource.getDatabaseSchema() + ".vehiclejourney;";
      Statement statementCourse = connexion.createStatement();
      ResultSet rsCourse = statementCourse.executeQuery(selectCourse);
      while (rsCourse.next())
      {
        String idCourse = rsCourse.getObject(1).toString();
        String selectCourseTM = "SELECT id FROM " + managerDataSource.getDatabaseSchema() + ".timetablevehiclejourney WHERE vehicleJourneyId='" + idCourse + "';";
        Statement statementCourseTM = connexion.createStatement();
        ResultSet rsCourseTM = statementCourseTM.executeQuery(selectCourseTM);
        if (!rsCourseTM.next())
        {
          withErrors = true;
          String[] args = new String[2];
          args[0] = idCourse;
          String errMsg = getText("message.validate.vehicleJourney.noTimetable", args);
          if (rsCourse.getObject(2) != null)
          {
            errMsg = errMsg + getText("message.validate.vehicleJourney.id") + rsCourse.getObject(2).toString();
          }
          if (rsCourse.getObject(3) != null)
          {
            errMsg = errMsg + getText("message.validate.vehicleJourney.comment") + rsCourse.getObject(3).toString();
          }
          if (rsCourse.getObject(4) != null)
          {
            errMsg = errMsg + getText("message.validate.vehicleJourney.route") + rsCourse.getObject(4).toString();
          }
          addActionError(errMsg);
        }
      }
    }
    catch (SQLException e)
    {
      withErrors = true;
      addActionError(getText("message.validate.vehicleJourney.error") + e.getMessage());
    }
  }

  private void validerCoordonnees()
  {
    withErrors = false;
    try
    {
      String selectCoordonnees = "SELECT objectid, \"name\" FROM " + managerDataSource.getDatabaseSchema() + ".stoparea WHERE (longitude IS NULL) OR (latitude IS NULL);";
      Statement statementCoordonnees = connexion.createStatement();
      ResultSet rsCoordonnees = statementCoordonnees.executeQuery(selectCoordonnees);
      while (rsCoordonnees.next())
      {
        withErrors = true;
        String[] args = new String[3];
        if (rsCoordonnees.getObject(2) == null)
        {
          args[0] = rsCoordonnees.getObject(1).toString();
          addActionError(getText("message.validate.coordinates.noCoordinates", args));
        }
        else
        {
          args[0] = rsCoordonnees.getObject(2).toString();
          args[1] = rsCoordonnees.getObject(1).toString();
          addActionError(getText("message.validate.coordinates.noCoordinates.args", args));
        }
      }
    }
    catch (SQLException e)
    {
      withErrors = true;
      addActionError(getText("message.validate.coordinates.error") + e.getMessage());
    }
  }

  private void validerArrets()
  {
    withErrors = false;
    try
    {
      String selectArrets = "SELECT objectId, \"name\" FROM " + managerDataSource.getDatabaseSchema() + ".stoparea WHERE areatype IN ('BoardingPosition','Quay') AND parentId IS NULL;";
      Statement statementArrets = connexion.createStatement();
      ResultSet rsArrets = statementArrets.executeQuery(selectArrets);
      while (rsArrets.next())
      {
        withErrors = true;
        String[] args = new String[3];
        if (rsArrets.getObject(2) == null)
        {
          args[0] = rsArrets.getObject(1).toString();
          addActionError(getText("message.validate.boardingPosition.into.stopPlace", args));
        }
        else
        {
          args[0] = rsArrets.getObject(2).toString();
          args[1] = rsArrets.getObject(1).toString();
          addActionError(getText("message.validate.boardingPosition.into.stopPlace.args", args));
        }
      }
    }
    catch (SQLException e)
    {
      withErrors = true;
      addActionError(getText("message.validate.boardingPosition.error") + e.getMessage());
    }
  }

  private void validerContraintes()
  {
    //TODO. A coder (Drop des contraintes cles etrangers, puis recréation de ces même cles)
    //
    withErrors = false;
    // connectionLink
    try
    {
      String add1 = "ALTER TABLE connectionlink ADD CONSTRAINT new_connectionlink_stoparea_arrival FOREIGN KEY (arrivalId) REFERENCES stoparea (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;";
      Statement a1Statement = connexion.createStatement();
      a1Statement.execute(add1);
      String drop1 = "ALTER TABLE connectionlink DROP CONSTRAINT new_connectionlink_stoparea_arrival;";
      Statement d1Statement = connexion.createStatement();
      d1Statement.execute(drop1);
      String add2 = "ALTER TABLE connectionlink ADD CONSTRAINT new_connectionlink_stoparea_departure FOREIGN KEY (departureId) REFERENCES stoparea (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;";
      Statement a2Statement = connexion.createStatement();
      a2Statement.execute(add2);
      String drop2 = "ALTER TABLE connectionlink DROP CONSTRAINT new_connectionlink_stoparea_departure;";
      Statement d2Statement = connexion.createStatement();
      d2Statement.execute(drop2);
    }
    catch (SQLException e)
    {
      withErrors = true;
      addActionError(getText("message.validate.constraint.error") + e.getMessage());
    }
  }

  public String decaler()
  {
    try
    {
      Date date = sdf.parse("00:" + decalage);
      Class.forName(managerDataSource.getDriverClassName());
      Properties props = new Properties();
      props.setProperty("user", managerDataSource.getUsername());
      props.setProperty("password", managerDataSource.getPassword());
      props.setProperty("allowEncodingChanges", "true");
      connexion = DriverManager.getConnection(managerDataSource.getUrl(), props);
      connexion.setAutoCommit(false);
      Statement selectStatement = connexion.createStatement();
      String selectStatementStr = "SELECT id, departuretime FROM " + managerDataSource.getDatabaseSchema() + ".vehiclejourneyatstop;";
      ResultSet rs = selectStatement.executeQuery(selectStatementStr);
      Map<Long, String> arrivalTimes = new HashMap<Long, String>();
      while (rs.next())
      {
        long time = sdf.parse(rs.getObject(2).toString()).getTime() - date.getTime() - (long) (60 * 60 * 1000);
        arrivalTimes.put(new Long(rs.getObject(1).toString()), sdf.format(new Date(time)));
      }
      for (Long key : arrivalTimes.keySet())
      {
        Statement updateSt = connexion.createStatement();
        String updateStatementStr = "UPDATE " + managerDataSource.getDatabaseSchema() + ".vehiclejourneyatstop SET arrivaltime='" + arrivalTimes.get(key) + "' WHERE id='" + key.longValue() + "';";
        int number = updateSt.executeUpdate(updateStatementStr);
        if (number != 1)
        {
          addActionError(getText("message.validate.shift.update") + " \"" + key.longValue() + "\" : " + number);
        }
      }
      connexion.commit();
    }
    catch (ParseException e)
    {
      addActionError(getText("message.validate.shift.data.error") + e.getMessage());
    }
    catch (SQLException e)
    {
      addActionError(getText("message.validate.shift.error") + e.getMessage());
    }
    catch (ClassNotFoundException e)
    {
      addActionError(getText("message.validate.shift.error") + e.getMessage());
    } finally
    {
      try
      {
        if (connexion != null)
        {
          connexion.close();
        }
      }
      catch (Exception e)
      {
        logger.error("Echec de la tentative de fermeture de la connexion " + e.getMessage(), e);
      }
    }
    addActionMessage(getText("message.validate.shift.success"));
    return SUCCESS;
  }

  public String purger()
  {
    try
    {
      Date date = sdf2.parse(purge);
      Calendar cal = Calendar.getInstance();
      cal.setTime(date);
      cal.add(Calendar.DAY_OF_MONTH, 1);
      Date nextDate = cal.getTime();
      String nextPurge = sdf2.format(nextDate);
      Class.forName(managerDataSource.getDriverClassName());
      Properties props = new Properties();
      props.setProperty("user", managerDataSource.getUsername());
      props.setProperty("password", managerDataSource.getPassword());
      props.setProperty("allowEncodingChanges", "true");
      connexion = DriverManager.getConnection(managerDataSource.getUrl(), props);
      connexion.setAutoCommit(false);
      connexion.createStatement().execute("ALTER TABLE company DISABLE TRIGGER ALL;");
      connexion.createStatement().execute("ALTER TABLE connectionlink DISABLE TRIGGER ALL;");
      connexion.createStatement().execute("ALTER TABLE routingConstraint_stoparea DISABLE TRIGGER ALL;");
      connexion.createStatement().execute("ALTER TABLE routingConstraint DISABLE TRIGGER ALL;");
      connexion.createStatement().execute("ALTER TABLE journeypattern DISABLE TRIGGER ALL;");
      connexion.createStatement().execute("ALTER TABLE line DISABLE TRIGGER ALL;");
      connexion.createStatement().execute("ALTER TABLE ptnetwork DISABLE TRIGGER ALL;");
      connexion.createStatement().execute("ALTER TABLE route DISABLE TRIGGER ALL;");
      connexion.createStatement().execute("ALTER TABLE stoparea DISABLE TRIGGER ALL;");
      connexion.createStatement().execute("ALTER TABLE stoppoint DISABLE TRIGGER ALL;");
      connexion.createStatement().execute("ALTER TABLE timetable_period DISABLE TRIGGER ALL;");
      connexion.createStatement().execute("ALTER TABLE timetable_date DISABLE TRIGGER ALL;");
      connexion.createStatement().execute("ALTER TABLE timetable DISABLE TRIGGER ALL;");
      connexion.createStatement().execute("ALTER TABLE timetablevehiclejourney DISABLE TRIGGER ALL;");
      connexion.createStatement().execute("ALTER TABLE vehiclejourney DISABLE TRIGGER ALL;");
      connexion.createStatement().execute("ALTER TABLE vehiclejourneyatstop DISABLE TRIGGER ALL;");
      String deleteDates = "DELETE FROM " + managerDataSource.getDatabaseSchema() + ".timetable_date WHERE date <= '" + purge + "';";
      Statement deleteDatesStatement = connexion.createStatement();
      int numberOfDates = deleteDatesStatement.executeUpdate(deleteDates);
      addActionMessage(getText("message.validate.purge.dates") + numberOfDates);
      String selectDates = "SELECT timetableid, \"position\" FROM " + managerDataSource.getDatabaseSchema() + ".timetable_date ORDER BY timetableid, \"position\";";
      Statement selectDatesStatement = connexion.createStatement();
      ResultSet rsDates = selectDatesStatement.executeQuery(selectDates);
      String timetableId = null;
      int count = 0;
      while (rsDates.next())
      {
        String id = rsDates.getString(1);
        int position = rsDates.getInt(2);
        if (id.equals(timetableId))
        {
          count++;
        }
        else
        {
          count = 0;
        }
        timetableId = id;
        String updateDates2 = "UPDATE " + managerDataSource.getDatabaseSchema() + ".timetable_date SET \"position\" = '" + count + "' WHERE timetableid = '" + id + "' AND \"position\" = '" + position + "';";
        Statement updateDatesStatement2 = connexion.createStatement();
        updateDatesStatement2.executeUpdate(updateDates2);
      }
      String deletePeriodes = "DELETE FROM " + managerDataSource.getDatabaseSchema() + ".timetable_period WHERE periodEnd <= '" + purge + "';";
      Statement deletePeriodesStatement = connexion.createStatement();
      int numberOfPeriodes = deletePeriodesStatement.executeUpdate(deletePeriodes);
      addActionMessage(getText("message.validate.purge.periods") + numberOfPeriodes);
      String updatePeriodes = "UPDATE " + managerDataSource.getDatabaseSchema() + ".timetable_period SET periodStart = '" + nextPurge + "' WHERE periodStart <= '" + purge + "';";
      Statement updatePeriodesStatement = connexion.createStatement();
      int numberOfPeriodesUpdates = updatePeriodesStatement.executeUpdate(updatePeriodes);
      addActionMessage(getText("message.validate.purge.shorten.periods") + numberOfPeriodesUpdates);
      String selectPeriodes = "SELECT timetableid, \"position\" FROM " + managerDataSource.getDatabaseSchema() + ".timetable_period ORDER BY timetableid, \"position\";";
      Statement selectPeriodesStatement = connexion.createStatement();
      ResultSet rsPeriodes = selectPeriodesStatement.executeQuery(selectPeriodes);
      timetableId = null;
      count = 0;
      while (rsPeriodes.next())
      {
        String id = rsPeriodes.getString(1);
        int position = rsPeriodes.getInt(2);
        if (id.equals(timetableId))
        {
          count++;
        }
        else
        {
          count = 0;
        }
        timetableId = id;
        String updatePeriodes2 = "UPDATE " + managerDataSource.getDatabaseSchema() + ".timetable_period SET \"position\" = '" + count + "' WHERE timetableid = '" + id + "' AND \"position\" = '" + position + "';";
        Statement updatePeriodesStatement2 = connexion.createStatement();
        updatePeriodesStatement2.executeUpdate(updatePeriodes2);
      }
      String deleteTMs = "DELETE FROM " + managerDataSource.getDatabaseSchema() + ".timetable WHERE id NOT IN ((SELECT timetableid FROM " + managerDataSource.getDatabaseSchema() + ".timetable_date) UNION (SELECT timetableid FROM " + managerDataSource.getDatabaseSchema() + ".timetable_period));";
      Statement deleteTMsStatement = connexion.createStatement();
      int numberOfTMs = deleteTMsStatement.executeUpdate(deleteTMs);
      addActionMessage(getText("message.validate.purge.timetable") + numberOfTMs);
      String deleteLinks = "DELETE FROM " + managerDataSource.getDatabaseSchema() + ".timetablevehiclejourney WHERE timetableId NOT IN (SELECT id FROM " + managerDataSource.getDatabaseSchema() + ".timetable);";
      Statement deleteLinksStatement = connexion.createStatement();
      int numberOfLinks = deleteLinksStatement.executeUpdate(deleteLinks);
      addActionMessage(getText("message.validate.purge.timetable.link") + numberOfLinks);
      String deleteCourses = "DELETE FROM " + managerDataSource.getDatabaseSchema() + ".vehiclejourney WHERE id NOT IN (SELECT vehicleJourneyId FROM " + managerDataSource.getDatabaseSchema() + ".timetablevehiclejourney);";
      Statement deleteCoursesStatement = connexion.createStatement();
      int numberOfCourses = deleteCoursesStatement.executeUpdate(deleteCourses);
      addActionMessage(getText("message.validate.purge.vehicleJourney") + numberOfCourses);
      String deleteMissions = "DELETE FROM " + managerDataSource.getDatabaseSchema() + ".journeypattern WHERE id NOT IN (SELECT journeyPatternId FROM " + managerDataSource.getDatabaseSchema() + ".vehiclejourney);";
      Statement deleteMissionsStatement = connexion.createStatement();
      int numberOfMissions = deleteMissionsStatement.executeUpdate(deleteMissions);
      addActionMessage(getText("message.validate.purge.journeyPattern") + numberOfMissions);
      String deleteItineraires = "DELETE FROM " + managerDataSource.getDatabaseSchema() + ".route WHERE id NOT IN (SELECT routeId FROM " + managerDataSource.getDatabaseSchema() + ".vehiclejourney);";
      Statement deleteItinerairesStatement = connexion.createStatement();
      int numberOfItineraires = deleteItinerairesStatement.executeUpdate(deleteItineraires);
      addActionMessage(getText("message.validate.purge.route") + numberOfItineraires);
      //LES RETOUR DES ITINERAIRES ????
      String deleteHoraires = "DELETE FROM " + managerDataSource.getDatabaseSchema() + ".vehiclejourneyatstop WHERE vehicleJourneyId NOT IN (SELECT id FROM " + managerDataSource.getDatabaseSchema() + ".vehiclejourney);";
      Statement deleteHorairesStatement = connexion.createStatement();
      int numberOfHoraires = deleteHorairesStatement.executeUpdate(deleteHoraires);
      addActionMessage(getText("message.validate.purge.vehicleJourneyAtStop") + numberOfHoraires);
      String deleteArretItineraires = "DELETE FROM " + managerDataSource.getDatabaseSchema() + ".stoppoint WHERE routeId NOT IN (SELECT id FROM " + managerDataSource.getDatabaseSchema() + ".route);";
      Statement deleteArretItinerairesStatement = connexion.createStatement();
      int numberOfArretItineraires = deleteArretItinerairesStatement.executeUpdate(deleteArretItineraires);
      addActionMessage(getText("message.validate.purge.stoppointOnRoute") + numberOfArretItineraires);
      String deleteArretPhysiques = "DELETE FROM " + managerDataSource.getDatabaseSchema() + ".stoparea WHERE (areatype = 'BoardingPosition' OR areatype = 'Quay') AND (id NOT IN (SELECT stopAreaId FROM " + managerDataSource.getDatabaseSchema() + ".stoppoint));";
      Statement deleteArretPhysiquesStatement = connexion.createStatement();
      int numberOfArretPhysiques = deleteArretPhysiquesStatement.executeUpdate(deleteArretPhysiques);
      addActionMessage(getText("message.validate.purge.boardingPosition") + numberOfArretPhysiques);
      String deleteArretCommercials = "DELETE FROM " + managerDataSource.getDatabaseSchema() + ".stoparea WHERE (areatype = 'CommercialStopPoint') AND (id NOT IN (SELECT parentId FROM " + managerDataSource.getDatabaseSchema() + ".stoparea));";
      Statement deleteArretCommercialsStatement = connexion.createStatement();
      int numberOfArretCommercials = deleteArretCommercialsStatement.executeUpdate(deleteArretCommercials);
      addActionMessage(getText("message.validate.purge.commercialStopPlace") + numberOfArretCommercials);
      String deletePolesEchangess = "DELETE FROM " + managerDataSource.getDatabaseSchema() + ".stoparea WHERE (areatype = 'StopPlace') AND (id NOT IN (SELECT parentId FROM " + managerDataSource.getDatabaseSchema() + ".stoparea));";
      Statement deletePolesEchangessStatement = connexion.createStatement();
      int numberOfPolesEchangess = deletePolesEchangessStatement.executeUpdate(deletePolesEchangess);
      addActionMessage(getText("message.validate.purge.stopPlace") + numberOfPolesEchangess);
      String deleteLignes = "DELETE FROM " + managerDataSource.getDatabaseSchema() + ".line WHERE id NOT IN (SELECT lineId FROM " + managerDataSource.getDatabaseSchema() + ".route);";
      Statement deleteLignesStatement = connexion.createStatement();
      int numberOfLignes = deleteLignesStatement.executeUpdate(deleteLignes);
      addActionMessage(getText("message.validate.purge.lines") + numberOfLignes);
      connexion.createStatement().execute("ALTER TABLE company ENABLE TRIGGER ALL;");
      connexion.createStatement().execute("ALTER TABLE connectionlink ENABLE TRIGGER ALL;");
      connexion.createStatement().execute("ALTER TABLE routingConstraint_stoparea ENABLE TRIGGER ALL;");
      connexion.createStatement().execute("ALTER TABLE routingConstraint ENABLE TRIGGER ALL;");
      connexion.createStatement().execute("ALTER TABLE journeypattern ENABLE TRIGGER ALL;");
      connexion.createStatement().execute("ALTER TABLE line ENABLE TRIGGER ALL;");
      connexion.createStatement().execute("ALTER TABLE ptnetwork ENABLE TRIGGER ALL;");
      connexion.createStatement().execute("ALTER TABLE route ENABLE TRIGGER ALL;");
      connexion.createStatement().execute("ALTER TABLE stoparea ENABLE TRIGGER ALL;");
      connexion.createStatement().execute("ALTER TABLE stoppoint ENABLE TRIGGER ALL;");
      connexion.createStatement().execute("ALTER TABLE timetable_period ENABLE TRIGGER ALL;");
      connexion.createStatement().execute("ALTER TABLE timetable_date ENABLE TRIGGER ALL;");
      connexion.createStatement().execute("ALTER TABLE timetable ENABLE TRIGGER ALL;");
      connexion.createStatement().execute("ALTER TABLE timetablevehiclejourney ENABLE TRIGGER ALL;");
      connexion.createStatement().execute("ALTER TABLE vehiclejourney ENABLE TRIGGER ALL;");
      connexion.createStatement().execute("ALTER TABLE vehiclejourneyatstop ENABLE TRIGGER ALL;");

      connexion.commit();
    }
    catch (ParseException e)
    {
      addActionError(getText("message.validate.purge.data.error") + e.getMessage());
    }
    catch (SQLException e)
    {
      addActionError(getText("message.validate.purge.error") + e.getMessage());
    }
    catch (ClassNotFoundException e)
    {
      addActionError(getText("message.validate.purge.error") + e.getMessage());
    } finally
    {
      try
      {
        if (connexion != null)
        {
          connexion.close();
        }
      }
      catch (Exception e)
      {
        logger.error("Echec de la tentative de fermeture de la connexion " + e.getMessage(), e);
      }
    }
    addActionMessage(getText("message.validate.purge.success"));
    return SUCCESS;
  }

  public String barycentre()
  {
    try
    {
      ApplicationContext applicationContext = SingletonManager.getApplicationContext();
      ICoordonnees coordonnees = (ICoordonnees) applicationContext.getBean("coordonnees");
      coordonnees.calculBarycentre();
      addActionMessage(getText("message.validate.barycentre.calculation"));
    }
    catch (RuntimeException e)
    {
      addActionError(getText("message.validate.barycentre.error") + e.getMessage());
    }
    return INPUT;
  }

  public String convertir()
  {
    try
    {
      ApplicationContext applicationContext = SingletonManager.getApplicationContext();
      IConvertisseur convertisseur = (IConvertisseur) applicationContext.getBean("convertisseur");
      convertisseur.deLambert2AWGS84();
      addActionMessage(getText("message.validate.convert"));
    }
    catch (RuntimeException e)
    {
      addActionError(getText("message.validate.convert.error") + e.getMessage());
    }
    return INPUT;
  }

  public void setManagerDataSource(ChouetteDriverManagerDataSource managerDataSource)
  {
    this.managerDataSource = managerDataSource;
  }

  public void setInclusif(String inclusif)
  {
    this.inclusif = inclusif;
  }

  public void setDecalage(String decalage)
  {
    this.decalage = decalage;
  }

  public void setPurge(String purge)
  {
    this.purge = purge;
  }

  public String getUseGeometry()
  {
    return useGeometry;
  }

  public void setUseGeometry(String useGeometry)
  {
    this.useGeometry = useGeometry;
  }
}
