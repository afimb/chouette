package unit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;
import org.exolab.castor.types.Time;
import org.testng.annotations.Test;

import chouette.schema.types.ChouetteAreaType;
import fr.certu.chouette.critere.AndClause;
import fr.certu.chouette.critere.IClause;
import fr.certu.chouette.critere.ScalarClause;
import fr.certu.chouette.dao.ISelectionSpecifique;
import fr.certu.chouette.modele.ArretItineraire;
import fr.certu.chouette.modele.Correspondance;
import fr.certu.chouette.modele.Course;
import fr.certu.chouette.modele.Horaire;
import fr.certu.chouette.modele.Itineraire;
import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.modele.Reseau;
import fr.certu.chouette.modele.TableauMarche;
import fr.certu.chouette.modele.Transporteur;
import fr.certu.chouette.service.commun.CodeIncident;
import fr.certu.chouette.service.commun.ServiceException;
import fr.certu.chouette.service.database.IArretItineraireManager;
import fr.certu.chouette.service.database.ICorrespondanceManager;
import fr.certu.chouette.service.database.ICourseManager;
import fr.certu.chouette.service.database.IHoraireManager;
import fr.certu.chouette.service.database.IItineraireManager;
import fr.certu.chouette.service.database.ILigneManager;
import fr.certu.chouette.service.database.IPositionGeographiqueManager;
import fr.certu.chouette.service.database.IReseauManager;
import fr.certu.chouette.service.database.ITableauMarcheManager;
import fr.certu.chouette.service.database.ITransporteurManager;
import fr.certu.chouette.service.database.impl.modele.EtatMajArretItineraire;
import fr.certu.chouette.service.database.impl.modele.EtatMajHoraire;
import fr.certu.chouette.service.identification.IIdentificationManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;

@ContextConfiguration(locations = {"classpath:testContext.xml"})
public class DBServiceTestCase extends AbstractTestNGSpringContextTests {

    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(DBServiceTestCase.class);
    private IItineraireManager itineraireManager;
    private ILigneManager ligneManager;
    private ICourseManager courseManager;
    private IArretItineraireManager arretItineraireManager;
    private IPositionGeographiqueManager positionGeographiqueManager;
    private IHoraireManager horaireManager;
    private ITableauMarcheManager tableauMarcheManager;
    private IReseauManager reseauManager;
    private ITransporteurManager transporteurManager;
    private IIdentificationManager identificationManager;
    private ICorrespondanceManager correspondanceManager;
    private ISelectionSpecifique selectionSpecifique;

    public DBServiceTestCase() {
    }

    @BeforeMethod
    protected void getbeans() throws Exception {
        itineraireManager = (IItineraireManager) applicationContext.getBean("itineraireManager");
        ligneManager = (ILigneManager) applicationContext.getBean("ligneManager");
        arretItineraireManager = (IArretItineraireManager) applicationContext.getBean("arretItineraireManager");
        courseManager = (ICourseManager) applicationContext.getBean("courseManager");
        horaireManager = (IHoraireManager) applicationContext.getBean("horaireManager");
        tableauMarcheManager = (ITableauMarcheManager) applicationContext.getBean("tableauMarcheManager");
        reseauManager = (IReseauManager) applicationContext.getBean("reseauManager");
        transporteurManager = (ITransporteurManager) applicationContext.getBean("transporteurManager");
        identificationManager = (IIdentificationManager) applicationContext.getBean("identificationManager");
        positionGeographiqueManager = (IPositionGeographiqueManager) applicationContext.getBean("positionGeographiqueManager");
        correspondanceManager = (ICorrespondanceManager) applicationContext.getBean("correspondanceManager");

        selectionSpecifique = (ISelectionSpecifique) applicationContext.getBean("selectionSpecifique");
    }

    @Test(groups = "tests unitaires services persistence", description = "teste la protection des services contre des données invalides")
    public void testLimite() {
        Ligne uneLigne = GenerateurDonnee.creerLigne();

        Itineraire aller;

        String valeurOk = uneLigne.getRegistrationNumber();

        // tentative avec chaine trop longue

        int MAX = 256;
        char[] tropLong = new char[MAX];
        Arrays.fill(tropLong, 'a');
        String tropLongRegsitrationNumber = String.copyValueOf(tropLong);
        uneLigne.setRegistrationNumber(tropLongRegsitrationNumber);
        try {
            ligneManager.creer(uneLigne);
            assert false : "pas d'exception levee sur un champ trop long";
        } catch (ServiceException e) {
            assert CodeIncident.CONTRAINTE_INVALIDE.equals(e.getCode()) :
                    "l'exception levé n'a pas le code attendu " + CodeIncident.CONTRAINTE_INVALIDE
                    + " mais " + e.getCode();
        }

        // tentative avec null
//	   uneLigne.setRegistrationNumber( null);
//	   try
//	   {
//		   ligneManager.creer( uneLigne);
//		   assert false: "echec de la recuperation sur donnee invalide, pas d'exception levee";
//	   }
//	   catch( ServiceException e)
//	   {
//		   logger.debug( "interception "+e.getMessage()+", code="+e.getCode());
//	   }
//	   catch( Exception e)
//	   {
//		   logger.error( e.getMessage(), e);
//		   throw new RuntimeException( "echec de la recuperation sur donnee invalide");
//	   }

        // tentative avec donnee valide

//        uneLigne.setRegistrationNumber(valeurOk);
//        ligneManager.creer(uneLigne);
//
//        // tentative d'ajout de doublon / registration
//        Ligne unDoublonLigne = GenerateurDonnee.creerLigne();
//        unDoublonLigne.setRegistrationNumber(valeurOk);
//
//        try {
//            ligneManager.creer(unDoublonLigne);
//            assert false : "pas d'exception levée sur une ligne créée avec meme registrationNumber";
//        } catch (ServiceException e) {
//            assert CodeIncident.CONTRAINTE_INVALIDE.equals(e.getCode()) :
//                    "l'exception levé n'a pas le code attendu " + CodeIncident.CONTRAINTE_INVALIDE
//                    + " mais " + e.getCode();
//        }

        // tentative avec référence fausse

        aller = GenerateurDonnee.creerItineraire(uneLigne.getId());
        itineraireManager.creer(aller);

        aller.setId(-23L);
        try {
            itineraireManager.modifier(aller);
            assert false : "pas d'exception levée, sur màj avec id inexistant";
        } catch (ServiceException e) {
            assert CodeIncident.DONNEE_INVALIDE.equals(e.getCode()) :
                    "l'exception levé n'a pas le code attendu " + CodeIncident.DONNEE_INVALIDE
                    + " mais " + e.getCode();
        }

        ligneManager.supprimer(uneLigne.getId());
    }

