package unit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Set;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Unmarshaller;
import org.testng.annotations.Test;

import chouette.schema.ChouettePTNetwork;
import chouette.schema.ChouettePTNetworkTypeType;
import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.service.database.IExportManager;
import fr.certu.chouette.service.fichier.IImportateur;
import fr.certu.chouette.service.validation.commun.TypeInvalidite;
import fr.certu.chouette.service.validation.commun.ValidationException;
import fr.certu.chouette.service.xml.ILecteurEchangeXML;
import fr.certu.chouette.service.xml.ILecteurFichierXML;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;

@ContextConfiguration(locations = {"classpath:testContext.xml"})
public class ExportMissionITL extends AbstractTestNGSpringContextTests
{
	private static final Logger logger = Logger
			.getLogger(ExportMissionITL.class);
	
	private IExportManager			exportManager;
	private IImportateur			importateur;
	private ILecteurEchangeXML		lecteurEchangeXML;
	private ILecteurFichierXML 		lecteurFichierXML;
	//private String 					fileName0 			= "src/test/resources/validation/C_100100150.xml";
	private String 					fileName0 			= "src/test/resources/validation/ATC_100110200.xml";
	private String 					fileName1 			= "src/test/resources/validation/ATC_014014012.xml";
	private String 					fileName2 			= "src/test/resources/validation/ATC_100110200.xml";
	private String 					fileName3 			= "src/test/resources/validation/ATC_014014012.xml";
	private String 					fileName4 			= "src/test/resources/validation/ATC_100110200.xml";
	private String[]				fileNames			= {fileName0, fileName1, fileName2, fileName3, fileName4};
	private String 					logFile 			= "validationLog.txt";
	private String					schemaLocaction		= "http://www.trident.org/schema/trident";
		
	@BeforeMethod
	public void getBeans() {
            exportManager = (IExportManager) applicationContext.getBean("exportManager");
            importateur = (IImportateur) applicationContext.getBean("importateur");
            lecteurEchangeXML = (ILecteurEchangeXML) applicationContext.getBean("lecteurEchangeXML");
            lecteurFichierXML = (ILecteurFichierXML) applicationContext.getBean("lecteurFichierXML");
            try {
                Logger.getRootLogger().addAppender(new FileAppender(new SimpleLayout(), logFile));
            } catch (Exception e) {
                e.printStackTrace();
            }
            //Logger.getRootLogger().addAppender(new ConsoleAppender());
            Logger.getRootLogger().setLevel(Level.WARN);
            schemaLocaction += " ../../../main/castor/Chouette.xsd";
    }

	@Test(groups="test d'export avec Mission", description="export d'une ligne avec missions.")
	public void exportMission() {
		for (int i = 0; i < 1/*fileNames.length*/; i++) {
			try {
				String fileName = fileNames[i];
				ChouettePTNetwork chouettePTNetwork =  readFromFile(fileName);
				ILectureEchange lectureEchange = lecteurEchangeXML.lire(chouettePTNetwork);
				importateur.importer(false, lectureEchange);		
				String registrationNumber = chouettePTNetwork.getChouetteLineDescription().getLine().getRegistration().getRegistrationNumber();
				ChouettePTNetworkTypeType chouettePTNetworkType = exportManager.getExportParRegistration(registrationNumber);
				lecteurFichierXML.ecrire(chouettePTNetworkType, new File( "Myapres_import"+i+".xml"));
				lecteurFichierXML.ecrire(chouettePTNetwork, new File( "Myorigine"+i+".xml"));
			}
			catch(ValidationException e) {
				List<TypeInvalidite> categories = e.getCategories();
				if (categories != null)
					for (int j = 0; j < categories.size(); j++) {
						TypeInvalidite typeInvalidite = categories.get(j);
						System.out.println("TypeInvalidite : "+typeInvalidite);
						Set<String> messages = e.getTridentIds(typeInvalidite);
						if (messages != null) {
							String[] _messages = messages.toArray(new String[0]);
							for (int k = 0; k < _messages.length; k++)
								System.out.println("\t"+_messages[k]);
						}
					}
			}
		}
	}
	
	private ChouettePTNetwork readFromFile(File file) {
		Unmarshaller anUnmarshaller = new Unmarshaller(ChouettePTNetwork.class);
		anUnmarshaller.setValidation(false);
	    ChouettePTNetwork aReturnValue = null;
	    FileReader fileReader = null;
	    try {
	    	fileReader = new FileReader(file);
	    }
	    catch (FileNotFoundException e) {
	    	logger.debug( e.getMessage(), e);
	    }
	    try {
	    	aReturnValue = (ChouettePTNetwork)anUnmarshaller.unmarshal(fileReader);
		}
	    catch (org.exolab.castor.xml.ValidationException e) {
	    	logger.debug( e.getMessage(), e);
	    }
	    catch (MarshalException e) {
	    	logger.debug( e.getMessage(), e);
	    }
	    return aReturnValue;
	}
	
	private ChouettePTNetwork readFromFile(String fileName) {
		File file = null;
		try {
			file = new File(fileName);
		}
		catch (NullPointerException e) {
	    	logger.debug( e.getMessage(), e);
		}
		return readFromFile(file);
	}

	
}
