package unit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.testng.annotations.Test;

import util.DataFilesManager;
import chouette.schema.ChouetteArea;
import chouette.schema.ChouettePTNetwork;
import chouette.schema.ChouettePTNetworkTypeType;
import chouette.schema.ChouetteRemoveLineTypeType;
import chouette.schema.ITL;
import chouette.schema.StopArea;
import chouette.schema.StopPoint;
import chouette.schema.types.ChouetteAreaType;
import fr.certu.chouette.critere.ScalarClause;
import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.modele.BaseObjet;
import fr.certu.chouette.modele.InterdictionTraficLocal;
import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.modele.Reseau;
import fr.certu.chouette.modele.TableauMarche;
import fr.certu.chouette.service.commun.CodeIncident;
import fr.certu.chouette.service.commun.ServiceException;
import fr.certu.chouette.service.database.IExportManager;
import fr.certu.chouette.service.database.IITLManager;
import fr.certu.chouette.service.database.ILigneManager;
import fr.certu.chouette.service.database.IPositionGeographiqueManager;
import fr.certu.chouette.service.database.IReseauManager;
import fr.certu.chouette.service.fichier.IImportateur;
import fr.certu.chouette.service.identification.IIdentificationManager;
import fr.certu.chouette.service.validation.commun.TypeInvalidite;
import fr.certu.chouette.service.validation.util.MainSchemaProducer;
import fr.certu.chouette.service.xml.ILecteurEchangeXML;
import fr.certu.chouette.service.xml.ILecteurFichierXML;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;

@ContextConfiguration(locations = {"classpath:testContext.xml"})
public class Export extends AbstractTestNGSpringContextTests
{
	private static final Logger logger = Logger.getLogger(Export.class);
	private IExportManager exportManager;
	private IImportateur importateur;
	private IIdentificationManager identification;
	private ILecteurEchangeXML lecteurEchangeXML;
	private ILecteurFichierXML lecteurFichierXML;	
	private IPositionGeographiqueManager positionGeographiqueManager;
	private IITLManager itlManager;
	private ILigneManager ligneManager;
	private IReseauManager reseauManager;
	
	private DataFilesManager dataFilesManager;
	
	@BeforeMethod
	public void getBeans() throws Exception
	{
		dataFilesManager = new DataFilesManager();
		exportManager = (IExportManager)applicationContext.getBean("exportManager");
		importateur = (IImportateur)applicationContext.getBean("importateur");
		identification = (IIdentificationManager)applicationContext.getBean("identificationManager");
		lecteurEchangeXML = (ILecteurEchangeXML)applicationContext.getBean("lecteurEchangeXML");
		positionGeographiqueManager = (IPositionGeographiqueManager)applicationContext.getBean("positionGeographiqueManager");
		itlManager = (IITLManager)applicationContext.getBean("itlManager");
		ligneManager = (ILigneManager)applicationContext.getBean("ligneManager");
		reseauManager = (IReseauManager)applicationContext.getBean("reseauManager");
		lecteurFichierXML = (ILecteurFichierXML)applicationContext.getBean("lecteurFichierXML");		
	}
	
	@Test(groups="tests unitaires", description="import - export d'une ligne")
	public void lireSystemeOrigine()
	{
		String objectId = "Te-er:erzer:zer";
		StringTokenizer tokenizer = new StringTokenizer( objectId, ":");
		tokenizer.hasMoreElements();
		System.out.println( tokenizer.nextToken());
	}
	