    @Test(groups = "tests unitaires services persistence", description = "service ligne - repérage ligne homonyme")
    public void testLigneHomonyme() {
        Ligne ligne1 = GenerateurDonnee.creerLigne();

        List<Ligne> lignes = ligneManager.select(ScalarClause.newEqualsClause("name", ligne1.getName()));

        for (Ligne ligne : lignes) {
            ligneManager.supprimer(ligne.getId());
        }

        assert !ligneManager.nomConnu(ligne1.getName());
        ligneManager.creer(ligne1);
        assert ligneManager.nomConnu(ligne1.getName());
        ligneManager.supprimer(ligne1.getId());
        assert !ligneManager.nomConnu(ligne1.getName());
    }

    @Test(groups = "tests unitaires services persistence", description = "service crud ligne-transporteur")
    public void testSelectionCoursesTriees() {
        List<Course> courses = itineraireManager.getCoursesItineraire(304856L);

        for (Course course : courses) {
            logger.debug(course.getId());
        }
    }

    @Test(groups = "tests unitaires services persistence", description = "vérifie le respect des contraintes d'unicité")
    public void testContrainteUnicite() {
        Reseau res1 = GenerateurDonnee.creerResau();
        Reseau res2 = GenerateurDonnee.creerResau();

        res2.setRegistrationNumber(res1.getRegistrationNumber());

        reseauManager.creer(res1);

        try {
            reseauManager.creer(res2);
            assert false : "Le regsitrationNumber doit être unique, la contrainte n'est pas vérifiée avec "
                    + res1.getRegistrationNumber();
        } catch (ServiceException e) {
            assert e.getCode().equals(CodeIncident.CONTRAINTE_INVALIDE) :
                    "Le code d'exception n'est pas celui attendu " + CodeIncident.CONTRAINTE_INVALIDE
                    + " mais " + e.getCode();
        }
        assert res2.getId() == null : "La tentative d'enregistrement a modifié l'instance!";
        res2.setRegistrationNumber(res1.getRegistrationNumber() + "A");
        reseauManager.creer(res2);

        reseauManager.supprimer(res1.getId());
        reseauManager.supprimer(res2.getId());
    }

    @Test(groups = "tests unitaires services persistence", description = "service de sélection de TM")
    public void testSelectionTM() {
        TableauMarche tm = GenerateurDonnee.creerTM();

        tableauMarcheManager.creer(tm);

        IClause clause1 = ScalarClause.newIlikeClause("comment", tm.getComment().substring(0, tm.getComment().length() / 2));
        IClause clause2 = ScalarClause.newEqualsClause("objectId", tm.getObjectId());

        IClause maClause = new AndClause(clause1, clause2);
        List<TableauMarche> tms = tableauMarcheManager.select(maClause);

        int total = tms.size();
        assert total > 0;

        tableauMarcheManager.supprimer(tm.getId());

        tms = tableauMarcheManager.select(maClause);
        assert (total - 1) == tms.size();
    }

    @Test(groups = "tests unitaires services persistence", description = "service crud ligne-transporteur")
    public void testTransporteurLigne() {
        Ligne uneLigne = GenerateurDonnee.creerLigne();
        Transporteur unTransporteur = GenerateurDonnee.creerTransporteur();

        transporteurManager.creer(unTransporteur);

        Transporteur unTransporteurLu = null;
        unTransporteurLu = transporteurManager.lire(unTransporteur.getId());
        assert unTransporteurLu != null : "Echec de l'enregistrement du transporteur";

        uneLigne.setIdTransporteur(unTransporteur.getId());
        ligneManager.creer(uneLigne);

        Ligne ligneLue = null;
        ligneLue = ligneManager.lire(uneLigne.getId());
        assert ligneLue != null : "Echec de l'enregistrement de la ligne";

        transporteurManager.supprimer(unTransporteur.getId());

        try {
            unTransporteurLu = transporteurManager.lire(unTransporteur.getId());
            throw new RuntimeException("Echec de la suppression du transporteur");
        } catch (ServiceException e) {
            if (!CodeIncident.IDENTIFIANT_INCONNU.equals(e.getCode())) {
                throw new RuntimeException("Echec de la suppression du transporteur");
            }
        }
        ligneLue = null;
        ligneLue = ligneManager.lire(uneLigne.getId());

        assert ligneLue.getIdTransporteur() == null : "Echec de la suppression du lien ligne transporteur";

        Transporteur unTransporteurAutre = GenerateurDonnee.creerTransporteur();
        transporteurManager.creer(unTransporteurAutre);
        unTransporteurLu = transporteurManager.lire(unTransporteurAutre.getId());
        assert unTransporteurAutre != null : "Echec de l'enregistrement du transporteur";

        transporteurManager.supprimer(unTransporteurAutre.getId());
        try {
            unTransporteurLu = transporteurManager.lire(unTransporteurAutre.getId());
        } catch (ServiceException e) {
            if (!CodeIncident.IDENTIFIANT_INCONNU.equals(e.getCode())) {
                throw new RuntimeException("Echec de la suppression du transporteur");
            }
        }
    }

