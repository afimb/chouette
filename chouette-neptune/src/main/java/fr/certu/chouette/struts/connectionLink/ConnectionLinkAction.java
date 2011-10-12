package fr.certu.chouette.struts.connectionLink;

import java.io.File;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.manager.INeptuneManager;
import fr.certu.chouette.model.neptune.AreaCentroid;
import fr.certu.chouette.model.neptune.ConnectionLink;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.type.Address;
import fr.certu.chouette.model.neptune.type.ChouetteAreaEnum;
import fr.certu.chouette.struts.GeneriqueAction;

public class ConnectionLinkAction extends GeneriqueAction implements ModelDriven<ConnectionLink>, Preparable
{

   private static final long serialVersionUID = 6964959559153714259L;
   private static final Logger log = Logger.getLogger(ConnectionLinkAction.class);
   @Getter @Setter private INeptuneManager<ConnectionLink> connectionLinkManager;
   @Getter @Setter private INeptuneManager<StopArea> stopAreaManager;
   @Getter @Setter private Long idCorrespondance;
   @Getter @Setter private StopArea criteria;
   @Getter @Setter private StopArea start;
   @Getter @Setter private StopArea end;
   @Getter @Setter private String actionSuivante;
   @Getter @Setter private Long idPositionGeographique;
   private String durationsFormat = "mm:ss";
   @Getter private ConnectionLink model = new ConnectionLink();
   @Setter private String mappedRequest;
   @Getter @Setter private String fichierContentType;
   @Getter @Setter private File fichier;
   private static String actionMsg = null;
   private static String actionErr = null;    
   private static String fieldErr = null;    



   /********************************************************
    *                  MODEL + PREPARE                     *
    ********************************************************/

   public void prepare() throws Exception
   {
      log.debug("Prepare with id : " + getIdCorrespondance());
      if (getIdCorrespondance() == null)
      {
         model = new ConnectionLink();
      }
      else
      {
         model = connectionLinkManager.getById(getIdCorrespondance());

         this.start = model.getStartOfLink();

         this.end = model.getEndOfLink();

      }
   }

   /********************************************************
    *                           CRUD                       
    * @throws ChouetteException *
    ********************************************************/
   @SuppressWarnings("unchecked")
   @SkipValidation
   public String list() throws ChouetteException
   {
      this.request.put("correspondances", connectionLinkManager.getAll(null));
      log.debug("List of connectionLinks");
      if (actionMsg != null) {
         addActionMessage(actionMsg);
         actionMsg = null;
      }
      if (fieldErr != null &&  actionErr != null) {
         addFieldError(actionErr, actionErr);
         fieldErr = null;
         actionErr = null;
      }
      else if (actionErr != null) {
         addActionError(actionErr);
         actionErr = null;
      }

      return LIST;
   }

   @SkipValidation
   public String add()
   {
      setMappedRequest(SAVE);
      return EDIT;
   }

   public String save()
   {
      try
      {
         connectionLinkManager.addNew(null,getModel());
         actionMsg = getText("connectionlink.create.ok");
      }
      catch (Exception exception)
      {
         actionErr = getText("connectionlink.create.ko");
      }
      setMappedRequest(UPDATE);
      setIdCorrespondance(model.getId());
      log.debug("Create connectionLink with id : " + getModel().getId());
      return REDIRECTEDIT;
   }

   @SkipValidation
   public String edit()
   {
      setMappedRequest(UPDATE);
      if (actionMsg != null) {
         addActionMessage(actionMsg);
         actionMsg = null;
      }
      if (fieldErr != null &&  actionErr != null) {
         addFieldError(actionErr, actionErr);
         fieldErr = null;
         actionErr = null;
      }
      else if (actionErr != null) {
         addActionError(actionErr);
         actionErr = null;
      }

      return EDIT;
   }

   public String update()
   {
      try
      {
         connectionLinkManager.update(null,getModel());
         actionMsg = getText("connectionlink.update.ok");
      }
      catch (Exception ex)
      {
         actionErr = getText("connectionlink.update.ko");
      }
      setMappedRequest(UPDATE);
      log.debug("Update connectionLink with id : " + getModel().getId());
      return REDIRECTEDIT;
   }

   public String delete() 
   {
      try {
         connectionLinkManager.remove(null,getModel(),false);
         actionMsg = getText("connectionlink.delete.ok");
         log.debug("Delete connectionLink with id : " + getModel().getId());


      } catch (ChouetteException e) {
         actionErr= getText("connectionlink.delete.ko");
      }
      return REDIRECTLIST;
   }

   @SkipValidation
   public String cancel()
   {
      actionMsg = getText("connectionlink.cancel.ok");
      return REDIRECTLIST;
   }

