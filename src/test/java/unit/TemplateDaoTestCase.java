package unit;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;
import org.exolab.castor.types.Duration;
import org.exolab.castor.types.Time;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import chouette.schema.Address;
import fr.certu.chouette.critere.AndClause;
import fr.certu.chouette.critere.IClause;
import fr.certu.chouette.critere.NotClause;
import fr.certu.chouette.critere.OrClause;
import fr.certu.chouette.critere.Ordre;
import fr.certu.chouette.critere.ScalarClause;
import fr.certu.chouette.critere.VectorClause;
import fr.certu.chouette.dao.ISelectionSpecifique;
import fr.certu.chouette.dao.ITemplateDao;
import fr.certu.chouette.dao.hibernate.SelectionSpecifique;
import fr.certu.chouette.manager.SingletonManager;
import fr.certu.chouette.modele.ArretItineraire;
import fr.certu.chouette.modele.Correspondance;
import fr.certu.chouette.modele.Course;
import fr.certu.chouette.modele.Horaire;
import fr.certu.chouette.modele.Itineraire;
import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.modele.Periode;
import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.modele.Reseau;
import fr.certu.chouette.modele.TableauMarche;
import fr.certu.chouette.modele.Transporteur;

public class TemplateDaoTestCase
{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger
			.getLogger(TemplateDaoTestCase.class);

	private ITemplateDao<Reseau> reseauDao;
	private ITemplateDao<Ligne> ligneDao;
	private ITemplateDao<Transporteur> transporteurDao;
	private ITemplateDao<Itineraire> itineraireDao;
	private ITemplateDao<ArretItineraire> arretDao;
	private ITemplateDao<PositionGeographique> positionGeographiqueDao;
	private ITemplateDao<TableauMarche> tmDao;
	private ITemplateDao<Course> courseDao;
	private ITemplateDao<Horaire> horaireDao;
	
	private ISelectionSpecifique selectionSpecifique;
	private SelectionSpecifique modificationSpecifique;

	public TemplateDaoTestCase() {
	}

	@BeforeSuite
	protected void setUp() throws Exception {
		ApplicationContext applicationContext = SingletonManager.getApplicationContext();

		reseauDao = (ITemplateDao<Reseau>)applicationContext.getBean( "reseauDao");
		ligneDao = (ITemplateDao<Ligne>)applicationContext.getBean( "ligneDao");
		transporteurDao = (ITemplateDao<Transporteur>)applicationContext.getBean( "transporteurDao");
		itineraireDao = (ITemplateDao<Itineraire>)applicationContext.getBean( "itineraireDao");
		arretDao = (ITemplateDao<ArretItineraire>)applicationContext.getBean( "arretItineraireDao");
		positionGeographiqueDao = (ITemplateDao<PositionGeographique>)applicationContext.getBean( "positionGeographiqueDao");
		tmDao = (ITemplateDao<TableauMarche>)applicationContext.getBean( "tableauMarcheDao");
		courseDao = (ITemplateDao<Course>)applicationContext.getBean( "courseDao");
		horaireDao = (ITemplateDao<Horaire>)applicationContext.getBean( "horaireDao");
		
		selectionSpecifique = ( ISelectionSpecifique)applicationContext.getBean( "selectionSpecifique");
		modificationSpecifique = ( SelectionSpecifique)applicationContext.getBean( "modificationSpecifique");
	}

	
	@Test(groups="tests unitaires", description="conversion des valeurs decimales")
	public void convertionBigDecimal()
	{
		Random random = new Random();
		int partieEntiere = random.nextInt(100000);
		int partieDecimale = random.nextInt(99);
		
		String valeur = partieEntiere + "." + partieDecimale;
		Float floatVal = new Float( valeur);
		logger.debug( "floatVal="+floatVal+", valeur="+valeur);
		
		BigDecimal bigDecimal = new BigDecimal( floatVal.toString());
		logger.debug( "bigDecimal="+bigDecimal+", valeur="+valeur);
	}
	
   @Test(groups="tests unitaires", description="")
   public void test_filtreCalendriers() {
	   
	   Calendar calendar = Calendar.getInstance();
	   
	   calendar.set(2000, 00, 01);
	   Date dateDebutPeriode = null; //calendar.getTime();
	   
	   calendar.set(2009, 00, 01);
	   Date dateFinPeriode = null; //calendar.getTime();
	   
	   String commentaire = "Vacances";
	   
	   Long idReseau = new Long(16649933);
	   
	   List <TableauMarche> calendriers = selectionSpecifique.getCalendriersFiltres(dateDebutPeriode, dateFinPeriode, commentaire, idReseau);
	   
	   System.out.println("---> GOT A COLLECTION OF RESULTS WITH A SIZE OF / " + calendriers.size());
	   
	   for (TableauMarche calendrier : calendriers) {
		   
		   logger.debug("--> FOUND A RESULT WITH ID / " + calendrier.getId() + " AND COMMENT / " + calendrier.getComment());
		   
		   for (Periode periode : calendrier.getPeriodes()) {
			   
			   System.out.println("!> GOT A PERIODE WITH DEBUT / " + periode.getDebut() + " AND FIN / " + periode.getFin());
		   }
	   }
   }
   
   @Test(groups="tests unitaires", description="dao du tri de la sélection des lignes")
   public void test_tri()
   {
		Collection <Ordre> ordres = new ArrayList<Ordre>();
		ordres.add(new Ordre("name", true));
		List<Ligne> lignes = ligneDao.select(null, ordres);
		
		Ligne ligneA = null;
		Ligne ligneB = null;
		boolean isAnew = false;
		boolean isBnew = false;
		if ( lignes.size()==0)
		{
		   ligneA = GenerateurDonnee.creerLigne();
		   ligneB = GenerateurDonnee.creerLigne();
		   ligneB.setName( ligneA.getName()+"B");
		   ligneDao.save( ligneA);
		   ligneDao.save( ligneB);
		   isAnew = true;
		   isBnew = true;
		}
		else if ( lignes.size()==1)
		{
		   ligneA = lignes.get(0);
		   ligneB = GenerateurDonnee.creerLigne();
		   ligneB.setName( ligneA.getName()+"B");
		   ligneDao.save( ligneB);
		   isBnew = true;
		}
		else
		{
		   ligneA = lignes.get(0);
		   ligneB = lignes.get(1);
		   ligneB.setName( ligneA.getName()+"B");
		   ligneDao.update( ligneB);
		}
	   
		lignes = ligneDao.select( null, ordres);
		assert lignes.get(0).getName().equals(ligneA.getName()):"nom lu ="+lignes.get(0).getName()+", attendu "+ligneA.getName();
		assert lignes.get(1).getName().equals(ligneB.getName()):"nom lu ="+lignes.get(0).getName()+", attendu "+ligneB.getName();
		
		ligneA.setName(  ligneB.getName()+"C");
	    ligneDao.update( ligneA);
		   
		lignes = ligneDao.select( null, ordres);
		assert lignes.get(0).getName().equals(ligneB.getName()):"nom lu ="+lignes.get(0).getName()+", attendu "+ligneB.getName();
		assert lignes.get(1).getName().equals(ligneA.getName()):"nom lu ="+lignes.get(0).getName()+", attendu "+ligneA.getName();

		if ( isAnew)
			ligneDao.remove( ligneA.getId());
		if ( isBnew)
			ligneDao.remove( ligneB.getId());
   }	
	