	@Test(groups="tests unitaires", description="import XML de zones - ex arret physique sur nouvelle zone")
	public void export_zone_ex_nv() 
	{
		Random random = new Random();
		String cle = String.valueOf( random.nextInt(1000000));
		
		ChouettePTNetworkTypeType ligneXML = GenerateurDonnee.creerXMLaleatoire( cle, 1, 7, 8);
		
		ITL grande = GenerateurDonnee.creerXMLITL();
		grande.setLineIdShortCut( ligneXML.getChouetteLineDescription().getLine().getObjectId());
		List<String> gItlPhysiqueId = new ArrayList<String>();
		
		ITL petite = GenerateurDonnee.creerXMLITL();
		petite.setLineIdShortCut( ligneXML.getChouetteLineDescription().getLine().getObjectId());
		List<String> pItlPhysiqueId = new ArrayList<String>();
		
		PositionGeographique exArretPhysique = getArretPhysique( ligneXML).get( 2);
		String objectIdOrigine = exArretPhysique.getObjectId();
		positionGeographiqueManager.creer(exArretPhysique);
		PositionGeographique arretInitialLu = positionGeographiqueManager.lire( exArretPhysique.getId());
		String objectIdDestination = arretInitialLu.getObjectId();
		for (int i=0; i<ligneXML.getChouetteArea().getStopAreaCount(); i++) 
		{
			StopArea stopArea = ligneXML.getChouetteArea().getStopArea( i);
			if ( stopArea.getObjectId().equals( objectIdOrigine))
			{
				stopArea.setObjectId( objectIdDestination);
			}
			gItlPhysiqueId.add( stopArea.getObjectId());
			if ( (i%2)==0)
			{
				pItlPhysiqueId.add( stopArea.getObjectId());
			}
		}
		for (int i=0; i<ligneXML.getChouetteLineDescription().getStopPointCount(); i++) 
		{
			StopPoint stopPoint = ligneXML.getChouetteLineDescription().getStopPoint( i);
			if ( stopPoint.getContainedIn().equals( objectIdOrigine))
			{
				stopPoint.setContainedIn( objectIdDestination);
			}
		}
		
		String nomApresImport = arretInitialLu.getName();
		String nomAvantImport = "azerty " + nomApresImport;
		arretInitialLu.setName( nomAvantImport);
		positionGeographiqueManager.modifier( arretInitialLu);
		
		PositionGeographique zoneNouvelle =  GenerateurDonnee.creerZoneAleatoire( cle);
		zoneNouvelle.setAreaType( ChouetteAreaType.COMMERCIALSTOPPOINT);
		zoneNouvelle.getStopArea().addContains( exArretPhysique.getObjectId());
		ligneXML.getChouetteArea().addStopArea( zoneNouvelle.getStopArea());
		ligneXML.getChouetteArea().addAreaCentroid( zoneNouvelle.getAreaCentroid());
		
		
		// ajout des zones d'ITL
		ligneXML.getChouetteLineDescription().addITL( grande);
		ligneXML.getChouetteArea().addStopArea( 
				GenerateurDonnee.creerXMLITLStopArea(
						grande, 
						gItlPhysiqueId));
		ligneXML.getChouetteLineDescription().addITL( petite);
		ligneXML.getChouetteArea().addStopArea( 
				GenerateurDonnee.creerXMLITLStopArea(
						petite, 
						pItlPhysiqueId));
		
		lecteurFichierXML.ecrire( ligneXML, new File( "origine.xml"));
		
		ILectureEchange lectureEchange = lecteurEchangeXML.lire(ligneXML);
		importateur.importer( false, lectureEchange);
		
		// conservation de l'id d'arret physique existant
		arretInitialLu = positionGeographiqueManager.lire( exArretPhysique.getId());
		
		assert arretInitialLu.getName().equals( nomApresImport) : "la propriété nom de l'arret physique existant "+arretInitialLu.getName()+" n'a pas été maj, "+nomApresImport+" attendu";
		assert arretInitialLu.getObjectId().equals( exArretPhysique.getObjectId()) : "la propriété objectId de l'arret physique existant n'a pas été conservé";
		assert arretInitialLu.getIdParent()!=null : "l'arret physique existant n'a pas été relié à la nouvelle zone";
	
		PositionGeographique zoneLue = positionGeographiqueManager.lire( arretInitialLu.getIdParent());
		assert zoneLue.getObjectId().equals( zoneNouvelle.getObjectId()) : "l'objectId de la nouvelle n'est pas correct";
		
		List<InterdictionTraficLocal> itlGrandes = itlManager.select(ScalarClause.newEqualsClause( "objectId", grande.getAreaId()));
		assert itlGrandes.size()==1;
		
		InterdictionTraficLocal itlGrande = itlGrandes.get( 0);
		Set<String> gPhysiques = new HashSet<String>();
		for (Long physiqueId : itlGrande.getArretPhysiqueIds()) {
			gPhysiques.add( positionGeographiqueManager.lire( physiqueId).getObjectId());
		}
		assert gPhysiques.size()==gItlPhysiqueId.size();
		gPhysiques.removeAll( gItlPhysiqueId);
		assert gPhysiques.size()==0;
		
		List<InterdictionTraficLocal> itlPetites = itlManager.select(ScalarClause.newEqualsClause("objectId", petite.getAreaId()));
		assert itlPetites.size()==1;
		
		InterdictionTraficLocal itlPetite = itlPetites.get( 0);
		Set<String> pPhysiques = new HashSet<String>();
		for (Long physiqueId : itlPetite.getArretPhysiqueIds()) {
			pPhysiques.add( positionGeographiqueManager.lire( physiqueId).getObjectId());
		}
		assert pPhysiques.size()==pItlPhysiqueId.size();
		pPhysiques.removeAll( pItlPhysiqueId);
		assert pPhysiques.size()==0;
	}

