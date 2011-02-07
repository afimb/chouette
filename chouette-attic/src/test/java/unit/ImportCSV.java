package unit;

import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.modele.ArretItineraire;
import fr.certu.chouette.modele.Course;
import fr.certu.chouette.modele.Horaire;
import fr.certu.chouette.modele.Itineraire;
import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.modele.TableauMarche;
import fr.certu.chouette.modele.Transporteur;
import fr.certu.chouette.service.commun.CodeIncident;
import fr.certu.chouette.service.commun.ServiceException;
import fr.certu.chouette.service.fichier.IImportateur;
import fr.certu.chouette.service.importateur.monoligne.ILecteurCSV;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = {"classpath:testContext.xml"})
public class ImportCSV extends AbstractTestNGSpringContextTests {
	
	private static final Logger logger = Logger.getLogger(ImportCSV.class);
	private final static String REP = "target/test-classes";
	private final static String SEP = "$";
	private ILecteurCSV lecteurCSV;
	private IImportateur importateur = null;
	
	@BeforeMethod
	protected void getBeans() {
            lecteurCSV = (ILecteurCSV) applicationContext.getBean("lecteurCSV");
            importateur = (IImportateur) applicationContext.getBean("importateur");
	}
	
	@Test(groups="tests unitaires", description="les 2 (le 1 et le 4) allers suivent un meme itineraire, mais sont distincts, il n'y a que des aller")
	public void importRERA_2() {
		String nom = "RERA_3itineraires_4aller.csv";
		try
		{
			lecteurCSV.lire( nom);
		}
		catch( ServiceException e)
		{
			if ( CodeIncident.ERR_CSV_NON_TROUVE.equals( e.getCode()))
				lecteurCSV.lire( REP + File.separator + nom);
			else
				throw e;
		}
		ILectureEchange lectureEchange = lecteurCSV.getLectureEchange();
		
		Transporteur transporteur = lectureEchange.getTransporteur();
		assert transporteur!=null: "auncun transporteur instancie apres lecture";
		assert transporteur.getCode()!=null : "code non defini";
		assert transporteur.getRegistrationNumber()!=null && transporteur.getRegistrationNumber().length()>0: "code registre non defini";
		assert transporteur.getFax()!=null : "fax non defini";
		assert transporteur.getName()!=null && transporteur.getName().length()>0: "nom non defini";
		
		List<Course> courses = lectureEchange.getCourses();
		Set<String> idCourses = new HashSet<String>();
		for (Course course : courses) {
			idCourses.add( course.getObjectId());
		}
		assert courses.size()==5;
		
		List<Itineraire> itineraires = lectureEchange.getItineraires();
		Set<String> idItineraires = new HashSet<String>();
		for (Itineraire itineraire : itineraires) {
			idItineraires.add( itineraire.getObjectId());
		}
		assert itineraires.size()==3;
		
		List<PositionGeographique> arretsPhysiques = lectureEchange.getArretsPhysiques();
		assert arretsPhysiques.size()==46:46+" arrets attendus, "+arretsPhysiques.size()+" trouves";
		Set<String> idPhysiques = new HashSet<String>();
		for (PositionGeographique physique : arretsPhysiques) {
			idPhysiques.add( physique.getObjectId());
		}
		
		List<ArretItineraire> arrets = lectureEchange.getArrets();
		assert arrets.size()==73:73+" arrets attendus, "+arrets.size()+" arrets lus";
		Set<String> idArretsItineraire = new HashSet<String>();
		Set<String> idArrets = new HashSet<String>();
		Set<String> proprietesUniques = new HashSet<String>();
		for (ArretItineraire arret : arrets) {
			idArrets.add( arret.getObjectId());
			assert idPhysiques.contains( arret.getContainedIn());
			
			String idItineraire = lectureEchange.getItineraireArret( arret.getObjectId());
			assert idItineraires.contains( idItineraire);
			idArretsItineraire.add( idItineraire);
			
			String proprieteUnique = arret.getObjectId();
			assert !proprietesUniques.contains( proprieteUnique);
			proprietesUniques.add( proprieteUnique);
		}
		assert idArretsItineraire.size()==itineraires.size();
		
		List<Horaire> horaires = lectureEchange.getHoraires();
		Set<String> idPassagesArrets = new HashSet<String>();
		for (Horaire horaire : horaires) {
			assert idCourses.contains( horaire.getVehicleJourneyId());
			assert idArrets.contains( horaire.getStopPointId());
			idPassagesArrets.add( horaire.getStopPointId());
		}
		assert idPassagesArrets.size()==idArrets.size();
		
		List<TableauMarche> marches = lectureEchange.getTableauxMarche();
		for (TableauMarche marche : marches) {
			int totalCourses = marche.getVehicleJourneyIdCount();
			for (int i = 0; i < totalCourses; i++) {
				assert idCourses.contains( marche.getVehicleJourneyId( i));
			}
			assert marche.getPeriodes().size()==1;
			assert marche.getPeriodes().get( 0).getDebut()!=null;
			assert marche.getPeriodes().get( 0).getFin()!=null;
		}
		assert marches.size()==2;
		

		importateur.importer( true, lectureEchange);
	}
	