    @Test(groups = "tests unitaires services persistence", description = "associer dissocier des itineraires")
    public void associationItineraire() {
        Ligne ligne = GenerateurDonnee.creerLigne();

        ligneManager.creer(ligne);
        assert ligne.getObjectId().equals(identificationManager.getIdFonctionnel("Line", ligne));

        Itineraire r1 = GenerateurDonnee.creerItineraire(ligne.getId());
        Itineraire r2 = GenerateurDonnee.creerItineraire(ligne.getId());
        Itineraire r3 = GenerateurDonnee.creerItineraire(ligne.getId());
        Itineraire r4 = GenerateurDonnee.creerItineraire(ligne.getId());

        r1.setWayBack("R");
        r2.setWayBack("R");
        r3.setWayBack("R");
        r4.setWayBack("R");
        itineraireManager.creer(r1);
        itineraireManager.creer(r2);
        itineraireManager.creer(r3);
        itineraireManager.creer(r4);

        itineraireManager.associerItineraire(r1.getId(), r2.getId());
        itineraireManager.associerItineraire(r3.getId(), r4.getId());

        r1 = itineraireManager.lire(r1.getId());
        r2 = itineraireManager.lire(r2.getId());
        r3 = itineraireManager.lire(r3.getId());
        r4 = itineraireManager.lire(r4.getId());

        Itineraire lectureItineraire = itineraireManager.lire(r1.getId());
        assert lectureItineraire.getIdRetour().equals(r2.getId());
        assert r1.getWayBack().equals("R");
        lectureItineraire = itineraireManager.lire(r2.getId());
        assert lectureItineraire.getIdRetour().equals(r1.getId());
        assert r2.getWayBack().equals("A");
        lectureItineraire = itineraireManager.lire(r4.getId());
        assert lectureItineraire.getIdRetour().equals(r3.getId());
        assert r3.getWayBack().equals("R");
        lectureItineraire = itineraireManager.lire(r3.getId());
        assert lectureItineraire.getIdRetour().equals(r4.getId());
        assert r4.getWayBack().equals("A");




        itineraireManager.dissocierItineraire(r1.getId());
        lectureItineraire = itineraireManager.lire(r1.getId());
        assert lectureItineraire.getIdRetour() == null;
        lectureItineraire = itineraireManager.lire(r2.getId());
        assert lectureItineraire.getIdRetour() == null;



        itineraireManager.associerItineraire(r1.getId(), r4.getId());
        lectureItineraire = itineraireManager.lire(r1.getId());
        assert lectureItineraire.getIdRetour().equals(r4.getId());
        lectureItineraire = itineraireManager.lire(r4.getId());
        assert lectureItineraire.getIdRetour().equals(r1.getId());
        lectureItineraire = itineraireManager.lire(r3.getId());
        assert lectureItineraire.getIdRetour() == null;


        itineraireManager.supprimer(r1.getId());
        itineraireManager.supprimer(r2.getId());
        itineraireManager.supprimer(r3.getId());
        itineraireManager.supprimer(r4.getId());

        ligneManager.supprimer(ligne.getId());
    }

    @Test(groups = "tests unitaires services persistence", description = "associer dissocier des zones et des correspondances")
    public void correspondance_zones() {
        PositionGeographique zone = GenerateurDonnee.creerZone("");
        PositionGeographique zoneParente = GenerateurDonnee.creerZone("");
        PositionGeographique arretPhysique = GenerateurDonnee.creerArretPhysique("");

        positionGeographiqueManager.creer(zone);
        positionGeographiqueManager.creer(zoneParente);
        positionGeographiqueManager.creer(arretPhysique);

        positionGeographiqueManager.associerGeoPositions(zoneParente.getId(), zone.getId());

        Correspondance correspondance = GenerateurDonnee.creerCorrespondance();
        correspondanceManager.creer(correspondance);

        Correspondance correspondanceLue = correspondanceManager.lire(correspondance.getId());
        assert correspondance != null;

        correspondanceLue.setIdDepartArrivee(arretPhysique.getId(), zone.getId());

        correspondanceManager.modifier(correspondanceLue);
        correspondanceLue = correspondanceManager.lire(correspondance.getId());
        assert correspondanceLue.getIdArrivee().equals(zone.getId());
        assert correspondanceLue.getIdDepart().equals(arretPhysique.getId());

        List<Correspondance> correspondances = correspondanceManager.getCorrespondancesParGeoPosition(arretPhysique.getId());
        assert correspondances.size() == 1;
        assert correspondances.get(0).getIdArrivee().equals(zone.getId()) && correspondances.get(0).getIdDepart().equals(arretPhysique.getId());
        correspondances = correspondanceManager.getCorrespondancesParGeoPosition(zone.getId());
        assert correspondances.size() == 1;
        assert correspondances.get(0).getIdArrivee().equals(zone.getId()) && correspondances.get(0).getIdDepart().equals(arretPhysique.getId());

        zone = positionGeographiqueManager.lire(zone.getId());
        List<PositionGeographique> zonesParentes = positionGeographiqueManager.getGeoPositionsParentes(zone.getIdParent());
        assert zonesParentes.size() == 1 : "1 zone parente attendue, " + zonesParentes.size() + " zones trouvées";
        assert zonesParentes.get(0).getId().equals(zoneParente.getId());

        correspondanceManager.supprimer(correspondance.getId());
        positionGeographiqueManager.supprimer(zone.getId());
        positionGeographiqueManager.supprimer(zoneParente.getId());
        positionGeographiqueManager.supprimer(arretPhysique.getId());
    }

