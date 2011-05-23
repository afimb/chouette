package fr.certu.chouette.struts.line;

import chouette.schema.ChouettePTNetworkTypeType;
import chouette.schema.ChouetteRemoveLineTypeType;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;

import fr.certu.chouette.critere.AndClause;
import fr.certu.chouette.critere.IClause;
import fr.certu.chouette.critere.ScalarClause;
import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.modele.Reseau;
import fr.certu.chouette.modele.Transporteur;
import fr.certu.chouette.service.commun.ServiceException;
import fr.certu.chouette.service.database.IExportManager;
import fr.certu.chouette.service.database.IExportManager.ExportMode;
import fr.certu.chouette.service.database.ILigneManager;
import fr.certu.chouette.service.database.IReseauManager;
import fr.certu.chouette.service.database.ITransporteurManager;
import fr.certu.chouette.service.validation.commun.TypeInvalidite;
import fr.certu.chouette.service.validation.commun.ValidationException;
import fr.certu.chouette.service.validation.util.MainSchemaProducer;
import fr.certu.chouette.service.xml.ILecteurFichierXML;
import fr.certu.chouette.struts.GeneriqueAction;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Set;
import org.hibernate.exception.ConstraintViolationException;

public class LineAction extends GeneriqueAction implements ModelDriven<Ligne>, Preparable {
    
    private static final long serialVersionUID = -7602165137555108469L;
    private static final Log log = LogFactory.getLog(LineAction.class);
    private Ligne lineModel = new Ligne();
    private ILigneManager ligneManager;
    private ITransporteurManager transporteurManager;
    private IReseauManager reseauManager;
    private Long idLigne;
    private String mappedRequest;
    private String useAmivif;
    private boolean detruireAvecTMs;
    private boolean detruireAvecArrets;
    private boolean detruireAvecTransporteur;
    private boolean detruireAvecReseau;
    private List<Reseau> networks;
    private List<Transporteur> companies;
    private String networkName = "";
    private String companyName = "";
    private IExportManager exportManager;
    private ILecteurFichierXML lecteurFichierXML;
    private ExportMode exportMode;
    private File temp;
    private String nomFichier;
    // Filter
    private Long filterNetworkId;
    private Long filterCompanyId;
    private String filterLineName;
    
    public Long getIdLigne() {
        return idLigne;
    }
    
    public void setIdLigne(Long idLigne) {
        this.idLigne = idLigne;
    }

    /********************************************************
     * MODEL + PREPARE *
     ********************************************************/
    public Ligne getModel() {
        return lineModel;
    }
    
    public void prepare() throws Exception {
        log.debug("Prepare with id : " + getIdLigne());
        if (getIdLigne() == null) {
            lineModel = new Ligne();
        } else {
            lineModel = ligneManager.lire(getIdLigne());
        }
        
        networks = new ArrayList<Reseau>(reseauManager.lire());
        companies = new ArrayList<Transporteur>(transporteurManager.lire());
    }

    /********************************************************
     * CRUD *
     ********************************************************/
    @SkipValidation
    public String list() {
        log.debug("List of lines");
        IClause clauseFiltre = new AndClause().add(
                ScalarClause.newEqualsClause("idReseau", filterNetworkId)).add(
                ScalarClause.newEqualsClause("idTransporteur", filterCompanyId)).add(
                ScalarClause.newIlikeClause("name", filterLineName));
        this.request.put("lignes", ligneManager.select(clauseFiltre));
        return LIST;
    }
    
    @SkipValidation
    public String add() {
        setMappedRequest(SAVE);
        return EDIT;
    }
    
    public String save() {
        Ligne ligne = getModel();
        if (ligne == null) {
            return INPUT;
        }
        if (ligneManager.nomConnu(ligne.getName())) {
            addActionMessage(getText("ligne.homonyme"));
        }
        if (ligne.getIdReseau().equals(new Long(-1))) {
            ligne.setIdReseau(null);
        }
        if (ligne.getIdTransporteur().equals(new Long(-1))) {
            ligne.setIdTransporteur(null);
        }
        try {
            ligneManager.creer(ligne);
        } catch (Exception e) {
            addActionError(getText("ligne.homonyme"));
            return INPUT;
        }
        setMappedRequest(SAVE);
        addActionMessage(getText("ligne.create.ok"));
        log.debug("Create line with id : " + getModel().getId());
        
        return REDIRECTLIST;
    }
    
