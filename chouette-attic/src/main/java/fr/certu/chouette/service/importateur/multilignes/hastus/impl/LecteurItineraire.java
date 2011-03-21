package fr.certu.chouette.service.importateur.multilignes.hastus.impl;

import chouette.schema.types.PTDirectionType;
import fr.certu.chouette.modele.Itineraire;
import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.modele.Mission;
import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.service.importateur.multilignes.hastus.ILecteurItineraire;
import fr.certu.chouette.service.importateur.commun.CodeIncident;
import fr.certu.chouette.service.importateur.commun.ServiceException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

public class LecteurItineraire extends Lecteur implements ILecteurItineraire {
    
    private static final Logger                            logger                 = Logger.getLogger(LecteurItineraire.class);
    private              String                            cleAller;              // "Aller"
    private              String                            cleRetour;             // "Retour"
    private              Map<String, Ligne>                ligneParRegistration;  /// Ligne par registration (LecteurLigne)
    private              Map<String, Itineraire>           itineraireParNames;    /// Ensemble des name des Itineraire (<Ligne.registrationNumber>-<zone.registrationNumber>-<zone.registrationNumber>)
    private              Map<String, Itineraire>           itineraireParNumber;   /// Itineraire par number (<Ligne.registrationNumber>-<integer>)
    private              Map<Itineraire, Ligne>            ligneParItineraire;    /// Ligne par Itineraire
    private              Map<String, PositionGeographique> zones;                 /// PositionGeographique (non arrêt physique) par name | registrationNumber(LecteurZone)
    private              Map<String, Mission>              missionParNom;         ///

    @Override
    public void reinit() {
        ligneParItineraire  = new HashMap<Itineraire, Ligne>();
        itineraireParNumber = new HashMap<String, Itineraire>();
        itineraireParNames  = new HashMap<String, Itineraire>();
	missionParNom       = new HashMap<String, Mission>();
    }

    @Override
    public boolean isTitreReconnu(String[] ligneCSV) {
        if ((ligneCSV == null) || (ligneCSV.length == 0))
            return false;
        if (ligneCSV[0] == null)
            return false;
        return ligneCSV[0].trim().equals(getCleCode());
    }
    
    @Override
    public void lire(String[] ligneCSV) {
        if ((ligneCSV == null) || (ligneCSV.length == 0))
            return;
        if (ligneCSV.length != 8)
            throw new ServiceException(CodeIncident.INVALIDE_LONGUEUR_ITINERAIRE, "FATAL01004 : Mauvais nombre de champs dans la section '04'.");
	if ((ligneCSV[1] == null) || (ligneCSV[1].trim().length() <= 0))
	    throw new ServiceException(CodeIncident.NULL_NAME_ITINERAIRE, "ERROR04001 : Le second champs de la section '04' doit etre non null.");
	if ((ligneCSV[4] == null) || (ligneCSV[1].trim().length() <= 0))
	    throw new ServiceException(CodeIncident.NULL_NAME_ITINERAIRE, "ERROR04002 : Le cinquieme champs de la section '04' doit etre non null.");
	if ((ligneCSV[6] == null) || (ligneCSV[1].trim().length() <= 0))
	    throw new ServiceException(CodeIncident.NULL_NAME_ITINERAIRE, "ERROR04003 : Le septieme champs de la section '04' doit etre non null.");
	Ligne ligne = ligneParRegistration.get(ligneCSV[6].trim());
	if (ligne == null)
	    throw new ServiceException(CodeIncident.INVALIDE_LIGNE_ITINERAIRE, "ERROR04101 : Le septieme champs de la section '04' doit etre egal au sixieme (ou huitieme si non nul) champs d'une ligne de la section '03'.");

        logger.debug("CREATION D'ITINERAIRE "+ligneCSV[1].trim());
	Itineraire itineraire = new Itineraire();
	itineraire.setObjectVersion(1);
	itineraire.setCreationTime(new Date(System.currentTimeMillis()));
	itineraire.setName(ligneCSV[1].trim()+"_"+ligneCSV[4].trim());
	if ((ligneCSV[2] != null) && (ligneCSV[2].trim().length() > 0))
	    itineraire.setPublishedName(ligneCSV[2].trim());
        else
            itineraire.setPublishedName(itineraire.getName());
        if ((ligneCSV[3] != null) && (ligneCSV[3].trim().length() > 0))
	    if (ligneCSV[3].trim().equals(getCleAller()))
		itineraire.setDirection(PTDirectionType.A);
	    else if (ligneCSV[3].trim().equals(getCleRetour()))
		itineraire.setDirection(PTDirectionType.R);
	    else
                itineraire.setPublishedName(itineraire.getPublishedName()+" --> "+ligneCSV[3].trim());
        itineraire.setNumber(ligneCSV[4].trim());
        boolean invalideSens = false;
        if ((ligneCSV[5] != null) && (ligneCSV[5].trim().length() > 0))
	    if (ligneCSV[5].trim().equals(getCleAller()))
		itineraire.setDirection(PTDirectionType.A);
	    else if (ligneCSV[5].trim().equals(getCleRetour()))
		itineraire.setDirection(PTDirectionType.R);
	    else
                invalideSens = true;
        itineraire.setObjectId(getIdentificationManager().getIdFonctionnel(getHastusCode(), "ChouetteRoute", toTrident(itineraire.getName())));
        
        if ((ligneCSV[7] != null) && (ligneCSV[7].trim().length() > 0))
            itineraire.setComment(ligneCSV[7].trim());

        addItineraire(itineraire, ligne);
        
        if (invalideSens)
            throw new ServiceException(CodeIncident.INVALIDE_DIRECTION_ITINERAIRE, "ERROR04103 : Le sixième champ de la section 04 doit être vide ou avoir l'une des valeur  \"Aller\" ou \"Retour\".");
    }
    
