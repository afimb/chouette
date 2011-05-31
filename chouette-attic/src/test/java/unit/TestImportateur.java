package unit;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.Test;

import chouette.schema.ChouettePTNetworkTypeType;
import chouette.schema.ConnectionLink;
import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.manager.SingletonManager;
import fr.certu.chouette.modele.ArretItineraire;
import fr.certu.chouette.modele.Correspondance;
import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.modele.Reseau;
import fr.certu.chouette.modele.Transporteur;
import fr.certu.chouette.service.commun.CodeIncident;
import fr.certu.chouette.service.commun.ServiceException;
import fr.certu.chouette.service.database.ICorrespondanceManager;
import fr.certu.chouette.service.database.IPositionGeographiqueManager;
import fr.certu.chouette.service.database.IReseauManager;
import fr.certu.chouette.service.database.ITransporteurManager;
import fr.certu.chouette.service.fichier.IImportateur;
import fr.certu.chouette.service.xml.ILecteurEchangeXML;
import fr.certu.chouette.service.xml.ILecteurFichierXML;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;

@ContextConfiguration(locations = {"classpath:testContext.xml"})
public class TestImportateur extends AbstractTestNGSpringContextTests {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(TestImportateur.class);
	
	private IImportateur importateur = null;
	private ITransporteurManager transporteurManager;
	private IReseauManager reseauManager;
	private ILecteurEchangeXML lecteurEchangeXML;
	private ILecteurFichierXML lecteurFichierXML;
	private ICorrespondanceManager correspondanceManager;
	private IPositionGeographiqueManager positionGeographiqueManager;
	private GenerateurDonnee generateurDonnee = new GenerateurDonnee();
	
	@BeforeMethod
	public void getBeans()
	{
		importateur = ( IImportateur)applicationContext.getBean( "importateur");
		lecteurEchangeXML = ( ILecteurEchangeXML)applicationContext.getBean( "lecteurEchangeXML");
		positionGeographiqueManager = ( IPositionGeographiqueManager)applicationContext.getBean( "positionGeographiqueManager");
		correspondanceManager = ( ICorrespondanceManager)applicationContext.getBean( "correspondanceManager");
		transporteurManager = ( ITransporteurManager)applicationContext.getBean( "transporteurManager");
		reseauManager = ( IReseauManager)applicationContext.getBean( "reseauManager");
		lecteurFichierXML = ( ILecteurFichierXML)applicationContext.getBean( "lecteurFichierXML");
	}
	
	@Test(groups="tests unitaires", description="import des correspondances")
	public void importCorrespondance()
	{
		int max_arrets = 4;
		int max_corres = max_arrets / 2;
		Random random = new Random();
		ChouettePTNetworkTypeType chouettePTNetwork = generateurDonnee.creerXMLaleatoire( "TEST"+random.nextInt(10000), 1, max_arrets, 1, max_corres);
		logger.debug( "Démarrage réel");
		
		// Creer une zone externe à la ligne
		PositionGeographique zoneExterne = generateurDonnee.creerZone( "TEST");
		positionGeographiqueManager.creer(zoneExterne);
		zoneExterne = positionGeographiqueManager.lire( zoneExterne.getId());
		
		assert zoneExterne!=null;
		
		// Creer la correspondance externe
		Correspondance corresExterne = generateurDonnee.creerCorrespondance();
		corresExterne.setStartOfLink( chouettePTNetwork.getChouetteArea().getStopArea( 0).getObjectId());
		corresExterne.setEndOfLink( zoneExterne.getObjectId());
		corresExterne.setObjectId( "TEST:ConnectionLink:"+random.nextInt( 250000));
		chouettePTNetwork.addConnectionLink( corresExterne.getConnectionLink());
		
		ILectureEchange lectureEchange = lecteurEchangeXML.lire(chouettePTNetwork);
		importateur.importer( false, lectureEchange);
		
		assert chouettePTNetwork.getConnectionLinkCount()==max_corres+1;
		
		// présence des correspondances internes
		for (int i = 0; i < max_corres; i++) {
			Correspondance corresLus = correspondanceManager.lireParObjectId( chouettePTNetwork.getConnectionLink(i).getObjectId());
			assert corresLus!=null;
		}
		
		// présence de la correspondance externe
		Correspondance corresExterneLue = correspondanceManager.lireParObjectId( corresExterne.getObjectId());
		assert corresExterneLue!=null;
		
		Date dureeInitiale = corresExterneLue.getDefaultDuration();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime( dureeInitiale);
		calendar.set( Calendar.MINUTE, calendar.get( Calendar.MINUTE)+3);
		corresExterneLue.setDefaultDuration( calendar.getTime());
		
		// mise à jour de la dernier corres
		ConnectionLink derniere = chouettePTNetwork.getConnectionLink( max_corres);
		assert derniere.getObjectId().equals( corresExterneLue.getObjectId());
		derniere.setDefaultDuration( corresExterneLue.getConnectionLink().getDefaultDuration());
		
		lectureEchange = lecteurEchangeXML.lire(chouettePTNetwork);
		importateur.importer( false, lectureEchange);
		
		assert chouettePTNetwork.getConnectionLinkCount()==max_corres+1;
		
		// vérification de la mise à jour de la correspondance externe
		corresExterneLue = correspondanceManager.lireParObjectId( corresExterne.getObjectId());
		assert corresExterneLue!=null;
		Date dateAttendue = calendar.getTime();
		Date dateLue = corresExterneLue.getDefaultDuration();
		
		
		assert  (dateLue.getTime()-dateAttendue.getTime())<1000L
		:"lu "+corresExterneLue.getDefaultDuration()+" attendu="+calendar.getTime()+", diff="+(dateLue.getTime()-dateAttendue.getTime());
	}
	
