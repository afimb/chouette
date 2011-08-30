package fr.certu.chouette.struts.upload;

import amivif.schema.RespPTLineStructTimetable;
import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.service.amivif.IAmivifAdapter;
import fr.certu.chouette.service.amivif.ILecteurAmivifXML;
import fr.certu.chouette.service.commun.CodeIncident;
import fr.certu.chouette.service.commun.ServiceException;
import fr.certu.chouette.service.fichier.IImportateur;
import fr.certu.chouette.service.identification.IIdentificationManager;
import fr.certu.chouette.service.importateur.IReducteur;
import fr.certu.chouette.service.importateur.monoitineraire.csv.IImportHorairesManager;
import fr.certu.chouette.service.importateur.monoitineraire.csv.impl.LecteurCSV;
import fr.certu.chouette.service.importateur.monoligne.ILecteurCSV;
import fr.certu.chouette.service.importateur.multilignes.ILecteurPrincipal;
import fr.certu.chouette.service.validation.commun.TypeInvalidite;
import fr.certu.chouette.service.xml.ILecteurEchangeXML;
import fr.certu.chouette.service.xml.ILecteurFichierXML;
import fr.certu.chouette.struts.GeneriqueAction;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.log4j.Logger;
import org.springframework.util.FileCopyUtils;

@SuppressWarnings("serial")
public class ImportAction extends GeneriqueAction {

    //private static final Log logger = LogFactory.getLog(ImportAction.class);
    private static final String SUCCESS_ITINERAIRE = "success_itineraire";
    private static final String INPUT_ITINERAIRE = "input_itineraire";
    private static final Logger logger = Logger.getLogger(ImportAction.class);
    private String fichierContentType;
    private File fichier;
    private boolean incremental;
    private String fichierFileName;
    private ILecteurCSV lecteurCSV;
    private ILecteurPrincipal lecteurCSVPrincipal;
    private ILecteurPrincipal lecteurCSVHastus;
    private ILecteurPrincipal lecteurCSVPegase;
    private ILecteurPrincipal lecteurXMLAltibus;
    private ILecteurEchangeXML lecteurEchangeXML;
    private ILecteurFichierXML lecteurFichierXML;
    private IImportateur importateur = null;
    private IAmivifAdapter amivifAdapter;
    private ILecteurAmivifXML lecteurAmivifXML;
    private String useAmivif;
    private String useCSVGeneric;
    private String useHastus;
    private String useAltibus;
    private String usePegase;
    private IIdentificationManager identificationManager;
    private IImportHorairesManager importHorairesManager;
    private Long idLigne;
    private String logFileName;
    private File logFile;
    private IReducteur reducteur;
    private String baseName;
    private InputStream inputStream;
    private String importHastusLogFileName;
    private String tmprep;

    public ImportAction() {
        super();
    }

