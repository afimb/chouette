package fr.certu.chouette.struts.export;

import fr.certu.chouette.struts.*;
import au.com.bytecode.opencsv.CSVWriter;
import chouette.schema.ChouettePTNetworkTypeType;
import chouette.schema.ChouetteRemoveLineTypeType;
import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.service.amivif.IAmivifAdapter;
import fr.certu.chouette.service.amivif.ILecteurAmivifXML;
import fr.certu.chouette.service.database.IExportManager;
import fr.certu.chouette.service.database.ILigneManager;
import fr.certu.chouette.service.database.IReseauManager;
import fr.certu.chouette.service.database.ITransporteurManager;
import fr.certu.chouette.service.exportateur.IExportCorrespondances;
import fr.certu.chouette.service.exportateur.monoitineraire.csv.IExportHorairesManager;
import fr.certu.chouette.service.exportateur.monoitineraire.csv.impl.EcrivainCSV;
import fr.certu.chouette.service.importateur.monoligne.csv.LecteurCSV;
import fr.certu.chouette.service.validation.commun.TypeInvalidite;
import fr.certu.chouette.service.validation.commun.ValidationException;
import fr.certu.chouette.service.validation.util.MainSchemaProducer;
import fr.certu.chouette.service.xml.ILecteurFichierXML;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@SuppressWarnings("serial")
public class ExportAction extends GeneriqueAction
{

  private final Log log = LogFactory.getLog(ExportAction.class);
  private ILigneManager ligneManager;
  private ITransporteurManager transporteurManager;
  private IReseauManager reseauManager;
  private Long idLigne;
  private Long idTransporteur;
  private Long idReseau;
  private Long idItineraire;
  private String nomFichier;
  private IExportManager exportManager;
  private IExportHorairesManager exportHorairesManager;
  private IExportCorrespondances exportCorrespondances;
  private IAmivifAdapter amivifAdapter;
  private ILecteurAmivifXML lecteurAmivifXML;
  private ILecteurFichierXML lecteurFichierXML;
  private LecteurCSV lecteurCSV;
  private File temp;
  private String origin;

  public String getOrigin()
  {
    return origin;
  }

  public void setOrigin(String origin)
  {
    this.origin = origin;
  }

  public InputStream getInputStream() throws Exception
  {
    return new FileInputStream(temp.getPath());
  }

  @Override
  public String execute() throws Exception
  {
    return SUCCESS;
  }

  public String exportChouetteNetwork() throws Exception
  {
    log.debug("Export Chouette : toutes les lignes du reseau : " + idReseau);
    List<Ligne> lignes = reseauManager.getLignesReseau(idReseau);
    if ((lignes == null) || (lignes.size() == 0))
    {
      addActionMessage(getText("export.network.noline"));
      return INPUT;
    }
    return exportLignes(lignes, "reseau_" + idReseau);
  }

  public String exportChouetteCompany() throws Exception
  {
    log.debug("Export Chouette : toutes les lignes du transporteur : " + idTransporteur);
    List<Ligne> lignes = transporteurManager.getLignesTransporteur(idTransporteur);
    if ((lignes == null) || (lignes.size() == 0))
    {
      addActionMessage(getText("export.company.noline"));
      return INPUT;
    }
    return exportLignes(lignes, "transporteur_" + idTransporteur);
  }

