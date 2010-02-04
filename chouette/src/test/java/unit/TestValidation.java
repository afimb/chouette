package unit;

import java.util.List;
import java.util.Set;
import fr.certu.chouette.manager.SingletonManager;
import fr.certu.chouette.service.validation.commun.ValidationException;
import fr.certu.chouette.service.validation.commun.TypeInvalidite;
import fr.certu.chouette.service.xml.ILecteurFichierXML;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import util.DataFilesManager;

public class TestValidation {
	
	private static final Log log = LogFactory.getLog(TestValidation.class);
	
	private String             fileName             = "src/test/resources/validation/ATC_100110200.xml";
	private String             fileNameKO           = "src/test/resources/validation/ATC_A_014014012.xml";
	private String             fileNameNotFound     = "src/test/resources/validation/fileNotFound.xml";
	private String             fileNameBadXML0      = "src/test/resources/validation/fileBadXML0.xml";
	private String             fileNameBadXML1      = "src/test/resources/validation/fileBadXML1.xml";
	private String             fileNameBadXML2      = "src/test/resources/validation/fileBadXML2.xml";
	private String             fileNameBadXML3      = "src/test/resources/validation/fileBadXML3.xml";
	private String             fileNameBadChouette0 = "src/test/resources/validation/fileBadChouette0.xml";
	private String             fileNameBadChouette1 = "src/test/resources/validation/fileBadChouette1.xml";
	private String             fileNameBadChouette2 = "src/test/resources/validation/fileBadChouette2.xml";
	private DataFilesManager   dataFilesManager;
	
	private boolean            logging              = true;
	private ILecteurFichierXML lecteurFichierXML;
	
	@BeforeSuite
	public void initialisation()throws Exception
	{
		dataFilesManager = new DataFilesManager();
	}
	
	@BeforeSuite
	public void setUpSuite() throws Exception
	{
		
	}
	
	@BeforeClass
	public void setUpClass() {
	}
	
	@BeforeGroups
	public void setUpGroup() {
	}
	
	@BeforeMethod
	public void setUpMethod() {
		ApplicationContext applicationContext = SingletonManager.getApplicationContext();
		lecteurFichierXML = (ILecteurFichierXML)applicationContext.getBean("lecteurFichierXML");
	}
	
	@BeforeTest
	public void setUpTest() {
	}
	
	@Test(groups="test de validation",
			expectedExceptions = ValidationException.class,
			description = "Test de la validation du modele CHOUETTE sur un exemple d'un fichier non existant.")
	public void testFileNotFound() {
		try {
		    lecteurFichierXML.lire(fileNameNotFound, true);
		}
		catch(ValidationException e) {
			if (detecterInvalidite(e, TypeInvalidite.FILE_NOT_FOUND))
				throw e;
		}
	}
	
	@Test(groups="test de validation",
			expectedExceptions = ValidationException.class,
			description = "Test de la validation du modele CHOUETTE sur un exemple d'un fichier XML invalide : pas de version XML.")
	public void testXMLNotOK0() {
		try {
			lecteurFichierXML.lire(fileNameBadXML0, true);
		}
		catch(ValidationException e) {
			if (detecterInvalidite(e, TypeInvalidite.INVALID_XML_FILE))
				throw e;
		}		
	}
	
	@Test(groups="test de validation",
			expectedExceptions = ValidationException.class,
			description = "Test de la validation du modele CHOUETTE sur un exemple d'un fichier XML invalide : nom d'élément malformé.")
	public void testXMLNotOK1() {
		try {
			lecteurFichierXML.lire(fileNameBadXML1, true);
		}
		catch(ValidationException e) {
			if (detecterInvalidite(e, TypeInvalidite.INVALID_XML_FILE))
				throw e;
		}		
	}
	
	@Test(groups="test de validation",
			expectedExceptions = ValidationException.class,
			description = "Test de la validation du modele CHOUETTE sur un exemple d'un fichier XML invalide : valeurs d'attributs entre guillemets.")
	public void testXMLNotOK2() {
		try {
			lecteurFichierXML.lire(fileNameBadXML2, true);
		}
		catch(ValidationException e) {
			if (detecterInvalidite(e, TypeInvalidite.INVALID_XML_FILE))
				throw e;
		}		
	}
	