	@Test(groups="tests unitaires", description="import XML de zones - ex arret physique sur ex zone")
	public void export_zone_ex_ex() 
	{
		Random random = new Random();
		String cle = String.valueOf( random.nextInt(1000000));
		
		ChouettePTNetworkTypeType ligneXML = GenerateurDonnee.creerXMLaleatoire( cle, 1, 7, 8);
		
		PositionGeographique exArretPhysique = getArretPhysique( ligneXML).get( 2);
		String objectIdOrigine = exArretPhysique.getObjectId();
		positionGeographiqueManager.creer( exArretPhysique);
		PositionGeographique arretInitialLu = positionGeographiqueManager.lire( exArretPhysique.getId());
		String objectIdDestination = arretInitialLu.getObjectId();
		for (int i=0; i<ligneXML.getChouetteArea().getStopAreaCount(); i++) {
			StopArea stopArea = ligneXML.getChouetteArea().getStopArea( i);
			if ( stopArea.getObjectId().equals( objectIdOrigine))
			{
				stopArea.setObjectId( objectIdDestination);
			}
		}
		for (int i=0; i<ligneXML.getChouetteLineDescription().getStopPointCount(); i++) {
			StopPoint stopPoint = ligneXML.getChouetteLineDescription().getStopPoint( i);
			if ( stopPoint.getContainedIn().equals( objectIdOrigine))
			{
				stopPoint.setContainedIn( objectIdDestination);
			}
		}
		
		PositionGeographique zoneEx =  GenerateurDonnee.creerZoneAleatoire( cle);
		zoneEx.setAreaType( ChouetteAreaType.STOPPLACE);
		zoneEx.getStopArea().addContains( exArretPhysique.getObjectId());
		positionGeographiqueManager.creer(zoneEx);
		
		String nomZoneAvantImport = zoneEx.getName();
		String nomZoneAprestImport = "azerty " + nomZoneAvantImport;
		zoneEx.setName( nomZoneAprestImport);
		ligneXML.getChouetteArea().addStopArea( zoneEx.getStopArea());
		ligneXML.getChouetteArea().addAreaCentroid( zoneEx.getAreaCentroid());
		
		String nomApresImport = arretInitialLu.getName();
		String nomAvantImport = "azerty " + nomApresImport;
		arretInitialLu.setName( nomAvantImport);
		positionGeographiqueManager.associerGeoPositions( zoneEx.getId(), arretInitialLu.getId());
		positionGeographiqueManager.modifier( arretInitialLu);
		
		lecteurFichierXML.ecrire( ligneXML, new File( "origine.xml"));
		
		ILectureEchange lectureEchange = lecteurEchangeXML.lire(ligneXML);
		importateur.importer( false, lectureEchange);
		
		// conservation de l'id d'arret physique existant
		arretInitialLu = positionGeographiqueManager.lire( exArretPhysique.getId());
		
		assert arretInitialLu.getName().equals( nomApresImport) : "la propriété nom de l'arret physique existant "+arretInitialLu.getName()+" n'a pas été maj, "+nomApresImport+" attendu";
		assert arretInitialLu.getObjectId().equals( exArretPhysique.getObjectId()) : "la propriété objectId de l'arret physique existant n'a pas été conservé";
		assert arretInitialLu.getIdParent()!=null : "l'arret physique existant n'a pas été relié à la nouvelle zone";
		assert arretInitialLu.getIdParent().equals( zoneEx.getId()) : "l'arret physique ne pointe pas le bon id de zone";
	
		PositionGeographique zoneLue = positionGeographiqueManager.lire( arretInitialLu.getIdParent());
		assert zoneLue.getObjectId().equals( zoneEx.getObjectId()) : "l'objectId de la nouvelle n'est pas correct";
		assert zoneLue.getName().equals( nomZoneAprestImport) : "le nom de la zone n'a pas été màj par import XML";
	}

