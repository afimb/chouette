package unit;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.exolab.castor.types.Time;

import chouette.schema.AreaCentroid;
import chouette.schema.ChouetteArea;
import chouette.schema.ChouetteLineDescription;
import chouette.schema.ChouettePTNetwork;
import chouette.schema.ChouettePTNetworkTypeType;
import chouette.schema.ITL;
import chouette.schema.ProjectedPoint;
import chouette.schema.PtLink;
import chouette.schema.StopArea;
import chouette.schema.StopAreaExtension;
import chouette.schema.StopPoint;
import chouette.schema.Timetable;
import chouette.schema.TridentObjectTypeType;
import chouette.schema.VehicleJourneyAtStop;
import chouette.schema.types.BoardingAlightingPossibilityType;
import chouette.schema.types.ChouetteAreaType;
import chouette.schema.types.ConnectionLinkTypeType;
import chouette.schema.types.PTDirectionType;
import chouette.schema.types.ServiceStatusValueType;
import chouette.schema.types.TransportModeNameType;
import fr.certu.chouette.modele.ArretItineraire;
import fr.certu.chouette.modele.Correspondance;
import fr.certu.chouette.modele.Course;
import fr.certu.chouette.modele.Horaire;
import fr.certu.chouette.modele.InterdictionTraficLocal;
import fr.certu.chouette.modele.Itineraire;
import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.modele.Periode;
import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.modele.Reseau;
import fr.certu.chouette.modele.TableauMarche;
import fr.certu.chouette.modele.Transporteur;

public class GenerateurDonnee {

    private static Random random = new Random();

    public static ChouettePTNetworkTypeType creerXMLaleatoire(
            String cle,
            int max_itineraires,
            int max_arrets,
            int max_courses,
            int max_coorespondances) {
        ChouettePTNetworkTypeType resultat = creerXMLaleatoire(cle, max_itineraires, max_arrets, max_courses);

        assert max_coorespondances < max_arrets : "le nb de corresp doit etre inférieur à celui des arrets";

        // ajout de correspondances internes
        for (int i = 0; i < max_coorespondances; i++) {
            Correspondance correspondance = creerCorrespondance();

            StopArea depart = resultat.getChouetteArea().getStopArea(i);
            StopArea arrivee = resultat.getChouetteArea().getStopArea((i + 1) % max_arrets);

            correspondance.setStartOfLink(depart.getObjectId());
            correspondance.setEndOfLink(arrivee.getObjectId());

            resultat.addConnectionLink(correspondance.getConnectionLink());
        }

        return resultat;
    }