    public File getImportHastusLogFile() {
        return new File(importHastusLogFileName);
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public InputStream getInputStream() throws Exception {
        return inputStream;
        //return new FileInputStream(logFile.getPath());
    }

    @Override
    public String execute() throws Exception {
        return SUCCESS;
    }

    public String reduireHastus() {
        String canonicalPath = null;
        try {
            canonicalPath = fichier.getCanonicalPath();
        } catch (IOException e) {
            StackTraceElement[] stackTraces = e.getStackTrace();
            String msg = "";
            if (stackTraces != null) {
                for (int i = 0; i < stackTraces.length; i++) {
                    msg += stackTraces[i].getClassName() + "." + stackTraces[i].getMethodName()
                            + "(" + stackTraces[i].getLineNumber() + ")\n";
                }
            }
            addActionError(msg);
            return INPUT;
        }
        String newCanonicalPath = reducteur.reduire(canonicalPath, true);
        addActionMessage(getText("message.reduce.hastus.data") + newCanonicalPath);
        return SUCCESS;
    }

    public String analyseHastus() {
        String canonicalPath = null;
        try {
            canonicalPath = fichier.getCanonicalPath();
        } catch (IOException e) {
            StackTraceElement[] stackTraces = e.getStackTrace();
            String msg = "";
            if (stackTraces != null) {
                for (int i = 0; i < stackTraces.length; i++) {
                    msg += stackTraces[i].getClassName() + "." + stackTraces[i].getMethodName()
                            + "(" + stackTraces[i].getLineNumber() + ")\n";
                }
            }
            addActionError(msg);
            return INPUT;
        }
        String newCanonicalPath = reducteur.reduire(canonicalPath, true);
        try {
            lecteurCSVHastus.lireCheminFichier(newCanonicalPath);
        } catch (ServiceException e) {
            if (CodeIncident.ERR_CSV_NON_TROUVE.equals(e.getCode())) {
                String message = getText("import.csv.fichier.introuvable");
                message += getExceptionMessage(e);
                addActionError(message);
            } else {
                String defaut = "defaut";
                List<String> args = new ArrayList<String>();
                args.add("");
                args.add("");
                args.add("");
                String message = getText("import.csv.format.ko", args.toArray(new String[0]));
                message += getExceptionMessage(e);
                addActionError(message);
            }
            return INPUT;
        }
        List<ILectureEchange> lecturesEchange = lecteurCSVHastus.getLecturesEchange();
        addActionMessage(getText("message.validate.hastus.data") + logFileName);
        return SUCCESS;
    }

    public String importAltibus() {
        try {
            lecteurXMLAltibus.lireCheminFichier(null);
        } catch (Throwable e) {
            addActionError(getExceptionMessage(e));
            return INPUT;
        }
        addActionMessage(getText("message.import.altibus.success"));
        return SUCCESS;
    }

    public String importPegase() {
        String canonicalPath = null;
        try {
            canonicalPath = fichier.getCanonicalPath();
            logger.debug("IMPORT DU FICHIER PEGASE : " + canonicalPath);
        } catch (Exception e) {
            addActionError(getExceptionMessage(e));
            return INPUT;
        }
        try {
            lecteurCSVPegase.lireCheminFichier(canonicalPath);
        } catch (Throwable e) {
            addActionError(getExceptionMessage(e));
            return INPUT;
        }
        boolean echec = false;
        List<ILectureEchange> lecturesEchange = lecteurCSVPegase.getLecturesEchange();
        for (ILectureEchange lectureEchange : lecturesEchange) {
            try {
                importateur.importer(false, lectureEchange, false);
                String[] args = new String[1];
                args[0] = lectureEchange.getLigneRegistration();
                addActionMessage(getText("message.import.pegase.line.success", args));
            } catch (Exception e) {
                echec = true;
                this.addFieldError("fieldName_1", "errorMessage 1");
                this.addFieldError("fieldName_2", "errorMessage 2");

                String[] args = new String[2];
                args[0] = lectureEchange.getLigneRegistration();
                args[1] = getExceptionMessage(e);
                addActionError(getText("message.import.pegase.line.failure", args));
                logger.error("Impossible de créer la ligne en base, msg = " + e.getMessage(), e);
            }
        }
        if (echec) {
            return INPUT;
        }
        addActionMessage(getText("message.import.pegase.lines.success"));
        return SUCCESS;
    }

    private void setLog() {
        try {
            inputStream = new FileInputStream(importHastusLogFileName);
        } catch (IOException e) {
        }
    }

    public String importHastusZip() {
        try {
            setLog();
            String result = SUCCESS;
            ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(fichier));
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            if (zipEntry == null) {
                addActionMessage(fichierFileName + " : " + getText("message.import.hastus.bad_zip"));
                //addActionMessage("Rapport : <a href://"+getImportHastusLogFileName()+">"+getImportHastusLogFileName()+"</a>");
                return INPUT;
            }
            while (zipEntry != null) {
                byte[] bytes = new byte[4096];
                int len = zipInputStream.read(bytes);
                File temp = new File(tmprep, zipEntry.getName());
                FileOutputStream fos = new FileOutputStream(temp);
                while (len > 0) {
                    fos.write(bytes, 0, len);
                    len = zipInputStream.read(bytes);
                }
                if (!result.equals(importHastus(temp))) {
                    result = INPUT;
                }
                zipEntry = zipInputStream.getNextEntry();
                temp.delete();
            }
            //addActionMessage("Rapport : <a href://"+getImportHastusLogFileName()+">"+getImportHastusLogFileName()+"</a>");
            return result;
        } catch (Exception e) {
            addActionError("PROBLEM DE LECTURE ARCHIVE : " + getExceptionMessage(e));
            setLog();
            return INPUT;
        }
    }

