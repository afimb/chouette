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

import fr.certu.chouette.dao.hibernate.SelectionSpecifique;
import fr.certu.chouette.manager.SingletonManager;
import fr.certu.chouette.modele.ArretItineraire;
import fr.certu.chouette.modele.Course;
import fr.certu.chouette.modele.Horaire;
import fr.certu.chouette.modele.Itineraire;
import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.service.database.ICourseManager;
import fr.certu.chouette.service.database.IHoraireManager;
import fr.certu.chouette.service.database.IItineraireManager;
import fr.certu.chouette.service.database.ILigneManager;
import fr.certu.chouette.service.database.impl.modele.EtatMajArretItineraire;
import fr.certu.chouette.service.database.impl.modele.EtatMajHoraire;

public class HoraireDepartTest 
{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger
			.getLogger(HoraireDepartTest.class);
	private IItineraireManager itineraireManager;
	private ILigneManager ligneManager;
	private ICourseManager courseManager;
	private IHoraireManager horaireManager;
	private SelectionSpecifique modificationSpecifique;

	@BeforeSuite
	protected void setUp() throws Exception 
	{
		ApplicationContext applicationContext = SingletonManager.getApplicationContext();

		itineraireManager = ( IItineraireManager)applicationContext.getBean( "itineraireManager");
		ligneManager = ( ILigneManager)applicationContext.getBean( "ligneManager");
		courseManager = ( ICourseManager)applicationContext.getBean( "courseManager");
		horaireManager = ( IHoraireManager)applicationContext.getBean( "horaireManager");
		modificationSpecifique = ( SelectionSpecifique)applicationContext.getBean( "modificationSpecifique");
	}
	
	@Test(groups="tests unitaires", description="controle de la validation des horaires de courses")
	public void validationHoraire()
	{
		int MAX_ARRETS = 7;
		int MAX_COURSES = 1;
		
		List<Date> horaires = new ArrayList<Date>();
		long depart = 0L;
		
//		for (int i = 0; i < MAX_ARRETS*MAX_COURSES; i++) {
//			horaires.add( new Date( depart + i * 1000L * 60L * 45L));
//		}
//		horaires.set( 1, null);
		horaires.add( new Date( depart + 1000L * 60L * 50L));
		horaires.add( null);
		horaires.add( new Date( depart + 1000L * 60L * 45L + 3600L * 1000L));
		horaires.add( new Date( depart + 1000L * 60L * 45L * 2L + 3600L * 1000L));
		horaires.add( new Date( depart + 1000L * 60L * 45L * 3L + 3600L * 1000L));
		horaires.add( new Date( depart + 1000L * 60L * 45L * 4L));
		horaires.add( new Date( depart + 1000L * 60L * 45L * 5L));
		
		List<Integer> invalides = horaireManager.filtreHorairesInvalides( horaires, MAX_ARRETS);
		for (Integer invalide : invalides) {
			logger.debug( "invalide="+invalide);
		}
	}

	
	@Test(groups="tests unitaires services persistence", description="deplacer les arrets sur un itineraire")
	public void deplacerArretsAvecCourse()
	{
	   int maxArrets = 6;
	   Ligne uneLigne = GenerateurDonnee.creerLigne();
	   ligneManager.creer( uneLigne);
	   
	   Itineraire aller = GenerateurDonnee.creerItineraire( uneLigne.getId());
	   itineraireManager.creer( aller);
	   
	   List<ArretItineraire> arretsItineraire = itineraireManager.getArretsItineraire( aller.getId());
	   assert arretsItineraire.size()==0;
	   
	   List<EtatMajArretItineraire> majItineraire = new ArrayList<EtatMajArretItineraire>();
	   for (int i = 0; i < maxArrets; i++) 
	   {
		   EtatMajArretItineraire etatMaj = EtatMajArretItineraire.creerCreation( i, "A"+i);
		   majItineraire.add( etatMaj);
	   }
	   
	   itineraireManager.modifierArretsItineraire( aller.getId(), majItineraire);
	   
	   arretsItineraire = itineraireManager.getArretsItineraire( aller.getId());
	   assert arretsItineraire.size()==maxArrets;
	   
	   Course course = GenerateurDonnee.creerCourse( aller.getId());
	   courseManager.creer(course);
	   
	   Long idCourse = course.getId();
	   
	   Long heureDepart = 8L * 1000L * 3600L;
	   Long duree = 3L * 1000L * 60L;
	   
	   int totalHoraires = 0;
	   Map<Long, Date> heureParArret = new Hashtable<Long, Date>();
	   List<Long> horairesIds = new ArrayList<Long>();
	   List<EtatMajHoraire> majHoraires = new ArrayList<EtatMajHoraire>();
	   for (int j = 0; j < maxArrets; j++) 
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
	   
	   List<Horaire> horairesLus = courseManager.getHorairesCourseOrdonnes(idCourse);
	   assert totalHoraires==horairesLus.size():"Echec à la sauvegarde des horaires";
	   
	   for (int i = 0; i < horairesIds.size(); i++) 
	   {
		   assert horairesLus.get( i).getDepart()==(i==0);
	   }
	   
	   // échange des 2 premiers arrets
	   EtatMajArretItineraire etatMaj1 = EtatMajArretItineraire.creerDeplace(1, arretsItineraire.get( 0).getId());
	   EtatMajArretItineraire etatMaj2 = EtatMajArretItineraire.creerDeplace(0, arretsItineraire.get( 1).getId());
	   majItineraire.clear();
	   majItineraire.add( etatMaj1);
	   majItineraire.add( etatMaj2);
	   itineraireManager.modifierArretsItineraire( aller.getId(), majItineraire);
	   
	   horairesLus = courseManager.getHorairesCourseOrdonnes( course.getId());
	   for (int i = 0; i < horairesLus.size(); i++) 
	   {
		   Horaire horaire = horairesLus.get(i);
		   
		   Long idArret = horaire.getIdArret();
		   assert (idArret.equals( arretsItineraire.get( 1).getId()) && horaire.getDepart())
		   || ( !idArret.equals(arretsItineraire.get( 1).getId()) && !horaire.getDepart());
		   
		   Date horaireLu = horaire.getArrivalTime();
		   assert !idArret.equals( arretsItineraire.get( 0).getId())
		   	: "L'arret, déplacé de la position 0 à la position 1, ne devrait plus avoir d'horaire";
		   
		   assert !idArret.equals( arretsItineraire.get( 1).getId())
		   || horaireLu.equals( heureParArret.get( arretsItineraire.get( 0).getId()))
		   : "L'arret, déplacé de la position 1 à la position 0, devrait avoir l'horaire "+heureParArret.get( arretsItineraire.get( 0).getId());
		   
		   if ( !idArret.equals( arretsItineraire.get( 1).getId())
				 && !idArret.equals( arretsItineraire.get( 0).getId()))
		   {
			   assert horaireLu.equals( heureParArret.get( idArret)) : 
				   "Les autres horaires restent accrochés aux mêmes arrêts";
		   }
	   }
	}
	