    public static ChouettePTNetworkTypeType creerXMLaleatoire(
            String cle,
            int max_itineraires,
            int max_arrets,
            int max_courses) {
        ChouettePTNetworkTypeType resultat = new ChouettePTNetwork();

        Transporteur transporteur = creerTransporteur();
        resultat.addCompany(transporteur.getCompany());
        transporteur.setObjectId("TEST:Company:" + cle + "#" + 1);
        transporteur.setRegistrationNumber("001");

        Reseau reseau = creerResau();
        resultat.setPTNetwork(reseau.getPtNetwork());
        reseau.setObjectId("TEST:PTNetwork:" + cle + "#" + 1);
        reseau.setRegistrationNumber("002");

        Ligne ligne = creerLigne();
        ChouetteLineDescription description = new ChouetteLineDescription();
        resultat.setChouetteLineDescription(description);
        description.setLine(ligne.getLine());
        ligne.setObjectId("TEST:Line:" + cle + "#" + 1);
        ligne.setRegistrationNumber("003");

//		int max_itineraires = 4;
//		int max_arrets = 15;
        List<String> itineraireIds = new ArrayList<String>(max_itineraires);

        ChouetteArea chouetteArea = new ChouetteArea();
        resultat.setChouetteArea(chouetteArea);

        Map<String, List<String>> arretsParItineraire = new Hashtable<String, List<String>>();

        for (int i = 0; i < max_itineraires; i++) {
            List<String> arretIds = new ArrayList<String>(max_arrets);

            Itineraire itineraire = creerItineraire(0L);
            description.addChouetteRoute(itineraire.getChouetteRoute());
            itineraire.setObjectId("TEST:Route:" + cle + "#" + i);
            itineraireIds.add(itineraire.getObjectId());

            ArretItineraire precedent = null;
            for (int j = 0; j < max_arrets; j++) {
                ArretItineraire arret = creerArret(0L);
                arret.setObjectId("TEST:StopPoint:" + cle + "#" + i + "-" + j);

                // definition de l'arret physique
                StopArea stopArea = creerArretPhysiqueStopArea(cle, i, j);
                AreaCentroid centroid = creerArretPhysiqueAreaCentroid(i, j);

                chouetteArea.addAreaCentroid(centroid);
                chouetteArea.addStopArea(stopArea);

                // rattachement de l'arret logique a son arret physique
                arret.setContainedIn(stopArea.getObjectId());

                arretIds.add(arret.getObjectId());
                if (precedent != null) {
                    PtLink troncon = new PtLink();
                    troncon.setObjectId("TEST:PtLink:" + cle + "#" + i + "-" + j);
                    troncon.setStartOfLink(precedent.getObjectId());
                    troncon.setEndOfLink(arret.getObjectId());

                    description.addPtLink(troncon);
                    itineraire.getChouetteRoute().addPtLinkId(troncon.getObjectId());
                }
                description.addStopPoint(arret.getStopPoint());

                precedent = arret;
            }
            arretsParItineraire.put(itineraire.getObjectId(), arretIds);
        }

//		int max_courses = 8;
        List<String> courseObjectIds = new ArrayList<String>();
        for (int i = 0; i < max_itineraires; i++) {
            String idItineraire = itineraireIds.get(i);
            List<String> arretsParcourus = arretsParItineraire.get(idItineraire);

            for (int j = 0; j < max_courses; j++) {
                Course course = creerCourse(0L);
                course.setRouteId(idItineraire);
                course.setObjectId("TEST:Vehiclejourney:" + cle + "#" + i + "-" + j);
                course.setNumber(j);


                courseObjectIds.add(course.getObjectId());

                int totalArrets = arretsParcourus.size();
                Long heureDepart = 1000L * (4L * 3600L + j * 60L);
                Long duree = 3L * 1000L * 60L;
                VehicleJourneyAtStop[] lesHoraires = new VehicleJourneyAtStop[totalArrets];
                for (int k = 0; k < totalArrets; k++) {
                    Time heure = new Time(heureDepart + k * duree);
                    Horaire horaire = creerHoraire(0L, 0L, heure);

                    horaire.setVehicleJourneyId(course.getObjectId());
                    horaire.setStopPointId(arretsParcourus.get(k));

                    lesHoraires[ k] = horaire.getVehicleJourneyAtStop();
                }
                course.getVehicleJourney().setVehicleJourneyAtStop(lesHoraires);

                description.addVehicleJourney(course.getVehicleJourney());
            }
        }

        int max_tableauxMarche = 5;
        for (int i = 0; i < max_tableauxMarche; i++) {
            TableauMarche tableauMarche = creerTM();

            tableauMarche.setObjectId("TEST:Timetable:" + i);

            int totalCourses = courseObjectIds.size();
            for (int j = 0; j < totalCourses; j++) {
                String courseObjectId = courseObjectIds.get(j);
                if ((j % 3 == i % 3)) {
                    tableauMarche.addVehicleJourneyId(courseObjectId);
                }
            }
            resultat.addTimetable(tableauMarche.getTimetable());
        }

        return resultat;
    }

    public static ChouettePTNetworkTypeType creerChouettePTNetwork(int max_itineraires,
            int max_arrets,
            int max_courses) {
        return creerXMLaleatoire("", max_itineraires, max_arrets, max_courses);
    }