    public String importHastus(File file) {
        String canonicalPath = null;
        try {
            canonicalPath = file.getCanonicalPath();
        } catch (IOException e) {
            addActionError(getExceptionMessage(e));
            return INPUT;
        }


        if (incremental) {
            logger.debug("IMPORT INCREMENTAL DU FICHIER : " + canonicalPath);
        } else {
            logger.debug("IMPORT DU FICHIER : " + canonicalPath);
        }

        //String newCanonicalPath = reducteur.reduire(canonicalPath, true);
        //logger.debug("DECOMPRESSION VERS LE FICHIER : "+newCanonicalPath);
        try {
            //lecteurCSVHastus.lireCheminFichier(newCanonicalPath);
            lecteurCSVHastus.lireCheminFichier(canonicalPath);
        } catch (Exception e) {
            String message = "";
            //if (CodeIncident.ERR_CSV_NON_TROUVE.equals(e.getCode()))
            //message = getText("import.csv.fichier.introuvable");
            //else
            message = getText("import.csv.format.ko");
            message += " " + getExceptionMessage(e);
            addActionError(message);
            setLog();
            return INPUT;
        }
        boolean echec = false;
        logFileName = lecteurCSVHastus.getLogFileName();
        logFile = new File(logFileName);
        List<ILectureEchange> lecturesEchange = lecteurCSVHastus.getLecturesEchange();
        for (ILectureEchange lectureEchange : lecturesEchange) {
            try {
                importateur.importer(false, lectureEchange, incremental);
                String[] args = new String[1];
                args[0] = lectureEchange.getLigneRegistration();
                addActionMessage(getText("message.import.hastus.line.success", args));
            } //catch(ServiceException e) {
            catch (Exception e) {
                echec = true;
                this.addFieldError("fieldName_1", "errorMessage 1");
                this.addFieldError("fieldName_2", "errorMessage 2");
                String[] args = new String[2];
                args[0] = lectureEchange.getLigneRegistration();
                args[1] = getExceptionMessage(e);
                addActionError(getText("message.import.hastus.line.failure", args));
                logger.error("Impossible de créer la ligne en base, msg = " + e.getMessage(), e);
            }
        }
        if (echec) {
            setLog();
            return INPUT;
        }
        addActionMessage(getText("message.import.hastus.lines.success"));
        setLog();
        return SUCCESS;
    }

