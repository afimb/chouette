package fr.certu.chouette.struts.connectionLink;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;

import fr.certu.chouette.critere.AndClause;
import fr.certu.chouette.critere.ScalarClause;
import fr.certu.chouette.critere.VectorClause;
import fr.certu.chouette.modele.Correspondance;
import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.service.commun.CodeIncident;
import fr.certu.chouette.service.commun.ServiceException;
import fr.certu.chouette.service.database.ICorrespondanceManager;
import fr.certu.chouette.service.database.IPositionGeographiqueManager;
import fr.certu.chouette.service.importateur.IImportCorrespondances;
import fr.certu.chouette.struts.GeneriqueAction;
import fr.certu.chouette.struts.enumeration.ObjetEnumere;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ConnectionLinkAction extends GeneriqueAction implements ModelDriven<Correspondance>, Preparable {

    private static final long serialVersionUID = 6964959559153714259L;
    private static final Log log = LogFactory.getLog(ConnectionLinkAction.class);
    private ICorrespondanceManager correspondanceManager;
    private IPositionGeographiqueManager positionGeographiqueManager;
    private String useHastus;
    private Long idCorrespondance;
    private PositionGeographique criteria;
    private PositionGeographique start;
    private PositionGeographique end;
    private String actionSuivante;
    private Long idPositionGeographique;
    private String durationsFormat = "mm:ss";
    private Correspondance model = new Correspondance();
    private String mappedRequest;
    private String fichierContentType;
    private File fichier;
    private IImportCorrespondances importateurCorrespondances;
    private static String actionMsg = null;
    private static String actionErr = null;    
    private static String fieldErr = null;    

    public Long getIdCorrespondance() {
        return idCorrespondance;
    }

    public void setIdCorrespondance(Long idCorrespondance) {
        this.idCorrespondance = idCorrespondance;
    }

    public Long getIdPositionGeographique() {
        return idPositionGeographique;
    }

    public void setIdPositionGeographique(Long idPositionGeographique) {
        this.idPositionGeographique = idPositionGeographique;
    }

    /********************************************************
     *                  MODEL + PREPARE                     *
     ********************************************************/
    public Correspondance getModel() {
        return model;
    }

    public void prepare() throws Exception {
        log.debug("Prepare with id : " + getIdCorrespondance());
        if (getIdCorrespondance() == null) {
            model = new Correspondance();
        } else {
            model = correspondanceManager.lire(getIdCorrespondance());
            if (model.getIdDepart() != null) {
                this.start = positionGeographiqueManager.lire(model.getIdDepart());
            }
            if (model.getIdArrivee() != null) {
                this.end = positionGeographiqueManager.lire(model.getIdArrivee());
            }
        }
    }

    /********************************************************
     *                           CRUD                       *
     ********************************************************/
    @SkipValidation
    public String list() {
        this.request.put("correspondances", correspondanceManager.lire());
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
    public String add() {
        setMappedRequest(SAVE);
        return EDIT;
    }

    public String save() {
        try {
            correspondanceManager.creer(getModel());
            actionMsg = getText("connectionlink.create.ok");
        } catch (Exception exception) {
            actionErr = getText("connectionlink.create.ko");
        }
        setMappedRequest(UPDATE);
        setIdCorrespondance(model.getId());
        log.debug("Create connectionLink with id : " + getModel().getId());
        return REDIRECTEDIT;
    }

    @SkipValidation
    public String edit() {
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

    public String update() {
        try {
            correspondanceManager.modifier(getModel());
            actionMsg = getText("connectionlink.update.ok");
        } catch (Exception ex) {
            actionErr = getText("connectionlink.update.ko");
        }
        setMappedRequest(UPDATE);
        log.debug("Update connectionLink with id : " + getModel().getId());
        return REDIRECTEDIT;
    }

    public String delete() {
        correspondanceManager.supprimer(getModel().getId());
        actionMsg = getText("connectionlink.delete.ok");
        log.debug("Delete connectionLink with id : " + getModel().getId());
        return REDIRECTLIST;
    }

    @SkipValidation
    public String cancel() {
        actionMsg = getText("connectionlink.cancel.ok");
        return REDIRECTLIST;
    }

    @Override
    @SkipValidation
    public String input() throws Exception {
        return INPUT;
    }

    @SkipValidation
    public String search() {
        return SEARCH;
    }

    @SkipValidation
    public String doSearch() {
        Collection<String> areas = new HashSet<String>();
        if (criteria.getAreaType() != null) {
            areas.add(criteria.getAreaType().toString());
        } else {
            List<ObjetEnumere> areaEnumerations = getStopAreaEnum("");
            for (ObjetEnumere enumeration : areaEnumerations) {
                areas.add(enumeration.getEnumeratedTypeAccess().toString());
            }
        }
        if ("".equals(criteria.getName())) {
            criteria.setName(null);
        }
        if ("".equals(criteria.getCountryCode())) {
            criteria.setCountryCode(null);
        }
        List<PositionGeographique> positionGeographiquesResultat = positionGeographiqueManager.select(new AndClause().add(ScalarClause.newIlikeClause("name", criteria.getName())).
                add(ScalarClause.newIlikeClause("countryCode", criteria.getCountryCode())).
                add(VectorClause.newInClause("areaType", areas)));

        request.put("positionGeographiquesResultat", positionGeographiquesResultat);

        return SEARCH;
    }

    @SkipValidation
    public String cancelSearch() {
        return REDIRECTEDIT;
    }

    @SkipValidation
    public String addStart() {
        if (idPositionGeographique != null && idCorrespondance != null) {
            model = correspondanceManager.lire(idCorrespondance);
            model.setIdDepart(idPositionGeographique);
            correspondanceManager.modifier(model);
            actionMsg = getText("connectionlink.addStart.ok");
        }
        else
            actionErr = getText("connectionlink.addStart.nok");

        return REDIRECTEDIT;
    }

    @SkipValidation
    public String addEnd() {
        if (idPositionGeographique != null && idCorrespondance != null) {
            model = correspondanceManager.lire(idCorrespondance);
            model.setIdArrivee(idPositionGeographique);
            correspondanceManager.modifier(model);
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
    public String upload() {
        log.debug("Import ConnectionLinks");

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

        return REDIRECTLIST;
    }

    /********************************************************
     *                        INIT                          *
     ********************************************************/
    public void setUseHastus(String useHastus) {
        this.useHastus = useHastus;
    }

    public String getUseHastus() {
        return useHastus;
    }

    /********************************************************
     *                        MANAGER ET IMPORATEUR                     *
     ********************************************************/
    public void setPositionGeographiqueManager(IPositionGeographiqueManager positionGeographiqueManager) {
        this.positionGeographiqueManager = positionGeographiqueManager;
    }

    public void setCorrespondanceManager(ICorrespondanceManager correspondanceManager) {
        this.correspondanceManager = correspondanceManager;
    }

    public void setImportCorrespondances(IImportCorrespondances importateurCorrespondances) {
        this.importateurCorrespondances = importateurCorrespondances;
    }

    /********************************************************
     *                   METHODE ACTION                     *
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

    /********************************************************
     *                   FILTER                             *
     ********************************************************/
    public PositionGeographique getCriteria() {
        return criteria;
    }

    public void setCriteria(PositionGeographique criteria) {
        this.criteria = criteria;
    }

    /********************************************************
     *                   OTHERS METHODS                     *
     ********************************************************/
    public PositionGeographique getStart() {
        return start;
    }

    public void setStart(PositionGeographique start) {
        this.start = start;
    }

    public PositionGeographique getEnd() {
        return end;
    }

    public void setEnd(PositionGeographique end) {
        this.end = end;
    }

    public String getActionSuivante() {
        return actionSuivante;
    }

    public void setActionSuivante(String actionSuivante) {
        this.actionSuivante = actionSuivante;
    }

    public String getFichierContentType() {
        return fichierContentType;
    }

    public void setFichierContentType(String fichierContentType) {
        this.fichierContentType = fichierContentType;
    }

    public File getFichier() {
        return fichier;
    }

    public void setFichier(File fichier) {
        this.fichier = fichier;
    }

    public void setStrutsOccasionalTravellerDuration(String s) {
        SimpleDateFormat sdfHoraire = new SimpleDateFormat(durationsFormat);
        if (s != null && s.length() > 0) {
            try {
                Date d = sdfHoraire.parse(s);
                model.setOccasionalTravellerDuration(d);
            } catch (Exception ex) {
                addActionError(getExceptionMessage(ex));
            }
        } else {
            model.setOccasionalTravellerDuration(null);
        }
    }

    public String getStrutsOccasionalTravellerDuration() {
        if (model != null && model.getOccasionalTravellerDuration() != null) {
            Date d = model.getOccasionalTravellerDuration();
            SimpleDateFormat sdfHoraire = new SimpleDateFormat(durationsFormat);
            return sdfHoraire.format(d);
        } else {
            return null;
        }
    }

    public void setStrutsMobilityRestrictedTravellerDuration(String s) {
        SimpleDateFormat sdfHoraire = new SimpleDateFormat(durationsFormat);
        if (s != null && s.length() > 0) {
            try {
                Date d = sdfHoraire.parse(s);
                model.setMobilityRestrictedTravellerDuration(d);
            } catch (Exception ex) {
                addActionError(getExceptionMessage(ex));
            }
        } else {
            model.setMobilityRestrictedTravellerDuration(null);
        }
    }

    public String getStrutsMobilityRestrictedTravellerDuration() {
        if (model != null && model.getMobilityRestrictedTravellerDuration() != null) {
            Date d = model.getMobilityRestrictedTravellerDuration();
            SimpleDateFormat sdfHoraire = new SimpleDateFormat(durationsFormat);
            return sdfHoraire.format(d);
        } else {
            return null;
        }
    }

    public void setStrutsFrequentTravellerDuration(String s) {
        SimpleDateFormat sdfHoraire = new SimpleDateFormat(durationsFormat);
        if (s != null && s.length() > 0) {
            try {
                Date d = sdfHoraire.parse(s);
                model.setFrequentTravellerDuration(d);
            } catch (Exception ex) {
                addActionError(getExceptionMessage(ex));
            }
        } else {
            model.setFrequentTravellerDuration(null);
        }
    }

    public String getStrutsFrequentTravellerDuration() {
        if (model != null && model.getFrequentTravellerDuration() != null) {
            Date d = model.getFrequentTravellerDuration();
            SimpleDateFormat sdfHoraire = new SimpleDateFormat(durationsFormat);
            return sdfHoraire.format(d);
        } else {
            return null;
        }
    }

    public void setStrutsDefaultDuration(String s) {
        log.debug("setStrutsDefaultDuration");
        SimpleDateFormat sdfHoraire = new SimpleDateFormat(durationsFormat);
        if (s != null && s.length() > 0) {
            try {
                Date d = sdfHoraire.parse(s);
                model.setDefaultDuration(d);
            } catch (Exception ex) {
                addActionError(getExceptionMessage(ex));
            }
        } else {
            model.setDefaultDuration(null);
        }
    }

    public String getStrutsDefaultDuration() {
        log.debug("getStrutsDefaultDuration");
        if (model != null && model.getDefaultDuration() != null) {
            Date d = model.getDefaultDuration();
            SimpleDateFormat sdfHoraire = new SimpleDateFormat(durationsFormat);
            return sdfHoraire.format(d);
        } else {
            return null;
        }
    }
}
