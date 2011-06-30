package fr.certu.chouette.struts.company;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.validation.SkipValidation;

import chouette.schema.ChouettePTNetworkTypeType;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.filter.DetailLevelEnum;
import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.manager.INeptuneManager;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.service.commun.ServiceException;
import fr.certu.chouette.service.database.IExportManager;
import fr.certu.chouette.service.database.IExportManager.ExportMode;
import fr.certu.chouette.service.validation.commun.TypeInvalidite;
import fr.certu.chouette.service.validation.commun.ValidationException;
import fr.certu.chouette.service.validation.util.MainSchemaProducer;
import fr.certu.chouette.service.xml.ILecteurFichierXML;
import fr.certu.chouette.struts.GeneriqueAction;

public class CompanyAction extends GeneriqueAction implements ModelDriven<Company>, Preparable {

    private static final Log log = LogFactory.getLog(CompanyAction.class);
    private Company companyModel = new Company();
    @Getter @Setter private INeptuneManager<Company> companyManager;
    @Getter @Setter INeptuneManager<Line> lineManager;
    private Long idTransporteur;
    private String mappedRequest;
    private ExportMode exportMode;
    private File temp;
    private String nomFichier;
    private IExportManager exportManager;
    private ILecteurFichierXML lecteurFichierXML;
    private DetailLevelEnum level = DetailLevelEnum.ATTRIBUTE;
    
    public Long getIdTransporteur() {
        return idTransporteur;
    }

    public void setIdTransporteur(Long idTransporteur) {
        this.idTransporteur = idTransporteur;
    }

    /********************************************************
     *                  MODEL + PREPARE                     *
     ********************************************************/
    public Company getModel() {
        return companyModel;
    }

    public void prepare() throws Exception {
        log.debug("Prepare with id : " + getIdTransporteur());
        if (getIdTransporteur() == null) {
            companyModel = new Company();
        } else {
        	Filter filter = Filter.getNewEqualsFilter("id", getIdTransporteur());
            companyModel = companyManager.get(null, filter, level);
        }
    }

    /********************************************************
     *                           CRUD                       
     * @throws ChouetteException *
     ********************************************************/
    @SkipValidation
    public String list() throws ChouetteException {
        this.request.put("transporteurs", companyManager.getAll(null));
        log.debug("List of companies");
        return LIST;
    }

    @SkipValidation
    public String add() {
        setMappedRequest(SAVE);
        return EDIT;
    }

    public String save() {
        try {
            companyManager.addNew(null, getModel());
        }
        catch(Exception e) {
            addActionMessage(getText("transporteur.homonyme"));
            return INPUT;
        }
        setMappedRequest(SAVE);
        addActionMessage(getText("transporteur.create.ok"));
        log.debug("Create company with id : " + getModel().getId());
        return REDIRECTLIST;
    }

    @SkipValidation
    public String edit() {
        setMappedRequest(UPDATE);
        return EDIT;
    }

    public String update() {
        try {
        	companyManager.update(null, getModel());
        }
        catch(Exception e) {
            addActionMessage(getText("transporteur.homonyme"));
            return INPUT;
        }
        setMappedRequest(UPDATE);
        addActionMessage(getText("transporteur.update.ok"));
        log.debug("Update company with id : " + getModel().getId());
        return REDIRECTLIST;
    }

    public String delete() throws ChouetteException {
        companyManager.remove(null, getModel(), false);
        addActionMessage(getText("transporteur.delete.ok"));
        log.debug("Delete company with id : " + getModel().getId());
        return REDIRECTLIST;
    }

    @SkipValidation
    public String cancel() {
        addActionMessage(getText("transporteur.cancel.ok"));
        return REDIRECTLIST;
    }

    @SkipValidation
    public String exportChouette() throws Exception {
        try {
            log.debug("Export Chouette : toutes les lignes du transporteur : " + idTransporteur);
            
            Filter filter = Filter.getNewEqualsFilter("company.id", idTransporteur);
			List<Line> lignes = lineManager.getAll(null, filter , level); 
            if ((lignes == null) || (lignes.size() == 0)) {
                addActionMessage(getText("export.company.noline"));
                return REDIRECTLIST;
            }
            String id = "transporteur_" + idTransporteur;
            temp = File.createTempFile("exportChouette", ".zip");
            temp.deleteOnExit();
            ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(temp));
            zipOutputStream.setLevel(ZipOutputStream.DEFLATED);
            nomFichier = "C_" + exportMode + "_" + id + ".zip";
            for (Line ligne : lignes) {
                ChouettePTNetworkTypeType ligneLue = exportManager.getExportParIdLigne(ligne.getId());
                try {
                    MainSchemaProducer mainSchemaProducer = new MainSchemaProducer();
                    mainSchemaProducer.getASG(ligneLue);
                } catch (ValidationException e) {
                    List<TypeInvalidite> categories = e.getCategories();
                    if (categories != null) {
                        for (TypeInvalidite category : categories) {
                            Set<String> messages = e.getTridentIds(category);
                            for (String message : messages) {
                                log.error(message);
                            }
                        }
                    }
                    String _nomFichier = "C_INVALIDE_" + exportMode + "_" + id + "_" + ligne.getId();
                    File _temp = File.createTempFile(_nomFichier, ".xml");
                    _temp.deleteOnExit();
                    lecteurFichierXML.ecrire(ligneLue, _temp);
                    zipOutputStream.putNextEntry(new ZipEntry(_nomFichier + ".xml"));
                    byte[] bytes = new byte[(int) _temp.length()];
                    FileInputStream fis = new FileInputStream(_temp);
                    fis.read(bytes);
                    zipOutputStream.write(bytes);
                    zipOutputStream.flush();
                    continue;
                }
                String _nomFichier = "C_" + exportMode + "_" + id + "_" + ligne.getId();
                File _temp = File.createTempFile(_nomFichier, ".xml");
                _temp.deleteOnExit();
                lecteurFichierXML.ecrire(ligneLue, _temp);
                zipOutputStream.putNextEntry(new ZipEntry(_nomFichier + ".xml"));
                byte[] bytes = new byte[(int) _temp.length()];
                FileInputStream fis = new FileInputStream(_temp);
                fis.read(bytes);
                zipOutputStream.write(bytes);
                zipOutputStream.flush();
            }
            zipOutputStream.close();
        } catch (ServiceException exception) {
            log.debug("ServiceException : " + exception.getMessage());
            addActionError(getText(exception.getCode().name()));
            return REDIRECTLIST;
        }
        return EXPORT;
    }

    /********************************************************
     *                        MANAGER                       *
     ********************************************************/

    public void setExportManager(IExportManager exportManager) {
        this.exportManager = exportManager;
    }

    public void setLecteurFichierXML(ILecteurFichierXML lecteurFichierXML) {
        this.lecteurFichierXML = lecteurFichierXML;
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
}