	@Test(groups="tests unitaires", description="dao crud sur reseau-transporteur-ligne-itineraire-arret")
   public void test_minimal()
   {
	   Reseau reseau = GenerateurDonnee.creerResau();
	   Transporteur transporteur = GenerateurDonnee.creerTransporteur();
	   Ligne ligne = GenerateurDonnee.creerLigne();

	   int max_itineraire = 5;
	   int max_arrets = 5;

	   reseauDao.save( reseau);
	   Reseau resLu = reseauDao.get( reseau.getId());
	   assert resLu!=null: "echec de l'enregistrement du reseau";
	   
	   transporteurDao.save( transporteur);
	   Transporteur trspLu = transporteurDao.get( transporteur.getId());
	   assert trspLu!=null: "echec de l'enregistrement du transporteur";

	   ligne.setIdReseau( reseau.getId());
	   ligne.setIdTransporteur( transporteur.getId());
	   ligneDao.save( ligne);
	   Ligne ligneLue = ligneDao.get( ligne.getId());
	   assert ligneLue!=null: "echec de l'enregistrement de la ligne";

	   for (int i = 0; i < max_itineraire; i++)
	   {
		   Itineraire itineraire = GenerateurDonnee.creerItineraire( ligne.getId());
		   itineraireDao.save( itineraire);
		   Itineraire itiLu = itineraireDao.get( itineraire.getId());
		   assert itiLu!=null: "echec de l'enregistrement de l'itineraire";
		   
		   for (int j = 0; j < max_arrets; j++) 
		   {
			   ArretItineraire arret = GenerateurDonnee.creerArret(itineraire.getId());
			   arret.setPosition( j);
			   arretDao.save( arret);
			   ArretItineraire arretLu = arretDao.get( arret.getId());
			   assert arretLu!=null: "echec de l'enregistrement de l'arret";
		   }
		   
	   }
	   
	   List<Itineraire> lesItiLigne = selectionSpecifique.getItinerairesLigne( ligne.getId());
	   assert lesItiLigne.size()==max_itineraire: "echec de l'enregistrement des itineraires de la ligne";
	   
	   for (Itineraire itineraire : lesItiLigne) 
	   {
		   List<ArretItineraire> arretsIti = selectionSpecifique.getArretsItineraire( itineraire.getId());
		   assert arretsIti.size()==max_arrets: "echec de l'enregistrement des arrets de l'itineraire";
		   
		   for (ArretItineraire arret : arretsIti) 
		   {
			   arretDao.remove( arret.getId());
			   
			   try
			   {
				   arretDao.get( arret.getId());
				   throw new RuntimeException( "Echec suppression de l'arret "+arret.getId());
			   }
			   catch( ObjectRetrievalFailureException e){}
		   }
		   
		   itineraireDao.remove( itineraire.getId()); 
		   try
		   {
			   itineraireDao.get( itineraire.getId());
			   throw new RuntimeException( "Echec suppression de l'itineraire "+itineraire.getId());
		   }
		   catch( ObjectRetrievalFailureException e){}
	   }
	   
	   lesItiLigne = selectionSpecifique.getItinerairesLigne( ligne.getId());
	   assert lesItiLigne.size()==0: "echec de la suppression des itineraires de la ligne";
	   
	   ligneDao.remove( ligne.getId());
	   try
	   {
		   ligneDao.get( ligne.getId());
		   throw new RuntimeException( "Echec suppression de la ligne "+ligne.getId());
	   }
	   catch( ObjectRetrievalFailureException e){}
	   
	   List<Ligne> lesLignesRes = selectionSpecifique.getLignesReseau( reseau.getId());
	   assert lesLignesRes.size()==0: "echec de la suppression de la ligne";
	   
	   List<Ligne> lesLignesTrsp = selectionSpecifique.getLignesTransporteur( transporteur.getId());
	   assert lesLignesTrsp.size()==0: "echec de la suppression de la ligne";
	   
	   reseauDao.remove( reseau.getId());
	   try
	   {
		   reseauDao.get( reseau.getId());
		   throw new RuntimeException( "Echec suppression du reseau "+reseau.getId());
	   }
	   catch( ObjectRetrievalFailureException e){}
	   
	   transporteurDao.remove( transporteur.getId());
	   try
	   {
		   transporteurDao.get( transporteur.getId());
		   throw new RuntimeException( "Echec suppression du transporteur "+transporteur.getId());
	   }
	   catch( ObjectRetrievalFailureException e){}
   }
	
	@Test(groups="tests unitaires", description="dao crud sur course-horaire-TM")
	public void test_format_horaire()
	{
		   
		   Long heureDepart = 8L * 1000L * 3600L;
		   Long duree = 3L * 1000L * 60L;
		   Time heure = new Time( heureDepart + duree);		
		   
		   Horaire horaire = new Horaire();
		   horaire.setArrivalTime( heure.toDate());
		   
		   java.util.Date dateHeure = horaire.getArrivalTime();
		   logger.debug( dateHeure);
		   SimpleDateFormat sdf = new SimpleDateFormat( "hh:mm:ss");
		   logger.debug( sdf.format( dateHeure));
	}
	
	@Test(groups="tests unitaires", description="conersion de duree")
	public void test_format_duree()
	{
		String dureeXML = "PT0M15S";
		Duration duree = null;
		
		Correspondance corresp = new Correspondance();
		try {
			duree = new Duration( dureeXML);
			corresp.getConnectionLink().setDefaultDuration( duree);
		} catch (ParseException e) {
			logger.error( e.getMessage(),e);
			assert false;
		}
		Date date = corresp.getDefaultDuration();
		corresp.setDefaultDuration( date);
		
		assert corresp.getDefaultDuration().toString().equals( date.toString()):"duree lue="+corresp.getDefaultDuration()+", duree attendue="+date;
		assert corresp.getConnectionLink().getDefaultDuration().toString().equals( duree.toString()):
			"duree lue="+corresp.getConnectionLink().getDefaultDuration()+
			"duree attendue="+duree;
	}
   
