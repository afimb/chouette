package fr.certu.chouette.struts.export;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.manager.INeptuneManager;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.user.User;
import fr.certu.chouette.plugin.exchange.ParameterValue;
import fr.certu.chouette.plugin.exchange.SimpleParameterValue;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportHolder;
import fr.certu.chouette.struts.GeneriqueAction;

@SuppressWarnings("serial")
public class ExportAction extends GeneriqueAction
{
  private final Logger log = Logger.getLogger(ExportAction.class);
  @Setter INeptuneManager<Line> lineManager; 
//  @Setter INeptuneManager<Route> routeManager; 
//  @Setter INeptuneManager<ConnectionLink> connectionLinkManager; 
  @Getter @Setter private Long idLigne;
  //@Getter @Setter private Long idTransporteur;
  //@Getter @Setter private Long idReseau;
  @Getter @Setter private Long idItineraire;
  @Getter @Setter private String nomFichier;
  private File temp;
  @Getter @Setter private String origin;
private User user = null;

  public InputStream getInputStream() throws Exception
  {
    return new FileInputStream(temp.getPath());
  }

  @Override
  public String execute() throws Exception
  {
    return SUCCESS;
  }


  public String exportCSV() throws Exception
  {
    log.debug("Export Chouette");
    // Creation d'un fichier temporaire
    temp = File.createTempFile("exportCSV", ".csv");
    // Destruction de ce fichier temporaire à la sortie du programme
    temp.deleteOnExit();
   Line line = lineManager.getById(idLigne);
     List<Line> lignes = new ArrayList<Line>();
     lignes.add(line);
    List<ParameterValue> parameters = new ArrayList<ParameterValue>();
    SimpleParameterValue outputFile = new SimpleParameterValue("outputFile");
    parameters.add(outputFile);
    
    ReportHolder report = new ReportHolder();
    outputFile.setFilepathValue(temp.getAbsolutePath());
    lineManager.doExport(user  , lignes, "CSV", parameters, report );
    if (! report.getReport().getStatus().equals(Report.STATE.OK))
    {
       if (temp.exists() )temp.delete();
       nomFichier = "C_INVALIDE_CSV_" + idLigne ;
       temp = File.createTempFile(nomFichier, ".txt");
       PrintStream stream = new PrintStream(temp);
       Report.print(stream,report.getReport(),true);

    }
    
    addActionMessage(getText("reseau.export.gtfs.ok"));
    //	Nom du fichier de sortie
    nomFichier = "C_" + line.getRegistrationNumber() + ".csv";
    return SUCCESS;
  }


//  public String exportHorairesItineraire() throws Exception
//  {
//    // Creation d'un fichier temporaire
//    temp = File.createTempFile("exportHorairesItineraireCsv", ".xml");
//    // Destruction de ce fichier temporaire à la sortie du programme
//    temp.deleteOnExit();
//    //	Nom du fichier de sortie
//    nomFichier = "HORAIRES_" + idItineraire + ".csv";
//    List<String[]> donneesOut = exportHorairesManager.exporter(idItineraire);
//    EcrivainCSV ec = new EcrivainCSV();
//    ec.ecrire(donneesOut, temp);
//    return SUCCESS;
//  }

//  public String exportCorrespondances() throws IOException
//  {
//    temp = File.createTempFile("exportCSV", ".csv");
//    temp.deleteOnExit();
//    nomFichier = "Correspondances" + ".csv";
//    List<String[]> donneesOut = exportCorrespondances.exporter();
//    CSVWriter csvWriter = new CSVWriter(new FileWriter(temp), ';');
//    csvWriter.writeAll(donneesOut);
//    csvWriter.close();
//    return SUCCESS;
//  }



  @Override
  public String input() throws Exception
  {
    log.debug("Input export");
    return INPUT;
  }

  public List<Line> getLignes()
  {
    try
   {
      return lineManager.getAll(user);
   }
   catch (ChouetteException e)
   {
     // check error
     return null;
   }
  }


  
}