	@Test(groups="tests unitaires", description="verification du nettoyage par rollback")
    public void importAvecRollback()
    {
//		List<Transporteur> trsps = transporteurManager.lire();
//		for (Transporteur transporteur : trsps) {
//			transporteurManager.supprimer( transporteur.getId());
//		}
		
		List<Reseau> rsx = reseauManager.lire();
		for (Reseau reseau : rsx) {
			reseauManager.supprimer( reseau.getId());
		}
		
		
		ChouettePTNetworkTypeType ligneXML = generateurDonnee.creerChouettePTNetwork(4, 15, 8);
		ChouettePTNetworkTypeType ligneXMLmemeCode = generateurDonnee.creerChouettePTNetwork(4, 15, 8);
		
		String idTridentTrsp = ligneXML.getCompany(0).getObjectId();
		String codePerenneTrsp = ligneXML.getCompany(0).getRegistration().getRegistrationNumber();
		
		String idTridentReseau = ligneXML.getPTNetwork().getObjectId();
		String codePerenneReseau = ligneXML.getPTNetwork().getRegistration().getRegistrationNumber();
		
		logger.debug( "idTridentTrsp="+idTridentTrsp);
		// fixer un autre code TRIDENT pour un autre transporteur
		// => un 2° trsp doit etre créé
		ligneXMLmemeCode.getCompany(0).setObjectId( idTridentTrsp+"*");
		ligneXMLmemeCode.getCompany(0).getRegistration().setRegistrationNumber( codePerenneTrsp+"*");
		
		logger.debug( "idTridentReseau="+idTridentReseau);
		// fixer le meme code TRIDENT pour un autre reseau
		// => une exception doit etre levée
		ligneXMLmemeCode.getPTNetwork().setObjectId( idTridentReseau);
		ligneXMLmemeCode.getPTNetwork().getRegistration().setRegistrationNumber( codePerenneReseau+"*");
		
		ILectureEchange lectureEchange = lecteurEchangeXML.lire(ligneXML);
		importateur.importer( false, lectureEchange);
		
		// provoquer le rollback
		try
		{
			ILectureEchange lectureEchangeMemeCode = lecteurEchangeXML.lire(ligneXMLmemeCode);
			importateur.importer( false, lectureEchangeMemeCode);
			
			assert false:"Le 2° reseau duplique l'objectId du 1er reseau ("+idTridentReseau+"), une exception doit stopper l'import";
		}
		catch( Exception e)
		{
			logger.error( e.getMessage(), e);
		}
		
		// vérifier l'efficacité du rollback:
		// le transporteur est chargé avant le réseau
		try
		{
			Transporteur trsp = transporteurManager.lireParObjectId( idTridentTrsp+"*");
			assert false:"Le 2° trsp n'a pas été supprimé par rollback";
		}
		catch( ServiceException e)
		{
			assert CodeIncident.IDENTIFIANT_INCONNU.equals( e.getCode());
		}
    }	
	