    public String importCSVGeneric() {
        String canonicalPath = null;
        try {
            canonicalPath = fichier.getCanonicalPath();
            logger.debug("Importing Generic CSV File \"" + canonicalPath + "\"");
        } catch (Exception e) {
            addActionError(getExceptionMessage(e));
            return INPUT;
        }
        try {
            lecteurCSVPrincipal.lireCheminFichier(canonicalPath);
        } catch (ServiceException e) {
            StackTraceElement[] ste = e.getStackTrace();
            String txt = "";
            if (ste != null) {
                for (int kk = 0; kk < ste.length; kk++) {
                    txt += ste[kk].getClassName() + ":" + ste[kk].getLineNumber() + "\n";
                }
            }
            if (CodeIncident.ERR_CSV_NON_TROUVE.equals(e.getCode())) {
                String message = getText("import.csv.fichier.introuvable");
                message += getExceptionMessage(e);
                addActionError(message);
            } else {
                List<String> args = new ArrayList<String>();
                args.add(canonicalPath);
                String message = getText("import.csv.format.multilines.ko", args.toArray(new String[0]));
                message += getExceptionMessage(e);
                addActionError(message);
                //e.printStackTrace();
            }
            return INPUT;
        }
        List<ILectureEchange> lecturesEchange = lecteurCSVPrincipal.getLecturesEchange();
        //Map<String, String> oldTableauxMarcheObjectIdParRef = new HashMap<String, String>();
        //Map<String, String> oldPositionsGeographiquesObjectIdParRef = new HashMap<String, String>();
        //Map<String, String> oldObjectIdParOldObjectId = new HashMap<String, String>();
        for (ILectureEchange lectureEchange : lecturesEchange) {
            try {
                /*
                List<TableauMarche> tableauxMarche = lectureEchange.getTableauxMarche();
                for (TableauMarche tableauMarche : tableauxMarche)
                if (oldTableauxMarcheObjectIdParRef.get(tableauMarche.getComment()) != null)
                tableauMarche.setObjectId(oldTableauxMarcheObjectIdParRef.get(tableauMarche.getComment()));
                lectureEchange.setTableauxMarche(tableauxMarche);
                List<PositionGeographique> positionsGeographiques = lectureEchange.getZonesCommerciales();
                for (PositionGeographique positionGeographique : positionsGeographiques) {
                if (oldPositionsGeographiquesObjectIdParRef.get(positionGeographique.getName()) != null)
                positionGeographique.setObjectId(oldPositionsGeographiquesObjectIdParRef.get(positionGeographique.getName()));
                }
                lectureEchange.setZonesCommerciales(positionsGeographiques);
                List<String> objectIdZonesGeneriques = lectureEchange.getObjectIdZonesGeneriques();
                List<String> tmpObjectIdZonesGeneriques = new ArrayList<String>();
                for (String objectId : objectIdZonesGeneriques)
                if (oldObjectIdParOldObjectId.get(objectId) == null)
                tmpObjectIdZonesGeneriques.add(objectId);
                else
                tmpObjectIdZonesGeneriques.add(oldObjectIdParOldObjectId.get(objectId));
                lectureEchange.setObjectIdZonesGeneriques(tmpObjectIdZonesGeneriques);
                Map<String, String> zoneParenteParObjectId = lectureEchange.getZoneParenteParObjectId();
                Map<String, String> newZoneParenteParObjectId = new HashMap<String,String>();
                for (String objId : zoneParenteParObjectId.keySet()) {
                String commObjectId = zoneParenteParObjectId.get(objId);
                String newCommObjectId = oldObjectIdParOldObjectId.get(commObjectId);
                if (newCommObjectId == null)
                newZoneParenteParObjectId.put(objId, commObjectId);
                else
                newZoneParenteParObjectId.put(objId, newCommObjectId);
                }
                lectureEchange.setZoneParenteParObjectId(newZoneParenteParObjectId);
                 */
                importateur.importer(false, lectureEchange);
                /*
                Map<String, String> _oldTableauxMarcheObjectIdParRef = identificationManager.getDictionaryObjectId().getTableauxMarcheObjectIdParRef();
                for (String key : _oldTableauxMarcheObjectIdParRef.keySet())
                oldTableauxMarcheObjectIdParRef.put(key, _oldTableauxMarcheObjectIdParRef.get(key));
                
                Map<String, String> _oldPositionsGeographiquesObjectIdParRef = identificationManager.getDictionaryObjectId().getPositionsGeographiquesObjectIdParRef();
                for (String key : _oldPositionsGeographiquesObjectIdParRef.keySet())
                oldPositionsGeographiquesObjectIdParRef.put(key, _oldPositionsGeographiquesObjectIdParRef.get(key));
                
                Map<String, String> _oldObjectIdParOldObjectId = identificationManager.getDictionaryObjectId().getObjectIdParOldObjectId();
                for (String key : _oldObjectIdParOldObjectId.keySet())
                oldObjectIdParOldObjectId.put(key, _oldObjectIdParOldObjectId.get(key));
                 */
            } catch (Exception e) {
                addActionMessage(getText("message.import.generical.csv.failure"));
                logger.error("Impossible de créer la ligne en base, msg = " + e.getMessage(), e);
                return INPUT;
            }
        }
        //identificationManager.getDictionaryObjectId().completion();
        addActionMessage(getText("message.import.generical.csv.success"));
        return SUCCESS;
    }