    @SkipValidation
    public String edit() {
        setMappedRequest(UPDATE);
        return EDIT;
    }
    
    public String update() {
        Ligne ligne = getModel();
        if (ligne == null) {
            return INPUT;
        }
        if (ligneManager.nomConnu(ligne.getId(), ligne.getName())) {
            addActionMessage(getText("ligne.homonyme"));
        }
        if (ligne.getIdReseau().equals(new Long(-1))) {
            ligne.setIdReseau(null);
        }
        if (ligne.getIdTransporteur().equals(new Long(-1))) {
            ligne.setIdTransporteur(null);
        }
        try {
            ligneManager.modifier(ligne);
        } catch (Exception e) {
            addActionError(getText("ligne.homonyme"));
            return INPUT;
        }
        setMappedRequest(UPDATE);
        addActionMessage(getText("ligne.update.ok"));
        log.debug("Update network with id : " + getModel().getId());
        
        
        return REDIRECTLIST;
    }
    
    public String delete() {
        ligneManager.supprimer(getModel().getId(), detruireAvecTMs,
                detruireAvecArrets, detruireAvecTransporteur,
                detruireAvecReseau);
        addActionMessage(getText("ligne.delete.ok"));
        log.debug("Delete line with id : " + getModel().getId());
        
        return REDIRECTLIST;
    }
    
    @SkipValidation
    public String cancel() {
        addActionMessage(getText("ligne.cancel.ok"));
        return REDIRECTLIST;
    }
    
    @Override
    public String input() throws Exception {
        return INPUT;
    }
    
    @SkipValidation
    public String exportChouette() throws Exception {
        log.debug("Export Chouette");
        try {
            // Creation d'un fichier temporaire
            temp = File.createTempFile("exportChouette", ".xml");
            // Destruction de ce fichier temporaire à la sortie du programme
            temp.deleteOnExit();
            
            ChouettePTNetworkTypeType ligneLue = exportManager.getExportParIdLigne(idLigne, exportMode);
            try {
                MainSchemaProducer mainSchemaProducer = new MainSchemaProducer();
                mainSchemaProducer.getASG(ligneLue);

                //	Nom du fichier de sortie
                nomFichier = "C_" + exportMode + "_" + ligneLue.getChouetteLineDescription().getLine().getRegistration().getRegistrationNumber() + ".xml";
                lecteurFichierXML.ecrire(ligneLue, temp);
            } catch (ValidationException e) {
                List<TypeInvalidite> categories = e.getCategories();
                if (categories != null) {
                    for (TypeInvalidite category : categories) {
                        Set<String> messages = e.getTridentIds(category);
                        for (String message : messages) {
                            addActionError(message);
                            log.error(message);
                        }
                    }
                }
                nomFichier = "C_INVALIDE_" + exportMode + "_" + ligneLue.getChouetteLineDescription().getLine().getRegistration().getRegistrationNumber() + ".xml";
                lecteurFichierXML.ecrire(ligneLue, temp);
            }
        } catch (ServiceException exception) {
            log.error("ServiceException : " + exception.getMessage());
            addActionError(getText(exception.getCode().name()));
            return REDIRECTLIST;
        }
        return EXPORT;
    }
    
    @SkipValidation
    public String deleteChouette() throws Exception {
        try {
            // Creation d'un fichier temporaire
            temp = File.createTempFile("exportSupprimerChouette", ".xml");
            // Destruction de ce fichier temporaire à la sortie du programme
            temp.deleteOnExit();
            ChouetteRemoveLineTypeType ligneLue = exportManager.getSuppressionParIdLigne(idLigne);
            //	Nom du fichier de sortie
            nomFichier = "S_" + exportMode + "_" + ligneLue.getLine().getRegistration().getRegistrationNumber() + ".xml";
            lecteurFichierXML.ecrire(ligneLue, temp);
            ligneManager.supprimer(idLigne);
        } catch (ServiceException exception) {
            log.debug("ServiceException : " + exception.getMessage());
            addActionError(getText(exception.getCode().name()));
            return REDIRECTLIST;
        }
        
        return EXPORT;
    }