	@Test(groups="tests unitaires", description="chaine complete d'import d'une structure d'echange")
    public void importChaineComplete()
    {
		ChouettePTNetworkTypeType chouettePTNetwork = generateurDonnee.creerChouettePTNetwork(4, 15, 8);
		
		logger.debug( "Attente");
//		try
//		{
//			Thread.currentThread().sleep( 20000L);
//		}
//		catch( Exception e)
//		{
//		}
		logger.debug( "Démarrage réel");
		
		ILectureEchange lectureEchange = lecteurEchangeXML.lire(chouettePTNetwork);
		importateur.importer( false, lectureEchange);
    }
	
//	@Test(groups="charge", description="chaine complete d'import d'une structure d'echange")
    public void importChaineCompleteCharge()
    {
		ChouettePTNetworkTypeType chouettePTNetwork = generateurDonnee.creerChouettePTNetwork(4, 50, 1000);
		
		logger.debug( "Attente");
//		try
//		{
//			Thread.currentThread().sleep( 20000L);
//		}
//		catch( Exception e)
//		{
//		}
		logger.debug( "Démarrage réel");
		
		ILectureEchange lectureEchange = lecteurEchangeXML.lire(chouettePTNetwork);
		importateur.importer( false, lectureEchange);
    }	
	@Test(groups="tests d'ecriture d'un fichier de test pour import", description="ecriture d'un fichier d'import")
	public void produireFichierEchangeXML()
	{
		ChouettePTNetworkTypeType chouettePTNetwork = generateurDonnee.creerChouettePTNetwork(4, 15, 8);
		
		File file = new File( "echange_chouette_aleatoire.xml");
		
		try 
		{
			lecteurFichierXML.ecrire( chouettePTNetwork, file);
		} 
		catch ( Exception e) 
		{
			logger.error( e.getMessage(), e);
			assert false: e.getMessage();
		}
	}
	
//	@Test(groups="tests brouillon", description="chaine complete d'import d'une structure d'echange")
	public void modulo()
	{
		int j = 12;
		int i = 15;
		
		logger.debug( "12%3="+(j%3));
	}
	
	@Test(groups="tests charge", description="recherche de la limite memoire sur les donnees en entree")
	public void chargement()
	{
//		int max_courses = 10000;
//		List<Course> lesCourses = new ArrayList<Course>( max_courses);
//		for (int i = 0; i < max_courses; i++) 
//		{
//			Course course = generateurDonnee.creerCourse( 0L);
//			lesCourses.add( course);
//		}
		
//		int max_horaire = 550000;
//		List<Horaire> lesHoraires = new ArrayList<Horaire>( max_horaire);
//		for (int i = 0; i < max_horaire; i++) 
//		{
//			Time time = new Time( 1000L);
//			Horaire horaire = generateurDonnee.creerHoraire(0L, 0L, 0, time);
//			lesHoraires.add( horaire);
//		}
		
		int max_arrets = 140000;
		List<ArretItineraire> lesArrets = new ArrayList<ArretItineraire>( max_arrets);
		for (int i = 0; i < max_arrets; i++) 
		{
			ArretItineraire arret = generateurDonnee.creerArret(0L);
			arret.setPosition( i);
			lesArrets.add( arret);
		}
		
		
//		List<SimpleArret> lesSimplesArrets = new ArrayList<SimpleArret>( max_arrets);
//		for (int i = 0; i < max_arrets; i++) 
//		{
//			SimpleArret arret = generateurDonnee.creerSimpleArret(0L);
//			arret.setPosition( i);
//			lesSimplesArrets.add( arret);
//		}
		
	}
	
//	@Test(groups="tests charge", description="recherche de la limite memoire sur les donnees en entree")
	public void limiteInt()
	{
	      Timestamp unTimestamp = null;
	      
	      Calendar unCalendrier = Calendar.getInstance();
	      unCalendrier.setTimeInMillis( System.currentTimeMillis());
	      unTimestamp = new Timestamp( unCalendrier.getTime().getTime());
	      unTimestamp.setNanos( ( ( 1000*Integer.MAX_VALUE)%100000+1000000*unCalendrier.get( Calendar.MILLISECOND))%999999999);
	}
	
	public static void main( String[] args)
	{
		try
		{
			ApplicationContext applicationContext = SingletonManager.getApplicationContext();
	
			GenerateurDonnee monGenerateur = new GenerateurDonnee();
			IImportateur monImportateur = ( IImportateur)applicationContext.getBean( "importateur");
			ChouettePTNetworkTypeType chouettePTNetwork = monGenerateur.creerChouettePTNetwork(4, 15, 8);
			
			ILecteurEchangeXML monLecteurEchange = ( ILecteurEchangeXML)applicationContext.getBean( "lecteurEchangeXML");
			ILectureEchange lectureEchange = monLecteurEchange.lire( chouettePTNetwork);
			monImportateur.importer(false, lectureEchange);
		}
		catch( Exception e)
		{
			logger.error( e.getMessage(), e);
		}
		
//		int max_arrets = 1000000;
//		List<Arret> lesArrets = new ArrayList<Arret>( max_arrets);
//		for (int i = 0; i < max_arrets; i++) 
//		{
//			Arret arret = monGenerateur.creerArret(0L);
//			arret.setPosition( i);
//			lesArrets.add( arret);
//		}
	}
}