	@Test(groups="tests unitaires", description="import XML de zones - nv arret physique sur ex zone")
    public void export_zone_nv_ex() 
    {
		Random random = new Random();
		String cle = String.valueOf( random.nextInt(1000000));
		
		ChouettePTNetworkTypeType ligneXML = GenerateurDonnee.creerXMLaleatoire( cle, 1, 7, 8);
		
		PositionGeographique nvArretPhysique = getArretPhysique( ligneXML).get( 2);
		
		PositionGeographique zoneEx =  GenerateurDonnee.creerZoneAleatoire( cle);
		zoneEx.setAreaType( ChouetteAreaType.STOPPLACE);
		positionGeographiqueManager.creer(zoneEx);
		
		String nomZoneAvantImport = zoneEx.getName();
		String nomZoneAprestImport = "azerty " + nomZoneAvantImport;
		zoneEx.setName(nomZoneAprestImport);
		zoneEx.getStopArea().addContains(nvArretPhysique.getObjectId());
		ligneXML.getChouetteArea().addStopArea(zoneEx.getStopArea());
		ligneXML.getChouetteArea().addAreaCentroid(zoneEx.getAreaCentroid());
		
		lecteurFichierXML.ecrire(ligneXML, new File( "origine.xml"));
		
		ILectureEchange lectureEchange = lecteurEchangeXML.lire(ligneXML);
		importateur.importer( false, lectureEchange);
		
		// conservation de l'id de la zone existante
		PositionGeographique zoneLue = positionGeographiqueManager.lire(zoneEx.getId());
		assert zoneLue.getObjectId().equals(zoneEx.getObjectId()) : "l'objectId de la nouvelle n'est pas correct";
		assert zoneLue.getName().equals( nomZoneAprestImport) : "le nom de la zone n'a pas été màj par import XML";
		
		List<PositionGeographique> posGeos = positionGeographiqueManager.getGeoPositionsDirectementContenues( zoneLue.getId());
		assert posGeos.size()==1;
		assert posGeos.get( 0).getObjectId().equals( nvArretPhysique.getObjectId());
    }

	@Test(groups="tests unitaires", 
			description="import XML de zones - nv arret physique sur une nv zone")
    public void export_zone_nv_nv() 
    {
		Random random = new Random();
		String cle = String.valueOf( random.nextInt(1000000));
		
		ChouettePTNetworkTypeType ligneXML = GenerateurDonnee.creerXMLaleatoire( cle, 1, 7, 8);
		
		PositionGeographique nvArretPhysique = getArretPhysique( ligneXML).get( 2);
		
		PositionGeographique zoneEx =  GenerateurDonnee.creerZoneAleatoire( cle);
		zoneEx.setAreaType( ChouetteAreaType.STOPPLACE);
		zoneEx.getStopArea().addContains( nvArretPhysique.getObjectId());
		ligneXML.getChouetteArea().addStopArea( zoneEx.getStopArea());
		ligneXML.getChouetteArea().addAreaCentroid( zoneEx.getAreaCentroid());
		
		lecteurFichierXML.ecrire( ligneXML, new File( "origine.xml"));
		
		ILectureEchange lectureEchange = lecteurEchangeXML.lire(ligneXML);
		importateur.importer( false, lectureEchange);
		
		// conservation de l'id de la zone existante
		PositionGeographique zoneLue = positionGeographiqueManager.lireParObjectId( zoneEx.getObjectId());
		assert zoneLue.getObjectId().equals( zoneEx.getObjectId()) : "l'objectId de la nouvelle n'est pas correct";
		
		List<PositionGeographique> posGeos = positionGeographiqueManager.getGeoPositionsDirectementContenues( zoneLue.getId());
		assert posGeos.size()==1;
		assert posGeos.get( 0).getObjectId().equals( nvArretPhysique.getObjectId());
    }

//	@Test(groups="tests unitaires", 
//			description="export specifique")
    public void export_specifique() 
    {
		ChouettePTNetworkTypeType ligneXML = exportManager.getExportParRegistration( "003003021");
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
					System.out.println("TypeInvalidite : "+typeInvalidite);
					Set<String> messages = e.getTridentIds(typeInvalidite);
					if (messages != null) {
						String[] _messages = messages.toArray(new String[0]);
						for (int j = 0; j < _messages.length; j++)
							System.out.println("\t"+_messages[j]);
					}
				}
			if (flag)
				throw e;
		}
	}	
	private List<PositionGeographique> getArretPhysique(ChouettePTNetworkTypeType ligne)
	{
		ChouetteArea chouetteArea = ligne.getChouetteArea();
		int totalArrets = chouetteArea.getStopAreaCount();
		List<PositionGeographique> arretsPhysiques = new ArrayList<PositionGeographique>(totalArrets);
		for (int i = 0; i < totalArrets; i++) 
		{
			PositionGeographique arretPhysique = new PositionGeographique();
			arretPhysique.setStopArea(chouetteArea.getStopArea( i));
			arretPhysique.setAreaCentroid(chouetteArea.getAreaCentroid( i));
			arretsPhysiques.add(arretPhysique);
		}
		return arretsPhysiques;
	}