	@Test(groups="tests unitaires", description="dao crud sur course-horaire-TM")
   public void test_course()
   {
	   Ligne ligne = GenerateurDonnee.creerLigne();
	   Itineraire itineraire;

	   ligneDao.save( ligne);
	   itineraire = GenerateurDonnee.creerItineraire( ligne.getId());

	   itineraireDao.save(itineraire);
	   itineraire = itineraireDao.get( itineraire.getId());
	   
	   int maxArrets = 6;
	   ArrayList<Long> lesArretId = new ArrayList<Long>( maxArrets);
	   
	   for (int i = 0; i < maxArrets; i++) 
	   {
		   ArretItineraire arret = GenerateurDonnee.creerArret( itineraire.getId());
		   arretDao.save( arret);
		   lesArretId.add( arret.getId());
	   }
	   
	   int maxCourses = 5;
	   
	   for (int i = 0; i < maxCourses; i++) 
	   {
		   Course course = GenerateurDonnee.creerCourse( itineraire.getId());
		   courseDao.save( course);
		   Course courseLue = courseDao.get( course.getId());
		   assert courseLue!=null: "echec enregistrement course";
		   
		   int maxHoraire = maxArrets/2;
		   
		   Long idCourse = course.getId();
		   
		   Long heureDepart = 8L * 1000L * 3600L;
		   Long duree = 3L * 1000L * 60L;
		   
		   for (int j = 0; j < maxHoraire; j++) 
		   {
			   Random random = new Random();
			   Long idArret = lesArretId.get( 2*j + random.nextInt( 2));
			   Time heure = new Time( heureDepart + j*duree);
			   
			   Horaire horaire = GenerateurDonnee.creerHoraire(
					   idCourse, idArret, heure);
			   horaire.setDepart( j==0);
			   horaireDao.save( horaire);
			   
			   Horaire horaireLu = horaireDao.get( horaire.getId());
			   assert horaireLu!=null: "echec enregistrement horaire";
		   }
		   modificationSpecifique.referencerDepartsCourses(itineraire.getId());
		   
		   List<Horaire> lesHoraires = selectionSpecifique.getHorairesCourse(idCourse);
		   assert maxHoraire==lesHoraires.size() : "Echec à la sauvegarde des horaires, attendus:"+maxHoraire+", trouves="+lesHoraires.size();
	   }
	   
	   List<Course> coursesItineraire = selectionSpecifique.getCoursesItineraire( itineraire.getId());
	   logger.debug( "iti="+itineraire.getId());
	   for (Course course : coursesItineraire) {
		   logger.debug( "iti course = "+course.getIdItineraire()+", id="+course.getId());
	   }
	   
	   assert maxCourses==coursesItineraire.size():"echec enregistrement lien course itineraire, courses attendues="+maxCourses+", trouvees="+coursesItineraire.size();
	   
	   TableauMarche tm1 = GenerateurDonnee.creerTM();
	   TableauMarche tm2 = GenerateurDonnee.creerTM();
	   
	   tmDao.save( tm1);
	   tmDao.save( tm2);
	   
	   TableauMarche tm1Lu = tmDao.get( tm1.getId());
	   assert tm1Lu!=null:"echec enregistrement TM";
	   assert tm1.getTotalDates()==tm1Lu.getTotalDates():
		   "echec enregistrement jours du TM";
	   TableauMarche tm2Lu = tmDao.get( tm1.getId());
	   assert tm2Lu!=null:"echec enregistrement TM";
	   assert tm2.getTotalDates()==tm2Lu.getTotalDates():
		   "echec enregistrement jours du TM";
	   
	   Set<Long> tousTM = new HashSet<Long>();
	   List<Long> idTMs = new ArrayList<Long>();
	   idTMs.add( tm1.getId());
	   idTMs.add( tm2.getId());
	   tousTM.addAll( idTMs);
	   
	   for (Course course : coursesItineraire) 
	   {
		   modificationSpecifique.associerCourseTableauxMarche(
				   course.getId(), idTMs);
		   
		   // controler les courses du tm
		   List<Course> tmCourses = selectionSpecifique.getCoursesTableauMarche( tm1.getId());
		   boolean courseCouranteTrouvee = false;
		   for (Course course2 : tmCourses) 
		   {
			   if ( course2.getId().equals( course.getId()))
			   {
				   courseCouranteTrouvee = true;
				   break;
			   }
		   }
		   assert courseCouranteTrouvee: "association course TM mal enregistree";
		   
		   // controler les TM d'une course
		   List<TableauMarche> courseTM = selectionSpecifique.getTableauxMarcheCourse( course.getId());
		   assert idTMs.size()==courseTM.size():"association course TM mal enregistree";
		   
		   for (TableauMarche marche : courseTM) 
		   {
			   assert idTMs.contains( marche.getId()): "association course TM mal enregistree";
		   }
	   }
	   
	   TableauMarche tm3 = GenerateurDonnee.creerTM();
	   tmDao.save( tm3);
	   
	   Course uneCourse = coursesItineraire.get( 0);
	   
	   List<Long> idTM_1_3s = new ArrayList<Long>();
	   idTM_1_3s.add( tm1.getId());
	   idTM_1_3s.add( tm3.getId());
	   tousTM.addAll( idTM_1_3s);
	   modificationSpecifique.associerCourseTableauxMarche(
			   uneCourse.getId(), idTM_1_3s);
	   
	   List<TableauMarche> courseTM = selectionSpecifique.getTableauxMarcheCourse( uneCourse.getId());
	   assert idTM_1_3s.size()==courseTM.size():"maj association course TM mal enregistree";
	   
	   for (TableauMarche leTM : courseTM) 
	   {
		   assert idTM_1_3s.contains( leTM.getId()):"maj association course TM mal enregistree";
	   }
	   
	   for (Course course : coursesItineraire) 
	   {
		   modificationSpecifique.associerCourseTableauxMarche(
				   course.getId(), new ArrayList<Long>());
		   
		   List<TableauMarche> courseTMLu = selectionSpecifique.getTableauxMarcheCourse( course.getId());
		   assert 0==courseTMLu.size():"suppression association course TM mal enregistree";
		   
		   List<Horaire> lesHoraires = selectionSpecifique.getHorairesCourse( course.getId());
		   for (Horaire horaire : lesHoraires) 
		   {
			   horaireDao.remove( horaire.getId());
			   
			   try
			   {
				   horaireDao.get( horaire.getId());
			   }
			   catch( ObjectRetrievalFailureException e){}
		   }
		   
		   List<Horaire> lesHorairesLus = selectionSpecifique.getHorairesCourse( course.getId());
		   assert 0==lesHorairesLus.size(): "Echec suppression des horaires de la course "+course.getId();
		   
		   courseDao.remove( course.getId());
		   try
		   {
			   courseDao.get( course.getId());
		   }
		   catch( ObjectRetrievalFailureException e){}
	   }
	   
	   for (Long marcheId : tousTM) 
	   {
		   List<Course> coursesLues = selectionSpecifique.getCoursesTableauMarche( marcheId);
		   assert 0==coursesLues.size():"Echec de la suppression des relations entre course et TM idTM="+marcheId;
		   
		   tmDao.remove( marcheId);
		   try
		   {
			   tmDao.get( marcheId);
		   }
		   catch( ObjectRetrievalFailureException e){}
	   }
	   
	   for (Long idArret : lesArretId) {
		   arretDao.remove( idArret);
	   }

	   itineraireDao.remove( itineraire.getId());
	   try
	   {
		   itineraireDao.get( itineraire.getId());
	   }
	   catch ( ObjectRetrievalFailureException e) {}
	   
	   ligneDao.remove( ligne.getId());
	   try
	   {
		   ligneDao.get( ligne.getId());
	   }
	   catch (ObjectRetrievalFailureException e) {}
   }
	   
