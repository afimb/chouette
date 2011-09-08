package fr.certu.chouette.struts.journeyPattern;

import com.opensymphony.xwork2.ModelDriven;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.opensymphony.xwork2.Preparable;

import fr.certu.chouette.modele.Mission;
import fr.certu.chouette.service.commun.CodeIncident;
import fr.certu.chouette.service.commun.ServiceException;
import fr.certu.chouette.service.database.IMissionManager;
import fr.certu.chouette.struts.GeneriqueAction;
import fr.certu.chouette.struts.vehicleJourneyAtStop.VehicleJourneyAtStopAction;

public class JourneyPatternAction extends GeneriqueAction implements ModelDriven<Mission>, Preparable {

    private final Log log = LogFactory.getLog(JourneyPatternAction.class);
    private IMissionManager missionManager;
    //	Identifiants
    private Long idMission;
    private Long idLigne;
    private Long idItineraire;
    private Long idTableauMarche;
    private Date seuilDateDepartCourse;
    private Long page;
    private Mission model = new Mission();
    private String mappedRequest;
    //private static String actionMsg = null;
    //private static String actionErr = null;    

    public Long getIdMission() {
        return idMission;
    }

    public void setIdMission(Long idMission) {
        this.idMission = idMission;
    }

    public Long getIdItineraire() {
        return idItineraire;
    }

    public void setIdItineraire(Long idItineraire) {
        this.idItineraire = idItineraire;
    }

    public Long getIdLigne() {
        return idLigne;
    }

    public void setIdLigne(Long idLigne) {
        this.idLigne = idLigne;
    }

    public void setIdTableauMarche(Long idTableauMarche) {
        this.idTableauMarche = idTableauMarche;
    }

    public Long getIdTableauMarche() {
        return idTableauMarche;
    }

    /********************************************************
     *                  MODEL + PREPARE                     *
     ********************************************************/
    public Mission getModel() {
        return model;
    }

    public void prepare() throws Exception {
        log.debug("Prepare with id : " + getIdMission());
        if (getIdMission() == null) {
            model = new Mission();
        } else {
            model = missionManager.lire(getIdMission());
        }
    }

    /********************************************************
     *                           CRUD                       *
     ********************************************************/
    @SkipValidation
    public String list() {
        this.request.put("missions", missionManager.lire());
        if (VehicleJourneyAtStopAction.actionMsg != null) {
            addActionMessage(VehicleJourneyAtStopAction.actionMsg);
            VehicleJourneyAtStopAction.actionMsg = null;
        }
        if (VehicleJourneyAtStopAction.actionErr != null) {
            addActionError(VehicleJourneyAtStopAction.actionErr);
            VehicleJourneyAtStopAction.actionErr = null;
        }
        log.debug("List of journeyPattern");
        return LIST;
    }

    @SkipValidation
    public String add() {
        setMappedRequest(SAVE);
        return EDIT;
    }

    public String save() {
        try {
            missionManager.creer(model);
            VehicleJourneyAtStopAction.actionMsg = getText("mission.create.ok");
        } catch (ServiceException e) {
            VehicleJourneyAtStopAction.actionErr = getText(getKey(e, "error.mission.registration", "error.mission.create"));
        }
        setMappedRequest(SAVE);
        VehicleJourneyAtStopAction.actionMsg = getText("reseau.create.ok");
        log.debug("Create journeyPattern with id : " + getModel().getId());
        return REDIRECTLIST;
    }

    @SkipValidation
    public String edit() {
        setMappedRequest(UPDATE);
        return EDIT;
    }

    public String update() {
        try {
            if (model.getRegistrationNumber() != null) {
                if (model.getRegistrationNumber().trim().length() == 0) {
                    model.setRegistrationNumber(null);
                }
            }
            missionManager.modifier(model);
            VehicleJourneyAtStopAction.actionMsg = getText("mission.update.ok");
        } catch (ServiceException e) {
            VehicleJourneyAtStopAction.actionErr = getText(getKey(e, "error.mission.registration", "error.mission.update"));
        }
        setMappedRequest(UPDATE);
        log.debug("Update journeyPattern with id : " + getModel().getId());
        return REDIRECTLIST;
    }

//  public String delete()
//  {
//    missionManager.supprimer(getModel().getId());
//    addActionMessage(getText("mission.delete.ok"));
//    log.debug("Delete journeyPattern with id : " + getModel().getId());
//    return REDIRECTLIST;
//  }
    @SkipValidation
    public String cancel() {
        VehicleJourneyAtStopAction.actionMsg = getText("mission.cancel.ok");
        return REDIRECTLIST;
    }

    @Override
    @SkipValidation
    public String input() throws Exception {
        return REDIRECTLIST;
    }

    /********************************************************
     *                        MANAGER                       *
     ********************************************************/
    public void setMissionManager(IMissionManager missionManager) {
        this.missionManager = missionManager;
    }

    /********************************************************
     *                   METHOD ACTION                      *
     ********************************************************/
    // this prepares command for button on initial screen write
    public void setMappedRequest(String actionMethod) {
        this.mappedRequest = actionMethod;
    }

    // when invalid, the request parameter will restore command action
    public void setActionMethod(String method) {
        this.mappedRequest = method;
    }

    public String getActionMethod() {
        return mappedRequest;
    }

    private String getKey(ServiceException e, String special, String general) {
        return (CodeIncident.CONTRAINTE_INVALIDE.equals(e.getCode())) ? special : general;
    }

    public Date getSeuilDateDepartCourse() {
        return seuilDateDepartCourse;
    }

    public void setSeuilDateDepartCourse(Date seuilDateDepartCourse) {
        this.seuilDateDepartCourse = seuilDateDepartCourse;
    }

    public Long getPage() {
        return page;
    }

    public void setPage(Long page) {
        this.page = page;
    }
}