    @Test(groups = "tests unitaires services persistence", description = "associer dissocier des zones")
    public void associationZone() {
        PositionGeographique zone = GenerateurDonnee.creerZone("");
        PositionGeographique zoneParente = GenerateurDonnee.creerZone("");

        positionGeographiqueManager.creer(zone);
        positionGeographiqueManager.creer(zoneParente);
        assert zone.getObjectId().equals(identificationManager.getIdFonctionnel("StopArea", zone));
        assert zoneParente.getObjectId().equals(identificationManager.getIdFonctionnel("StopArea", zoneParente));

        PositionGeographique zoneLue = positionGeographiqueManager.lire(zone.getId());
        assert zone.getAreaType().equals(zoneLue.getAreaType());
        assert zone.getX().longValue() == zoneLue.getX().longValue() : "x lu " + zoneLue.getX() + ", x attendu " + zone.getX();
        assert zone.getName().equals(zoneLue.getName());

        String nomMaj = "tit";
        zone.setName(nomMaj);
        positionGeographiqueManager.modifier(zone);

        zoneLue = positionGeographiqueManager.lire(zone.getId());
        assert zone.getName().equals(zoneLue.getName());



        positionGeographiqueManager.associerGeoPositions(zoneParente.getId(), zone.getId());
        List<PositionGeographique> geoPos = positionGeographiqueManager.getGeoPositionsDirectementContenues(zoneParente.getId());
        assert geoPos.size() == 1;
        assert geoPos.get(0).getAreaType().equals(ChouetteAreaType.STOPPLACE);
        assert geoPos.get(0).getId().equals(zone.getId());

        positionGeographiqueManager.dissocierGeoPosition(zoneParente.getId());
        zoneLue = positionGeographiqueManager.lire(zone.getId());
        assert zoneLue.getIdParent() == null : "échec de dissociation sur la zone id=" + zone.getId();


        PositionGeographique arretPhysique = GenerateurDonnee.creerArretPhysique("");
        positionGeographiqueManager.creer(arretPhysique);

        positionGeographiqueManager.associerGeoPositions(zoneParente.getId(), zone.getId());
        positionGeographiqueManager.associerGeoPositions(zoneParente.getId(), arretPhysique.getId());

        geoPos = positionGeographiqueManager.getGeoPositionsDirectementContenues(zoneParente.getId());
        assert geoPos.size() == 2 : "echec association geoPos.size()=" + geoPos.size();
        Set<Long> idsContenus = new HashSet<Long>();
        for (PositionGeographique geo : geoPos) {
            assert geo.getIdParent().equals(zoneParente.getId());
            idsContenus.add(geo.getId());
        }
        assert idsContenus.contains(zone.getId()) && idsContenus.contains(arretPhysique.getId());

        positionGeographiqueManager.dissocierGeoPosition(zone.getId());
        positionGeographiqueManager.dissocierGeoPosition(arretPhysique.getId());
        zoneLue = positionGeographiqueManager.lire(zone.getId());
        assert zoneLue.getIdParent() == null : "échec de dissociation sur la zone id=" + zone.getId();

        positionGeographiqueManager.associerGeoPositions(zoneParente.getId(), zone.getId());
        geoPos = positionGeographiqueManager.getGeoPositionsDirectementContenues(zoneParente.getId());
        assert geoPos.size() == 1;
        assert geoPos.get(0).getId().equals(zone.getId());

        positionGeographiqueManager.supprimer(zoneParente.getId());

        zoneLue = positionGeographiqueManager.lire(zone.getId());
        assert zoneLue.getIdParent() == null : "échec de dissociation lors de la suppression";
        positionGeographiqueManager.supprimer(zone.getId());
        positionGeographiqueManager.supprimer(arretPhysique.getId());

        try {
            zoneLue = positionGeographiqueManager.lire(zone.getId());
            throw new RuntimeException("Echec de la suppression de la zone");
        } catch (ServiceException e) {
            if (!CodeIncident.IDENTIFIANT_INCONNU.equals(e.getCode())) {
                throw new RuntimeException("Echec de la suppression de la zone");
            }
        }
    }