//	@Test(groups="tests unitaires", description="import - export d'une ligne")
    public void exportLigne() 
	{
		String registration = "100110015";
		String nomFic = "test_export.xml";
		
		logger.debug( "début");
		ChouettePTNetworkTypeType ligneDeTestLue = exportManager.getExportParRegistration( registration);
		
		lecteurFichierXML.ecrire( ligneDeTestLue, new File( nomFic));
		logger.debug( "validation");
		
		try
		{
			lecteurFichierXML.lire( nomFic);
		}
		catch (Exception exception)
		{
			if (exception instanceof ServiceException)
			{
				if (exception instanceof fr.certu.chouette.service.validation.commun.ValidationException)
				{
					fr.certu.chouette.service.validation.commun.ValidationException validationException = (fr.certu.chouette.service.validation.commun.ValidationException) exception;
					//	Liste de codes d'erreur 
					List<TypeInvalidite> codeCategories =  validationException.getCategories();
					
					for (TypeInvalidite invalidite : codeCategories)
					{
						//	Liste des messages d'erreur
						Set<String> messages = validationException.getTridentIds(invalidite);
						
						for (String message : messages)
						{
							logger.error(message);
						}
					}
				}
				else
				{
					ServiceException serviceException = (ServiceException) exception;
					logger.error("Impossible de récupérer le fichier, msg=" + serviceException.getMessage(), serviceException);
				}
			}
		}
	}
      
    @Test(groups="tests unitaires", description = "import - export d'une ligne AFNOR")
    public void exportAFNOR() 
    {
    	String filename = "";
    	ChouettePTNetworkTypeType chouettePTNetworkType = null;
    	try
		{
			filename = dataFilesManager.getInputFileName("goodAFNORFile");			
			logger.debug("IMPORT XML DU FICHIER " + filename);
			chouettePTNetworkType = lecteurFichierXML.lire(filename, true);
			logger.debug("CREATION DU CHOUETTEPTNETWORKTYPETYPE REUSSI");
		}	
		catch (Exception e) 
		{
			String message = "Erreur de lecture du fichier " + filename;
			logger.error(message + ", msg = " + e.getMessage(), e);				
			assert false : "Echec export : " + message;				
		}
		try
		{
			ILectureEchange lectureEchange = lecteurEchangeXML.lire(chouettePTNetworkType);
			importateur.importer(false, lectureEchange);
			
			Ligne ligne = ligneManager.getLigneParRegistration(chouettePTNetworkType.getChouetteLineDescription().getLine().getRegistration().getRegistrationNumber());
			ChouettePTNetworkTypeType exportedLine = exportManager.getExportParIdLigne( ligne.getId());
			

			File beforeImportFile = dataFilesManager.getOutputFile("beforeAFNORImport");
			File afterImportFile = dataFilesManager.getOutputFile("afterAFNORImport");
			
			lecteurFichierXML.ecrire(chouettePTNetworkType, beforeImportFile);
			lecteurFichierXML.ecrire(exportedLine, afterImportFile);
			
			ILectureEchange lect = lecteurEchangeXML.lire(exportedLine);
			
			List<TableauMarche> tms = lect.getTableauxMarche();
			for (TableauMarche timetable : tms) 
			{
				logger.debug( "id="+timetable.getObjectId());
				int total = timetable.getVehicleJourneyIdCount();
				for (int i = 0; i < total; i++) 
				{
					logger.debug( "course :"+timetable.getVehicleJourneyId( i));
				}
			}			
			importateur.importer(false, lect);			
		}
		catch(Exception e)
		{
			logger.error(e.getMessage(), e);
			assert false : "Echec du test d'import - export AFNOR :" + e.getMessage();
		}
    }

	
	public void importer(String fileName) 
	{
		ChouettePTNetworkTypeType chouettePTNetworkType = null;
		try 
		{
			logger.debug("IMPORT XML DU FICHIER "+fileName);
			chouettePTNetworkType = lecteurFichierXML.lire(fileName, true);
			logger.debug("CREATION DU CHOUETTEPTNETWORKTYPE REUSSI");
		}
		catch (Exception e) 
		{
			logger.error("Erreur de lecture du fichier "+fileName+", msg = " + e.getMessage(), e);
			return;
		}
		ILectureEchange lectureEchange = lecteurEchangeXML.lire(chouettePTNetworkType);
		try 
		{
			importateur.importer(true, lectureEchange);
		}
		catch (ServiceException e) 
		{
			logger.error("Impossible de créer la ligne en base, msg = " + e.getMessage(), e);
			return;
		}
	}
	
	@Test(groups="tests unitaires", description="import - export d'une ligne")
    public void export() 
    {
		try
		{
			ChouettePTNetworkTypeType ligneDeTest = GenerateurDonnee.creerChouettePTNetwork( 4, 15, 8);
			
			ILectureEchange lectureEchange = lecteurEchangeXML.lire(ligneDeTest);
			importateur.importer( false, lectureEchange);
			
			Ligne ligne = ligneManager.getLigneParRegistration( ligneDeTest.getChouetteLineDescription().getLine().getRegistration().getRegistrationNumber());
			ChouettePTNetworkTypeType ligneDeTestLue = 
				exportManager.getExportParIdLigne( ligne.getId());
			
			lecteurFichierXML.ecrire( ligneDeTestLue, new File( "apres_import.xml"));
			lecteurFichierXML.ecrire( ligneDeTest, new File( "origine.xml"));
			
			ILectureEchange lect = lecteurEchangeXML.lire( ligneDeTestLue);
			
			List<TableauMarche> tms = lect.getTableauxMarche();
			for (TableauMarche timetable : tms) {
				logger.debug( "id="+timetable.getObjectId());
				int total = timetable.getVehicleJourneyIdCount();
				for (int i = 0; i < total; i++) {
					logger.debug( "course :"+timetable.getVehicleJourneyId( i));
				}
			}
			
			importateur.importer( false, lect);
			// tester le resultat enregistre
			// comparer ligneDeTest et ligneDeTestLue
		}
		catch(Exception e)
		{
			logger.error(e.getMessage(), e);
			assert false:"Echec du test d'export: " + e.getMessage();
		}
    }
	
	@Test(groups="tests unitaires", description="export en suppression d'une ligne")
    public void export_remove() 
    {
		try
		{
			ChouettePTNetworkTypeType ligneDeTest = GenerateurDonnee.creerChouettePTNetwork( 4, 15, 8);
			
			ILectureEchange lectureEchange = lecteurEchangeXML.lire(ligneDeTest);
			importateur.importer( false, lectureEchange);
			
			// si un reseau existe deja avec le même registrationNumber
			// il peut avoir un autre objectid
			List<Reseau> reseaux = reseauManager.select(ScalarClause.newEqualsClause("registrationNumber", ligneDeTest.getPTNetwork().getRegistration().getRegistrationNumber()));
			assert reseaux.size()==1:"Le registrationNumber ("+ligneDeTest.getPTNetwork().getRegistration().getRegistrationNumber()
										+") devrait être unique en base";
			
			ChouetteRemoveLineTypeType removeLine = exportManager.getSuppressionParRegistration(
					ligneDeTest.getChouetteLineDescription().getLine().getRegistration().getRegistrationNumber());

			assert removeLine.getLine().getObjectId().equals( ligneDeTest.getChouetteLineDescription().getLine().getObjectId());
			assert removeLine.getLine().getName().equals( ligneDeTest.getChouetteLineDescription().getLine().getName());
			assert removeLine.getLine().getPtNetworkIdShortcut().equals( reseaux.get(0).getObjectId()):
				"reseau objectId lu: "+removeLine.getLine().getPtNetworkIdShortcut()+
				", objectId attendu: "+reseaux.get(0).getObjectId();
		}
		catch( Exception e)
		{
			logger.error( e.getMessage(), e);
			assert false:"Echec du test d'export:"+e.getMessage();
		}
    }
	
	@Test(groups="tests unitaires", description="import - export d'une ligne avec 4 zones")
	public void export_zones()
	{
		ChouettePTNetworkTypeType ligneDeTest = GenerateurDonnee.creerChouettePTNetwork( 4, 15, 8);
		
		List<StopArea> arretsPhysiques = new ArrayList<StopArea>();
		for (int i=0; i<ligneDeTest.getChouetteArea().getStopAreaCount(); i++) {
			arretsPhysiques.add( ligneDeTest.getChouetteArea().getStopArea( i));
		}
		
		PositionGeographique zoneCommercialeA =  GenerateurDonnee.creerZone( "");
		PositionGeographique zoneCommercialeB =  GenerateurDonnee.creerZone( "");
		PositionGeographique zonePoleC =  GenerateurDonnee.creerZone( "");
		PositionGeographique zonePoleD =  GenerateurDonnee.creerZone( "");
		
		List<PositionGeographique> zones = new ArrayList<PositionGeographique>();
		List<String> zonesIds = new ArrayList<String>();
		zones.add( zoneCommercialeA);
		zones.add( zoneCommercialeB);
		zones.add( zonePoleC);
		zones.add( zonePoleD);
		zonesIds.add( zoneCommercialeA.getObjectId());
		zonesIds.add( zoneCommercialeB.getObjectId());
		zonesIds.add( zonePoleC.getObjectId());
		zonesIds.add( zonePoleD.getObjectId());
		
		zoneCommercialeA.setAreaType( ChouetteAreaType.COMMERCIALSTOPPOINT);
		zoneCommercialeB.setAreaType( ChouetteAreaType.COMMERCIALSTOPPOINT);
		zonePoleC.setAreaType( ChouetteAreaType.STOPPLACE);
		zonePoleD.setAreaType( ChouetteAreaType.STOPPLACE);
		
		// liaison aux arrets physiques
		zoneCommercialeA.getStopArea().addContains( arretsPhysiques.get( 0).getObjectId());
		zoneCommercialeA.getStopArea().addContains( arretsPhysiques.get( 1).getObjectId());
		zoneCommercialeB.getStopArea().addContains( arretsPhysiques.get( 2).getObjectId());
		zoneCommercialeB.getStopArea().addContains( arretsPhysiques.get( 3).getObjectId());
		zonePoleC.getStopArea().addContains( zoneCommercialeA.getObjectId());
		zonePoleC.getStopArea().addContains( arretsPhysiques.get( 4).getObjectId());
		zonePoleD.getStopArea().addContains( arretsPhysiques.get( 5).getObjectId());
		
		Map<String, StopArea> zonesCreees = new Hashtable<String, StopArea>();
		for (PositionGeographique area : zones) {
			ligneDeTest.getChouetteArea().addStopArea( area.getStopArea());
			ligneDeTest.getChouetteArea().addAreaCentroid( area.getAreaCentroid());
			
			zonesCreees.put( area.getStopArea().getObjectId(), area.getStopArea());
		}
		lecteurFichierXML.ecrire( ligneDeTest, new File( "origine.xml"));
		
		ILectureEchange lectureEchange = lecteurEchangeXML.lire(ligneDeTest);
		assert lectureEchange.getZoneParente( arretsPhysiques.get( 0).getObjectId()).equals( zoneCommercialeA.getStopArea().getObjectId());
		assert lectureEchange.getZoneParente( arretsPhysiques.get( 1).getObjectId()).equals( zoneCommercialeA.getStopArea().getObjectId());
		assert lectureEchange.getZoneParente( arretsPhysiques.get( 2).getObjectId()).equals( zoneCommercialeB.getStopArea().getObjectId());
		assert lectureEchange.getZoneParente( arretsPhysiques.get( 3).getObjectId()).equals( zoneCommercialeB.getStopArea().getObjectId());
		assert lectureEchange.getZoneParente( zoneCommercialeA.getObjectId()).equals( zonePoleC.getStopArea().getObjectId());
		assert lectureEchange.getZoneParente( arretsPhysiques.get( 4).getObjectId()).equals( zonePoleC.getStopArea().getObjectId());
		assert lectureEchange.getZoneParente( arretsPhysiques.get( 5).getObjectId()).equals( zonePoleD.getStopArea().getObjectId());
		
		importateur.importer( false, lectureEchange);
				
		BaseObjet test = new BaseObjet();
		test.setId( 1L);
		logger.debug( identification.getIdFonctionnel( "nom", test));
		
		ChouettePTNetworkTypeType ligneDeTestLue = 
			exportManager.getExportParRegistration( ligneDeTest.getChouetteLineDescription().getLine().getRegistration().getRegistrationNumber());
		
		lecteurFichierXML.ecrire( ligneDeTestLue, new File( "apres_import.xml"));
		
		// controler la présence des zones
		Map<String, StopArea> zoneRetrouvees = new Hashtable<String, StopArea>();
		for ( int i=0; i< ligneDeTestLue.getChouetteArea().getStopAreaCount(); i++) {
			StopArea area = ligneDeTestLue.getChouetteArea().getStopArea( i);
			
			if ( zonesIds.contains( area.getObjectId()))
				zoneRetrouvees.put( area.getObjectId(), area);
		}
		
		assert zoneRetrouvees.keySet().size()==zonesIds.size(): "total de zones à retrouver : "+zonesIds.size()+", seulement "+zoneRetrouvees.size()+" lues ";
		Collection<StopArea> areaRetourvees = zoneRetrouvees.values();
		zoneRetrouvees.keySet().removeAll( zonesIds);
		assert zoneRetrouvees.keySet().size()==0: "zones retrouvées à tort : "+zoneRetrouvees.size();
		
		for (StopArea area : areaRetourvees) {
			StopArea stopAreaCreee = zonesCreees.get( area.getObjectId());
			
			Set<String> liensCrees = new HashSet<String>( Arrays.asList( stopAreaCreee.getContains()));
			Set<String> liensRetrouves = new HashSet<String>( Arrays.asList( area.getContains()));
			
			assert area.getContainsCount()==liensRetrouves.size() : "répétition parmi les zones contenues de la zone "+area.getObjectId()+" liens : "+liensRetrouves;
			assert liensCrees.size()==liensRetrouves.size() : "relation contains mal restituées pour la zone "+area.getObjectId();
			liensRetrouves.removeAll( liensCrees);
			assert liensRetrouves.size()==0 : "relation contains mal restituées pour la zone "+area.getObjectId()+" les liens "+liensRetrouves+" ne sont pas ceux créés";
		}
	}
	
	@Test(groups="tests unitaires", description="import sur fichier avec des zones invalides")
	public void export_zones_invalides()
	{
		ChouettePTNetworkTypeType ligneDeTest = GenerateurDonnee.creerChouettePTNetwork( 4, 15, 8);
		
		List<StopArea> arretsPhysiques = new ArrayList<StopArea>();
		for (int i=0; i<ligneDeTest.getChouetteArea().getStopAreaCount(); i++) {
			arretsPhysiques.add( ligneDeTest.getChouetteArea().getStopArea( i));
		}
		
		PositionGeographique zoneCommercialeA =  GenerateurDonnee.creerZone( "");
		PositionGeographique zoneCommercialeB =  GenerateurDonnee.creerZone( "");
		PositionGeographique zonePoleC =  GenerateurDonnee.creerZone( "");
		PositionGeographique zonePoleD =  GenerateurDonnee.creerZone( "");
		
		List<PositionGeographique> zones = new ArrayList<PositionGeographique>();
		List<String> zonesIds = new ArrayList<String>();
		zones.add( zoneCommercialeA);
		zones.add( zoneCommercialeB);
		zones.add( zonePoleC);
		zones.add( zonePoleD);
		zonesIds.add( zoneCommercialeA.getObjectId());
		zonesIds.add( zoneCommercialeB.getObjectId());
		zonesIds.add( zonePoleC.getObjectId());
		zonesIds.add( zonePoleD.getObjectId());
		
		zoneCommercialeA.setAreaType( ChouetteAreaType.COMMERCIALSTOPPOINT);
		zoneCommercialeB.setAreaType( ChouetteAreaType.COMMERCIALSTOPPOINT);
		zonePoleC.setAreaType( ChouetteAreaType.STOPPLACE);
		zonePoleD.setAreaType( ChouetteAreaType.STOPPLACE);
		
		// liaison aux arrets physiques
		zoneCommercialeA.getStopArea().addContains( arretsPhysiques.get( 0).getObjectId());
		zoneCommercialeA.getStopArea().addContains( arretsPhysiques.get( 1).getObjectId());
		zoneCommercialeB.getStopArea().addContains( arretsPhysiques.get( 1).getObjectId());
		zoneCommercialeB.getStopArea().addContains( arretsPhysiques.get( 2).getObjectId());
		zonePoleC.getStopArea().addContains( zoneCommercialeA.getObjectId());
		zonePoleC.getStopArea().addContains( arretsPhysiques.get( 4).getObjectId());
		zonePoleD.getStopArea().addContains( arretsPhysiques.get( 5).getObjectId());
		
		for (PositionGeographique area : zones) 
		{
			ligneDeTest.getChouetteArea().addStopArea( area.getStopArea());
			ligneDeTest.getChouetteArea().addAreaCentroid( area.getAreaCentroid());
		}
		
		try
		{
			ILectureEchange lectureEchange = lecteurEchangeXML.lire(ligneDeTest);
			assert false : "le fichier XML est invalide un arret est rattaché à 2 zones";
		}
		catch( ServiceException e)
		{
			assert CodeIncident.DONNEE_INVALIDE.equals( e.getCode()) : "l'exception produite n'est pas bien formée";
		}
	}

	@Test(groups="tests unitaires", description="ecriture - lecture d'une ligne")
    public void lire_ecrire() 
	{
		ChouettePTNetworkTypeType ligneDeTest = GenerateurDonnee.creerChouettePTNetwork(4, 15, 8);
		
		String nomFic = "apres_import.xml";
		lecteurFichierXML.ecrire(ligneDeTest, new File(nomFic));
		ChouettePTNetworkTypeType ligneLue = null;		
		try
		{
			ligneLue = lire(nomFic);
		}
		catch( Exception e)
		{
			logger.error(e.getMessage(),e);
			assert false : "echec de lecture";
		}
		assert ligneLue != null : "fichier xml mal lu";
		assert ligneLue.getCompanyCount() == ligneDeTest.getCompanyCount() : "fichier xml mal lu";
		assert ligneLue.getPTNetwork().getName().equals(ligneDeTest.getPTNetwork().getName()) : "fichier xml mal lu";
		File fic = new File( nomFic);
		boolean resultat = fic.delete();		
		assert resultat : "fichier de test non detruit";		
	}

	private ChouettePTNetworkTypeType lire(String nom) throws FileNotFoundException, IOException, MarshalException, ValidationException
	{
		  FileReader aFileReader = new FileReader(nom);
	
	     Unmarshaller anUnmarshaller = new Unmarshaller(ChouettePTNetwork.class);
	     anUnmarshaller.setValidation(false);	     
	     ChouettePTNetworkTypeType aReturnValue = (ChouettePTNetworkTypeType) anUnmarshaller.unmarshal(aFileReader);
	     aFileReader.close();
	     return aReturnValue;
	}
}
