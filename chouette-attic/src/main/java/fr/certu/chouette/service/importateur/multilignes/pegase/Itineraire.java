package fr.certu.chouette.service.importateur.multilignes.pegase;

import chouette.schema.types.PTDirectionType;
import fr.certu.chouette.modele.ArretItineraire;
import fr.certu.chouette.modele.Mission;
import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.service.identification.IIdentificationManager;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;

public class Itineraire {

    private Ligne ligne;
    private String code;
    private String shortName;
    private String name;
    private Map<String, Course> courses;
    private IIdentificationManager identificationManager;
    private fr.certu.chouette.modele.Itineraire chouetteItineraire;
    private List<ArretItineraire> arretsItineraires;
    private List<Mission> missions;
    private static final Logger logger = Logger.getLogger(Itineraire.class);

    public Itineraire(IIdentificationManager identificationManager, Ligne ligne, String code, String shortName, String name) {
        this.identificationManager = identificationManager;
        this.ligne = ligne;
        this.code = code;
        this.shortName = shortName;
        this.name = name;
        this.courses = new HashMap<String, Course>();
    }

    public void setLigne(Ligne ligne) {
        this.ligne = ligne;
    }

    public Ligne getLigne() {
        return ligne;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Map<String, Course> getCourses() {
        return courses;
    }

    public Course getCourse(String serviceCode, String serviceType, String sens) throws CourseException {
        Course co = courses.get(serviceCode);
        if (co != null) {
            if (!co.getServiceType().equals(serviceType)) {
                throw new CourseException("ERREUR POUR COURSE SERVICE_TYPE : " + serviceType);
            }
            try {
                if (co.getSens() != sens.toUpperCase().charAt(0)) {
                    logger.error("ERREUR POUR COURSE SENS : " + sens);
                    ;//throw new CourseException("ERREUR POUR COURSE SENS : " + sens);
                }
            } catch (Throwable e) {
                throw new CourseException("ERREUR POUR COURSE SENS : " + sens);
            }
            return co;
        }
        try {
            co = new Course(identificationManager, this, serviceCode, serviceType, sens);
        } catch (CourseException e) {
            throw e;
        }
        courses.put(serviceCode, co);
        return co;
    }

    public Set<fr.certu.chouette.modele.TableauMarche> getTableauxMarche() {
        Set<fr.certu.chouette.modele.TableauMarche> tableauxMarche = new HashSet<fr.certu.chouette.modele.TableauMarche>();
        for (Course course : courses.values()) {
            tableauxMarche.addAll(course.getTableauxMarche());
        }
        return tableauxMarche;
    }

    public Set<PositionGeographique> getZonesCommerciales(Connection connexion) {
        Set<PositionGeographique> zonesCommerciales = new HashSet<PositionGeographique>();
        for (Course course : courses.values()) {
            zonesCommerciales.addAll(course.getZonesCommerciales(connexion));
        }
        return zonesCommerciales;
    }

    public Set<PositionGeographique> getArretsPhysiques() {
        Set<PositionGeographique> arretsPhysiques = new HashSet<PositionGeographique>();
        for (Course course : courses.values()) {
            arretsPhysiques.addAll(course.getArretsPhysiques());
        }
        return arretsPhysiques;
    }

    public fr.certu.chouette.modele.Itineraire getChouetteItineraire() {
        if (chouetteItineraire == null) {
            chouetteItineraire();
        }
        return chouetteItineraire;
    }

    private void chouetteItineraire() {
        chouetteItineraire = new fr.certu.chouette.modele.Itineraire();
        chouetteItineraire.setComment("Itineraire " + name);
        chouetteItineraire.setCreationTime(new Date());
        chouetteItineraire.setDirection(PTDirectionType.A);
        for (Course co : courses.values()) {
            if (co.getSens() == 'R') {
                chouetteItineraire.setDirection(PTDirectionType.R);
            }
        }
        chouetteItineraire.setName(name);
        chouetteItineraire.setNumber(code);
        chouetteItineraire.setObjectId(identificationManager.getIdFonctionnel("ChouetteRoute", String.valueOf(LecteurPrincipal.counter++)));
        chouetteItineraire.setObjectVersion(1);
        chouetteItineraire.setPublishedName(shortName);
    }

    public List<Mission> getMissions() {
        if (missions == null) {
            missions();
        }
        return missions;
    }

    private void missions() {
        missions = new ArrayList<Mission>();
        Mission mission = new Mission();
        mission.setComment("Mission " + name);
        mission.setCreationTime(new Date());
        mission.setName(name);
        mission.setObjectId(identificationManager.getIdFonctionnel("JourneyPattern", String.valueOf(LecteurPrincipal.counter++)));
        mission.setObjectVersion(1);
        mission.setPublishedName(shortName);
        mission.setRegistrationNumber(code);
        mission.setRouteId(getChouetteItineraire().getObjectId());
        missions.add(mission);
    }

    public List<fr.certu.chouette.modele.Course> getVehicleJourneys() {
        List<fr.certu.chouette.modele.Course> vehicleJourneys = new ArrayList<fr.certu.chouette.modele.Course>();
        for (Course co : courses.values()) {
            vehicleJourneys.add(co.getVehicleJourney());
        }
        return vehicleJourneys;
    }

    public List<ArretItineraire> getArretsItineraires() {
        if (arretsItineraires == null) {
            arretsItineraires();
        }
        return arretsItineraires;
    }

    private void arretsItineraires() {
        arretsItineraires = new ArrayList<ArretItineraire>();
        boolean flag = true;
        Course first = null;
        for (Course co : courses.values()) {
            if (flag) {
                first = co;
                for (Horaire ho : co.getHoraires().values()) {
                    arretsItineraires.add(ho.getArretItineraire());
                }
                flag = false;
            } else {
                for (Horaire ho1 : co.getHoraires().values()) {
                    for (Horaire ho2 : first.getHoraires().values()) {
                        if (ho1.getOrdre() == ho2.getOrdre()) {
                            ho1.setArretItineraire(ho2.getArretItineraire());
                            break;
                        }
                    }
                }
            }
        }
    }

    public List<fr.certu.chouette.modele.Horaire> getHoraires() {
        List<fr.certu.chouette.modele.Horaire> horaires = new ArrayList<fr.certu.chouette.modele.Horaire>();
        for (Course co : courses.values()) {
            horaires.addAll(co.getChouetteHoraires());
        }
        return horaires;
    }

    public void removeCourses(List<Course> coursesADetruire) {
        for (Course co : coursesADetruire) {
            courses.remove("" + co.getServiceCode());
        }
    }
}
