package fr.certu.chouette.struts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.PrincipalAware;
import org.apache.struts2.interceptor.PrincipalProxy;
import org.apache.struts2.interceptor.RequestAware;
import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import fr.certu.chouette.model.neptune.type.ChouetteAreaEnum;
import fr.certu.chouette.model.neptune.type.ConnectionLinkTypeEnum;
import fr.certu.chouette.model.neptune.type.DayTypeEnum;
import fr.certu.chouette.model.neptune.type.PTDirectionEnum;
import fr.certu.chouette.model.neptune.type.ServiceStatusValueEnum;
import fr.certu.chouette.model.neptune.type.TransportModeNameEnum;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportItem;
import fr.certu.chouette.struts.enumeration.ObjetEnumere;
import fr.certu.chouette.struts.outil.filAriane.FilAriane;

public class GeneriqueAction extends ActionSupport implements RequestAware, SessionAware, PrincipalAware
{

   private static final long   serialVersionUID      = 3507673074535689742L;
   private static final Logger logger                = Logger.getLogger(GeneriqueAction.class);
   public static final String  EDIT                  = "edit";
   public static final String  REDIRECT              = "redirect";
   public static final String  SAVE                  = "save";
   public static final String  UPDATE                = "update";
   public static final String  CREATEANDEDIT         = "createAndedit";
   public static final String  LIST                  = "list";
   public static final String  REDIRECTLIST          = "redirectList";
   public static final String  REDIRECTEDIT          = "redirectEdit";
   public static final String  SEARCH                = "search";
   public static final String  SEARCH_LINE           = "searchLine";
   public static final String  EXPORT                = "export";
   public static final String  AUTOCOMPLETE          = "autocomplete";
   @SuppressWarnings("rawtypes")
   protected Map               session;
   @SuppressWarnings("rawtypes")
   protected Map               request;
   protected PrincipalProxy    principalProxy;
   public static final String  AUTHORIZEDTYPESET_ALL = "All";
   public static final String  AUTHORIZEDTYPESET_C   = "CommercialStop";
   public static final String  AUTHORIZEDTYPESET_S   = "StopPlace";
   public static final String  AUTHORIZEDTYPESET_CS  = "CommercialStopStopPlace";
   public static final String  AUTHORIZEDTYPESET_QB  = "QuayBoardingPosition";
   public static final String  AUTHORIZEDTYPESET_R   = "RoutingConstraint";
   public static final String  MODE_LAST_ENTRY       = "Other";
   public static final int     WEEKDAY_TYPE          = 0;
   public static final int     WEEKEND_TYPE          = 1;
   public static final int     MONDAY_TYPE           = 2;
   public static final int     TUESDAY_TYPE          = 3;
   public static final int     WEDNESDAY_TYPE        = 4;
   public static final int     THURSDAY_TYPE         = 5;
   public static final int     FRIDAY_TYPE           = 6;
   public static final int     SATURDAY_TYPE         = 7;
   public static final int     SUNDAY_TYPE           = 8;
   public static final int     SCHOOLHOLLIDAY_TYPE   = 9;
   public static final int     PUBLICHOLLIDAY_TYPE   = 10;
   public static final int     MARKETDAY_TYPE        = 11;
   private String              geoportalApiKey;
   private String              baseLayerSource;
   private String              lambertSRID;

   @SuppressWarnings("rawtypes")
   public void setSession(Map session)
   {
      this.session = session;
   }

   @SuppressWarnings({ "rawtypes", "unchecked" })
   public void setRequest(Map request)
   {

      // If the context contains a new locale the breadcrumb is initialized
      if (ActionContext.getContext().getParameters().containsKey("request_locale"))
      {
         FilAriane filAriane = new FilAriane();
         session.put("filAriane", filAriane);
      }

      this.request = request;
   }

   public void setPrincipalProxy(PrincipalProxy principalProxy)
   {
      this.principalProxy = principalProxy;
   }

   public PrincipalProxy getPrincipalProxy()
   {
      return principalProxy;
   }

   @SuppressWarnings("unchecked")
   public FilAriane getFilAriane()
   {
      if (session.get("filAriane") == null)
      {
         FilAriane filAriane = new FilAriane();
         session.put("filAriane", filAriane);
         return filAriane;

      }
      else
      {
         return (FilAriane) session.get("filAriane");
      }
   }