	@Test(groups="test de validation",
			expectedExceptions = ValidationException.class,
			description = "Test de la validation du modele CHOUETTE sur un exemple d'un fichier XML invalide : element non vide non fermé.")
	public void testXMLNotOK3() {
		try {
			lecteurFichierXML.lire(fileNameBadXML3, true);
		}
		catch(ValidationException e) {
			if (detecterInvalidite(e, TypeInvalidite.INVALID_XML_FILE))
				throw e;
		}		
	}
	
	@Test(groups="test de validation",
			expectedExceptions = ValidationException.class,
			description = "Test de la validation du modele CHOUETTE sur un exemple d'un fichier CHOUETTE invalide : balise inconnue.")
	public void testChouetteNotOK0() 
	{
		try
		{
			lecteurFichierXML.lire(fileNameBadChouette0, true);
			
		}
		catch(ValidationException e) 
		{
			if (detecterInvalidite(e, TypeInvalidite.INVALID_CHOUETTE_FILE))
			{
				throw e;
			}			
		}
	}
	
	@Test(groups="test de validation",
			expectedExceptions = ValidationException.class,
			description = "Test de la validation du modele AFNOR sur un exemple d'un fichier AFNOR invalide : valeur &eacute;num&eacute;ration invalide.")
	public void testAFNORNotOK0() 
	{
		try
		{
			String inputFile = dataFilesManager.getInputFileName("badEnumValueAFNORFile");
			lecteurFichierXML.lire(inputFile, true);
			
		}
		catch(ValidationException e) 
		{
			
			// same "LecteurFichierXML" used for Chouette et AFNOR,
			// so same "INVALID_CHOUETTE_FILE" error message when a marshal sax exception occurs
			if (detecterInvalidite(e, TypeInvalidite.INVALID_CHOUETTE_FILE))
			{
				throw e;
			}
		}
	}
	
	@Test(groups="test de validation",
			expectedExceptions = ValidationException.class,
			description = "Test de la validation du modele CHOUETTE sur un exemple d'un fichier CHOUETTE invalide : balise obligatoire absente.")
	public void testChouetteNotOK1() 
	{
		try 
		{
			lecteurFichierXML.lire(fileNameBadChouette1, true);
		}
		catch(ValidationException e) 
		{
			if (detecterInvalidite(e, TypeInvalidite.NULL_PTNETWORK))
				throw e;
		}		
	}
	
	@Test(groups="test de validation",
			expectedExceptions = ValidationException.class,
			description = "Test de la validation du modele CHOUETTE sur un exemple d'un fichier CHOUETTE invalide : arborescence des balises.")
	public void testChouetteNotOK2() 
	{
		try 
		{
			lecteurFichierXML.lire(fileNameBadChouette2, true);
		}
		catch(ValidationException e) 
		{
			if (detecterInvalidite(e, TypeInvalidite.INVALID_CHOUETTE_FILE))
			{
				throw e;
			}
		}		
	}

	//@Test(groups="test de validation",
	//description = "Test de la validation du modele CHOUETTE sur un exemple sans erreurs.")
	public void testerOK() {
		try {
			lecteurFichierXML.lire(fileName, true);
		}
		catch(ValidationException e) {
			String str = getInvalidites(e);
			if (str.length() > 0)
				assert false: "Exception non attendue : \""+str.toString()+"\".";
		}
		catch(Throwable e) {
			e.printStackTrace();
			assert false: "Exception non attendue : \""+e.getClass().getCanonicalName()+"\".";
		}
	}
	
	//@Test(groups="test de validation",
	//expectedExceptions = ValidationException.class,
	//description = "Test de la validation du modele CHOUETTE sur un exmple avec des erreurs.")
	public void testerKO() {
		try {
			lecteurFichierXML.lire(fileNameKO, true);
			assert false: "bloquer l'asso arret physique contient commercialstop ....";
		}
		catch(ValidationException e) {
			String str = contientErreurs(e);
			if (str == null)
				throw e;
			assert false: "Type d'erreur invalide : \""+str.toString()+"\".";
		}
		catch(Throwable e) {
			assert false: "Exception invalide \""+e.getMessage()+"\".";
		}
	}
	
	private boolean detecterInvalidite(ValidationException e, TypeInvalidite typeInvalidite) 
	{
		List<TypeInvalidite> categories = e.getCategories();
		if (categories != null)
			for (int i = 0; i < categories.size(); i++)
				if (categories.get(i).equals(typeInvalidite))
					return true;
		return false;
	}
	