	@Test(groups="tests unitaires", description="dao crud sur arrets physiques")
   public void test_physique_integre()
   {
		try
		{
		   Ligne ligne = GenerateurDonnee.creerLigne();
		   Itineraire itineraire;
	
		   ligneDao.save( ligne);
		   
		   List<PositionGeographique> arretsPhysiquesLus = selectionSpecifique.getArretPhysiqueLigne( ligne.getId());
		   assert arretsPhysiquesLus.size()==0;
		   
		   itineraire = GenerateurDonnee.creerItineraire( ligne.getId());
	
		   itineraireDao.save( itineraire);
	
		   int max_arrets = 5;
		   Set<Long> idPhysiques = new HashSet<Long>( max_arrets);
		   Set<Long> idPhysiquesAgarder = new HashSet<Long>( max_arrets);
		   List<Long> idLogiques = new ArrayList<Long>( max_arrets);
		   List<Long> idLogiquesAgarder = new ArrayList<Long>( max_arrets);
		   for (int i = 0; i < max_arrets; i++) 
		   {
			   PositionGeographique physique = GenerateurDonnee.creerArretPhysique( "");
			   positionGeographiqueDao.save( physique);
			   
			   PositionGeographique physiqueLu = positionGeographiqueDao.get( physique.getId());
			   assert physiqueLu!=null:"echec enregistrement arret physique";
			   
			   ArretItineraire arret = GenerateurDonnee.creerArret( itineraire.getId());
			   arret.setIdPhysique( physique.getId());
			   arretDao.save( arret);
			   
			   idPhysiques.add( physiqueLu.getId());
			   idLogiques.add( arret.getId());
			   if ( i%2==0) 
			   {
				   idPhysiquesAgarder.add( physiqueLu.getId());
				   idLogiquesAgarder.add( arret.getId());
			   }
		   }
		   
		   List<PositionGeographique> arretsPhysiques = selectionSpecifique.getArretPhysiqueLigne( ligne.getId());
		   assert arretsPhysiques.size()==max_arrets;
		   for (PositionGeographique positionGeographique : arretsPhysiques) 
		   {
			   assert idPhysiques.contains( positionGeographique.getId());
		   }
		   logger.debug( "select arrets physiques ok");
		   
		   // controler le rattachement à l'itinéraire
		   List<ArretItineraire> arretsLu = selectionSpecifique.getArretsItineraire( itineraire.getId());
		   assert arretsLu!=null: "arrets mal enregistres sur l'itineraire";
		   assert arretsLu.size()==max_arrets: "arrets mal enregistres sur l'itineraire lu="+arretsLu.size()+", attendus="+max_arrets;
		   
		   int maxCourses = 5;
		   for (int i = 0; i < maxCourses; i++) 
		   {
			   Course course = GenerateurDonnee.creerCourse( itineraire.getId());
			   courseDao.save( course);
			   Course courseLue = courseDao.get( course.getId());
			   assert courseLue!=null: "echec enregistrement course";
			   
			   int maxHoraire = max_arrets/2;
			   
			   Long idCourse = course.getId();
			   
			   Long heureDepart = 8L * 1000L * 3600L;
			   Long duree = 3L * 1000L * 60L;
			   
			   for (int j = 0; j < maxHoraire; j++) 
			   {
				   Random random = new Random();
				   Long idArret = idLogiques.get( 2*j + random.nextInt( 2));
				   Time heure = new Time( heureDepart + j*duree);
				   
				   Horaire horaire = GenerateurDonnee.creerHoraire(
						   idCourse, idArret, heure);
				   horaire.setDepart( j==0);
				   horaireDao.save( horaire);
				   
				   Horaire horaireLu = horaireDao.get( horaire.getId());
				   assert horaireLu!=null: "echec enregistrement horaire";
			   }
			   modificationSpecifique.referencerDepartsCourses(itineraire.getId());
			   
			   List<Horaire> lesHoraires = selectionSpecifique.getHorairesCourse(idCourse);
			   assert maxHoraire==lesHoraires.size() : "Echec à la sauvegarde des horaires, attendus:"+maxHoraire+", trouves="+lesHoraires.size();
		   }
		   // controler le rattachement aux horaires
		   Map<Long, Integer> totalHorairesParidLogique = new Hashtable<Long, Integer>();
		   for (Long idLogique : idLogiques) 
		   {
			   List<Horaire> horaires = selectionSpecifique.getHorairesArretItineraire( idLogique);
			   totalHorairesParidLogique.put( idLogique, horaires.size());
		   }
		   
		   Collection<Long> idPhysiqueAsupprimer = new HashSet<Long>( idPhysiques);
		   idPhysiqueAsupprimer.removeAll( idPhysiquesAgarder);
		   modificationSpecifique.supprimerGeoPositions( idPhysiqueAsupprimer);
		   
		   // controler la màj de rattachement aux horaires
		   for (Long idArretLogique : totalHorairesParidLogique.keySet()) 
		   {
			   List<Horaire> horaires = selectionSpecifique.getHorairesArretItineraire( idArretLogique);
			   
			   boolean isArretLogiqueConserve = idLogiquesAgarder.contains( idArretLogique);
			   assert (horaires.size()==0 && !isArretLogiqueConserve) 
			   		|| (horaires.size()==totalHorairesParidLogique.get( idArretLogique) && isArretLogiqueConserve) : 
				   "la suppression des horaires à l'arrêt "+idArretLogique+" a échoué, "+horaires.size()+" restants";
		   }
		   
		   arretsLu = selectionSpecifique.getArretsItineraire( itineraire.getId());
		   assert arretsLu!=null: "arrets mal supprimes sur l'itineraire";
		   assert arretsLu.size()==idPhysiquesAgarder.size(): "arrets mal enregistres sur l'itineraire lu="+arretsLu.size()+", attendus="+idPhysiquesAgarder.size();
		   
		   for (Long idPhysique : idPhysiqueAsupprimer) 
		   {
			   try
			   {
				   positionGeographiqueDao.get( idPhysique);
				   assert false : "L'arret physique "+idPhysique+" aurait du etre supprime";
			   }
			   catch( ObjectRetrievalFailureException e){}	   
		   }
	
		   Itineraire itineraireLu = itineraireDao.get( itineraire.getId());
		   assert itineraireLu!=null;
		   
		   modificationSpecifique.supprimerItineraire( itineraire.getId());
		   try
		   {
			   itineraireLu = itineraireDao.get( itineraire.getId());
			   assert false;
		   }
		   catch( ObjectRetrievalFailureException e){}
		   
		   for (Long idPhysique : idPhysiquesAgarder) 
		   {
			   try
			   {
				   positionGeographiqueDao.remove( idPhysique);
				   positionGeographiqueDao.get( idPhysique);
				   assert false : "L'arret physique "+idPhysique+" aurait du etre supprime";
			   }
			   catch( ObjectRetrievalFailureException e){}	   
		   }
		}
		catch( Exception e)
		{
			logger.error( e.getMessage(), e);
			assert false;
		}
   }
	   
