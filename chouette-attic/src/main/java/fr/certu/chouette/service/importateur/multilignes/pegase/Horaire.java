package fr.certu.chouette.service.importateur.multilignes.pegase;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import fr.certu.chouette.modele.ArretItineraire;
import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.service.identification.IIdentificationManager;

public class Horaire {

    private Course course;
    private Arret arret;
    private int ordre;
    private Date heureDePassage;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("HH'H'mm");
    public static int NombreTotal = 0;
    public int increment;
    private IIdentificationManager identificationManager;
    private PositionGeographique arretPhysique;
    private ArretItineraire arretItineraire;
    private fr.certu.chouette.modele.Horaire horaire;
    private static final Logger logger = Logger.getLogger(fr.certu.chouette.service.importateur.multilignes.pegase.Horaire.class);

    public Horaire(IIdentificationManager identificationManager, Course course, Arret arret, String ordre, String heureDePassage) throws HoraireException {
        increment = 1;
        NombreTotal++;
        this.identificationManager = identificationManager;
        this.course = course;
        this.arret = arret;
        try {
            this.ordre = new Integer(ordre).intValue();
        } catch (NumberFormatException e) {
            throw new HoraireException("ERREUR POUR ORDRE : " + ordre);
        }
        try {
            this.heureDePassage = sdf.parse(heureDePassage);
        } catch (Exception e) {
            throw new HoraireException("ERREUR POUR HEURE_DE_PASSAGE : " + heureDePassage);
        }
    }

    public void increment() {
        increment++;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Course getCourse() {
        return course;
    }

    public void setArret(Arret arret) {
        this.arret = arret;
    }

    public Arret getArret() {
        return arret;
    }

    public void setOrdre(int ordre) {
        this.ordre = ordre;
    }

    public int getOrdre() {
        return ordre;
    }

    public void setHeureDePassage(Date heureDePassage) {
        this.heureDePassage = heureDePassage;
    }

    public Date getHeureDePassage() {
        return heureDePassage;
    }

    public PositionGeographique getZoneCommerciale(Connection connexion) {
        return arret.getZoneCommerciale(connexion);
    }

    public String getHeure() {
        return sdf.format(heureDePassage);
    }

    public PositionGeographique getArretPhysique() {
        if (arretPhysique == null) {
            arretPhysique = arret.getArretPhysique(course.getItineraire().getLigne(), course.getSens());
        }
        return arretPhysique;
    }

    public ArretItineraire getArretItineraire() {
        if (arretItineraire == null) {
            arretItineraire();
        }
        return arretItineraire;
    }

    private void arretItineraire() {
        arretItineraire = new ArretItineraire();
        arretItineraire.setContainedIn(arretPhysique.getObjectId());
        arretItineraire.setCreationTime(new Date());
        if (arret.getArretsItineraires(arretPhysique) == null) {
            arretItineraire.setName(arret.getName() + "_0");
        } else {
            arretItineraire.setName(arret.getName() + "_" + arret.getArretsItineraires(arretPhysique).size());
        }
        arretItineraire.setObjectId(identificationManager.getIdFonctionnel("StopPoint", String.valueOf(LecteurPrincipal.counter++)));
        arretItineraire.setObjectVersion(1);
        arretItineraire.setPosition(ordre - 1);
        arret.addArretItineraire(arretPhysique, arretItineraire);
    }

    public void setArretItineraire(ArretItineraire arretItineraire) {
        this.arretItineraire = arretItineraire;
    }

    public fr.certu.chouette.modele.Horaire getHoraire() {
        if (horaire == null) {
            horaire = new fr.certu.chouette.modele.Horaire();
            horaire.setArrivalTime(heureDePassage);
            if (ordre == 1) {
                horaire.setDepart(true);
            } else {
                horaire.setDepart(false);
            }
            horaire.setDepartureTime(heureDePassage);
            horaire.setStopPointId(getArretItineraire().getObjectId());
            horaire.setVehicleJourneyId(course.getVehicleJourney().getObjectId());
        }
        return horaire;
    }
}