    private void addItineraire(Itineraire itineraire, Ligne ligne) {
        Itineraire tmpItineraire = itineraireParNames.get(itineraire.getName());
        if (tmpItineraire != null)
            if (theSameItineraire(itineraire, tmpItineraire, ligne))
                return;
            else
                throw new ServiceException(CodeIncident.DUPLICATE_NAME_ITINERAIRE, "ERROR04102 : Deux lignes quelconques de la section '04' sont soit identiques soit avec deux deuxiemes champs distincts.");
        tmpItineraire = itineraireParNumber.get(itineraire.getNumber());
        if (tmpItineraire != null)
            if (theSameItineraire(itineraire, tmpItineraire, ligne))
                return;
            else
                throw new ServiceException(CodeIncident.DUPLICATE_NAME_ITINERAIRE, "ERROR04102 : Deux lignes quelconques de la section '04' sont soit identiques soit avec deux cinquiemes champs distincts.");
        itineraireParNames.put(itineraire.getName(), itineraire);
        itineraireParNumber.put(itineraire.getNumber(), itineraire);
        ligneParItineraire.put(itineraire, ligne);
        addMission(itineraire);
    }

    private void addMission(Itineraire itineraire) {
        Mission mission = missionParNom.get(itineraire.getNumber());
	if (mission == null) {
	    mission = new Mission();
	    mission.setObjectVersion(1);
	    mission.setCreationTime(new Date(System.currentTimeMillis()));
	    mission.setName(itineraire.getNumber());
	    mission.setRouteId(itineraire.getObjectId());
	    mission.setObjectId(getIdentificationManager().getIdFonctionnel(getHastusCode(), "JourneyPattern", toTrident(mission.getName())));
	    mission.setComment("Mission du parcours type "+mission.getName());
	    mission.setPublishedName("Mission_"+mission.getName());
	    missionParNom.put(mission.getName(), mission);
	}
    }

    private boolean theSameItineraire(Itineraire itineraire1, Itineraire itineraire2, Ligne ligne) {
        if ((itineraire1 == null) || (itineraire2 == null))
            if ((itineraire1 == null) && (itineraire2 == null))
                return true;
            else
                return false;

        if (((itineraire1.getName() == null) || (itineraire2.getName() == null)) &&
            ((itineraire1.getName() != null) || (itineraire2.getName() != null)))
            return false;
        if (itineraire1.getName() != null)
            if (!itineraire1.getName().equals(itineraire2.getName()))
                return false;
        if (((itineraire1.getNumber() == null) || (itineraire2.getNumber() == null)) &&
            ((itineraire1.getNumber() != null) || (itineraire2.getNumber() != null)))
            return false;
        if (itineraire1.getNumber() != null)
            if (!itineraire1.getNumber().equals(itineraire2.getNumber()))
                return false;
        if (((itineraire1.getPublishedName() == null) || (itineraire2.getPublishedName() == null)) &&
            ((itineraire1.getPublishedName() != null) || (itineraire2.getPublishedName() != null)))
            return false;
        if (itineraire1.getPublishedName() != null)
            if (!itineraire1.getPublishedName().equals(itineraire2.getPublishedName()))
                return false;
        if (((itineraire1.getDirection() == null) || (itineraire2.getDirection() == null)) &&
            ((itineraire1.getDirection() != null) || (itineraire2.getDirection() != null)))
            return false;
        if (itineraire1.getDirection() != null)
            if (itineraire1.getDirection().compareTo(itineraire2.getDirection()) != 0)
                return false;
        if (((itineraire1.getComment() == null) || (itineraire2.getComment() == null)) &&
            ((itineraire1.getComment() != null) || (itineraire2.getComment() != null)))
            return false;
        if (itineraire1.getComment() != null)
            if (!itineraire1.getComment().equals(itineraire2.getComment()))
                return false;
        if (((itineraire1.getWayBack() == null) || (itineraire2.getWayBack() == null)) &&
            ((itineraire1.getWayBack() != null) || (itineraire2.getWayBack() != null)))
            return false;
        if (itineraire1.getWayBack() != null)
            if (!itineraire1.getWayBack().equals(itineraire2.getWayBack()))
                return false;
        if (ligne != ligneParItineraire.get(itineraire2))
            return false;

        return true;
    }

    public String getCleAller() {
	return cleAller;
    }
    
    public void setCleAller(String cleAller) {
	this.cleAller = cleAller;
    }
    
    public String getCleRetour() {
	return cleRetour;
    }
    
    public void setCleRetour(String cleRetour) {
	this.cleRetour = cleRetour;
    }
    
    @Override
    public void setLigneParRegistration(Map<String, Ligne> ligneParRegistration) {
	this.ligneParRegistration = ligneParRegistration;
    }
    
    @Override
    public Map<Itineraire, Ligne> getLigneParItineraire() {
	return ligneParItineraire;
    }
    
    @Override
    public Map<String, Itineraire> getItineraireParNumber() {
	return itineraireParNumber;
    }

    @Override
    public Map<String, Mission> getMissionParNom() {
	return missionParNom;
    }
    
    @Override
    public void setZones(Map<String, PositionGeographique> zones) {
	this.zones = zones;
    }
    
    @Override
    public void completion() {
	this.zones.clear();
	this.itineraireParNames.clear();
    }
}