	@Test(groups="tests unitaires", description="dao crud minimal sur lieu")
   public void test_physique_isole()
	{
		PositionGeographique arretPhysique = GenerateurDonnee.creerArretPhysique( "");
		
		positionGeographiqueDao.save( arretPhysique);
		PositionGeographique lieuLu = positionGeographiqueDao.get( arretPhysique.getId());
		assert lieuLu!=null:"echec enregistrement lieu";
		
		String countryCode = "mon code";
		arretPhysique.getAreaCentroid().setAddress( new Address());
		arretPhysique.getAreaCentroid().getAddress().setCountryCode( countryCode);
		
		positionGeographiqueDao.update( arretPhysique);
		lieuLu = positionGeographiqueDao.get( arretPhysique.getId());
		assert !lieuLu.getAreaCentroid().getAddress().equals( countryCode):"echec enregistrement lieu";
		
		positionGeographiqueDao.remove( arretPhysique.getId());
		try
		{
			lieuLu = positionGeographiqueDao.get( arretPhysique.getId());
		}
		catch( ObjectRetrievalFailureException e){}
	}

	
	@Test(groups="tests unitaires", description="retirer un arret sur un itineraire")
	public void retirerArret()
	{
	   Ligne ligne = GenerateurDonnee.creerLigne();
	   Itineraire itineraire;
	
	   ligneDao.save( ligne);
	   itineraire = GenerateurDonnee.creerItineraire( ligne.getId());
	
	   itineraireDao.save(itineraire);
	   itineraire = itineraireDao.get( itineraire.getId());
	
	   int maxArrets = 6;
	   ArrayList<Long> lesArretId = new ArrayList<Long>( maxArrets);
	
	   List<Long> anciens = new ArrayList<Long>();
	   List<Integer> positions = new ArrayList<Integer>();
	   for (int i = 0; i < maxArrets; i++) 
	   {
		   ArretItineraire arret = GenerateurDonnee.creerArret( itineraire.getId());
		   arret.setPosition( i);
		   arretDao.save( arret);
		   lesArretId.add( arret.getId());
		   
		   anciens.add( arret.getId());
		   positions.add( i);
	   }
	
	   Collection<Long> idArretSupprime = new HashSet<Long>();
	   idArretSupprime.add( anciens.get( 1));
	   
	   modificationSpecifique.supprimerArretItineraire( anciens.get( 1));
	   
	   List<ArretItineraire> arrets = selectionSpecifique.getArretsItineraire( itineraire.getId());
	   assert arrets.size()==anciens.size()-1:"total d'arrets attendu "+(anciens.size()-1)+
	   				", total trouve "+arrets.size();
	   for (int i = 0; i < arrets.size(); i++) 
	   {
		   assert arrets.get( i).getPosition()==i:"ordre invalide, position attendue "+i+
		   ", position lue "+arrets.get( i).getPosition();
	   }
	   
	}
	
	@Test(groups="tests unitaires", description="deplacer des arrets")
	public void deplacerArrets()
	{
	   Ligne ligne = GenerateurDonnee.creerLigne();
	   Itineraire itineraire;
	
	   ligneDao.save( ligne);
	   itineraire = GenerateurDonnee.creerItineraire( ligne.getId());
	
	   itineraireDao.save(itineraire);
	   itineraire = itineraireDao.get( itineraire.getId());
	
	   int maxArrets = 6;
	   ArrayList<Long> lesArretId = new ArrayList<Long>( maxArrets);
	
	
	   List<Long> anciens = new ArrayList<Long>();
	   List<Long> nouveaux = null;
	   List<Integer> positions = new ArrayList<Integer>();
	   for (int i = 0; i < maxArrets; i++) 
	   {
		   ArretItineraire arret = GenerateurDonnee.creerArret( itineraire.getId());
		   arret.setPosition( i);
		   arretDao.save( arret);
		   lesArretId.add( arret.getId());
		   
		   anciens.add( arret.getId());
		   positions.add( i);
	   }
	
	   // permutation globale d'une position
	   nouveaux = new ArrayList<Long>( anciens);
	   Long premier = nouveaux.remove( 0);
	   nouveaux.add( premier);
	
	   List<ArretItineraire> arretsInitiaux = selectionSpecifique.getArretsItineraire( itineraire.getId());
	   Map<Integer, Long> arretIdParPosition = new Hashtable<Integer, Long>();
	   for ( int i=0; i<arretsInitiaux.size(); i++) 
	   {
		   arretIdParPosition.put( new Integer( i), arretsInitiaux.get( i).getId());
		   assert arretsInitiaux.get( i).getPosition()==i;
	   }
	
	   modificationSpecifique.deplacerArrets(anciens, nouveaux, positions);
	
	   List<ArretItineraire> arretsDeplaces = selectionSpecifique.getArretsItineraire( itineraire.getId());
	   for ( int i=0; i<arretsDeplaces.size(); i++) 
	   {
		   int positionCourante = i;
		   int positionDeplacee = (i+1)%arretsDeplaces.size();
	
		   assert arretsInitiaux.get( positionDeplacee).getId().equals( arretsDeplaces.get( positionCourante).getId())
		    : "arret initialement à la position "+positionDeplacee+" id="+arretsInitiaux.get( positionDeplacee).getId()
		    + " devrait se trouver à la position "+positionCourante+" avec le même id "+arretsDeplaces.get( positionCourante).getId();
	   }
	}