	@Test(groups="tests unitaires services persistence", description="maj du depart de course apres suppression d'arret")
	public void retraitArretDepart()
	{
	   int maxArrets = 6;
	   Ligne uneLigne = GenerateurDonnee.creerLigne();
	   ligneManager.creer( uneLigne);
	   
	   Itineraire aller = GenerateurDonnee.creerItineraire( uneLigne.getId());
	   itineraireManager.creer( aller);
	   
	   List<ArretItineraire> arretsItineraire = itineraireManager.getArretsItineraire( aller.getId());
	   assert arretsItineraire.size()==0;
	   
	   List<EtatMajArretItineraire> majItineraire = new ArrayList<EtatMajArretItineraire>();
	   for (int i = 0; i < maxArrets; i++) 
	   {
		   EtatMajArretItineraire etatMaj = EtatMajArretItineraire.creerCreation( i, "A"+i);
		   majItineraire.add( etatMaj);
	   }
	   
	   itineraireManager.modifierArretsItineraire( aller.getId(), majItineraire);
	   
	   arretsItineraire = itineraireManager.getArretsItineraire( aller.getId());
	   assert arretsItineraire.size()==maxArrets;
	   
	   Course course = GenerateurDonnee.creerCourse( aller.getId());
	   courseManager.creer(course);
	   
	   Long idCourse = course.getId();
	   
	   Long heureDepart = 8L * 1000L * 3600L;
	   Long duree = 3L * 1000L * 60L;
	   
	   int totalHoraires = 0;
	   List<EtatMajHoraire> majHoraires = new ArrayList<EtatMajHoraire>();
	   for (int j = 0; j < maxArrets; j++) 
	   {
		   if ( j%2==0)
		   {
			   totalHoraires++;
			   Long idArret = arretsItineraire.get( j).getId();
			   Time heure = new Time( heureDepart + j*duree);
			   
			   majHoraires.add(EtatMajHoraire.getCreation(idArret, idCourse,
						heure.toDate()));
		   }
	   }
	   horaireManager.modifier(majHoraires);
	   
	   List<Horaire> horairesLus = courseManager.getHorairesCourseOrdonnes(idCourse);
	   assert totalHoraires==horairesLus.size():"Echec à la sauvegarde des horaires";
	   
	   for (int i = 0; i < horairesLus.size(); i++) 
	   {
		   assert horairesLus.get( i).getDepart()==(i==0);
		   assert ( horairesLus.get( i).getDepart() && horairesLus.get( i).getIdArret().equals( arretsItineraire.get( 0).getId()))
		   || !horairesLus.get( i).getDepart();
	   }
	   
	   // suppression du 1er arret d'itinéraire au départ de la course
	   EtatMajArretItineraire etatMaj1 = EtatMajArretItineraire.creerSuppression( arretsItineraire.get( 0).getId());
	   majItineraire.clear();
	   majItineraire.add( etatMaj1);
	   itineraireManager.modifierArretsItineraire( aller.getId(), majItineraire);
	   
	   horairesLus = courseManager.getHorairesCourseOrdonnes( course.getId());
	   assert (totalHoraires-1)==horairesLus.size():"Echec à la sauvegarde des horaires";
	   
	   for (int i = 0; i < horairesLus.size(); i++) 
	   {
		   assert horairesLus.get( i).getDepart()==(i==0);
		   assert ( horairesLus.get( i).getDepart() && horairesLus.get( i).getIdArret().equals( arretsItineraire.get( 2).getId()))
		   || !horairesLus.get( i).getDepart();
	   }
	}
	