    @Test(groups = "tests unitaires services persistence", description = "deplacer les arrets sur un itineraire")
    public void deplacerArrets() {
        int maxArrets = 6;
        Ligne uneLigne = GenerateurDonnee.creerLigne();

        Itineraire aller;

        ligneManager.creer(uneLigne);
        assert uneLigne.getObjectId().equals(identificationManager.getIdFonctionnel("Line", uneLigne));

        aller = GenerateurDonnee.creerItineraire(uneLigne.getId());

        itineraireManager.creer(aller);

        assert aller.getObjectId().equals(identificationManager.getIdFonctionnel("Route", aller));

        Ligne ligneLue = ligneManager.lire(uneLigne.getId());
        assert ligneLue != null : "Echec de l'enregistrement de la ligne";

        Itineraire itineraireLu = itineraireManager.lire(aller.getId());
        assert itineraireLu != null : "Echec de l'enregistrement de l'itineraire";

        List<ArretItineraire> arretsItineraire = itineraireManager.getArretsItineraire(aller.getId());
        assert arretsItineraire.size() == 0;

        List<EtatMajArretItineraire> majItineraire = new ArrayList<EtatMajArretItineraire>();
        for (int i = 0; i < maxArrets; i++) {
            EtatMajArretItineraire etatMaj = EtatMajArretItineraire.creerCreation(i, "A" + i);
            majItineraire.add(etatMaj);
        }

        itineraireManager.modifierArretsItineraire(aller.getId(), majItineraire);

        arretsItineraire = itineraireManager.getArretsItineraire(aller.getId());
        assert arretsItineraire.size() == maxArrets;

        ArretItineraire arretAsupprimer = arretsItineraire.get(0);
        Long idPhysique = arretAsupprimer.getIdPhysique();
        EtatMajArretItineraire etatMaj = EtatMajArretItineraire.creerSuppression(arretAsupprimer.getId());
        majItineraire.clear();
        majItineraire.add(etatMaj);
        itineraireManager.modifierArretsItineraire(aller.getId(), majItineraire);

        arretsItineraire = itineraireManager.getArretsItineraire(aller.getId());
        assert arretsItineraire.size() == (maxArrets - 1) : arretsItineraire.size() + " arrets trouves, " + (maxArrets - 1) + " attendus";

        etatMaj = EtatMajArretItineraire.creerCreation(0, idPhysique);
        majItineraire.clear();
        majItineraire.add(etatMaj);
        itineraireManager.modifierArretsItineraire(aller.getId(), majItineraire);

        arretsItineraire = itineraireManager.getArretsItineraire(aller.getId());
        assert arretsItineraire.size() == maxArrets;

        // deplacement

        // Cas invalide: 2 fois le meme arret deplace
        Long idLogiquePrem = arretsItineraire.get(0).getId();
        Long idLogiqueDer = arretsItineraire.get(0).getId(); // erreur repetition d'arret

        EtatMajArretItineraire etatMaj1 = EtatMajArretItineraire.creerDeplace(arretsItineraire.size() - 1, idLogiquePrem);
        EtatMajArretItineraire etatMaj2 = EtatMajArretItineraire.creerDeplace(0, idLogiqueDer);
        majItineraire.clear();
        majItineraire.add(etatMaj1);
        majItineraire.add(etatMaj2);
        try {
            itineraireManager.modifierArretsItineraire(aller.getId(), majItineraire);
            assert false : "la postion 1 apres deplacement ne correspond a aucun des arrets initiaux";
        } catch (ServiceException e) {
        }

        // Cas invalide: collision sur un meme arret
        idLogiquePrem = arretsItineraire.get(0).getId();
        idLogiqueDer = arretsItineraire.get(arretsItineraire.size() - 1).getId();

        etatMaj1 = EtatMajArretItineraire.creerDeplace(arretsItineraire.size() - 1, idLogiquePrem);
        etatMaj2 = EtatMajArretItineraire.creerDeplace(1, idLogiqueDer);
        majItineraire.clear();
        majItineraire.add(etatMaj1);
        majItineraire.add(etatMaj2);
        try {
            itineraireManager.modifierArretsItineraire(aller.getId(), majItineraire);
            assert false : "la postion 1 apres deplacement ne correspond a aucun des arrets initiaux";
        } catch (ServiceException e) {
        }

        // Cas valide
        etatMaj1 = EtatMajArretItineraire.creerDeplace(arretsItineraire.size() - 1, idLogiquePrem);
        etatMaj2 = EtatMajArretItineraire.creerDeplace(0, idLogiqueDer);
        majItineraire.clear();
        majItineraire.add(etatMaj1);
        majItineraire.add(etatMaj2);
        itineraireManager.modifierArretsItineraire(aller.getId(), majItineraire);

        arretsItineraire = itineraireManager.getArretsItineraire(aller.getId());
        Set<Integer> lesPositions = new HashSet<Integer>();
        for (ArretItineraire arret : arretsItineraire) {
            lesPositions.add(arret.getPosition());
        }
        assert lesPositions.size() == maxArrets;
    }

    @Test(groups = "tests unitaires services persistence", description = "supprimer un arret sur un itineraire")
    public void supprimerArrets() {
        int maxArrets = 6;
        Ligne uneLigne = GenerateurDonnee.creerLigne();

        Itineraire aller;

        ligneManager.creer(uneLigne);
        assert uneLigne.getObjectId().equals(identificationManager.getIdFonctionnel("Line", uneLigne));

        aller = GenerateurDonnee.creerItineraire(uneLigne.getId());

        itineraireManager.creer(aller);

        assert aller.getObjectId().equals(identificationManager.getIdFonctionnel("Route", aller));

        Ligne ligneLue = ligneManager.lire(uneLigne.getId());
        assert ligneLue != null : "Echec de l'enregistrement de la ligne";

        Itineraire itineraireLu = itineraireManager.lire(aller.getId());
        assert itineraireLu != null : "Echec de l'enregistrement de l'itineraire";

        List<ArretItineraire> arretsItineraire = itineraireManager.getArretsItineraire(aller.getId());
        assert arretsItineraire.size() == 0;

        List<EtatMajArretItineraire> majItineraire = new ArrayList<EtatMajArretItineraire>();
        for (int i = 0; i < maxArrets; i++) {
            EtatMajArretItineraire etatMaj = EtatMajArretItineraire.creerCreation(i, "A" + i);
            majItineraire.add(etatMaj);
        }

        itineraireManager.modifierArretsItineraire(aller.getId(), majItineraire);

        arretsItineraire = itineraireManager.getArretsItineraire(aller.getId());
        assert arretsItineraire.size() == maxArrets;

        ArretItineraire arretAsupprimer = arretsItineraire.get(maxArrets / 2);
        EtatMajArretItineraire etatMaj = EtatMajArretItineraire.creerSuppression(arretAsupprimer.getId());
        EtatMajArretItineraire etatMaj1 = EtatMajArretItineraire.creerDeplace(maxArrets / 2, arretsItineraire.get(maxArrets - 1).getId());
        majItineraire.clear();
        majItineraire.add(etatMaj);
        majItineraire.add(etatMaj1);
        itineraireManager.modifierArretsItineraire(aller.getId(), majItineraire);

        List<ArretItineraire> arretsItineraireApres = itineraireManager.getArretsItineraire(aller.getId());
        assert arretsItineraireApres.size() == (maxArrets - 1) : arretsItineraireApres.size() + " arrets trouves, " + (maxArrets - 1) + " attendus";
        assert arretsItineraireApres.get(maxArrets / 2).getId().equals(arretsItineraire.get(maxArrets - 1).getId());
    }

