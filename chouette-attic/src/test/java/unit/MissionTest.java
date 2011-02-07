package unit;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.exolab.castor.types.Time;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import fr.certu.chouette.manager.SingletonManager;
import fr.certu.chouette.modele.ArretItineraire;
import fr.certu.chouette.modele.Course;
import fr.certu.chouette.modele.Horaire;
import fr.certu.chouette.modele.Itineraire;
import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.modele.Mission;
import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.service.commun.CodeIncident;
import fr.certu.chouette.service.commun.ServiceException;
import fr.certu.chouette.service.database.ICourseManager;
import fr.certu.chouette.service.database.IHoraireManager;
import fr.certu.chouette.service.database.IItineraireManager;
import fr.certu.chouette.service.database.ILigneManager;
import fr.certu.chouette.service.database.IMissionManager;
import fr.certu.chouette.service.database.IPositionGeographiqueManager;
import fr.certu.chouette.service.database.impl.modele.EtatMajArretItineraire;
import fr.certu.chouette.service.database.impl.modele.EtatMajHoraire;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;

@ContextConfiguration(locations = {"classpath:testContext.xml"})
public class MissionTest extends AbstractTestNGSpringContextTests { 
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger
			.getLogger(MissionTest.class);
	
	
	private final static int MAX_ARRETS = 6;
	
	private IItineraireManager itineraireManager;
	private ILigneManager ligneManager;
	private ICourseManager courseManager;
	private IMissionManager missionManager;
	private IPositionGeographiqueManager positionGeographiqueManager;
	private IHoraireManager horaireManager;

	@BeforeMethod
	protected void getBeans() throws Exception
	{
		itineraireManager = ( IItineraireManager)applicationContext.getBean( "itineraireManager");
		ligneManager = ( ILigneManager)applicationContext.getBean( "ligneManager");
		courseManager = ( ICourseManager)applicationContext.getBean( "courseManager");
		missionManager = ( IMissionManager)applicationContext.getBean( "missionManager");
		horaireManager = ( IHoraireManager)applicationContext.getBean( "horaireManager");
		positionGeographiqueManager = ( IPositionGeographiqueManager)applicationContext.getBean( "positionGeographiqueManager");
	}
	