    /********************************************************
     *                    FILTER                            *
     ********************************************************/
    public Long getFilterCompanyId() {
        return filterCompanyId;
    }
    
    public void setFilterCompanyId(Long filterCompanyId) {
        this.filterCompanyId = filterCompanyId;
    }
    
    public Long getFilterNetworkId() {
        return filterNetworkId;
    }
    
    public void setFilterNetworkId(Long filterNetworkId) {
        this.filterNetworkId = filterNetworkId;
    }
    
    public String getFilterLineName() {
        return filterLineName;
    }
    
    public void setFilterLineName(String filterLineName) {
        this.filterLineName = filterLineName;
    }

    /********************************************************
     *                    MANAGER                           *
     ********************************************************/
    public void setLigneManager(ILigneManager ligneManager) {
        this.ligneManager = ligneManager;
    }
    
    public void setReseauManager(IReseauManager reseauManager) {
        this.reseauManager = reseauManager;
    }
    
    public void setTransporteurManager(ITransporteurManager transporteurManager) {
        this.transporteurManager = transporteurManager;
    }
    
    public void setExportManager(IExportManager exportManager) {
        this.exportManager = exportManager;
    }
    
    public void setLecteurFichierXML(ILecteurFichierXML lecteurFichierXML) {
        this.lecteurFichierXML = lecteurFichierXML;
    }

    /********************************************************
     * METHOD ACTION *
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
     *                   EXPORT MODE                        *
     ********************************************************/
    public ExportMode getExportMode() {
        return exportMode;
    }
    
    public void setExportMode(ExportMode exportMode) {
        this.exportMode = exportMode;
    }
    
    public InputStream getInputStream() throws Exception {
        return new FileInputStream(temp.getPath());
    }
    
    public String getNomFichier() {
        return nomFichier;
    }

    /********************************************************
     * OTHERS METHODS *
     ********************************************************/
    public String getReseau(Long networkId) {
        if (networkId != null) {
            for (Reseau network : networks) {
                log.debug("networkId : " + networkId);
                if (network.getId().equals(networkId)) {
                    networkName = network.getName();
                    break;
                }
            }
            return networkName;
        } else {
            return "";
        }
    }
    
    public void setReseaux(List<Reseau> reseaux) {
        this.networks = reseaux;
    }
    
    public List<Reseau> getReseaux() {
        return networks;
    }
    
    public String getTransporteur(Long companyId) {
        if (companyId != null) {
            for (Transporteur company : companies) {
                if (company.getId().equals(companyId)) {
                    companyName = company.getName();
                }
                
            }
            return companyName;
        } else {
            return "";
        }
    }
    
    public List<Transporteur> getTransporteurs() {
        return companies;
    }
    
    public void setTransporteurs(List<Transporteur> transporteurs) {
        this.companies = transporteurs;
    }
    
    public String getUseAmivif() {
        return useAmivif;
    }
    
    public void setUseAmivif(String useAmivif) {
        this.useAmivif = useAmivif;
    }
    
    public void setDetruireAvecTMs(boolean detruireAvecTMs) {
        this.detruireAvecTMs = detruireAvecTMs;
    }
    
    public void setDetruireAvecArrets(boolean detruireAvecArrets) {
        this.detruireAvecArrets = detruireAvecArrets;
    }
    
    public void setDetruireAvecTransporteur(boolean detruireAvecTransporteur) {
        this.detruireAvecTransporteur = detruireAvecTransporteur;
    }
    
    public void setDetruireAvecReseau(boolean detruireAvecReseau) {
        this.detruireAvecReseau = detruireAvecReseau;
    }
}