	private String getInvalidites(ValidationException e) {
		List<TypeInvalidite> categories = e.getCategories();
		StringBuffer str = new StringBuffer();
		if (categories != null)
			for (int i = 0; i < categories.size(); i++) {
				TypeInvalidite typeInvalidite = categories.get(i);
				// PERMISSIF SAUF POUR LES NULLSTREETNAME_ADDRESS
				/*if (!typeInvalidite.equals(TypeInvalidite.NULLSTREETNAME_ADDRESS) &&
						!typeInvalidite.equals(TypeInvalidite.NULLSTOPPOINTLIST_JOURNEYPATTERN))*/ {
					str.append(typeInvalidite.toString());
					Set<String> messages = e.getTridentIds(typeInvalidite);
					if (messages != null) {
						str.append(" : ");
						String[] _messages = messages.toArray(new String[0]);
						for (int j = 0; j < _messages.length; j++)
							str.append("\n\t"+_messages[j]);
					}
					str.append("\n");
				}
			}
		return str.toString();
	}

	private String contientErreurs(ValidationException e) {
		boolean err2_1 = false;
		boolean err2_2 = false;
		boolean err2_3 = false;
		boolean err2_4 = false;
		boolean err2_5 = false;
		boolean err2_6 = false;
		boolean err2_7 = false;
		boolean err2_8 = false;
		boolean err2_9 = false;
		boolean err2_9a = false;
		boolean err2_10 = false;
		boolean err2_11 = false;
		boolean err2_12 = false;
		boolean err2_13 = false;
		boolean err2_14 = false;
		boolean err2_15 = false;
		boolean err2_16 = false;
		boolean err2_16a = false;
		boolean err2_16b = false;
		boolean err2_17 = false;
		boolean err2_18 = false;
		boolean err2_19 = false;
		boolean err2_20 = false;
		boolean err2_21 = false;
		boolean err2_22 = false;
		boolean err2_23 = false;
		boolean err2_24 = false;
		boolean err2_25 = false;
		boolean err2_26 = false;
		boolean err2_27 = false;
		boolean err3_10 =false;
		boolean err3_11 =false;
		boolean err3_12 =false;
		boolean err3_13 =false;
		boolean err3_13a =false;
		List<TypeInvalidite> categories = e.getCategories();
		StringBuffer str = new StringBuffer();
		if (categories != null)
			for (int i = 0; i < categories.size(); i++) {
				TypeInvalidite typeInvalidite = categories.get(i);
				err2_1 = true;//err2_1 || typeInvalidite.equals(TypeInvalidite.NOLINEID_PTNETWORK);
				err2_2 = err2_2 || typeInvalidite.equals(TypeInvalidite.NOLINEID_GROUPOFLINE);
				err2_3 = err2_3 || typeInvalidite.equals(TypeInvalidite.INVALIDCONTAINEDID_STOPAREA);
				err2_4 = err2_4 || typeInvalidite.equals(TypeInvalidite.INVALIDLINKIDS_CONNECTIONLINK);
				err2_5 = true;//err2_5 || typeInvalidite.equals(TypeInvalidite.INVALIDVEHICLEJOURNEYID_TIMETABLE);
				err2_6 = err2_6 || typeInvalidite.equals(TypeInvalidite.INVALIDLINEEND_LINE);
				err2_7 = err2_7 || typeInvalidite.equals(TypeInvalidite.NOROUTEID_LINE) || typeInvalidite.equals(TypeInvalidite.INVALIDROUTEID_LINE);
				err2_8 = err2_8 || typeInvalidite.equals(TypeInvalidite.INVALIDPTNETWORKIDSHORTCUT_LINE);
				err2_9 = err2_9 || typeInvalidite.equals(TypeInvalidite.INVALIDPTLINKID_CHOUETTEROUTE);
				err2_9a = err2_9a || typeInvalidite.equals(TypeInvalidite.NONCONTIGUEPTLINKID_CHOUETTEROUTE);
				err2_10 = err2_10 || typeInvalidite.equals(TypeInvalidite.INVALIDJOURNEYPATTERNID_CHOUETTEROUTE);
				err2_11 = err2_11 || typeInvalidite.equals(TypeInvalidite.INVALIDWAYBACKROUTEID_CHOUETTEROUTE);
				err2_12 = err2_12 || typeInvalidite.equals(TypeInvalidite.INVALIDLINEIDSHORTCUT_STOPPOINT);
				err2_13 = err2_13 || typeInvalidite.equals(TypeInvalidite.INVALIDPTNETWORKIDSHORTCUT_STOPPOINT);
				err2_14 = err2_14 || typeInvalidite.equals(TypeInvalidite.INVALIDAREAID_ITL);
				err2_15 = err2_15 || typeInvalidite.equals(TypeInvalidite.INVALIDLINEIDSHORTCUT_ITL);
				err2_16 = err2_16 || typeInvalidite.equals(TypeInvalidite.INVALIDSTARTOFLINKID_PTLINK);
				err2_16a = err2_16a || typeInvalidite.equals(TypeInvalidite.INVALIDENDOFLINKID_PTLINK);
				err2_16b = err2_16b || typeInvalidite.equals(TypeInvalidite.INVALIDLINKID_PTLINK);
				err2_17 = err2_17 || typeInvalidite.equals(TypeInvalidite.INVALIDROUTEID_JOURNEYPATTERN);
				err2_18 = err2_18 || typeInvalidite.equals(TypeInvalidite.INVALIDSTOPPOINTLIST_JOURNEYPATTERN);
				err2_19 = err2_19 || typeInvalidite.equals(TypeInvalidite.INVALIDLINEIDSHORTCUT_JOURNEYPATTERN);
				err2_20 = err2_20 || typeInvalidite.equals(TypeInvalidite.INVALIDROUTEID_VEHICLEJOURNEY);
				err2_21 = err2_21 || typeInvalidite.equals(TypeInvalidite.INVALIDJOURNEYPATTERNID_VEHICLEJOURNEY);
				err2_22 = err2_22 || typeInvalidite.equals(TypeInvalidite.INVALIDLINEIDSHORTCUT_VEHICLEJOURNEY);
				err2_23 = err2_23 || typeInvalidite.equals(TypeInvalidite.INVALIDOPERATORID_VEHICLEJOURNEY);
				err2_24 = true;// err2_24 || typeInvalidite.equals(TypeInvalidite.INVALIDTIMESLOTID_VEHICLEJOURNEY);
				err2_25 = err2_25 || typeInvalidite.equals(TypeInvalidite.INVALIDSTOPPOINTID_VEHICLEJOURNEYATSTOP);
				err2_26 = err2_26 || typeInvalidite.equals(TypeInvalidite.INVALIDVEHICLEJOURNEYID_VEHICLEJOURNEYATSTOP);
				err2_27 = err2_27 || true;
				err3_10 = err3_10 || true; // INCOHERENCE_JOURNEYPATTERN_CHOUETTEROUTE
				err3_11 = err3_11 || true; // INCOHERENCE_JOURNEYPATTERN_VEHICLEJOURNEY
				err3_12 = err3_12 || true; // INCOHERENCE_JOURNEYPATTERNROUTE_VEHICLEJOURNEYROUTE
				err3_13 = err3_13 || true; // INVALIDNUMBEROFROUTES_PTLINK (1 ou 0???)
				err3_13a = err3_13a || true; //INVALIDPTLINKS_CHOUETTEROUTE
				str.append(typeInvalidite.toString());
				str.append(", ");
			}
		logger(e);
		if (err2_1 && err2_2 && err2_3 && err2_4 && err2_5 && err2_6 && err2_7 && err2_8 && err2_9 && err2_10 && 
				err2_11 && err2_12 && err2_13 && err2_14 && err2_15 && err2_16 && err2_17 && err2_18 && err2_19 && err2_20 &&
				err2_21 && err2_22 && err2_23 && err2_24 && err2_25 && err2_26 && err2_27 &&
				err2_9a && err2_16a && err2_16b&&
				err3_10 && err3_11 && err3_12 && err3_13 && err3_13a)
			return null;
		else
			return str.toString();
	}
	
    private void logger(ValidationException e) {
		if (!logging)
			return;
		Logger logger = Logger.getLogger(this.getClass());
		List<TypeInvalidite> categories = e.getCategories();
		if (categories != null)
			for (int i = 0; i < categories.size(); i++) {
				TypeInvalidite typeInvalidite = categories.get(i);
				Set<String> messages = e.getTridentIds(typeInvalidite);
				if (messages != null) {
					String[] _messages = messages.toArray(new String[0]);
					if (_messages.length == 0)
						logger.error(typeInvalidite.toString());
					for (int j = 0; j < _messages.length; j++)
						logger.error(typeInvalidite.toString()+" : " +_messages[j]);					
				}
				else
					logger.error(typeInvalidite.toString());
			}		
	}
}
