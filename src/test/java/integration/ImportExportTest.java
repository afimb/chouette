package integration;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import amivif.schema.RespPTLineStructTimetableType;
import chouette.schema.ChouettePTNetworkType;
import chouette.schema.ChouetteRoute;
import chouette.schema.VehicleJourney;
import chouette.schema.VehicleJourneyAtStop;
import fr.certu.chouette.manager.SingletonManager;
import fr.certu.chouette.service.amivif.IAmivifAdapter;
import fr.certu.chouette.service.amivif.ILecteurAmivifXML;
import fr.certu.chouette.service.database.IExportManager;
import fr.certu.chouette.service.fichier.IImportateur;
import fr.certu.chouette.service.validation.commun.TypeInvalidite;
import fr.certu.chouette.service.validation.util.MainSchemaProducer;
import fr.certu.chouette.service.xml.ILecteurEchangeXML;
import fr.certu.chouette.service.xml.ILecteurFichierXML;

public class ImportExportTest {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger
			.getLogger(ImportExportTest.class);

	private IExportManager exportManager;
	private IImportateur importateur;
	private IAmivifAdapter amivifAdapter;
	private ILecteurFichierXML lecteurFichierXML;	
	private ILecteurAmivifXML lecteurAmivifXML;
	private ILecteurEchangeXML lecteurEchangeXML;
	
	@BeforeSuite
	public void initialisation()
	{
		ApplicationContext applicationContext = SingletonManager.getApplicationContext();

		exportManager = ( IExportManager)applicationContext.getBean( "exportManager");
		importateur = ( IImportateur)applicationContext.getBean( "importateur");
		amivifAdapter = ( IAmivifAdapter)applicationContext.getBean( "amivifAdapter");
		lecteurAmivifXML = ( ILecteurAmivifXML)applicationContext.getBean( "lecteurAmivifXML");
		lecteurFichierXML = ( ILecteurFichierXML)applicationContext.getBean( "lecteurFichierXML");
		lecteurEchangeXML = ( ILecteurEchangeXML)applicationContext.getBean( "lecteurEchangeXML");
	}
	
	
	@Test(groups="tests unitaires", description="processus d'import AMIVIF XML - export Chouette XML - comparaison")
	public void lot_importAmivif_exportChouette() throws FileNotFoundException, MarshalException, ValidationException
	{
		String repertoire = "src/test/resources";
		Set<String> fichiers = new HashSet<String>();
		
		File rep = new File( repertoire);
		String[] fics = rep.list();
		
		for (int i = 0; i < fics.length; i++) 
		{
			String nom = fics[ i];
			if ( nom.matches( "AMIV_S_\\d+.xml"))
				fichiers.add( repertoire+File.separator+nom);
		}
		
		for (String fichier : fichiers) 
		{
			importerAmivif_exportChouette( fichier);
		}
	}
	
	private void importerAmivif_exportChouette( String fichier) {
		RespPTLineStructTimetableType respPTLineStructTimetable = lecteurAmivifXML.lire( fichier);
		String registrationNumber = respPTLineStructTimetable.getLine().getRegistration().getRegistrationNumber();

		// conversion Amivif -> Chouette
		ChouettePTNetworkType chouetteXML = amivifAdapter.getATC( respPTLineStructTimetable);
		
		// validation format Chouette
		validation_chouette_xml( chouetteXML);
		
		importateur.importer( false, lecteurEchangeXML.lire( chouetteXML));
		
		ChouettePTNetworkType chouetteXMLExporte = exportManager.getExportParRegistration(registrationNumber);
		
		comparaison( chouetteXML, chouetteXMLExporte);
	}
	