	@Test(groups="tests unitaires", description="1 retour a les memes arrets qu'1 aller")
	public void importRERA_4()
	{
		
		String nom = "RERA_3itineraires_2aller_1retour.csv";
		try
		{
			lecteurCSV.lire( nom);
		}
		catch( ServiceException e)
		{
			if ( CodeIncident.ERR_CSV_NON_TROUVE.equals( e.getCode()))
				lecteurCSV.lire( REP + File.separator + nom);
			else
				throw e;
		}
		ILectureEchange lectureEchange = lecteurCSV.getLectureEchange();
		
		Transporteur transporteur = lectureEchange.getTransporteur();
		assert transporteur!=null: "auncun transporteur instancie apres lecture";
		assert transporteur.getCode()!=null : "code non defini";
		assert transporteur.getRegistrationNumber()!=null && transporteur.getRegistrationNumber().length()>0: "code registre non defini";
		assert transporteur.getFax()!=null : "fax non defini";
		assert transporteur.getName()!=null && transporteur.getName().length()>0: "nom non defini";
		
		List<Course> courses = lectureEchange.getCourses();
		Set<String> idCourses = new HashSet<String>();
		for (Course course : courses) {
			idCourses.add( course.getObjectId());
		}
		assert courses.size()==3;
		
		List<Itineraire> itineraires = lectureEchange.getItineraires();
		Set<String> idItineraires = new HashSet<String>();
		for (Itineraire itineraire : itineraires) {
			idItineraires.add( itineraire.getObjectId());
		}
		assert itineraires.size()==3:3+" itineraires attendus, "+itineraires.size()+" trouvés";
		
		List<PositionGeographique> arretsPhysiques = lectureEchange.getArretsPhysiques();
		assert arretsPhysiques.size()==27:27+" arrets physiques attendus, "+arretsPhysiques.size()+" lus";
		Set<String> idPhysiques = new HashSet<String>();
		for (PositionGeographique physique : arretsPhysiques) {
			idPhysiques.add( physique.getObjectId());
		}
		
		List<ArretItineraire> arrets = lectureEchange.getArrets();
		//assert arrets.size()==94;
		logger.debug( arrets.size());
		Set<String> idArretsItineraire = new HashSet<String>();
		Set<String> idArrets = new HashSet<String>();
		for (ArretItineraire arret : arrets) {
			idArrets.add( arret.getObjectId());
			assert idPhysiques.contains( arret.getContainedIn());
			
			String idItineraire = lectureEchange.getItineraireArret( arret.getObjectId());
			assert idItineraires.contains( idItineraire);
			idArretsItineraire.add( idItineraire);
		}
		assert idArretsItineraire.size()==itineraires.size();
		
		List<Horaire> horaires = lectureEchange.getHoraires();
		Set<String> idPassagesArrets = new HashSet<String>();
		for (Horaire horaire : horaires) {
			assert idCourses.contains( horaire.getVehicleJourneyId());
			assert idArrets.contains( horaire.getStopPointId());
			idPassagesArrets.add( horaire.getStopPointId());
		}
		assert idPassagesArrets.size()==idArrets.size();
		
		
		List<TableauMarche> marches = lectureEchange.getTableauxMarche();
		for (TableauMarche marche : marches) {
			int totalCourses = marche.getVehicleJourneyIdCount();
			for (int i = 0; i < totalCourses; i++) {
				assert idCourses.contains( marche.getVehicleJourneyId( i));
			}
			assert marche.getPeriodes().size()==1;
			assert marche.getPeriodes().get( 0).getDebut()!=null;
			assert marche.getPeriodes().get( 0).getFin()!=null;
		}
		assert marches.size()==3;

		importateur.importer( true, lectureEchange);
	}	
	@Test(groups="tests unitaires", description="les 2 allers suivent un meme itineraire, mais sont distincts, il y a 2 retours")
	public void importRERA_3()
	{
		String nom = "RERA_3itineraires_2retour_2aller.csv";
		try
		{
			lecteurCSV.lire( nom);
		}
		catch( ServiceException e)
		{
			if ( CodeIncident.ERR_CSV_NON_TROUVE.equals( e.getCode()))
				lecteurCSV.lire( REP + File.separator + nom);
			else
				throw e;
		}
		ILectureEchange lectureEchange = lecteurCSV.getLectureEchange();
		
		Transporteur transporteur = lectureEchange.getTransporteur();
		assert transporteur!=null: "auncun transporteur instancie apres lecture";
		assert transporteur.getCode()!=null : "code non defini";
		assert transporteur.getRegistrationNumber()!=null && transporteur.getRegistrationNumber().length()>0: "code registre non defini";
		assert transporteur.getFax()!=null : "fax non defini";
		assert transporteur.getName()!=null && transporteur.getName().length()>0: "nom non defini";
		
		List<Course> courses = lectureEchange.getCourses();
		Set<String> idCourses = new HashSet<String>();
		for (Course course : courses) {
			idCourses.add( course.getObjectId());
		}
		assert courses.size()==4;
		
		List<Itineraire> itineraires = lectureEchange.getItineraires();
		Set<String> idItineraires = new HashSet<String>();
		for (Itineraire itineraire : itineraires) {
			idItineraires.add( itineraire.getObjectId());
		}
		assert itineraires.size()==3:3+" itineraires attendus, "+itineraires.size()+" trouvés";
		
		List<PositionGeographique> arretsPhysiques = lectureEchange.getArretsPhysiques();
		assert arretsPhysiques.size()==40:40+" arrets physiques attendus, "+arretsPhysiques.size()+" lus";
		Set<String> idPhysiques = new HashSet<String>();
		for (PositionGeographique physique : arretsPhysiques) {
			idPhysiques.add( physique.getObjectId());
		}
		
		List<ArretItineraire> arrets = lectureEchange.getArrets();
		assert arrets.size()==70:70+" arrets attendus, "+arrets.size()+" arrets lus";
		Set<String> idArretsItineraire = new HashSet<String>();
		Set<String> idArrets = new HashSet<String>();
		Set<String> proprietesUniques = new HashSet<String>();
		for (ArretItineraire arret : arrets) {
			idArrets.add( arret.getObjectId());
			assert idPhysiques.contains( arret.getContainedIn());
			
			String idItineraire = lectureEchange.getItineraireArret( arret.getObjectId());
			assert idItineraires.contains( idItineraire);
			idArretsItineraire.add( idItineraire);
			
			String proprieteUnique = arret.getObjectId();
			assert !proprietesUniques.contains( proprieteUnique);
			proprietesUniques.add( proprieteUnique);
		}
		assert idArretsItineraire.size()==itineraires.size();
		
		List<Horaire> horaires = lectureEchange.getHoraires();
		Set<String> idPassagesArrets = new HashSet<String>();
		for (Horaire horaire : horaires) {
			assert idCourses.contains( horaire.getVehicleJourneyId());
			assert idArrets.contains( horaire.getStopPointId());
			idPassagesArrets.add( horaire.getStopPointId());
		}
		assert idPassagesArrets.size()==idArrets.size();

		
		List<TableauMarche> marches = lectureEchange.getTableauxMarche();
		for (TableauMarche marche : marches) {
			int totalCourses = marche.getVehicleJourneyIdCount();
			for (int i = 0; i < totalCourses; i++) {
				assert idCourses.contains( marche.getVehicleJourneyId( i));
			}
			assert marche.getPeriodes().size()==1;
			assert marche.getPeriodes().get( 0).getDebut()!=null;
			assert marche.getPeriodes().get( 0).getFin()!=null;
		}
		assert marches.size()==3;

		importateur.importer( true, lectureEchange);
		
	}
	
	
	@Test(groups="tests unitaires", description="les 2 allers suivent un meme itineraire, mais sont distincts, il y a 2 retours")
	public void testChaine()
	{
		String titi = "fg\\esfd\\dsfd";
		titi = titi.replaceAll( "\\\\", "\\\\\\\\");
		logger.debug( titi);
	}
}