	@Test(groups="tests unitaires services persistence", description="synchronisation des missions sur opération de mise à jour de course")
	public void synchroMissionApresMajCourse()
	{
	   Ligne uneLigne = GenerateurDonnee.creerLigne();
	   ligneManager.creer( uneLigne);
	
	   Itineraire aller = GenerateurDonnee.creerItineraire( uneLigne.getId());
	   itineraireManager.creer( aller);
	
	   List<ArretItineraire> arretsItineraire = placerArrets(aller);
	
	   Course courseA = GenerateurDonnee.creerCourse( aller.getId());
	   courseManager.creer(courseA);
	   affecterHorairesPairs( arretsItineraire, courseA.getId());
	   Course courseLueA = courseManager.lire( courseA.getId());
	   assert courseLueA.getIdMission()!=null;
	   Mission missionA = missionManager.lire( courseLueA.getIdMission());
	   assert missionA!=null;
	
	   Course courseB = GenerateurDonnee.creerCourse( aller.getId());
	   courseManager.creer(courseB);
	   affecterHorairesPairs( arretsItineraire, courseB.getId());
	   Course courseLueB = courseManager.lire( courseB.getId());
	   Mission missionB = missionManager.lire( courseLueB.getIdMission());
	   assert missionB!=null;
	
	   assert courseLueA.getIdMission().equals( courseLueB.getIdMission()):
		   "Les 2 courses devraient partager leur mission";
	
	   // ajout d'un horaire sur un arrêt impair
	   ajouterHoraires( 1, arretsItineraire, courseB.getId());
	   courseLueB = courseManager.lire( courseB.getId());
	
	   Long idMissionB = courseLueB.getIdMission();
	   assert idMissionB!=null:"La course n'a plus de mission";
	   assert !courseLueA.getIdMission().equals( idMissionB):
		   "Les 2 courses devraient se rattacher à des missions différentes";
	
	   // retrait de l'horaire sur l'arrêt impair
	   List<Horaire> horaires = courseManager.getHorairesCourseOrdonnes( courseB.getId());
	   retirerHoraires( horaires.get( 1));
	   courseLueB = courseManager.lire( courseB.getId());
	
	   assert courseLueB.getIdMission()!=null:"La course n'a plus de mission";
	   assert courseLueA.getIdMission().equals( courseLueB.getIdMission()):
		   "Les 2 courses devraient partager pas leur mission";
	
	   try
	   {
		   Mission missionBLue = missionManager.lire( idMissionB);
		   assert missionBLue==null:"Cette mission n'est plus référencée et aurait du être détruite";
	   }
	   catch( ServiceException e)
	   {
		   if ( !CodeIncident.IDENTIFIANT_INCONNU.equals( e.getCode()))
		   {
			   logger.debug( e);
			   assert false:"Incident de consultation "+e.getMessage()+" code="+e.getCode();
		   }
	   }
	
	   List<PositionGeographique> arretsPhysiques = ligneManager.getArretsPhysiques( uneLigne.getId());
	   ligneManager.supprimer( uneLigne.getId());
	
	   // vérifier que la mission a aussi été supprimée
	
	   try
	   {
		   Mission missionLue = missionManager.lire( courseLueA.getIdMission());
		   assert missionLue==null:"Cette mission aurait du être détruite avec la ligne";
	   }
	   catch( ServiceException e)
	   {
		   if ( !CodeIncident.IDENTIFIANT_INCONNU.equals( e.getCode()))
		   {
			   logger.debug( e);
			   assert false:"Incident de consultation "+e.getMessage()+" code="+e.getCode();
		   }
	   }
	
	   for (PositionGeographique arretPhysique : arretsPhysiques) {
		   positionGeographiqueManager.supprimer( arretPhysique.getId());
	   }
	}

	@Test(groups="tests unitaires services persistence", description="synchronisation des missions sur une suppression d'arrêt")
	public void synchroMissionApresSuppressionArret()
	{
	   Ligne uneLigne = GenerateurDonnee.creerLigne();
	   ligneManager.creer( uneLigne);
	   
	   Itineraire aller = GenerateurDonnee.creerItineraire( uneLigne.getId());
	   itineraireManager.creer( aller);
	   
	   List<ArretItineraire> arretsItineraire = placerArrets(aller);
	   
	   Course courseA = GenerateurDonnee.creerCourse( aller.getId());
	   courseManager.creer(courseA);
	   affecterHorairesPairs( arretsItineraire, courseA.getId());
	   Course courseLueA = courseManager.lire( courseA.getId());
	   assert courseLueA.getIdMission()!=null;
	   
	   Course courseB = GenerateurDonnee.creerCourse( aller.getId());
	   courseManager.creer(courseB);
	   affecterHorairesPairs( arretsItineraire, courseB.getId());
	   Course courseLueB = courseManager.lire( courseB.getId());
	   
	   // ajout d'un horaire sur un arrêt impair
	   int positionImpaire = 1;
	   ajouterHoraires( positionImpaire, arretsItineraire, courseB.getId());
	   courseLueB = courseManager.lire( courseB.getId());
	   
	   Long idMissionB = courseLueB.getIdMission();
	   assert idMissionB!=null:"La course n'a plus de mission";
	   assert !courseLueA.getIdMission().equals( idMissionB):
		   "Les 2 courses devraient se rattacher à des missions différentes";

	   // retrait l'arrêt en position impair
	   List<EtatMajArretItineraire> majArretsItineraire = new ArrayList<EtatMajArretItineraire>();
	   majArretsItineraire.add( EtatMajArretItineraire.creerSuppression( arretsItineraire.get( positionImpaire).getId()));
	   itineraireManager.modifierArretsItineraire( aller.getId(), majArretsItineraire);

	   courseLueB = courseManager.lire( courseB.getId());
	   courseLueA = courseManager.lire( courseA.getId());
	   
	   Long idMissionBapresSuppr = courseLueB.getIdMission();
	   assert idMissionBapresSuppr!=null:"La course n'a plus de mission";
	   assert courseLueA.getIdMission().equals( idMissionBapresSuppr):
		   "Les 2 courses devraient partager leur mission, idMission attendue pour la course ("+
		   courseB.getId()+"):\n"+courseLueA.getIdMission()+
		   ", mission trouvée "+idMissionBapresSuppr;
	   
	   List<PositionGeographique> arretsPhysiques = ligneManager.getArretsPhysiques( uneLigne.getId());
	   ligneManager.supprimer( uneLigne.getId());
	   
	   // vérifier que la mission a aussi été supprimée
	   
	   try
	   {
		   Mission missionLue = missionManager.lire( courseLueA.getIdMission());
		   assert missionLue==null:"Cette mission aurait du être détruite avec la ligne";
	   }
	   catch( ServiceException e)
	   {
		   if ( !CodeIncident.IDENTIFIANT_INCONNU.equals( e.getCode()))
		   {
			   logger.debug( e);
			   assert false:"Incident de consultation "+e.getMessage()+" code="+e.getCode();
		   }
	   }
	   
	   for (PositionGeographique arretPhysique : arretsPhysiques) {
		   positionGeographiqueManager.supprimer( arretPhysique.getId());
	   }
	}