	private void comparaison( ChouettePTNetworkType initial, ChouettePTNetworkType exporte)
	{
		assert initial.getPTNetwork().getRegistration().getRegistrationNumber().equals( exporte.getPTNetwork().getRegistration().getRegistrationNumber());
		assert initial.getPTNetwork().getObjectId().equals( exporte.getPTNetwork().getObjectId());
		
		assert initial.getCompany(0).getName().equals( exporte.getCompany(0).getName());
		assert initial.getCompany(0).getObjectId().equals( exporte.getCompany(0).getObjectId());
		
		assert initial.getChouetteLineDescription().getLine().getObjectId().equals( exporte.getChouetteLineDescription().getLine().getObjectId());
		
		int totalRoute = initial.getChouetteLineDescription().getChouetteRouteCount();
		assert totalRoute==exporte.getChouetteLineDescription().getChouetteRouteCount();
		for (int i = 0; i < totalRoute; i++) {
			ChouetteRoute initialRoute = initial.getChouetteLineDescription().getChouetteRoute()[ i];
			ChouetteRoute exporteRoute = initial.getChouetteLineDescription().getChouetteRoute()[ i];
			
			assert initialRoute.getObjectId().equals( exporteRoute.getObjectId());
			if ( initialRoute.getDirection()!=null)
				assert initialRoute.getDirection().equals( exporteRoute.getDirection());
			assert initialRoute.getName().equals( exporteRoute.getName());
			
			int totalPtLink = initialRoute.getPtLinkIdCount();
			assert totalPtLink == exporteRoute.getPtLinkIdCount();
			
			for (int j = 0; j < totalPtLink; j++) {
				assert initialRoute.getPtLinkId( j).equals( exporteRoute.getPtLinkId( j));
			}
			
			assert initialRoute.getJourneyPatternIdCount()== exporteRoute.getJourneyPatternIdCount();
		}
		
		int totalCourses = initial.getChouetteLineDescription().getVehicleJourneyCount();
		assert totalCourses == exporte.getChouetteLineDescription().getVehicleJourneyCount();
		for (int i = 0; i < totalCourses; i++) {
			VehicleJourney initialVehicle = initial.getChouetteLineDescription().getVehicleJourney( i);
			VehicleJourney exporteVehicle = exporte.getChouetteLineDescription().getVehicleJourney( i);
			
			assert initialVehicle.getObjectId().equals( exporteVehicle.getObjectId());
			assert initialVehicle.getRouteId().equals( exporteVehicle.getRouteId());
			assert initialVehicle.getJourneyPatternId().equals( exporteVehicle.getJourneyPatternId());
			
			int totalHoraires = initialVehicle.getVehicleJourneyAtStopCount();
			assert totalHoraires==exporteVehicle.getVehicleJourneyAtStopCount();
			
			for (int j = 0; j < totalHoraires; j++) {
				VehicleJourneyAtStop initialStop = initialVehicle.getVehicleJourneyAtStop( j);
				VehicleJourneyAtStop exporteStop = exporteVehicle.getVehicleJourneyAtStop( j);
				
				assert initialStop.getStopPointId().equals( exporteStop.getStopPointId());
			}
		}
		
		int totalCalendriers = initial.getTimetableCount();
		assert totalCalendriers==exporte.getTimetableCount();
	}
	
    private void validation_chouette_xml( ChouettePTNetworkType ligneXML) 
    {
		MainSchemaProducer valideur = new MainSchemaProducer();		
		try {
			valideur.getASG( ligneXML);
		}
		catch(fr.certu.chouette.service.validation.commun.ValidationException e) {
			List<TypeInvalidite> categories = e.getCategories();
			boolean flag = false;
			if (categories != null)
				for (int i = 0; i < categories.size(); i++) {
					TypeInvalidite typeInvalidite = categories.get(i);
					if (typeInvalidite.equals(TypeInvalidite.INVALID_XML_FILE))
						flag = true;
					logger.debug("TypeInvalidite : "+typeInvalidite);
					Set<String> messages = e.getTridentIds(typeInvalidite);
					if (messages != null) {
						String[] _messages = messages.toArray(new String[0]);
						for (int j = 0; j < _messages.length; j++)
							logger.debug("\t"+_messages[j]);
					}
				}
			if (flag)
				throw e;
		}
	}	
}