    @Test(groups = "tests unitaires services persistence", description = "filtre de lignes par reseaux et transporteurs")
    public void testFiltreDeLignes() {
        Reseau unReseauA = GenerateurDonnee.creerResau();
        Reseau unReseauB = GenerateurDonnee.creerResau();
        Transporteur unTransporteurA = GenerateurDonnee.creerTransporteur();
        Transporteur unTransporteurB = GenerateurDonnee.creerTransporteur();

        reseauManager.creer(unReseauA);
        reseauManager.creer(unReseauB);
        transporteurManager.creer(unTransporteurA);
        transporteurManager.creer(unTransporteurB);

        Reseau unReseauLu = null;
        unReseauLu = reseauManager.lire(unReseauA.getId());
        assert unReseauLu != null : "Echec de l'enregistrement du reseau";
        unReseauLu = null;
        unReseauLu = reseauManager.lire(unReseauB.getId());
        assert unReseauLu != null : "Echec de l'enregistrement du reseau";

        Transporteur unTransporteurLu = null;
        unTransporteurLu = transporteurManager.lire(unTransporteurA.getId());
        assert unTransporteurLu != null : "Echec de l'enregistrement du transporteur";
        unTransporteurLu = null;
        unTransporteurLu = transporteurManager.lire(unTransporteurB.getId());
        assert unTransporteurLu != null : "Echec de l'enregistrement du transporteur";

        Ligne uneLigneAA = GenerateurDonnee.creerLigne();
        uneLigneAA.setIdReseau(unReseauA.getId());
        uneLigneAA.setIdTransporteur(unTransporteurA.getId());
        ligneManager.creer(uneLigneAA);

        Ligne uneLigneAB = GenerateurDonnee.creerLigne();
        uneLigneAB.setIdReseau(unReseauA.getId());
        uneLigneAB.setIdTransporteur(unTransporteurB.getId());
        ligneManager.creer(uneLigneAB);

        Collection<Long> idReseaux = new ArrayList<Long>();
        idReseaux.add(unReseauA.getId());
        idReseaux.add(unReseauB.getId());

        Collection<Long> idTransporteurs = new ArrayList<Long>();
        idTransporteurs.add(unTransporteurA.getId());
        idTransporteurs.add(unTransporteurB.getId());

        List<Ligne> lignes = ligneManager.filtrer(idReseaux, idTransporteurs);
        assert lignes.size() == 2 : "total attendu=" + 2 + ", trouve=" + lignes.size();
        for (Ligne ligne : lignes) {
            assert idReseaux.contains(ligne.getIdReseau());
            assert idTransporteurs.contains(ligne.getIdTransporteur());
        }
    }

    @Test(groups = "tests unitaires services persistence", description = "service crud ligne-reseau")
    public void testReseauLigne() {
        Ligne uneLigne = GenerateurDonnee.creerLigne();
        Reseau unReseau = GenerateurDonnee.creerResau();

        reseauManager.creer(unReseau);

        Reseau unReseauLu = null;
        unReseauLu = reseauManager.lire(unReseau.getId());
        assert unReseauLu != null : "Echec de l'enregistrement du reseau";

        uneLigne.setIdReseau(unReseau.getId());
        ligneManager.creer(uneLigne);

        Ligne ligneLue = null;
        ligneLue = ligneManager.lire(uneLigne.getId());
        assert ligneLue != null : "Echec de l'enregistrement de la ligne";

        reseauManager.supprimer(unReseau.getId());

        try {
            unReseauLu = reseauManager.lire(unReseau.getId());
            throw new RuntimeException("Echec de la suppression du réseau");
        } catch (ServiceException e) {
            if (!CodeIncident.IDENTIFIANT_INCONNU.equals(e.getCode())) {
                throw new RuntimeException("Echec de la suppression du réseau");
            }
        }
        ligneLue = null;
        ligneLue = ligneManager.lire(uneLigne.getId());

        assert ligneLue.getIdReseau() == null : "Echec de la suppression du lien ligne reseau";

        Reseau unReseauAutre = GenerateurDonnee.creerResau();
        reseauManager.creer(unReseauAutre);
        unReseauLu = reseauManager.lire(unReseauAutre.getId());
        assert unReseauAutre != null : "Echec de l'enregistrement du reseau";

        reseauManager.supprimer(unReseauAutre.getId());
        try {
            unReseauLu = reseauManager.lire(unReseauAutre.getId());
        } catch (ServiceException e) {
            if (!CodeIncident.IDENTIFIANT_INCONNU.equals(e.getCode())) {
                throw new RuntimeException("Echec de la suppression du réseau");
            }
        }
    }

