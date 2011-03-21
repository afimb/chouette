package fr.certu.chouette.shell;

import chouette.schema.ChouettePTNetworkTypeType;
import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.service.database.IExportManager;
import fr.certu.chouette.service.database.ILigneManager;
import fr.certu.chouette.service.xml.ILecteurFichierXML;
import java.io.File;
import java.util.Enumeration;
import java.util.List;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.NestedRuntimeException;

public class ExportMain {
    
    private static final Logger             logger               = Logger.getLogger(fr.certu.chouette.shell.ExportMain.class);
    private static final String             CHEMIN_CONFIG_SPRING = "applicationContext.xml";
    private              IExportManager     exportManager;
    private              ILigneManager      ligneManager;
    private static       ILecteurFichierXML lecteurFichierXML;
    private static       Version            version;
    private static       File               directory;
    
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
	Enumeration loggers = LogManager.getLoggerRepository().getCurrentLoggers();
	while (loggers.hasMoreElements())
	    ((Logger)loggers.nextElement()).setLevel(Level.FATAL);
	
	try {
	    ExportMain exportMain = new ExportMain();
	    exportMain.export(args);
	}
	catch(Exception e) {
	    System.out.println("Probleme fatal : "+e.getMessage());
	    e.printStackTrace();
	}
    }
    
    private ExportMain() {
	getBeans();
    }
    
    private void export(String[] args) {
	System.out.println("Export de lignes (Chouette "+version.getVersion()+"):");
	parseArgs(args);
	
	List<Ligne> lignes = ligneManager.lire();
	int counter = 1;
	for (Ligne ligne : lignes) {
	    try {
		System.out.println("\tExport de la ligne : "+ligne.getName());
		ChouettePTNetworkTypeType ligneLue = exportManager.getExportParIdLigne(ligne.getId());
		File temp = new File(directory, "LIGNE_"+Integer.toString(counter++)+".xml");
		while (temp.exists())
		    temp = new File("LIGNE_"+Integer.toString(counter++)+".xml");
		lecteurFichierXML.ecrire(ligneLue, temp);
	    }
	    catch(Throwable e) {
		logger.fatal("\tErreur d'import de la ligne "+ligne.getName(), e);
	    }
	}
    }
    
    private void  parseArgs(String[] args) {
	if (args != null) {
	    if (args.length == 0)
		return;
	    else if (args.length == 2)
		if (args[0].equals("-d")) {
		    directory = new File(args[1]);
		    if (directory.exists()) {
			if (directory.isDirectory())
			    return;
		    }
		    else {
			directory.mkdir();
			return;
		    }
		}
	    printUsage();
	    System.exit(0);
	}
    }
    
    private void printUsage() {
	System.out.println("Option : -d directory");
    }
    
    private void getBeans() {
	ClassPathXmlApplicationContext factory = null;
	try {
	    factory = new ClassPathXmlApplicationContext(CHEMIN_CONFIG_SPRING);
	}
	catch(BeansException e) {
	    System.out.println("Echec du chargement du context \""+CHEMIN_CONFIG_SPRING+"\"\n"+e.getMessage());
	    System.exit(0);
	}
	catch(Exception e) {
	    System.out.println("Echec de creation du context \""+CHEMIN_CONFIG_SPRING+"\"\n"+e.getMessage());
	    System.exit(1);
	}
	try {
	    exportManager = (IExportManager)factory.getBean("exportManager");
	}
	catch(BeansException e) {
	    System.out.println("Echec du chargement du Bean \"exportManager\"\n"+e.getMessage());
	    System.exit(2);
	}
	catch(NestedRuntimeException e) {
	    System.out.println("Echec de creation du Bean \"exportManager\"\n"+e.getMessage());
	    System.exit(3);
	}
	try {
	    ligneManager = (ILigneManager)factory.getBean("ligneManager");
	}
	catch(BeansException e) {
	    System.out.println("Echec du chargement du Bean \"ligneManager\"\n"+e.getMessage());
	    System.exit(2);
	}
	catch(NestedRuntimeException e) {
	    System.out.println("Echec de creation du Bean \"ligneManager\"\n"+e.getMessage());
	    System.exit(3);
	}
	try {
	    lecteurFichierXML = (ILecteurFichierXML)factory.getBean("lecteurFichierXML");
	}
	catch(BeansException e) {
	    System.out.println("Echec du chargement du Bean \"lecteurFichierXML\"\n"+e.getMessage());
	    System.exit(2);
	}
	catch(NestedRuntimeException e) {
	    System.out.println("Echec de creation du Bean \"lecteurFichierXML\"\n"+e.getMessage());
	    System.exit(3);
	}
	try {
	    version = (Version)factory.getBean("version");
	}
	catch(BeansException e) {
	    System.out.println("Echec du chargement du Bean \"version\"\n"+e.getMessage());
	    System.exit(8);
	}
	catch(NestedRuntimeException e) {
	    System.out.println("Echec de creation du Bean \"version\"\n"+e.getMessage());
	    System.exit(9);
	}
    }
}
