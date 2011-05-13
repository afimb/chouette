package fr.certu.chouette.service.importateur.multilignes.pegase;

import chouette.schema.types.TransportModeNameType;
import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.service.identification.IIdentificationManager;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Course {

    private Itineraire itineraire;
    private int serviceCode;
    private String serviceType;
    private char sens;
    private Set<TableauMarche> tms;
    private Map<String, Horaire> horaires;
    private IIdentificationManager identificationManager;
    private fr.certu.chouette.modele.Course vehicleJourney;

    public Course(IIdentificationManager identificationManager, Itineraire itineraire, String serviceCode, String serviceType, String sens) throws CourseException {
        this.identificationManager = identificationManager;
        this.itineraire = itineraire;
        try {
            this.serviceCode = new Integer(serviceCode).intValue();
        } catch (Throwable e) {
            throw new CourseException("ERREUR POUR SERVICECODE : " + serviceCode);
        }
        this.serviceType = serviceType;
        try {
            this.sens = sens.toUpperCase().charAt(0);
        } catch (Throwable e) {
            throw new CourseException("ERREUR POUR SENS : " + sens);
        }
        if ((this.sens != 'A') && (this.sens != 'R')) {
            throw new CourseException("ERREUR POUR SENS : " + sens);
        }
        this.tms = new HashSet<TableauMarche>();
        this.horaires = new HashMap<String, Horaire>();
    }

    public void setItineraire(Itineraire itineraire) {
        this.itineraire = itineraire;
    }

    public Itineraire getItineraire() {
        return itineraire;
    }

    public void setServiceCode(int serviceCode) {
        this.serviceCode = serviceCode;
    }

    public int getServiceCode() {
        return serviceCode;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setSens(char sens) {
        this.sens = sens;
    }

    public char getSens() {
        return sens;
    }

    public Set<TableauMarche> getTMs() {
        return tms;
    }

    public void addTM(TableauMarche tm) {
        tms.add(tm);
    }

    public void addTMs(Set<TableauMarche> tms) {
        this.tms.addAll(tms);
    }

    public Map<String, Horaire> getHoraires() {
        return horaires;
    }

    public Horaire getHoraire(Arret arret, String ordre, String heureDePassage) throws HoraireException {
        String key = ordre + "_" + heureDePassage + "_" + arret.getCode();
        Horaire horaire = horaires.get(key);
        if (horaire != null) {
            horaire.increment();
            return horaire;
        }
        try {
            horaire = new Horaire(identificationManager, this, arret, ordre, heureDePassage);
        } catch (HoraireException e) {
            throw e;
        }
        horaires.put(key, horaire);
        return horaire;
    }

    public Set<fr.certu.chouette.modele.TableauMarche> getTableauxMarche() {
        Set<fr.certu.chouette.modele.TableauMarche> tableauxMarche = new HashSet<fr.certu.chouette.modele.TableauMarche>();
        for (TableauMarche tm : getTMs()) {
            tableauxMarche.add(tm.getTableauxMarche());
        }
        return tableauxMarche;
    }

    public Set<PositionGeographique> getZonesCommerciales(Connection connexion) {
        Set<PositionGeographique> zonesCommerciales = new HashSet<PositionGeographique>();
        for (Horaire horaire : horaires.values()) {
            zonesCommerciales.add(horaire.getZoneCommerciale(connexion));
        }
        return zonesCommerciales;
    }

    public Set<PositionGeographique> getArretsPhysiques() {
        Set<PositionGeographique> arretsPhysiques = new HashSet<PositionGeographique>();
        for (Horaire horaire : horaires.values()) {
            arretsPhysiques.add(horaire.getArretPhysique());
        }
        return arretsPhysiques;
    }

    public fr.certu.chouette.modele.Course getVehicleJourney() {
        if (vehicleJourney == null) {
            vehicleJourney();
        }
        return vehicleJourney;
    }

    private void vehicleJourney() {
        vehicleJourney = new fr.certu.chouette.modele.Course();
        vehicleJourney.setComment("COURSE " + serviceCode + " DE TYPE " + serviceType + " DANS LE SENS " + sens);
        vehicleJourney.setCreationTime(new Date());
        vehicleJourney.setJourneyPatternId(itineraire.getMissions().get(0).getObjectId());
        vehicleJourney.setNumber(serviceCode);
        vehicleJourney.setObjectId(identificationManager.getIdFonctionnel("VehicleJourney", String.valueOf(LecteurPrincipal.counter++)));
        vehicleJourney.setObjectVersion(1);
        vehicleJourney.setRouteId(itineraire.getChouetteItineraire().getObjectId());
        if (serviceType.equals("TAD")) {
            vehicleJourney.setTransportMode(TransportModeNameType.SHUTTLE);
        }
        vehicleJourney.setTransportMode(TransportModeNameType.BUS);
        for (TableauMarche tm : tms) {
            tm.getTableauxMarche().addVehicleJourneyId(vehicleJourney.getObjectId());
        }
    }

    public List<fr.certu.chouette.modele.Horaire> getChouetteHoraires() {
        List<fr.certu.chouette.modele.Horaire> chouetteHoraires = new ArrayList<fr.certu.chouette.modele.Horaire>();
        for (Horaire horaire : horaires.values()) {
            chouetteHoraires.add(horaire.getHoraire());
        }
        return chouetteHoraires;
    }
}