    @Test(groups = "tests unitaires services persistence", description = "service modification d'horaires")
    public void testModificationHoraires() {
        Ligne uneLigne = GenerateurDonnee.creerLigne();
        ligneManager.creer(uneLigne);

        Itineraire aller = GenerateurDonnee.creerItineraire(uneLigne.getId());
        itineraireManager.creer(aller);

        int maxArrets = 6;
        ArrayList<Long> lesArretId = new ArrayList<Long>(maxArrets);
        for (int i = 0; i < maxArrets; i++) {
            ArretItineraire arret = GenerateurDonnee.creerArret(aller.getId());
            arret.setPosition(i);
            arretItineraireManager.creer(arret);
            assert arret.getObjectId().equals(identificationManager.getIdFonctionnel("StopPoint", arret));

            lesArretId.add(arret.getId());
        }

        Course course = GenerateurDonnee.creerCourse(aller.getId());
        courseManager.creer(course);

        Long idCourse = course.getId();
        Long heureDepart = 8L * 1000L * 3600L;
        Long duree = 3L * 1000L * 60L;

        List<EtatMajHoraire> majHoraires = new ArrayList<EtatMajHoraire>();
        for (Long idArret : lesArretId) {
            Time heure = new Time(heureDepart);
            majHoraires.add(EtatMajHoraire.getCreation(idArret, idCourse, heure.toDate()));
            heureDepart += duree;
        }
        horaireManager.modifier(majHoraires);

        List<Horaire> horaires = courseManager.getHorairesCourse(course.getId());
        Map<Long, Date> timeParArret = new Hashtable<Long, Date>();
        Long autreHeureDepart = 18L * 1000L * 3600L;

        majHoraires.clear();
        for (int i = 0; i < horaires.size(); i++) {
            Horaire horaire = horaires.get(i);
            Time heure = new Time(autreHeureDepart + i * duree);
            timeParArret.put(horaire.getIdArret(), heure.toDate());
            horaire.setDepartureTime(heure.toDate());
            horaire.setArrivalTime(heure.toDate());
            majHoraires.add(
                    EtatMajHoraire.getModification(horaire));

        }
        horaireManager.modifier(majHoraires);

        horaires = courseManager.getHorairesCourse(course.getId());
        for (Horaire horaire : horaires) {
            assert timeParArret.get(horaire.getIdArret()).equals(horaire.getDepartureTime());
        }
        ligneManager.supprimer(uneLigne.getId());
    }