   public List<ObjetEnumere> getDirectionsEnum()
   {
      Map<String, String> cleParTraduction = new Hashtable<String, String>();
      SortedSet<String> traductionTriees = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);

      ResourceBundle rsDir = ResourceBundle.getBundle("directions", getLocale());

      Enumeration<String> rsDirEnum = rsDir.getKeys();
      while (rsDirEnum.hasMoreElements())
      {
         String cle = rsDirEnum.nextElement();
         String traduction = rsDir.getString(cle);
         if (traduction != null && !traduction.isEmpty())
         {
            cleParTraduction.put(traduction, cle);
         }
      }
      traductionTriees.addAll(cleParTraduction.keySet());

      List<ObjetEnumere> directions = new ArrayList<ObjetEnumere>();
      for (String traduction : traductionTriees)
      {
         PTDirectionEnum ptDirectionType = null;
         try
         {
            ptDirectionType = PTDirectionEnum.fromValue(cleParTraduction.get(traduction));

         }
         catch (Exception e)
         {
            logger.error(e.getMessage(), e);
         }
         // EVOCASTOR
         directions.add(new ObjetEnumere(ptDirectionType, traduction));
      }
      return directions;
   }

   public List<ObjetEnumere> getModesOfTransportEnum()
   {
      ResourceBundle rsDir = ResourceBundle.getBundle("modesOfTransport", getLocale());
      String lastValue = rsDir.getString(MODE_LAST_ENTRY);
      ArrayList<ObjetEnumere> modes = new ArrayList<ObjetEnumere>();

      Map<String, String> cleParTraduction = new Hashtable<String, String>();
      SortedSet<String> traductionTriees = new TreeSet<String>(new ComparatorSpecial(lastValue));

      Enumeration<String> rsDirEnum = rsDir.getKeys();
      while (rsDirEnum.hasMoreElements())
      {
         String cle = rsDirEnum.nextElement();
         String traduction = rsDir.getString(cle);
         if (traduction != null && !traduction.isEmpty())
         {
            cleParTraduction.put(traduction, cle);
         }
      }
      traductionTriees.addAll(cleParTraduction.keySet());

      for (String traduction : traductionTriees)
      {
         TransportModeNameEnum modeType = null;
         try
         {
            logger.debug("Traduction : " + traduction + " --- Traduction key : " + cleParTraduction.get(traduction));
            modeType = TransportModeNameEnum.fromValue(cleParTraduction.get(traduction));
            logger.debug("modeType based on traduction key : " + modeType);
         }
         catch (Exception e)
         {
            logger.error(e.getMessage(), e);
         }
         modes.add(new ObjetEnumere(modeType, traduction));
      }
      // To make the first line of the list empty in the web view
      modes.add(0, null);

      return modes;

      // Locale locale = Locale.getDefault();
      // Properties modesOfTransport = new Properties();
      // ResourceFinder resourceFinder = new ResourceFinder("");
      // String fileName = "modesOfTransport_" +
      // locale.getLanguage().toLowerCase() + ".properties";
      //
      // try
      // {
      // modesOfTransport = resourceFinder.findProperties(fileName);
      // }
      // catch (IOException exception)
      // {
      // log.debug("No properties file with name : " + fileName);
      // try
      // {
      // modesOfTransport =
      // resourceFinder.findProperties("modesOfTransport_fr.properties");
      // }
      // catch (IOException frenchException)
      // {
      // log.debug("No properties file with name : " + fileName);
      // }
      // }
      // return modesOfTransport;
   }

   public List<ObjetEnumere> getDayTypeEnum()
   {
      Map<String, String> cleParTraduction = new Hashtable<String, String>();
      ResourceBundle rsDir = ResourceBundle.getBundle("dayType", getLocale());

      Enumeration<String> rsDirEnum = rsDir.getKeys();
      while (rsDirEnum.hasMoreElements())
      {
         String cle = rsDirEnum.nextElement();
         String traduction = rsDir.getString(cle);
         if (traduction != null && !traduction.isEmpty())
         {
            cleParTraduction.put(traduction, cle);
         }
      }

      // On ne prend que les tableaux de marche de lundi à dimanche
      ObjetEnumere[] joursTypesTab = new ObjetEnumere[SUNDAY_TYPE - 1];

      for (String traduction : cleParTraduction.keySet())
      {
         DayTypeEnum dayTypeType = null;
         try
         {
            dayTypeType = DayTypeEnum.fromValue(cleParTraduction.get(traduction));

         }
         catch (Exception e)
         {
            logger.error(e.getMessage(), e);
         }
         // On ne prend que les tableaux de marche de lundi à dimanche
         // EVOCASTOR
         if (MONDAY_TYPE <= dayTypeType.ordinal() && dayTypeType.ordinal() <= SUNDAY_TYPE)
         {
            joursTypesTab[dayTypeType.ordinal() - 2] = new ObjetEnumere(dayTypeType, traduction);
         }
      }
      List<ObjetEnumere> dayType = Arrays.asList(joursTypesTab);
      return dayType;
   }

   public List<ObjetEnumere> getServiceStatusEnum()
   {
      Map<String, String> cleParTraduction = new Hashtable<String, String>();
      SortedSet<String> traductionTriees = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);

      ResourceBundle rsDir = ResourceBundle.getBundle("serviceStatus", getLocale());

      Enumeration<String> rsDirEnum = rsDir.getKeys();
      while (rsDirEnum.hasMoreElements())
      {
         String cle = rsDirEnum.nextElement();
         String traduction = rsDir.getString(cle);
         if (traduction != null && !traduction.isEmpty())
         {
            cleParTraduction.put(traduction, cle);
         }
      }
      traductionTriees.addAll(cleParTraduction.keySet());

      List<ObjetEnumere> serviceStatus = new ArrayList<ObjetEnumere>();
      for (String traduction : traductionTriees)
      {
         ServiceStatusValueEnum statutType = null;
         try
         {
            statutType = ServiceStatusValueEnum.fromValue(cleParTraduction.get(traduction));
         }
         catch (Exception e)
         {
            logger.error(e.getMessage(), e);
         }
         serviceStatus.add(new ObjetEnumere(statutType, traduction));
         // log.debug( "statutType="+statutType+" "+traduction);
      }
      return serviceStatus;
   }

   public List<ObjetEnumere> getStopAreaEnum(String authorizedTypes)
   {
      List<ObjetEnumere> toutesZonesTypes = getStopPlaceEnum();
      toutesZonesTypes.addAll(getBoardingPositionEnum());

      if (AUTHORIZEDTYPESET_ALL.equals(authorizedTypes))
      {
         return toutesZonesTypes;

      }
      else if (AUTHORIZEDTYPESET_CS.equals(authorizedTypes))
      {
         List<ObjetEnumere> l = new ArrayList<ObjetEnumere>();
         for (int i = 0; i < toutesZonesTypes.size(); i++)
         {
            ChouetteAreaEnum type = (ChouetteAreaEnum) toutesZonesTypes.get(i).getEnumeratedTypeAccess();
            if (type == ChouetteAreaEnum.STOPPLACE || type == ChouetteAreaEnum.COMMERCIALSTOPPOINT)
            {
               l.add(toutesZonesTypes.get(i));
            }
         }
         return l;

      }
      else if (AUTHORIZEDTYPESET_QB.equals(authorizedTypes))
      {
         List<ObjetEnumere> l = new ArrayList<ObjetEnumere>();
         for (int i = 0; i < toutesZonesTypes.size(); i++)
         {
            ChouetteAreaEnum type = (ChouetteAreaEnum) toutesZonesTypes.get(i).getEnumeratedTypeAccess();
            if (type == ChouetteAreaEnum.BOARDINGPOSITION || type == ChouetteAreaEnum.QUAY)
            {
               l.add(toutesZonesTypes.get(i));
            }
         }
         return l;

      }
      else if (AUTHORIZEDTYPESET_S.equals(authorizedTypes))
      {
         List<ObjetEnumere> l = new ArrayList<ObjetEnumere>();
         for (int i = 0; i < toutesZonesTypes.size(); i++)
         {
            ChouetteAreaEnum type = (ChouetteAreaEnum) toutesZonesTypes.get(i).getEnumeratedTypeAccess();
            if (type == ChouetteAreaEnum.STOPPLACE)
            {
               l.add(toutesZonesTypes.get(i));
            }
         }
         return l;

      }
      else if (AUTHORIZEDTYPESET_C.equals(authorizedTypes))
      {
         List<ObjetEnumere> l = new ArrayList<ObjetEnumere>();
         for (int i = 0; i < toutesZonesTypes.size(); i++)
         {
            ChouetteAreaEnum type = (ChouetteAreaEnum) toutesZonesTypes.get(i).getEnumeratedTypeAccess();
            if (type == ChouetteAreaEnum.COMMERCIALSTOPPOINT)
            {
               l.add(toutesZonesTypes.get(i));
            }
         }
         return l;
      }
      else if (AUTHORIZEDTYPESET_R.equals(authorizedTypes))
      {
         List<ObjetEnumere> l = getRoutingConstraintEnum();
         return l;
      }
      else
      {
         return toutesZonesTypes;
      }
   }

   public List<ObjetEnumere> getBoardingPositionEnum()
   {
      Map<String, String> cleParTraduction = new Hashtable<String, String>();
      SortedSet<String> traductionTriees = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);

      ResourceBundle rsDir = ResourceBundle.getBundle("boardingPosition", getLocale());

      Enumeration<String> rsDirEnum = rsDir.getKeys();
      while (rsDirEnum.hasMoreElements())
      {
         String cle = rsDirEnum.nextElement();
         String traduction = rsDir.getString(cle);
         if (traduction != null && !traduction.isEmpty())
         {
            cleParTraduction.put(traduction, cle);
         }
      }
      traductionTriees.addAll(cleParTraduction.keySet());

      List<ObjetEnumere> boardingPosition = new ArrayList<ObjetEnumere>();
      for (String traduction : traductionTriees)
      {
         ChouetteAreaEnum boardingPositionType = null;
         try
         {
            boardingPositionType = ChouetteAreaEnum.fromValue(cleParTraduction.get(traduction));

         }
         catch (Exception e)
         {
            logger.error(e.getMessage(), e);
         }
         boardingPosition.add(new ObjetEnumere(boardingPositionType, traduction));
         // log.debug( "statutType="+statutType+" "+traduction);
      }
      return boardingPosition;
   }

   public List<ObjetEnumere> getStopPlaceEnum()
   {
      Map<String, String> cleParTraduction = new Hashtable<String, String>();
      SortedSet<String> traductionTriees = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);

      ResourceBundle rsDir = ResourceBundle.getBundle("stopPlace", getLocale());

      Enumeration<String> rsDirEnum = rsDir.getKeys();
      while (rsDirEnum.hasMoreElements())
      {
         String cle = rsDirEnum.nextElement();
         String traduction = rsDir.getString(cle);
         if (traduction != null && !traduction.isEmpty())
         {
            cleParTraduction.put(traduction, cle);
         }
      }
      traductionTriees.addAll(cleParTraduction.keySet());

      List<ObjetEnumere> stopPlace = new ArrayList<ObjetEnumere>();
      for (String traduction : traductionTriees)
      {
         ChouetteAreaEnum stopPlaceType = null;
         try
         {
            stopPlaceType = ChouetteAreaEnum.fromValue(cleParTraduction.get(traduction));
         }
         catch (Exception e)
         {
            logger.error(e.getMessage(), e);
         }
         stopPlace.add(new ObjetEnumere(stopPlaceType, traduction));
         // log.debug( "statutType="+statutType+" "+traduction);
      }
      return stopPlace;
   }

   public List<ObjetEnumere> getRoutingConstraintEnum()
   {
      Map<String, String> cleParTraduction = new Hashtable<String, String>();
      SortedSet<String> traductionTriees = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);

      ResourceBundle rsDir = ResourceBundle.getBundle("routingConstraint", getLocale());

      Enumeration<String> rsDirEnum = rsDir.getKeys();
      while (rsDirEnum.hasMoreElements())
      {
         String cle = rsDirEnum.nextElement();
         String traduction = rsDir.getString(cle);
         if (traduction != null && !traduction.isEmpty())
         {
            cleParTraduction.put(traduction, cle);
         }
      }
      traductionTriees.addAll(cleParTraduction.keySet());

      List<ObjetEnumere> routingConstraints = new ArrayList<ObjetEnumere>();
      for (String traduction : traductionTriees)
      {
         ChouetteAreaEnum routingConstraintType = null;
         try
         {
            routingConstraintType = ChouetteAreaEnum.fromValue(cleParTraduction.get(traduction));
         }
         catch (Exception e)
         {
            logger.error(e.getMessage(), e);
         }
         routingConstraints.add(new ObjetEnumere(routingConstraintType, traduction));
         // log.debug( "statutType="+statutType+" "+traduction);
      }
      return routingConstraints;
   }

   public List<ObjetEnumere> getConnectionLinkTypeEnum()
   {
      Map<String, String> cleParTraduction = new Hashtable<String, String>();
      SortedSet<String> traductionTriees = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);

      ResourceBundle rsDir = ResourceBundle.getBundle("connectionLink", getLocale());

      Enumeration<String> rsDirEnum = rsDir.getKeys();
      while (rsDirEnum.hasMoreElements())
      {
         String cle = rsDirEnum.nextElement();
         String traduction = rsDir.getString(cle);
         if (traduction != null && !traduction.isEmpty())
         {
            cleParTraduction.put(traduction, cle);
         }
      }
      traductionTriees.addAll(cleParTraduction.keySet());

      List<ObjetEnumere> connectionLink = new ArrayList<ObjetEnumere>();
      for (String traduction : traductionTriees)
      {
         ConnectionLinkTypeEnum correspondanceType = null;
         try
         {
            correspondanceType = ConnectionLinkTypeEnum.fromValue(cleParTraduction.get(traduction));
         }
         catch (Exception e)
         {
            logger.error(e.getMessage(), e);
         }
         connectionLink.add(new ObjetEnumere(correspondanceType, traduction));
         // log.debug( "correspondanceType="+correspondanceType+" "+traduction);
      }
      return connectionLink;
   }

   private class ComparatorSpecial implements Comparator<String>
   {

      private String lastValue;

      public ComparatorSpecial(String lastValue)
      {
         if (lastValue == null)
         {
            throw new IllegalArgumentException();
         }
         this.lastValue = lastValue;
      }

      @Override
      public int compare(String o1, String o2)
      {
         if (lastValue.equals(o1))
         {
            return 1;
         }
         if (lastValue.equals(o2))
         {
            return -1;
         }
         return String.CASE_INSENSITIVE_ORDER.compare(o1, o2);
      }
   }

   /********************************************************
    * Geoportal api key *
    ********************************************************/
   public String getGeoportalApiKey()
   {
      return geoportalApiKey;
   }

   public void setGeoportalApiKey(String geoportalApiKey)
   {
      this.geoportalApiKey = geoportalApiKey;
   }

   /********************************************************
    * Base Layer Source *
    ********************************************************/
   public String getBaseLayerSource()
   {
      return baseLayerSource;
   }

   public void setBaseLayerSource(String baseLayerSource)
   {
      this.baseLayerSource = baseLayerSource;
   }

   /********************************************************
    * LambertSRID *
    ********************************************************/
   public String getLambertSRID()
   {
      return lambertSRID;
   }

   public void setLambertSRID(String lambertSRID)
   {
      this.lambertSRID = lambertSRID;
   }

   /********************************************************
    * Current Locale *
    ********************************************************/
   public String getCurrentLocale()
   {
      return this.getLocale().getLanguage();
   }

   public synchronized String getExceptionMessage(Throwable ex)
   {
      Locale defaultLocale = Locale.getDefault();
      Locale.setDefault(getLocale());
      String message = ex.getLocalizedMessage();
      Locale.setDefault(defaultLocale);
      return message;
   }
   
   /**
    * @param report
    * @param level
    */
   public void logReport(Report report, Level level)
   {
      logger.log(level,report.getLocalizedMessage());
      logItems("",report.getItems(),level);

   }

   /**
    * log report details from import plugins
    * 
    * @param indent text indentation for sub levels
    * @param items report items to log
    * @param level log level 
    */
   public void logItems(String indent, List<ReportItem> items, Level level) 
   {
      if (items == null) return;
      for (ReportItem item : items) 
      {
         logger.log(level,indent+item.getStatus().name()+" : "+item.getLocalizedMessage());
         logItems(indent+"   ",item.getItems(),level);
      }

   }

   

   
}