	@Test(groups="tests unitaires", description="deplacer des arrets et leurs horaires")
	public void deplacerArretsHoraires()
	{
	   Ligne ligne = GenerateurDonnee.creerLigne();
	   Itineraire itineraire;

	   ligneDao.save( ligne);
	   itineraire = GenerateurDonnee.creerItineraire( ligne.getId());

	   itineraireDao.save(itineraire);
	   itineraire = itineraireDao.get( itineraire.getId());
	   
	   int maxArrets = 6;
	   ArrayList<Long> lesArretId = new ArrayList<Long>( maxArrets);
	   

	   List<Long> anciens = new ArrayList<Long>();
	   List<Long> nouveaux = null;
	   List<Integer> positions = new ArrayList<Integer>();
	   for (int i = 0; i < maxArrets; i++) 
	   {
		   ArretItineraire arret = GenerateurDonnee.creerArret( itineraire.getId());
		   arret.setPosition( i);
		   arretDao.save( arret);
		   lesArretId.add( arret.getId());
		   
		   anciens.add( arret.getId());
		   positions.add( i);
	   }
	   
	   Course course = GenerateurDonnee.creerCourse( itineraire.getId());
	   courseDao.save( course);
	   Course courseLue = courseDao.get( course.getId());
	   assert courseLue!=null: "echec enregistrement course";
	   
	   int maxHoraire = maxArrets/2;
	   
	   Long idCourse = course.getId();
	   Long heureDepart = 8L * 1000L * 3600L;
	   Long duree = 3L * 1000L * 60L;
	   
	   Random random = new Random();
	   Map<Integer, java.util.Date> horairesParPositionArret = new Hashtable<Integer, java.util.Date>();
	   for (int j = 0; j < maxHoraire; j++) 
	   {
		   Long idArret = lesArretId.get( 2*j + random.nextInt( 2));
		   Time heure = new Time( heureDepart + j*duree);
		   
		   Horaire horaire = GenerateurDonnee.creerHoraire(
				   idCourse, idArret, heure);
		   horaire.setDepart( j==0);
		   horaireDao.save( horaire);
		   
		   Horaire horaireLu = horaireDao.get( horaire.getId());
		   assert horaireLu!=null: "echec enregistrement horaire";
		   
		   ArretItineraire arret = arretDao.get( horaire.getIdArret());
		   horairesParPositionArret.put( arret.getPosition(), heure.toDate());
	   }
	   modificationSpecifique.referencerDepartsCourses(itineraire.getId());
	   
	   // test sur une permutation qui decale tous les arrets
	   List<Horaire> lesHoraires = selectionSpecifique.getHorairesCourse(idCourse);
	   assert maxHoraire==lesHoraires.size() : "Echec à la sauvegarde des horaires, attendus:"+maxHoraire+", trouves="+lesHoraires.size();
	   
	   // permutation globale d'une position
	   nouveaux = new ArrayList<Long>( anciens);
	   Long premier = nouveaux.remove( 0);
	   nouveaux.add( premier);
	   
	   List<ArretItineraire> arretsInitiaux = selectionSpecifique.getArretsItineraire( itineraire.getId());
	   Map<Integer, Long> arretIdParPosition = new Hashtable<Integer, Long>();
	   for ( int i=0; i<arretsInitiaux.size(); i++) 
	   {
		   arretIdParPosition.put( new Integer( i), arretsInitiaux.get( i).getId());
		   assert arretsInitiaux.get( i).getPosition()==i;
	   }

	   modificationSpecifique.deplacerArrets(anciens, nouveaux, positions);
	   
	   List<Horaire> lesNouveauxHoraires = selectionSpecifique.getHorairesCourse(idCourse);
	   Map<Integer, java.util.Date> horairesParPositionArretDeplace = new Hashtable<Integer, java.util.Date>();
	   for (Horaire horaire : lesNouveauxHoraires) 
	   {
		   ArretItineraire arret = arretDao.get( horaire.getIdArret());
		   horairesParPositionArretDeplace.put( arret.getPosition(), horaire.getArrivalTime());
		   logger.debug( "horairesParArretDeplace cle="+horaire.getId()+", heure="+horaire.getArrivalTime());
	   }
	   
	   List<ArretItineraire> arretsDeplaces = selectionSpecifique.getArretsItineraire( itineraire.getId());
	   for ( int i=0; i<arretsDeplaces.size(); i++) 
	   {
		   int positionCourante = i;
		   int positionDeplacee = (i+1)%arretsDeplaces.size();
		   
		   Long arretInitialId = arretsInitiaux.get( positionDeplacee).getId();
		   Long arretDeplaceId = arretsDeplaces.get( positionCourante).getId();
		   
		   assert arretInitialId.equals( arretDeplaceId)
		    : "arret initialement à la position "+positionDeplacee+" id="+arretInitialId
		    + " devrait se trouver à la position "+positionCourante+" avec le même id "+arretDeplaceId;
	   }
	   for ( int i=0; i<arretsDeplaces.size(); i++) 
	   {
		   int positionCourante = i;
		   
		   java.util.Date heureArretInitial = horairesParPositionArret.get( positionCourante);
		   java.util.Date heureArretDeplace = horairesParPositionArretDeplace.get( positionCourante);

		   assert (heureArretInitial==null && heureArretDeplace==null) || 
		   		heureArretInitial.equals( heureArretDeplace)
		    : "initialement la position "+positionCourante+" avec horaire "+heureArretInitial
		    + " devrait correspondre àà la position "+positionCourante+" avec horaire "+heureArretDeplace;
	   }
	}
	