   @Override
   @SkipValidation
   public String input() throws Exception
   {
      return INPUT;
   }

   @SkipValidation
   public String search()
   {
      return SEARCH;
   }

   @SuppressWarnings("unchecked")
   @SkipValidation
   public String doSearch() throws Exception
   {
      Collection<String> areas = new HashSet<String>();
      Filter filter1 = null;
      if (criteria.getAreaType() != null)
      {
         areas.add(criteria.getAreaType().toString());
         filter1 = Filter.getNewEqualsFilter(StopArea.AREA_TYPE, criteria.getAreaType());
      }
      else
      {
         List<ChouetteAreaEnum> types = new ArrayList<ChouetteAreaEnum>();
         types.add(ChouetteAreaEnum.BOARDINGPOSITION);
         types.add(ChouetteAreaEnum.QUAY);
         types.add(ChouetteAreaEnum.COMMERCIALSTOPPOINT);
         types.add(ChouetteAreaEnum.STOPPLACE);
         filter1 = Filter.getNewInFilter(StopArea.AREA_TYPE, types);
      }
      Filter filter2 = null;
      if ("".equals(criteria.getName()))
      {
         filter2 = Filter.getNewIgnoreCaseLikeFilter(StopArea.NAME, criteria.getName());
      }
      Filter filter3 = null;
      if (criteria.getAreaCentroid() != null && criteria.getAreaCentroid().getAddress() != null)
      {
         if ("".equals(criteria.getAreaCentroid().getAddress().getCountryCode()))
         {
            filter3 = Filter.getNewIgnoreCaseLikeFilter(StopArea.AREACENTROID+"."+AreaCentroid.ADDRESS+"."+Address.COUNTRY_CODE,
                  criteria.getAreaCentroid().getAddress().getCountryCode());

         }
      } 
      Filter filter = Filter.getNewAndFilter(filter1,filter2,filter3);

      List<StopArea> positionGeographiquesResultat = stopAreaManager.getAll(null,filter);

      request.put("positionGeographiquesResultat", positionGeographiquesResultat);

      return SEARCH;
   }

   @SkipValidation
   public String cancelSearch()
   {
      return REDIRECTEDIT;
   }

   @SkipValidation
   public String addStart() throws Exception
   {
      if (idPositionGeographique != null && idCorrespondance != null)
      {
         model = connectionLinkManager.getById(idCorrespondance);
         StopArea startOfLink = stopAreaManager.getById(idPositionGeographique);
         model.setStartOfLink(startOfLink);
         connectionLinkManager.update(null,model);
         actionMsg = getText("connectionlink.addStart.ok");
      }
      else
         actionErr = getText("connectionlink.addStart.nok");
      return REDIRECTEDIT;
   }

   @SkipValidation
   public String addEnd() throws Exception
   {
      if (idPositionGeographique != null && idCorrespondance != null)
      {
         model = connectionLinkManager.getById(idCorrespondance);
         StopArea endOfLink = stopAreaManager.getById(idPositionGeographique);
         model.setEndOfLink(endOfLink);
         connectionLinkManager.update(null,model);
         actionMsg = getText("connectionlink.addEnd.ok");
      }
      else
         actionErr = getText("connectionlink.addEnd.nok");
      return REDIRECTEDIT;
   }

   /**
    * Connection Links Import
    * @return String result REDIRECTLIST
    */
   @SkipValidation
   public String upload()
   {
      log.debug("Import ConnectionLinks");

      /*
        // Validate File path
        String canonicalPath = null;
        try {
            canonicalPath = fichier.getCanonicalPath();
        } catch (Exception exception) {
            log.debug("Invalid path file : " + exception.getMessage());
            fieldErr = "fichier";
            actionErr = getText("invalid.path.file");
            return REDIRECTLIST;
        }

        // Connection links importation
        try {
            List<String> messages = importateurCorrespondances.lire(canonicalPath);
            if (messages != null) {
                // same error on several connectionlinks, retreive duplicates
                Map<String, String> duplicates = new HashMap<String, String>();
                if (messages.size() > 0) {
                    for (String errMsg : messages) {
                        if (!duplicates.containsKey(errMsg)) {
                            duplicates.put(errMsg, null);
                            log.debug(errMsg);
                            actionErr = errMsg;
                        }
                    }
                } else {
                    log.debug("Could not import connection links");
                    actionErr = getText("import.connectionLink.failure");
                }
            } else {
                log.debug("Import connection links success");
                actionMsg = getText("import.connectionLink.success");
            }
        } catch (ServiceException serviceException) {
            if (CodeIncident.ERR_CSV_NON_TROUVE.equals(serviceException.getCode())) {
                log.debug("Unable to find csv file : " + serviceException.getMessage());
                fieldErr = "fichier";
                actionErr = getText("import.csv.fichier.introuvable");
            } else {
                log.debug("Bad format file : " + serviceException.getMessage());
                actionErr = getText("import.csv.format.ko");
            }
        }
       */

      return REDIRECTLIST;
   }