    public static Date creerDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2007 + random.nextInt(3));
        calendar.set(Calendar.MONTH, 1 + random.nextInt(10));
        calendar.set(Calendar.DAY_OF_MONTH, 1 + random.nextInt(26));

        return calendar.getTime();
    }

    public static Periode creerPeriode() {
        Periode periode = new Periode();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2007);
        calendar.set(Calendar.MONTH, 2 + random.nextInt(3));
        calendar.set(Calendar.DAY_OF_MONTH, 1 + random.nextInt(5));
        periode.setDebut(calendar.getTime());

        calendar.set(Calendar.MONTH, 8 + random.nextInt(3));
        calendar.set(Calendar.DAY_OF_MONTH, 15 + random.nextInt(5));
        periode.setFin(calendar.getTime());

        return periode;
    }

    public static TableauMarche creerTM() {
        TableauMarche tm = new TableauMarche();

        tm.setComment("tm" + random.nextFloat());
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2007);
        calendar.set(Calendar.MONTH, 2);
        calendar.set(Calendar.DAY_OF_MONTH, 24);
        tm.ajoutDate(calendar.getTime());

        calendar.set(Calendar.DAY_OF_MONTH, 25);
        tm.ajoutDate(calendar.getTime());

        int totalPeriodes = 3;
        for (int i = 0; i < totalPeriodes; i++) {
            Periode periode = new Periode();

            calendar.set(Calendar.YEAR, 2007);
            calendar.set(Calendar.MONTH, 2 + random.nextInt(3));
            calendar.set(Calendar.DAY_OF_MONTH, 1 + random.nextInt(5));
            periode.setDebut(calendar.getTime());

            calendar.set(Calendar.MONTH, 8 + random.nextInt(3));
            calendar.set(Calendar.DAY_OF_MONTH, 15 + random.nextInt(5));
            periode.setFin(calendar.getTime());
            tm.ajoutPeriode(periode);
        }

        return tm;
    }

    public static Timetable creerXmlTM() {
        Timetable timet = new Timetable();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2007);
        calendar.set(Calendar.MONTH, 2);
        calendar.set(Calendar.DAY_OF_MONTH, 24);
        org.exolab.castor.types.Date premDate = new org.exolab.castor.types.Date(calendar.getTime());
        calendar.set(Calendar.DAY_OF_MONTH, 25);
        org.exolab.castor.types.Date secDate = new org.exolab.castor.types.Date(calendar.getTime());

        org.exolab.castor.types.Date[] lesDates = new org.exolab.castor.types.Date[2];
        lesDates[ 0] = premDate;
        lesDates[ 1] = secDate;

        timet.setCalendarDay(lesDates);

        return timet;
    }

    public static Reseau creerResau() {
        Reseau reseau = new Reseau();

        reseau.setComment("commentaire " + random.nextFloat());
        reseau.setDescription("desc " + random.nextFloat());
        reseau.setName("nom " + random.nextFloat());

        reseau.setRegistrationNumber(String.valueOf(random.nextLong()));

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2007);
        calendar.set(Calendar.MONTH, 2);
        calendar.set(Calendar.DAY_OF_MONTH, 24);
        reseau.setVersionDate(calendar.getTime());

        majTridentObjet(reseau.getPtNetwork());

        return reseau;
    }

    public static PositionGeographique creerArretPhysique(String cle) {
        PositionGeographique arretPhysique = new PositionGeographique();
        int iUnique = random.nextInt();
        int jUnique = random.nextInt();
        arretPhysique.setAreaCentroid(creerArretPhysiqueAreaCentroid(iUnique, jUnique));
        arretPhysique.setStopArea(creerArretPhysiqueStopArea(cle, iUnique, jUnique));
        arretPhysique.setAreaType(ChouetteAreaType.QUAY);

        return arretPhysique;
    }

    public static Correspondance creerCorrespondance() {
        Correspondance correspondance = new Correspondance();
        correspondance.setComment("test " + random.nextInt(20));
        correspondance.setName("nom " + random.nextInt(20));

        correspondance.setDefaultDuration(creerDuree());
        correspondance.setMobilityRestrictedTravellerDuration(creerDuree());
        correspondance.setOccasionalTravellerDuration(creerDuree());
        correspondance.setFrequentTravellerDuration(creerDate());

        List<ConnectionLinkTypeType> lesTypes = new ArrayList<ConnectionLinkTypeType>();
        //EVOCASTOR
        ConnectionLinkTypeType[] connectionLinksTypes = ConnectionLinkTypeType.values();
        for (ConnectionLinkTypeType connectionLinkTypeType : connectionLinksTypes) {
            lesTypes.add(connectionLinkTypeType);
        }
        //--
        correspondance.setLinkType(lesTypes.get(random.nextInt(lesTypes.size())));
        correspondance.setLiftAvailability(random.nextBoolean());
        correspondance.setMobilityRestrictedSuitability(random.nextBoolean());
        correspondance.setStairsAvailability(random.nextBoolean());

        correspondance.setLinkDistance(new BigDecimal(random.nextInt(300)));

        majTridentObjet(correspondance.getConnectionLink());
        return correspondance;
    }

    private static Date creerDuree() {
        int milli = random.nextInt(99);
        int sec = random.nextInt(59);
        int min = random.nextInt(59);
        int heure = random.nextInt(23);

        return new Date(milli + 1000L * (sec + 60 * (min + 60 * (heure))));
    }

    public static PositionGeographique creerZone(String cle) {
        PositionGeographique zone = new PositionGeographique();
        int iUnique = random.nextInt();
        int jUnique = random.nextInt();
        zone.setAreaCentroid(creerArretPhysiqueAreaCentroid(iUnique, jUnique));
        zone.setStopArea(creerArretPhysiqueStopArea(cle, iUnique, jUnique));
        zone.setAreaType(ChouetteAreaType.STOPPLACE);

        return zone;
    }

    public static PositionGeographique creerZone(String cle, ChouetteAreaType areaType) {
        PositionGeographique zone = creerZone(cle);
        zone.setAreaType(areaType);
        return zone;
    }

    public static PositionGeographique creerZoneAleatoire(String cle) {
        PositionGeographique zone = new PositionGeographique();
        int iUnique = random.nextInt();
        int jUnique = random.nextInt();
        zone.setAreaCentroid(creerArretPhysiqueAreaCentroid(iUnique, jUnique));
        zone.setStopArea(creerArretPhysiqueStopArea(cle, iUnique, jUnique));
        zone.setAreaType(ChouetteAreaType.STOPPLACE);
        zone.getStopArea().setObjectId(zone.getStopArea().getObjectId() + "-" + random.nextInt(1000000));

        return zone;
    }

    public static StopArea creerArretPhysiqueStopArea(String cle, int i, int j) {
        StopArea area = new StopArea();
        majTridentObjet(area);
        String areaObjId = "TEST:Area:" + cle + "#" + i + "-" + j;
        area.setObjectId(areaObjId);
        area.setCentroidOfArea("TEST:Place:" + i + "-" + j);
        area.setName("Rue " + i + "-" + j);

        StopAreaExtension extension = new StopAreaExtension();
        ChouetteAreaType areaType = random.nextBoolean() ? ChouetteAreaType.BOARDINGPOSITION : ChouetteAreaType.QUAY;
        extension.setAreaType(areaType);
        area.setStopAreaExtension(extension);

        return area;
    }

    public static AreaCentroid creerArretPhysiqueAreaCentroid(int i, int j) {
        AreaCentroid place = new AreaCentroid();

        ProjectedPoint projectedPoint = new ProjectedPoint();
        projectedPoint.setX(new BigDecimal(random.nextInt()));
        projectedPoint.setY(new BigDecimal(random.nextInt()));

        place.setProjectedPoint(projectedPoint);

        majTridentObjet(place);
        String placeObjId = "TEST:Place:" + i + "-" + j;
        place.setObjectId(placeObjId);

        return place;
    }

    public static Ligne creerLigne() {
        Ligne ligne = new Ligne();

        ligne.setComment("commentaire " + random.nextFloat());
        ligne.setName("nom ligne " + random.nextInt());
        ligne.setNumber("no ligne " + random.nextInt());

        ligne.setRegistrationNumber(String.valueOf(random.nextLong()));

        majTridentObjet(ligne.getLine());
        return ligne;
    }

    public static ITL creerXMLITL() {
        ITL itl = new ITL();

        itl.setAreaId("id:" + random.nextLong());
        itl.setName("nom ligne " + random.nextInt());

        return itl;
    }

    public static StopArea creerXMLITLStopArea(ITL itl, List<String> physiqueIds) {
        StopArea area = new StopArea();
        StopAreaExtension extension = new StopAreaExtension();

        extension.setAreaType(ChouetteAreaType.ITL);
        area.setStopAreaExtension(extension);
        area.setObjectId(itl.getAreaId());
        area.setName(itl.getName());

        for (String physiqueId : physiqueIds) {
            area.addContains(physiqueId);
        }
        return area;
    }

    public static Transporteur creerTransporteur() {
        Transporteur transporteur = new Transporteur();

        transporteur.setName("nom trsp " + random.nextInt());
        transporteur.setPhone("" + random.nextInt());
        transporteur.setOperatingDepartmentName("Departement: " + random.nextInt());
        transporteur.setOrganisationalUnit("" + random.nextLong());

        transporteur.setRegistrationNumber(String.valueOf(random.nextLong()));

        majTridentObjet(transporteur.getCompany());

        return transporteur;
    }

    public static Itineraire creerItineraire(Long idLigne) {
        Itineraire itineraire = new Itineraire();
        int max = 5 + random.nextInt(3);

        itineraire.setComment("commentaire iti " + random.nextInt());
        itineraire.setName("nom iti " + random.nextInt());
        itineraire.setNumber("n iti " + random.nextInt());
        itineraire.setPublishedName("Itinraire " + random.nextInt());
        itineraire.setWayBack(random.nextBoolean() ? "A" : "R");

        List<PTDirectionType> lesDirections = new ArrayList<PTDirectionType>();
        //CASTOREVO
        PTDirectionType[] directionsTypes = PTDirectionType.values();
        for (PTDirectionType directionType : directionsTypes) {

            lesDirections.add(directionType);
        }
        //--
        PTDirectionType direction = lesDirections.get(random.nextInt(lesDirections.size()));
        itineraire.setDirection(direction);

        majTridentObjet(itineraire.getChouetteRoute());
        itineraire.setIdLigne(idLigne);

        return itineraire;
    }

    public static ArretItineraire creerArret(Long idItineraire) {
        ArretItineraire arret = new ArretItineraire();
        StopPoint stoppoint = new StopPoint();

//		ProjectedPoint projectedPoint = new ProjectedPoint();
//		projectedPoint.setX( new BigDecimal( random.nextInt()));
//		projectedPoint.setY( new BigDecimal( random.nextInt()));
//		stoppoint.setProjectedPoint(projectedPoint);

        majTridentObjet(stoppoint);
        arret.setStopPoint(stoppoint);
        arret.setIdItineraire(idItineraire);

        return arret;
    }

    public static Horaire creerHoraire(Long idCourse, Long idArret,
            Time heure) {
        Horaire horaire = new Horaire();
        horaire.setIdArret(idArret);
        horaire.setIdCourse(idCourse);
        horaire.setModifie(false);

        horaire.setArrivalTime(heure.toDate());
        horaire.setDepartureTime(heure.toDate());
        horaire.setWaitingTime(heure.toDate());

        List<BoardingAlightingPossibilityType> lesPossibilites = new ArrayList<BoardingAlightingPossibilityType>();
        //CASTOREVO
        BoardingAlightingPossibilityType[] BAPTypes = BoardingAlightingPossibilityType.values();
        for (BoardingAlightingPossibilityType boardingAlightingPossibilityType : BAPTypes) {
            lesPossibilites.add(boardingAlightingPossibilityType);
        }
        //--
        horaire.setBoardingAlightingPossibility(lesPossibilites.get(random.nextInt(lesPossibilites.size())));

        return horaire;
    }

    public static Course creerCourse(Long idItineraire) {
        Course course = new Course();
        course.setIdItineraire(idItineraire);

        course.setFacility("equipement " + random.nextInt());
        course.setNumber(random.nextInt(50));

        List<ServiceStatusValueType> lesServices = new ArrayList<ServiceStatusValueType>();
        //CASTOREVO
        ServiceStatusValueType[] servicesTypes = ServiceStatusValueType.values();
        for (ServiceStatusValueType serviceStatusValueType : servicesTypes) {
            lesServices.add(serviceStatusValueType);
        }
        //--
        course.setStatusValue(lesServices.get(random.nextInt(lesServices.size())));


        List<TransportModeNameType> lesModes = new ArrayList<TransportModeNameType>();
        //CASTOREVO
        TransportModeNameType[] modesTypes = TransportModeNameType.values();
        for (TransportModeNameType transportModeNameType : modesTypes) {
            lesModes.add(transportModeNameType);
        }
        //--
        course.setTransportMode(lesModes.get(random.nextInt(lesModes.size())));


        return course;
    }

    //EVOCASTOR 
    private static void majTridentObjet(chouette.schema.TridentObjectTypeType trident) {
        trident.setObjectId("id:" + random.nextLong());
        trident.setCreatorId("test");
        int objectVersion = random.nextInt();
        if (objectVersion < 1) {
            objectVersion = 1 - objectVersion;
        }
        trident.setObjectVersion(objectVersion);
        trident.setCreationTime(new Date());
    }
}