	@Test(groups="tests unitaires", description="permutation de 2 arrets")
	public void permuterDeuxArrets()
	{
	   Ligne ligne = GenerateurDonnee.creerLigne();
	   Itineraire itineraire;

	   ligneDao.save( ligne);
	   itineraire = GenerateurDonnee.creerItineraire( ligne.getId());

	   itineraireDao.save(itineraire);
	   itineraire = itineraireDao.get( itineraire.getId());
	   
	   int maxArrets = 6;
	   ArrayList<Long> lesArretId = new ArrayList<Long>( maxArrets);
	   

	   List<Long> anciens = new ArrayList<Long>();
	   List<Long> nouveaux = null;
	   List<Integer> positions = new ArrayList<Integer>();
	   for (int i = 0; i < maxArrets; i++) 
	   {
		   ArretItineraire arret = GenerateurDonnee.creerArret( itineraire.getId());
		   arret.setPosition( i);
		   arretDao.save( arret);
		   lesArretId.add( arret.getId());
		   
		   anciens.add( arret.getId());
		   positions.add( i);
	   }

	   // permutation globale d'une position
	   nouveaux = new ArrayList<Long>( anciens);
	   Long dernier = nouveaux.remove( anciens.size()-1);
	   Long premier = nouveaux.remove( 0);
	   nouveaux.add( 0, dernier);
	   nouveaux.add( premier);
	   
	   List<ArretItineraire> arretsInitiaux = selectionSpecifique.getArretsItineraire( itineraire.getId());
	   Map<Integer, Long> arretIdParPosition = new Hashtable<Integer, Long>();
	   for ( int i=0; i<arretsInitiaux.size(); i++) 
	   {
		   arretIdParPosition.put( new Integer( i), arretsInitiaux.get( i).getId());
		   assert arretsInitiaux.get( i).getPosition()==i;
	   }

	   modificationSpecifique.deplacerArrets(anciens, nouveaux, positions);
	   
	   List<ArretItineraire> arretsDeplaces = selectionSpecifique.getArretsItineraire( itineraire.getId());
	   for ( int i=0; i<arretsDeplaces.size(); i++) 
	   {
		   int positionCourante = i;
		   int positionDeplacee = i;
		   if ( i==0)
		   {
			   positionDeplacee = arretsDeplaces.size()-1;
		   }
		   else if ( i==arretsDeplaces.size()-1)
		   {
			   positionDeplacee = 0;
		   }

		   assert arretsInitiaux.get( positionDeplacee).getId().equals( arretsDeplaces.get( positionCourante).getId())
		    : "arret initialement à la position "+positionDeplacee+" id="+arretsInitiaux.get( positionDeplacee).getId()
		    + " devrait se trouver à la position "+positionCourante+" avec le même id "+arretsDeplaces.get( positionCourante).getId();
	   }
	}
	   
	@Test(groups="tests unitaires", description="echange d'horaires")
	public void echangeHoraires()
	{
	   Ligne ligne = GenerateurDonnee.creerLigne();
	   Itineraire itineraire;

	   ligneDao.save( ligne);
	   itineraire = GenerateurDonnee.creerItineraire( ligne.getId());

	   itineraireDao.save(itineraire);
	   itineraire = itineraireDao.get( itineraire.getId());
	   
	   int maxArrets = 6;
	   ArrayList<Long> lesArretId = new ArrayList<Long>( maxArrets);
	   
	   for (int i = 0; i < maxArrets; i++) 
	   {
		   ArretItineraire arret = GenerateurDonnee.creerArret( itineraire.getId());
		   arretDao.save( arret);
		   lesArretId.add( arret.getId());
	   }
   
	   Course course = GenerateurDonnee.creerCourse( itineraire.getId());
	   courseDao.save( course);
	   Course courseLue = courseDao.get( course.getId());
	   assert courseLue!=null: "echec enregistrement course";
	   
	   int maxHoraire = maxArrets/2;
	   
	   Long idCourse = course.getId();
	   Long heureDepart = 8L * 1000L * 3600L;
	   Long duree = 3L * 1000L * 60L;
	   
	   Random random = new Random();
	   for (int j = 0; j < maxHoraire; j++) 
	   {
		   Long idArret = lesArretId.get( 2*j + random.nextInt( 2));
		   Time heure = new Time( heureDepart + j*duree);
		   
		   Horaire horaire = GenerateurDonnee.creerHoraire(
				   idCourse, idArret, heure);
		   horaireDao.save( horaire);
		   
		   Horaire horaireLu = horaireDao.get( horaire.getId());
		   assert horaireLu!=null: "echec enregistrement horaire";
	   }
	   modificationSpecifique.referencerDepartsCourses(itineraire.getId());
	   
	   
	   // test sur une permutation qui decale tous les arrets
	   List<Horaire> lesHoraires = selectionSpecifique.getHorairesCourse(idCourse);
	   assert maxHoraire==lesHoraires.size() : "Echec à la sauvegarde des horaires, attendus:"+maxHoraire+", trouves="+lesHoraires.size();
	   
	   List<Long> arretsOrdreInitial = new ArrayList<Long>( maxHoraire);
	   for (Horaire horaire : lesHoraires) 
	   {
		   arretsOrdreInitial.add( horaire.getIdArret());
		   //logger.debug( "arret id = "+horaire.getIdArret()+", position "+logPosition++);
	   }
	   
	   List<Long> arretsOrdreNouveau = new ArrayList<Long>( arretsOrdreInitial);
	   Long premier = arretsOrdreNouveau.remove( 0);
	   arretsOrdreNouveau.add( premier);
	   
	   modificationSpecifique.echangerHoraires( arretsOrdreInitial, arretsOrdreNouveau);
	   
	   List<Horaire> lesNouveauxHoraires = selectionSpecifique.getHorairesCourse(idCourse);
	   controleConcordance(maxHoraire, idCourse, lesHoraires, arretsOrdreInitial, arretsOrdreNouveau, lesNouveauxHoraires);
	   
	   // --------------------------------------------------------
	   // 2eme test
	   
	   // test sur une permutation du premier et du dernier arret
	   List<Horaire> lesHorairesAvant = new ArrayList<Horaire>( lesNouveauxHoraires);
	   List<Long> arretsOrdreAvant = new ArrayList<Long>( maxHoraire);
	   for (Horaire horaire : lesHorairesAvant) 
	   {
		   arretsOrdreAvant.add( horaire.getIdArret());
		   //logger.debug( "arret id = "+horaire.getIdArret()+", position "+logPosition++);
	   }
	   
	   List<Long> arretsOrdreApres = new ArrayList<Long>( arretsOrdreAvant);
	   Long premierArret = arretsOrdreApres.remove( 0);
	   Long dernierArret = arretsOrdreApres.remove( arretsOrdreApres.size()-1);
	   arretsOrdreApres.add( 0, dernierArret);
	   arretsOrdreApres.add( premierArret);
	   
	   modificationSpecifique.echangerHoraires( arretsOrdreAvant, arretsOrdreApres);
	   
	   List<Horaire> lesHorairesApres = selectionSpecifique.getHorairesCourse(idCourse);
	   controleConcordance(maxHoraire, idCourse, lesHorairesAvant, arretsOrdreAvant, arretsOrdreApres, lesHorairesApres);
	   
	   
	}