  private String exportLignes(List<Ligne> lignes, String id) throws Exception
  {
    temp = File.createTempFile("exportChouette", ".zip");
    temp.deleteOnExit();
    ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(temp));
    zipOutputStream.setLevel(ZipOutputStream.DEFLATED);
    nomFichier = "C_" + id + ".zip";
    for (Ligne ligne : lignes)
    {
      ChouettePTNetworkTypeType ligneLue = exportManager.getExportParIdLigne(ligne.getId());
      try
      {
        MainSchemaProducer mainSchemaProducer = new MainSchemaProducer();
        mainSchemaProducer.getASG(ligneLue);
      }
      catch (ValidationException e)
      {
        List<TypeInvalidite> categories = e.getCategories();
        if (categories != null)
        {
          for (TypeInvalidite category : categories)
          {
            Set<String> messages = e.getTridentIds(category);
            for (String message : messages)
            {
              log.error(message);
            }
          }
        }
        String _nomFichier = "C_INVALIDE_" + id + "_" + ligne.getId();
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
      String _nomFichier = "C_" + id + "_" + ligne.getId();
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
    return SUCCESS;
  }

  public String exportChouetteLine() throws Exception
  {
    log.debug("Export Chouette");
    // Creation d'un fichier temporaire
    temp = File.createTempFile("exportChouette", ".xml");
    // Destruction de ce fichier temporaire à la sortie du programme
    temp.deleteOnExit();    
    ChouettePTNetworkTypeType ligneLue = exportManager.getExportParIdLigne(idLigne);
    try
    {
      MainSchemaProducer mainSchemaProducer = new MainSchemaProducer();
      mainSchemaProducer.getASG(ligneLue);
    }
    catch (ValidationException e)
    {
      List<TypeInvalidite> categories = e.getCategories();
      if (categories != null)
      {
        for (TypeInvalidite category : categories)
        {
          Set<String> messages = e.getTridentIds(category);
          for (String message : messages)
          {
            log.error(message);
          }
        }
      }
      nomFichier = "C_INVALIDE_" + ligneLue.getChouetteLineDescription().getLine().getRegistration().getRegistrationNumber() + ".xml";
      lecteurFichierXML.ecrire(ligneLue, temp);
      log.debug("return result success");
      return SUCCESS;
    }
    //	Nom du fichier de sortie
    nomFichier = "C_" + ligneLue.getChouetteLineDescription().getLine().getRegistration().getRegistrationNumber() + ".xml";
    lecteurFichierXML.ecrire(ligneLue, temp);
    return SUCCESS;
  }

  public String exportCSV() throws Exception
  {
    log.debug("Export Chouette");
    // Creation d'un fichier temporaire
    temp = File.createTempFile("exportCSV", ".csv");
    // Destruction de ce fichier temporaire à la sortie du programme
    temp.deleteOnExit();
    ChouettePTNetworkTypeType ligneLue = exportManager.getExportParIdLigne(idLigne);
    if (lecteurCSV == null)
    {
      log.error("EXPORT CSV : lecteurCSV == null");
      return ERROR;
    }
    if (ligneLue == null)
    {
      log.error("EXPORT CSV : ChouettePTNetworkType null");
      return ERROR;
    }
    if (!temp.exists())
    {
      log.error("EXPORT CSV : temp n'existe pas.");
      return ERROR;
    }
    //	Nom du fichier de sortie
    nomFichier = "C_" + ligneLue.getChouetteLineDescription().getLine().getRegistration().getRegistrationNumber() + ".csv";
    log.info("EXPORT CSV: DEBUT D'ECRITURE");
    lecteurCSV.ecrire(ligneLue, temp);
    log.info("EXPORT CSV: FIN D'ECRITURE");
    return SUCCESS;
  }

  public String exportAmivif() throws Exception
  {
    // Creation d'un fichier temporaire
    temp = File.createTempFile("exportAmivif", ".xml");
    // Destruction de ce fichier temporaire à la sortie du programme
    temp.deleteOnExit();
    ChouettePTNetworkTypeType ligneLue = exportManager.getExportParIdLigne(idLigne);
    //	Nom du fichier de sortie
    nomFichier = "AMIV_S_" + ligneLue.getChouetteLineDescription().getLine().getRegistration().getRegistrationNumber() + ".xml";
    lecteurAmivifXML.ecrire(amivifAdapter.getCTA(ligneLue), temp);
    return SUCCESS;
  }

  public String exportSupprimerChouette() throws Exception
  {
    // Creation d'un fichier temporaire
    temp = File.createTempFile("exportSupprimerChouette", ".xml");
    // Destruction de ce fichier temporaire à la sortie du programme
    temp.deleteOnExit();
    ChouetteRemoveLineTypeType ligneLue = exportManager.getSuppressionParIdLigne(idLigne);
    //	Nom du fichier de sortie
    nomFichier = "S_" + ligneLue.getLine().getRegistration().getRegistrationNumber() + ".xml";
    lecteurFichierXML.ecrire(ligneLue, temp);
    ligneManager.supprimer(idLigne);
    return SUCCESS;
  }

  public String exportSupprimerAmivif() throws Exception
  {
    // Creation d'un fichier temporaire
    temp = File.createTempFile("exportSupprimerAmivif", ".xml");
    // Destruction de ce fichier temporaire à la sortie du programme
    temp.deleteOnExit();
    ChouetteRemoveLineTypeType ligneLue = exportManager.getSuppressionParIdLigne(idLigne);
    //	Nom du fichier de sortie
    nomFichier = "AMIV_D_" + ligneLue.getLine().getRegistration().getRegistrationNumber() + ".xml";
    lecteurAmivifXML.ecrire(amivifAdapter.getCTA(ligneLue), temp);
    ligneManager.supprimer(idLigne);
    return SUCCESS;
  }

  public String exportHorairesItineraire() throws Exception
  {
    // Creation d'un fichier temporaire
    temp = File.createTempFile("exportHorairesItineraireCsv", ".xml");
    // Destruction de ce fichier temporaire à la sortie du programme
    temp.deleteOnExit();
    //	Nom du fichier de sortie
    nomFichier = "HORAIRES_" + idItineraire + ".csv";
    List<String[]> donneesOut = exportHorairesManager.exporter(idItineraire);
    EcrivainCSV ec = new EcrivainCSV();
    ec.ecrire(donneesOut, temp);
    return SUCCESS;
  }

  public String exportCorrespondances() throws IOException
  {
    temp = File.createTempFile("exportCSV", ".csv");
    temp.deleteOnExit();
    nomFichier = "Correspondances" + ".csv";
    List<String[]> donneesOut = exportCorrespondances.exporter();
    CSVWriter csvWriter = new CSVWriter(new FileWriter(temp), ';');
    csvWriter.writeAll(donneesOut);
    csvWriter.close();
    return SUCCESS;
  }

  public String getNomFichier()
  {
    return nomFichier;
  }

  public IExportManager getExportManager()
  {
    return exportManager;
  }

  public void setExportManager(IExportManager exportManager)
  {
    this.exportManager = exportManager;
  }

  public void setIdLigne(Long idLigne)
  {
    this.idLigne = idLigne;
  }

  public void setIdTransporteur(Long idTransporteur)
  {
    this.idTransporteur = idTransporteur;
  }

  public void setIdReseau(Long idReseau)
  {
    this.idReseau = idReseau;
  }

  public void setIdItineraire(Long idItineraire)
  {
    this.idItineraire = idItineraire;
  }

  public void setAmivifAdapter(IAmivifAdapter amivifAdapter)
  {
    this.amivifAdapter = amivifAdapter;
  }

  public void setLigneManager(ILigneManager ligneManager)
  {
    this.ligneManager = ligneManager;
  }

  public void setTransporteurManager(ITransporteurManager transporteurManager)
  {
    this.transporteurManager = transporteurManager;
  }

  public void setReseauManager(IReseauManager reseauManager)
  {
    this.reseauManager = reseauManager;
  }

  public void setExportHorairesManager(IExportHorairesManager exportHorairesManager)
  {
    this.exportHorairesManager = exportHorairesManager;
  }

  public void setExportCorrespondances(IExportCorrespondances exportCorrespondances)
  {
    this.exportCorrespondances = exportCorrespondances;
  }

  @Override
  public String input() throws Exception
  {
    log.debug("Input export");
    return INPUT;
  }

  public List<Ligne> getLignes()
  {
    return ligneManager.lire();
  }

  public void setLecteurAmivifXML(ILecteurAmivifXML lecteurAmivifXML)
  {
    this.lecteurAmivifXML = lecteurAmivifXML;
  }

  public void setLecteurFichierXML(ILecteurFichierXML lecteurFichierXML)
  {
    this.lecteurFichierXML = lecteurFichierXML;
  }

  public void setLecteurCSV(LecteurCSV lecteurCSV)
  {
    this.lecteurCSV = lecteurCSV;
  }

  public LecteurCSV getLecteurCSV()
  {
    return lecteurCSV;
  }
  
}