	@Test(groups="tests unitaires services persistence", description="maj du depart de course apres modif d'horaire")
	public void majArretDepart()
	{
	   int maxArrets = 6;
	   Ligne uneLigne = GenerateurDonnee.creerLigne();
	   ligneManager.creer( uneLigne);
	   
	   Itineraire aller = GenerateurDonnee.creerItineraire( uneLigne.getId());
	   itineraireManager.creer( aller);
	   
	   List<ArretItineraire> arretsItineraire = itineraireManager.getArretsItineraire( aller.getId());
	   assert arretsItineraire.size()==0;
	   
	   List<EtatMajArretItineraire> majItineraire = new ArrayList<EtatMajArretItineraire>();
	   for (int i = 0; i < maxArrets; i++) 
	   {
		   EtatMajArretItineraire etatMaj = EtatMajArretItineraire.creerCreation( i, "A"+i);
		   majItineraire.add( etatMaj);
	   }
	   
	   itineraireManager.modifierArretsItineraire( aller.getId(), majItineraire);
	   
	   arretsItineraire = itineraireManager.getArretsItineraire( aller.getId());
	   assert arretsItineraire.size()==maxArrets;
	   
	   Course course = GenerateurDonnee.creerCourse( aller.getId());
	   courseManager.creer(course);
	   
	   Long idCourse = course.getId();
	   
	   Long heureDepart = 8L * 1000L * 3600L;
	   Long duree = 3L * 1000L * 60L;
	   
	   int totalHoraires = 0;
	   List<EtatMajHoraire> majHoraires = new ArrayList<EtatMajHoraire>();
	   for (int j = 0; j < maxArrets; j++) 
	   {
		   if ( j%2==0)
		   {
			   totalHoraires++;
			   Long idArret = arretsItineraire.get( j).getId();
			   Time heure = new Time( heureDepart + j*duree);
			   
			   majHoraires.add(EtatMajHoraire.getCreation(idArret, idCourse,
						heure.toDate()));
		   }
	   }
	   horaireManager.modifier(majHoraires);
	   
	   List<Horaire> horairesLus = courseManager.getHorairesCourseOrdonnes(idCourse);
	   assert totalHoraires==horairesLus.size():"Echec à la sauvegarde des horaires";
	   
	   for (int i = 0; i < horairesLus.size(); i++) 
	   {
		   assert horairesLus.get( i).getDepart()==(i==0);
		   assert ( horairesLus.get( i).getDepart() && horairesLus.get( i).getIdArret().equals( arretsItineraire.get( 0).getId()))
		   || !horairesLus.get( i).getDepart();
	   }
	   
	   majHoraires.clear();
	   majHoraires.add(EtatMajHoraire.getSuppression(horairesLus.get(0)));
	   horaireManager.modifier(majHoraires);
	   
	   horairesLus = courseManager.getHorairesCourseOrdonnes(idCourse);
	   assert (totalHoraires-1)==horairesLus.size():"Echec à la sauvegarde des horaires";
	   
	   for (int i = 0; i < horairesLus.size(); i++) 
	   {
		   assert horairesLus.get( i).getDepart()==(i==0);
		   assert ( horairesLus.get( i).getDepart() && horairesLus.get( i).getIdArret().equals( arretsItineraire.get( 2).getId()))
		   || !horairesLus.get( i).getDepart();
	   }
	}
	