	private void controleConcordance(int maxHoraire, 
			Long idCourse, 
			List<Horaire> horairesAvant, 
			List<Long> listeArretsAvant, 
			List<Long> listeArretsApres, 
			List<Horaire> horairesApres) {
		assert maxHoraire==horairesAvant.size() : "Modification du nombre d'horaires après un échange, attendus:"+maxHoraire+", trouves:"+horairesAvant.size();
		   
		   for (int i = 0; i < maxHoraire; i++) 
		   {
			   Horaire unNouvelHoraire = horairesApres.get( i);
			   int index = listeArretsApres.indexOf( unNouvelHoraire.getIdArret());
			   Horaire unInitialHoraire = horairesAvant.get( index);
			   
			   if ( !unNouvelHoraire.getArrivalTime().equals( unInitialHoraire.getArrivalTime()))
			   {
				   logger.debug( "Avant");
				   for (int j = 0; j < maxHoraire; j++) {
					   logger.debug( horairesAvant.get(j).getIdArret()+" "+horairesAvant.get(j).getArrivalTime());
				   }
				   logger.debug( "Permutation");
				   logger.debug( "arretsOrdreInitial="+listeArretsAvant);
				   logger.debug( "arretsOrdreNouveau="+listeArretsApres);
				   logger.debug( "Apres");
				   for (int j = 0; j < maxHoraire; j++) {
					   logger.debug( horairesApres.get(j).getIdArret()+" "+horairesApres.get(j).getArrivalTime());
				   }
			   }
			   assert unNouvelHoraire.getArrivalTime().equals( unInitialHoraire.getArrivalTime()) : 
			   "échange mal enregistre, arret="+unNouvelHoraire.getIdArret()+
			   ", horaire attendu="+unInitialHoraire.getArrivalTime()+
			   ", horaire lu="+unNouvelHoraire.getArrivalTime()+
			   ", idCourse="+idCourse+
			   ", arretsOrdreInitial="+listeArretsAvant+
			   ", arretsOrdreNouveau="+listeArretsApres;
			   assert !unNouvelHoraire.isModifie() : "échange mal enregistre";
		   }
	}
	
   
	@Test(groups="tests unitaires", description="dao crud sur TM")
   public void test_tm()
   {
	   TableauMarche tm = GenerateurDonnee.creerTM();

	   int totalDates = tm.getTotalDates();
		
		tmDao.save( tm);
		
		int totalPeriodes = tm.getTotalPeriodes();
		List<Periode> periodesInitiales = tm.getPeriodes();
		
		Long tmId = tm.getId();
		
		TableauMarche tmLu = tmDao.get( tmId);
		
		assert tmLu!=null:"echec enregistrement TM";
		List<Periode> periodesLues = tmLu.getPeriodes();
		
		int nbDays = tmLu.getTotalDates();
		assert totalDates==nbDays: "echec enregistrement des jours du TM "+
			totalDates+" dates attendues, "+nbDays+" dates lues";
		assert totalPeriodes==tmLu.getTotalPeriodes(): "echec enregistrement des périodes du TM";
		
		Calendar calendar = Calendar.getInstance();
		for (int i = 0; i < totalPeriodes; i++) {
			
			calendar.setTime( periodesInitiales.get( i).getDebut());
			calendar.set( Calendar.HOUR_OF_DAY, 0);
			calendar.set( Calendar.MINUTE, 0);
			calendar.set( Calendar.SECOND, 0);
			calendar.set( Calendar.MILLISECOND, 0);
			periodesInitiales.get( i).setDebut( calendar.getTime());
			calendar.setTime( periodesInitiales.get( i).getFin());
			calendar.set( Calendar.HOUR_OF_DAY, 0);
			calendar.set( Calendar.MINUTE, 0);
			calendar.set( Calendar.SECOND, 0);
			calendar.set( Calendar.MILLISECOND, 0);
			periodesInitiales.get( i).setFin( calendar.getTime());
			
			assert periodesInitiales.get( i).getDebut().equals( periodesLues.get( i).getDebut()):
				"debut initial ="+periodesInitiales.get( i).getDebut()+
				"debut lu ="+periodesLues.get( i).getDebut();
			assert periodesInitiales.get( i).getFin().equals( periodesLues.get( i).getFin());
			assert periodesInitiales.get( i).equals( periodesLues.get( i));
		}
		java.util.Date autreDate = GenerateurDonnee.creerDate();
		tmLu.ajoutDate( autreDate);
		tmLu.ajoutPeriode( GenerateurDonnee.creerPeriode());
		
		tmDao.update( tmLu);
		
		tmLu = tmDao.get( tmId);
		assert tmLu!=null:"echec enregistrement TM";
		assert totalDates+1==tmLu.getTotalDates():"echec enregistrement TM";
		assert totalPeriodes+1==tmLu.getTotalPeriodes():"echec enregistrement TM";
		
		tmDao.remove( tmId);
		try
		{
			tmDao.get( tmId);
		}
		catch( ObjectRetrievalFailureException e){}
   }
   
   public void test_maj_itineraire()
   {
	   Ligne ligne = GenerateurDonnee.creerLigne();
	   Itineraire itineraire;

	   ligneDao.save( ligne);
	   
	   itineraire = GenerateurDonnee.creerItineraire( ligne.getId());

	   itineraireDao.save( itineraire);

	   int max_arrets = 5;
	   for (int i = 0; i < max_arrets; i++) 
	   {
		   ArretItineraire arret = GenerateurDonnee.creerArret( itineraire.getId());
		   arretDao.save( arret);
	   }

	   itineraire = itineraireDao.get( itineraire.getId());
	   List<ArretItineraire> arrets = selectionSpecifique.getArretsItineraire( itineraire.getId());

	   Long premArr = arrets.get( 0).getId();
	   Long derArr = arrets.get( arrets.size()-1).getId();
	   logger.debug( "debut");
	   logger.debug( "premArr="+premArr+", der="+derArr);

	   // mise au debut du dernier arret
	   ArretItineraire dernier = arrets.remove( arrets.size()-1);
	   arrets.add(0, dernier);

	   itineraireDao.update( itineraire);
	   itineraire = itineraireDao.get( itineraire.getId());

	   // TODO developper la fonction swap
	   
	   List<ArretItineraire> nv_arrets = selectionSpecifique.getArretsItineraire( itineraire.getId());
	   logger.debug( "apres maj");
	   logger.debug( "premArr="+nv_arrets.get( 0).getId()+", der="+nv_arrets.get( arrets.size()-1).getId());
	   assert derArr==nv_arrets.get( 0).getId():"echec de la mj de l'ordre des arrts";
	   
	   // TODO Supprimer ligne itineraire
   }
}