    public String importCSV() {
        String canonicalPath = null;
        //	Recuperation du chemin du fichier
        try {
            canonicalPath = fichier.getCanonicalPath();
            lecteurCSV.lireCheminFichier(canonicalPath);
        } catch (ServiceException e) {
            if (CodeIncident.ERR_CSV_NON_TROUVE.equals(e.getCode())) {
                String message = getText("import.csv.fichier.introuvable");
                message += getExceptionMessage(e);
                addActionError(message);
            } else {
                String defaut = "defaut";
                List<String> args = new ArrayList<String>();
                args.add("");
                args.add("");
                args.add("");
                String message = getText("import.csv.format.ko", args.toArray(new String[0]));
                message += getExceptionMessage(e);
                addActionError(message);
            }
            return INPUT;
        } catch (Exception e) {
            addActionError(getExceptionMessage(e));
            return INPUT;
        }
        //	Donnees convertibles en format chouette
        ILectureEchange lectureEchange = lecteurCSV.getLectureEchange();
        //	TODO : Recuperation de l'objet chouettePTNetworkType
        // ChouettePTNetworkType chouettePTNetworkType = exportManager.getExportParRegistration(lectureEchange.getReseau().getRegistrationNumber());
        //	TODO : Validation du fichier CSV
        //	Import des données CSV
        try {
            importateur.importer(true, lectureEchange);
        } catch (ServiceException e) {
            addActionMessage(getText("message.import.csv.failure"));
            logger.error("Impossible de creer la ligne en base, msg = " + e.getMessage(), e);
            return INPUT;
        }
        addActionMessage(getText("message.import.csv.success"));
        return SUCCESS;
    }