	@Test(groups="tests unitaires", description="selection des horaires des départs de courses d'un itineraire")
	public void testHorairesDepartCourseItineraire()
	{
		   int maxArrets = 20;
		   Ligne uneLigne = GenerateurDonnee.creerLigne();
		   ligneManager.creer( uneLigne);
		   
		   Itineraire aller = GenerateurDonnee.creerItineraire( uneLigne.getId());
		   Itineraire retour = GenerateurDonnee.creerItineraire( uneLigne.getId());
		   itineraireManager.creer( aller);
		   itineraireManager.creer( retour);
		   assert !aller.getId().equals( retour.getId());
		   
		   List<ArretItineraire> arretsItineraire = itineraireManager.getArretsItineraire( aller.getId());
		   assert arretsItineraire.size()==0;
		   
		   List<EtatMajArretItineraire> majItineraire = new ArrayList<EtatMajArretItineraire>();
		   for (int i = 0; i < maxArrets; i++) 
		   {
			   EtatMajArretItineraire etatMaj = EtatMajArretItineraire.creerCreation( i, "A"+i);
			   majItineraire.add( etatMaj);
		   }
		   itineraireManager.modifierArretsItineraire( aller.getId(), majItineraire);
		   
		   majItineraire = new ArrayList<EtatMajArretItineraire>();
		   for (int i = 0; i < maxArrets; i++) 
		   {
			   EtatMajArretItineraire etatMaj = EtatMajArretItineraire.creerCreation( i, "A"+i);
			   majItineraire.add( etatMaj);
		   }
		   itineraireManager.modifierArretsItineraire( retour.getId(), majItineraire);
		   
		   arretsItineraire = itineraireManager.getArretsItineraire( aller.getId());
		   assert arretsItineraire.size()==maxArrets;
		   
		   Course courseA = GenerateurDonnee.creerCourse( aller.getId());
		   Course courseB = GenerateurDonnee.creerCourse( retour.getId());
		   courseManager.creer(courseA);
		   courseManager.creer(courseB);
		   
		   List<Horaire> horairesA = creerHoraires(maxArrets, itineraireManager.getArretsItineraire( aller.getId()), courseA.getId());
		   List<Horaire> horairesB = creerHoraires(maxArrets, itineraireManager.getArretsItineraire( retour.getId()), courseB.getId());
		   
		   List<Long> horairesLus = modificationSpecifique.getIdsHorairesItineraire( aller.getId());
		   assert horairesLus.size()==horairesA.size():
			   horairesLus.size()+" horaires lus, "+
			   horairesA.size()+" horaires créés";
		   

		   List<Long> idHoraireDepartAs = modificationSpecifique.getIdsPremiersHorairesItineraire( aller.getId());
		   assert idHoraireDepartAs.size()==1;
		   assert idHoraireDepartAs.get( 0).equals( horairesA.get( 0).getId()):
			   "attendu "+idHoraireDepartAs.get( 0)+", lu "+horairesA.get( 0).getId();
		   
		   List<Long> idHoraireDepartBs = modificationSpecifique.getIdsPremiersHorairesItineraire( retour.getId());
		   assert idHoraireDepartBs.size()==1;
		   assert idHoraireDepartBs.get( 0).equals( horairesB.get( 0).getId());
		   
		   // vérifier que le référencement est fait pour A
		   Horaire departA = horaireManager.lire( idHoraireDepartAs.get( 0));
		   assert departA.getDepart();
		   
		   // vérifier que le référencement est aussi fait pour B
		   Horaire departB = horaireManager.lire( idHoraireDepartBs.get( 0));
		   assert departB.getDepart();
	}

	private List<Horaire> creerHoraires(int maxArrets, List<ArretItineraire> arretsItineraire, Long idCourse) {
		
		Long heureDepart = 8L * 1000L * 3600L;
		Long duree = 3L * 1000L * 60L;
		int totalHoraires = 0;
		List<EtatMajHoraire> majHoraires = new ArrayList<EtatMajHoraire>();
		for (int j = 0; j < maxArrets; j++) 
		{
			if (j % 2 == 0 && j > 1) 
			{
				totalHoraires++;
				Long idArret = arretsItineraire.get(j).getId();
				Time heure = new Time(heureDepart + j * duree);

				majHoraires.add(EtatMajHoraire.getCreation(idArret, idCourse,
						heure.toDate()));
			}
		}
		horaireManager.modifier(majHoraires);

		List<Horaire> horaires = courseManager.getHorairesCourseOrdonnes(idCourse);
		logger.debug( "course "+idCourse);
		for (Horaire horaire : horaires) 
		{
			logger.debug( horaire.getId() + " "+ horaire.getArrivalTime());
		}

		return new ArrayList<Horaire>(horaires);
	}}
