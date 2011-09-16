package fr.certu.chouette.struts.journeyPattern;

import java.sql.Time;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.manager.INeptuneManager;
import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.user.User;
import fr.certu.chouette.struts.GeneriqueAction;
import fr.certu.chouette.struts.vehicleJourneyAtStop.VehicleJourneyAtStopAction;

public class JourneyPatternAction extends GeneriqueAction implements
      ModelDriven<JourneyPattern>, Preparable
{

   private static final long               serialVersionUID = -3763356660998019932L;
   private final Log                       log              = LogFactory
                                                                  .getLog(JourneyPatternAction.class);
   @Setter
   private INeptuneManager<JourneyPattern> journeyPatternManager;
   // Identifiants
   @Getter
   @Setter
   private Long                            idMission;
   @Getter
   @Setter
   private Long                            idLigne;
   @Getter
   @Setter
   private Long                            idItineraire;
   @Getter
   @Setter
   private Long                            idTableauMarche;
   @Getter
   @Setter
   private Time                            seuilHeureDepartCourse;
   @Getter
   @Setter
   private Long                            page;
   @Getter
   private JourneyPattern                  model            = new JourneyPattern();
   @Setter
   private String                          mappedRequest;
   private User                            user             = null;

   // private static String actionMsg = null;
   // private static String actionErr = null;

   /********************************************************
    * MODEL + PREPARE *
    ********************************************************/

   public void prepare() throws Exception
   {
      log.debug("Prepare with id : " + getIdMission());
      if (getIdMission() == null)
      {
         model = new JourneyPattern();
      }
      else
      {
         model = journeyPatternManager.getById(getIdMission());
      }
   }

   /********************************************************
    * CRUD *
    ********************************************************/
   @SuppressWarnings("unchecked")
   @SkipValidation
   public String list()
   {
      try
      {
         this.request.put("missions", journeyPatternManager.getAll(user));
      }
      catch (ChouetteException e)
      {
         addActionError(e.getLocalizedMessage());
      }
      if (VehicleJourneyAtStopAction.actionMsg != null)
      {
         addActionMessage(VehicleJourneyAtStopAction.actionMsg);
         VehicleJourneyAtStopAction.actionMsg = null;
      }
      if (VehicleJourneyAtStopAction.actionErr != null)
      {
         addActionError(VehicleJourneyAtStopAction.actionErr);
         VehicleJourneyAtStopAction.actionErr = null;
      }
      log.debug("List of journeyPattern");
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
         journeyPatternManager.save(user,model,false);
         VehicleJourneyAtStopAction.actionMsg = getText("mission.create.ok");
      }
      catch (ChouetteException e)
      {
         VehicleJourneyAtStopAction.actionErr = getText(getKey(e,
               "error.mission.registration", "error.mission.create"));
      }
      setMappedRequest(SAVE);
      VehicleJourneyAtStopAction.actionMsg = getText("reseau.create.ok");
      log.debug("Create journeyPattern with id : " + getModel().getId());
      return REDIRECTLIST;
   }

   @SkipValidation
   public String edit()
   {
      setMappedRequest(UPDATE);
      return EDIT;
   }

   public String update()
   {
      try
      {
         if (model.getRegistrationNumber() != null)
         {
            if (model.getRegistrationNumber().trim().length() == 0)
            {
               model.setRegistrationNumber(null);
            }
         }
         journeyPatternManager.update(user,model);
         VehicleJourneyAtStopAction.actionMsg = getText("mission.update.ok");
      }
      catch (ChouetteException e)
      {
         VehicleJourneyAtStopAction.actionErr = getText(getKey(e,
               "error.mission.registration", "error.mission.update"));
      }
      setMappedRequest(UPDATE);
      log.debug("Update journeyPattern with id : " + getModel().getId());
      return REDIRECTLIST;
   }

   // public String delete()
   // {
   // missionManager.supprimer(getModel().getId());
   // addActionMessage(getText("mission.delete.ok"));
   // log.debug("Delete journeyPattern with id : " + getModel().getId());
   // return REDIRECTLIST;
   // }
   @SkipValidation
   public String cancel()
   {
      VehicleJourneyAtStopAction.actionMsg = getText("mission.cancel.ok");
      return REDIRECTLIST;
   }

   @Override
   @SkipValidation
   public String input() throws Exception
   {
      return REDIRECTLIST;
   }

   /********************************************************
    * MANAGER *
    ********************************************************/

   /********************************************************
    * METHOD ACTION *
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

   private String getKey(ChouetteException e, String special, String general)
   {
      // TODO ???
//      return (CodeIncident.CONTRAINTE_INVALIDE.equals(e.getCode())) ? special
//            : general;
      return general;
   }

}