    public String importXMLs() throws Exception {
        File temp = null;
        try {
            String result = SUCCESS;
            ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(fichier));
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            if (zipEntry == null) {
                addActionError(getText("zipfile.empty"));
                return INPUT;
            }
            while (zipEntry != null) {
                byte[] bytes = new byte[4096];
                int len = zipInputStream.read(bytes);
                temp = new File(tmprep, zipEntry.getName());
                FileOutputStream fos = new FileOutputStream(temp);
                while (len > 0) {
                    fos.write(bytes, 0, len);
                    len = zipInputStream.read(bytes);
                }
                if (!result.equals(importXML(temp))) {
                    result = INPUT;
                }
                zipEntry = zipInputStream.getNextEntry();
                temp.delete();
            }
            return result;
        } catch (Exception e) {
            if (temp != null)
                addActionError(getExceptionMessage(e) + " : " + temp.getAbsolutePath());
            else
                addActionError(getExceptionMessage(e));
            return INPUT;
        }
    }

    public String importXML() throws Exception {
        try {
            return importXML(fichier);
        } catch (Exception e) {
            addActionError(getExceptionMessage(e));
            return INPUT;
        }
    }

    private String importXML(File file) throws Exception {
        String canonicalPath = file.getCanonicalPath();
        chouette.schema.ChouettePTNetworkTypeType chouettePTNetworkType = null;
        try {
            logger.debug("IMPORT XML DU FICHIER " + canonicalPath);
            chouettePTNetworkType = lecteurFichierXML.lire(canonicalPath);
            logger.debug("CREATION DU CHOUETTEPTNETWORKTYPE REUSSI");
        } catch (Exception exception) {
            gestionException(exception);
            return INPUT;
        }
        ILectureEchange lectureEchange = lecteurEchangeXML.lire(chouettePTNetworkType);
        try {
            importateur.importer(false, lectureEchange);
        } catch (ServiceException serviceException) {
            addActionError(getText("message.import.xml.failure"));
            logger.error("Impossible de créer la ligne en base, msg = " + serviceException.getMessage(), serviceException);
            return INPUT;
        }
        String[] args = new String[1];
        args[0] = lectureEchange.getLigne().getName();
        addActionMessage(getText("message.import.xml.success", args));
        return SUCCESS;
    }

    public String importAmivifXML() {
        String canonicalPath = copieTemporaire();
        //	Creation de l'objet ChouettePTNetworkType
        RespPTLineStructTimetable amivifLine = null;
        try {
            amivifLine = lecteurAmivifXML.lire(canonicalPath);
        } catch (Exception exception) {
            gestionException(exception);
            return INPUT;
        }
        //	Donnees convertibles en format chouette
        ILectureEchange lectureEchange = lecteurEchangeXML.lire(amivifAdapter.getATC(amivifLine));
        //	Import des donnees XML
        try {
            importateur.importer(false, lectureEchange);
        } catch (ServiceException serviceException) {
            addActionError(getText("message.import.amivif.xml.failure"));
            logger.error("Impossible de créer la ligne en base, msg = " + serviceException.getMessage(), serviceException);
            return INPUT;
        }
        addActionMessage(getText("message.import.amivif.xml.success"));
        return SUCCESS;
    }

    public String importHorairesItineraire() {
        LecteurCSV lecteurCsvItineraire = new LecteurCSV();
        List<String[]> donneesIn = null;

        try {
            donneesIn = lecteurCsvItineraire.lire(fichier);
        } catch (ServiceException e) {
            if (CodeIncident.ERR_CSV_NON_TROUVE.equals(e.getCode())) {
                String message = getText("import.csv.fichier.introuvable");
                message += getExceptionMessage(e);
                addActionError(message);
            } else {
                String defaut = "defaut";
                List<String> args = new ArrayList<String>();
                args.add("");
                args.add("");
                args.add("");
                String message = getText("import.csv.format.ko", args.toArray(new String[0]));
                message += getExceptionMessage(e);
                addActionError(message);
            }
            return INPUT_ITINERAIRE;
        } catch (Exception e) {
            e.printStackTrace();
            String message = getText("import.csv.fichier.introuvable");
            message += getExceptionMessage(e);
            addActionError(message);
            return INPUT_ITINERAIRE;
        }


        //	Import des données CSV
        try {
            importHorairesManager.importer(donneesIn);
        } //catch (ServiceException e) 
        catch (Exception e) {
            addActionMessage(getText("message.import.vehicleJourneyAtStop.failure"));
            logger.error("Impossible d'importer les horaires de l'itineraire, msg = " + e.getMessage(), e);
            return INPUT_ITINERAIRE;
        }
        addActionMessage(getText("message.import.vehicleJourneyAtStop.success"));
        return SUCCESS_ITINERAIRE;
    }

    private void gestionException(Exception exception) {
        if (exception instanceof ServiceException) {
            if (exception instanceof fr.certu.chouette.service.validation.commun.ValidationException) {
                fr.certu.chouette.service.validation.commun.ValidationException validationException = (fr.certu.chouette.service.validation.commun.ValidationException) exception;
                //	Liste de codes d'erreur 
                List<TypeInvalidite> codeCategories = validationException.getCategories();
                for (TypeInvalidite invalidite : codeCategories) {
                    //	Liste des messages d'erreur
                    Set<String> messages = validationException.getTridentIds(invalidite);
                    int count = 0;
                    for (String message : messages) {
                        if (count > 5) {
                            addActionError("etc...");
                            break;
                        }
                        addActionError(message);
                        //logger.error(message);
                        count++;
                    }
                }
            } else {
                ServiceException serviceException = (ServiceException) exception;
                addActionError(getText("message.import.file.exception"));
                logger.error("Impossible de recuperer le fichier, msg = " + serviceException.getMessage(), serviceException);
            }
        }
        //TODO 
    }

    private String copieTemporaire() {
        try {
            File temp = File.createTempFile("ligne", ".xml");
            FileCopyUtils.copy(fichier, temp);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        String canonicalPath = null;
        //	Recupération du chemin du fichier
        try {
            canonicalPath = fichier.getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return canonicalPath;
    }

    @Override
    public String input() throws Exception {
        return INPUT;
    }

    public void setImportateur(IImportateur importateur) {
        this.importateur = importateur;
    }

    public void setLecteurCSV(ILecteurCSV lecteurCSV) {
        this.lecteurCSV = lecteurCSV;
    }

    public void setLecteurCSVPrincipal(ILecteurPrincipal lecteurPrincipal) {
        this.lecteurCSVPrincipal = lecteurPrincipal;
    }

    public ILecteurPrincipal getLecteurCSVPrincipal() {
        return lecteurCSVPrincipal;
    }

    public void setLecteurCVSHastus(ILecteurPrincipal lecteurPrincipal) {
        this.lecteurCSVHastus = lecteurPrincipal;
    }

    public ILecteurPrincipal getLecteurCVSHastus() {
        return lecteurCSVHastus;
    }

    public void setLecteurCVSPegase(ILecteurPrincipal lecteurPrincipal) {
        this.lecteurCSVPegase = lecteurPrincipal;
    }

    public ILecteurPrincipal getLecteurCVSPegase() {
        return lecteurCSVPegase;
    }

    public void setLecteurXMLAltibus(ILecteurPrincipal lecteurXMLAltibus) {
        this.lecteurXMLAltibus = lecteurXMLAltibus;
    }

    public String getFichierFileName() {
        return fichierFileName;
    }

    public void setFichierFileName(String fichierFileName) {
        this.fichierFileName = fichierFileName;
    }

    public File getFichier() {
        return fichier;
    }

    public void setFichier(File fichier) {
        this.fichier = fichier;
    }

    public boolean isIncremental() {
        return incremental;
    }

    public void setIncremental(boolean incremental) {
        this.incremental = incremental;
    }

    public String getFichierContentType() {
        return fichierContentType;
    }

    public void setFichierContentType(String fichierContentType) {
        logger.debug(fichierContentType);
        this.fichierContentType = fichierContentType;
    }

    public void setLecteurEchangeXML(ILecteurEchangeXML lecteurEchangeXML) {
        this.lecteurEchangeXML = lecteurEchangeXML;
    }

    public void setLecteurFichierXML(ILecteurFichierXML lecteurFichierXML) {
        this.lecteurFichierXML = lecteurFichierXML;
    }

    public void setAmivifAdapter(IAmivifAdapter amivifAdapter) {
        this.amivifAdapter = amivifAdapter;
    }

    public void setLecteurAmivifXML(ILecteurAmivifXML lecteurAmivifXML) {
        this.lecteurAmivifXML = lecteurAmivifXML;
    }

    public String getUseAmivif() {
        return useAmivif;
    }

    public void setUseAmivif(String useAmivif) {
        this.useAmivif = useAmivif;
    }

    public void setUseCSVGeneric(String useCSVGeneric) {
        this.useCSVGeneric = useCSVGeneric;
    }

    public String getUseCSVGeneric() {
        return useCSVGeneric;
    }

    public void setUseHastus(String useHastus) {
        this.useHastus = useHastus;
    }

    public String getUseHastus() {
        return useHastus;
    }

    public void setImportHastusLogFileName(String importHastusLogFileName) {
        this.importHastusLogFileName = importHastusLogFileName;
    }

    public String getImportHastusLogFileName() {
        return importHastusLogFileName;
    }

    public void setUseAltibus(String useAltibus) {
        this.useAltibus = useAltibus;
    }

    public String getUseAltibus() {
        return useAltibus;
    }

    public void setUsePegase(String usePegase) {
        this.usePegase = usePegase;
    }

    public String getUsePegase() {
        return usePegase;
    }

    public void setIdentificationManager(IIdentificationManager identificationManager) {
        this.identificationManager = identificationManager;
    }

    public void setImportHorairesManager(IImportHorairesManager importHorairesManager) {
        this.importHorairesManager = importHorairesManager;
    }

    public IIdentificationManager getIdentificationManager() {
        return identificationManager;
    }

    public Long getIdLigne() {
        return idLigne;
    }

    public void setIdLigne(Long idLigne) {
        this.idLigne = idLigne;
    }

    public void setLogFileName(String logFileName) {
        this.logFileName = logFileName;
    }

    public void setReducteur(IReducteur reducteur) {
        this.reducteur = reducteur;
    }

    public void setBaseName(String baseName) {
        this.baseName = baseName;
    }

    public void setTmprep(String tmprep) {
        this.tmprep = tmprep;
    }

    public String getTmprep() {
        return tmprep;
    }
}