    @Test(groups = "tests unitaires services persistence", description = "service crud itineraire-arret-horaire-course-TM")
    public void testCrud() {
        Ligne uneLigne = GenerateurDonnee.creerLigne();

        Itineraire aller;
        Itineraire retour;

        ligneManager.creer(uneLigne);
        assert uneLigne.getObjectId().equals(identificationManager.getIdFonctionnel("Line", uneLigne));
        logger.debug(uneLigne.toString());

        aller = GenerateurDonnee.creerItineraire(uneLigne.getId());
        retour = GenerateurDonnee.creerItineraire(uneLigne.getId());

        itineraireManager.creer(aller);
        itineraireManager.creer(retour);

        assert aller.getObjectId().equals(identificationManager.getIdFonctionnel("Route", aller));
        assert retour.getObjectId().equals(identificationManager.getIdFonctionnel("Route", retour));

        Ligne ligneLue = ligneManager.lire(uneLigne.getId());
        assert ligneLue != null : "Echec de l'enregistrement de la ligne";

        Itineraire itineraireLu = itineraireManager.lire(aller.getId());
        assert itineraireLu != null : "Echec de l'enregistrement de l'itineraire";

        int maxArrets = 6;
        ArrayList<Long> lesArretId = new ArrayList<Long>(maxArrets);

        for (int i = 0; i < maxArrets; i++) {
            ArretItineraire arret = GenerateurDonnee.creerArret(itineraireLu.getId());
            arret.setPosition(i);
            arretItineraireManager.creer(arret);
            assert arret.getObjectId().equals(identificationManager.getIdFonctionnel("StopPoint", arret));

            lesArretId.add(arret.getId());
        }

        int maxCourses = 5;
        int maxHoraire = maxArrets / 2;

        for (int i = 0; i < maxCourses; i++) {
            Course course = GenerateurDonnee.creerCourse(itineraireLu.getId());
            courseManager.creer(course);

            Long idCourse = course.getId();

            Long heureDepart = 8L * 1000L * 3600L;
            Long duree = 3L * 1000L * 60L;

            List<EtatMajHoraire> majHoraires = new ArrayList<EtatMajHoraire>();
            Map<Long, Time> timeParArret = new Hashtable<Long, Time>();
            for (int j = 0; j < maxHoraire; j++) {
                Random random = new Random();
                Long idArret = lesArretId.get(2 * j + random.nextInt(2));
                Time heure = new Time(heureDepart + j * duree);

                timeParArret.put(idArret, heure);
                majHoraires.add(EtatMajHoraire.getCreation(idArret, idCourse, heure.toDate()));
            }
            horaireManager.modifier(majHoraires);

            List<Horaire> horairesLus = courseManager.getHorairesCourseOrdonnes(idCourse);
            assert maxHoraire == horairesLus.size() : "Echec à la sauvegarde des horaires";

            for (Horaire horaire : horairesLus) {
                Time heure = timeParArret.get(horaire.getIdArret());
                assert heure != null : "echec de la sauvegarde de l'horaire";
                assert horaire.getArrivalTime().equals(heure.toDate()) : "echec de la sauvegarde de l'horaire";

                assert (horaire.getDepart() && horaire.getIdArret().equals(majHoraires.get(0).getHoraire().getIdArret()))
                        || (!horaire.getDepart() && !horaire.getIdArret().equals(majHoraires.get(0).getHoraire().getIdArret()));
            }
        }

        List<Course> coursesItineraire = selectionSpecifique.getCoursesItineraire(itineraireLu.getId());
        assert maxCourses == coursesItineraire.size() : "echec enregistrement lien course itineraire";

        // suppression d'arret
        List<ArretItineraire> arretsIti = selectionSpecifique.getArretsItineraire(itineraireLu.getId());
        int totalarretsiti = arretsIti.size();
        assert maxArrets == totalarretsiti : "echec enregistrement lien arret iti";
        List<Horaire> horairesCourse = selectionSpecifique.getHorairesCourse(coursesItineraire.get(0).getId());
        assert maxHoraire == horairesCourse.size() : "echec enregistrement lien arret iti";

        if (maxHoraire > 0) {
            Long idArret = horairesCourse.get(maxHoraire / 2).getIdArret();

            arretItineraireManager.supprimer(idArret);
        }
        arretsIti = selectionSpecifique.getArretsItineraire(itineraireLu.getId());
        totalarretsiti = arretsIti.size();
        assert maxArrets - 1 == totalarretsiti : "echec suppression lien arret iti";
        horairesCourse = selectionSpecifique.getHorairesCourse(coursesItineraire.get(0).getId());
        assert maxHoraire - 1 == horairesCourse.size() : "echec suppression lien arret iti";

        int maxTM = 4;
        List<Long> tmIds = new ArrayList<Long>(maxTM);
        for (int i = 0; i < maxTM; i++) {
            TableauMarche tm = GenerateurDonnee.creerTM();

            tableauMarcheManager.creer(tm);

            tmIds.add(tm.getId());
        }

        if (maxCourses > 1 && maxTM > 1) {
            Course courseA = coursesItineraire.get(0);
            Course courseB = coursesItineraire.get(1);

            // premiere association

            tableauMarcheManager.associerCourseTableauxMarche(courseA.getId(), tmIds);
            tableauMarcheManager.associerCourseTableauxMarche(courseB.getId(), tmIds);

            List<TableauMarche> tmCourseA = courseManager.getTableauxMarcheCourse(courseA.getId());
            List<TableauMarche> tmCourseB = courseManager.getTableauxMarcheCourse(courseB.getId());

            assert tmIds.size() == tmCourseA.size() : "echec de l'association des tm a une course";
            assert tmIds.size() == tmCourseB.size() : "echec de l'association des tm a une course";
            for (TableauMarche marche : tmCourseB) {
                assert tmIds.contains(marche.getId()) : "echec de l'association des tm a une course";
            }
            for (TableauMarche marche : tmCourseA) {
                assert tmIds.contains(marche.getId()) : "echec de l'association des tm a une course";
            }

            Long tmId = tmIds.get(0);
            List<Course> courseTM1 = tableauMarcheManager.getCoursesTableauMarche(tmId);

            assert 2 == courseTM1.size() : "echec de l'association des tm a une course";
            for (Course course : courseTM1) {
                assert course.getId().longValue() == courseA.getId().longValue()
                        || course.getId().longValue() == courseB.getId().longValue() : "echec de l'association des tm a une course";
            }

            // mise a jour
            List<Long> tmReduitIds = new ArrayList<Long>();
            tmReduitIds.add(tmId);

            tableauMarcheManager.associerCourseTableauxMarche(courseA.getId(), tmReduitIds);

            tmCourseA = courseManager.getTableauxMarcheCourse(courseA.getId());
            tmCourseB = courseManager.getTableauxMarcheCourse(courseB.getId());

            assert 1 == tmCourseA.size() : "echec maj de l'association des tm a une course";
            assert tmIds.size() == tmCourseB.size() : "echec maj de l'association des tm a une course";

            assert tmCourseA.get(0).getId().equals(tmReduitIds.get(0)) : "echec maj de l'association des tm a une course";
            for (TableauMarche marche : tmCourseB) {
                assert tmIds.contains(marche.getId()) : "echec maj de l'association des tm a une course";
            }

            courseTM1 = tableauMarcheManager.getCoursesTableauMarche(tmId);
            assert 2 == courseTM1.size() : "echec maj de l'association des tm a une course";

            Long autreTM = tmIds.get(1);
            courseTM1 = tableauMarcheManager.getCoursesTableauMarche(autreTM);
            assert 1 == courseTM1.size() : "echec maj de l'association des tm a une course";
            assert courseTM1.get(0).getId().equals(courseB.getId()) : "echec maj de l'association des tm a une course";

            // suppression de course

            courseManager.supprimer(courseA.getId());

            List<TableauMarche> tmCourse = courseManager.getTableauxMarcheCourse(courseA.getId());
            assert 0 == tmCourse.size() : "echec maj des associations tm course apres suppression de course";

            tmCourseB = courseManager.getTableauxMarcheCourse(courseB.getId());
            assert tmIds.size() == tmCourseB.size() : "echec maj des associations tm course apres suppression de course";

            // suppression de TM
            tableauMarcheManager.supprimer(autreTM);

            courseTM1 = tableauMarcheManager.getCoursesTableauMarche(autreTM);
            assert 0 == courseTM1.size() : "echec maj de l'association des tm a une course";

            tmCourseB = courseManager.getTableauxMarcheCourse(courseB.getId());
            assert tmIds.size() - 1 == tmCourseB.size() : "echec maj des associations tm course apres suppression de course";
        }

        // suppression d'itineraire

        itineraireManager.supprimer(itineraireLu.getId());

        for (int i = 0; i < maxTM; i++) {
            List<Course> lesCourses = tableauMarcheManager.getCoursesTableauMarche(tmIds.get(i));
            assert 0 == lesCourses.size() : "echec maj des associations tm course apres suppression d'itineraire";
        }

        ligneManager.supprimer(uneLigne.getId());
    }
}