   /********************************************************
    *                        INIT                          *
    ********************************************************/

   /********************************************************
    *                        MANAGER ET IMPORATEUR                     *
    ********************************************************/

   /********************************************************
    *                   METHODE ACTION                     *
    ********************************************************/
   // this prepares command for button on initial screen write

   // when invalid, the request parameter will restore command action
   public void setActionMethod(String method)
   {
      this.mappedRequest = method;
   }

   public String getActionMethod()
   {
      return mappedRequest;
   }

   /********************************************************
    *                   FILTER                             *
    ********************************************************/

   /********************************************************
    *                   OTHERS METHODS                     *
    ********************************************************/


   public void setStrutsOccasionalTravellerDuration(String s)
   {
      SimpleDateFormat sdfHoraire = new SimpleDateFormat(durationsFormat);
      if (s != null && s.length() > 0)
      {
         try
         {
            Date d = sdfHoraire.parse(s);
            Time t = new Time(d.getTime());
            model.setOccasionalTravellerDuration(t);
         }
         catch (Exception ex)
         {
            addActionError(getExceptionMessage(ex));
         }
      }
      else
      {
         model.setOccasionalTravellerDuration(null);
      }
   }

   public String getStrutsOccasionalTravellerDuration()
   {
      if (model != null && model.getOccasionalTravellerDuration() != null)
      {
         Date d = model.getOccasionalTravellerDuration();
         SimpleDateFormat sdfHoraire = new SimpleDateFormat(durationsFormat);
         return sdfHoraire.format(d);
      }
      else
      {
         return null;
      }
   }

   public void setStrutsMobilityRestrictedTravellerDuration(String s)
   {
      SimpleDateFormat sdfHoraire = new SimpleDateFormat(durationsFormat);
      if (s != null && s.length() > 0)
      {
         try
         {
            Date d = sdfHoraire.parse(s);
            Time t = new Time(d.getTime());
            model.setMobilityRestrictedTravellerDuration(t);
         }
         catch (Exception ex)
         {
            addActionError( getExceptionMessage(ex));
         }
      }
      else
      {
         model.setMobilityRestrictedTravellerDuration(null);
      }
   }

   public String getStrutsMobilityRestrictedTravellerDuration()
   {
      if (model != null && model.getMobilityRestrictedTravellerDuration() != null)
      {
         Date d = model.getMobilityRestrictedTravellerDuration();
         SimpleDateFormat sdfHoraire = new SimpleDateFormat(durationsFormat);
         return sdfHoraire.format(d);
      }
      else
      {
         return null;
      }
   }

   public void setStrutsFrequentTravellerDuration(String s)
   {
      SimpleDateFormat sdfHoraire = new SimpleDateFormat(durationsFormat);
      if (s != null && s.length() > 0)
      {
         try
         {
            Date d = sdfHoraire.parse(s);
            Time t = new Time(d.getTime());
            model.setFrequentTravellerDuration(t);
         }
         catch (Exception ex)
         {
            addActionError( getExceptionMessage(ex));
         }
      }
      else
      {
         model.setFrequentTravellerDuration(null);
      }
   }

   public String getStrutsFrequentTravellerDuration()
   {
      if (model != null && model.getFrequentTravellerDuration() != null)
      {
         Date d = model.getFrequentTravellerDuration();
         SimpleDateFormat sdfHoraire = new SimpleDateFormat(durationsFormat);
         return sdfHoraire.format(d);
      }
      else
      {
         return null;
      }
   }

   public void setStrutsDefaultDuration(String s)
   {
      log.debug("setStrutsDefaultDuration");
      SimpleDateFormat sdfHoraire = new SimpleDateFormat(durationsFormat);
      if (s != null && s.length() > 0)
      {
         try
         {
            Date d = sdfHoraire.parse(s);
            Time t = new Time(d.getTime());
            model.setDefaultDuration(t);
         }
         catch (Exception ex)
         {
            addActionError( getExceptionMessage(ex));
         }
      }
      else
      {
         model.setDefaultDuration(null);
      }
   }

   public String getStrutsDefaultDuration()
   {
      log.debug("getStrutsDefaultDuration");
      if (model != null && model.getDefaultDuration() != null)
      {
         Date d = model.getDefaultDuration();
         SimpleDateFormat sdfHoraire = new SimpleDateFormat(durationsFormat);
         return sdfHoraire.format(d);
      }
      else
      {
         return null;
      }
   }
}