	private List<ArretItineraire> placerArrets(Itineraire aller) {
		List<EtatMajArretItineraire> majItineraire = new ArrayList<EtatMajArretItineraire>();
		   for (int i = 0; i < MAX_ARRETS; i++) 
		   {
			   EtatMajArretItineraire etatMaj = EtatMajArretItineraire.creerCreation( i, "A"+i);
			   majItineraire.add( etatMaj);
		   }
		   
		   itineraireManager.modifierArretsItineraire( aller.getId(), majItineraire);
		   return itineraireManager.getArretsItineraire( aller.getId());
	}
	
	private void affecterHorairesPairs( List<ArretItineraire> arretsItineraire, Long idCourse)
	{
	   Long heureDepart = 8L * 1000L * 3600L;
	   Long duree = 3L * 1000L * 60L;
	   
	   int totalHoraires = 0;
	   Map<Long, Date> heureParArret = new Hashtable<Long, Date>();
	   List<EtatMajHoraire> majHoraires = new ArrayList<EtatMajHoraire>();
	   for (int j = 0; j < MAX_ARRETS; j++) 
	   {
		   if ( j%2==0)
		   {
			   totalHoraires++;
			   Long idArret = arretsItineraire.get( j).getId();
			   Time heure = new Time( heureDepart + j*duree);
			   
			   majHoraires.add(EtatMajHoraire.getCreation(idArret, idCourse,
						heure.toDate()));
			   heureParArret.put( idArret, heure.toDate());
		   }
	   }
	   horaireManager.modifier(majHoraires);
		
	}
	
	private void ajouterHoraires( int position, List<ArretItineraire> arretsItineraire, Long idCourse)
	{
	   Long heureDepart = 8L * 1000L * 3600L;
	   Long duree = 3L * 1000L * 60L;
	   
	   Map<Long, Date> heureParArret = new Hashtable<Long, Date>();
	   List<EtatMajHoraire> majHoraires = new ArrayList<EtatMajHoraire>();

	   Long idArret = arretsItineraire.get( position).getId();
	   Time heure = new Time( heureDepart + position*duree);
	   
	   majHoraires.add(EtatMajHoraire.getCreation(idArret, idCourse,
				heure.toDate()));
	   heureParArret.put( idArret, heure.toDate());

	   horaireManager.modifier(majHoraires);
	}
	
	private void retirerHoraires( Horaire horaire)
	{
	   List<EtatMajHoraire> majHoraires = new ArrayList<EtatMajHoraire>();
	   majHoraires.add(EtatMajHoraire.getSuppression(horaire));
	   horaireManager.modifier(majHoraires);
	}

}
