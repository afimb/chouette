package fr.certu.chouette.service.importateur.multilignes.hastus.impl;

import chouette.schema.types.TransportModeNameType;
import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.modele.Reseau;
import fr.certu.chouette.modele.Transporteur;
import fr.certu.chouette.service.importateur.multilignes.hastus.ILecteurLigne;
import fr.certu.chouette.service.importateur.multilignes.hastus.commun.CodeIncident;
import fr.certu.chouette.service.importateur.multilignes.hastus.commun.ServiceException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

public class LecteurLigne extends Lecteur implements ILecteurLigne {
    
    private static final Logger                            logger                 = Logger.getLogger(LecteurLigne.class);
    private              String                            cleBus;                // "BUS"
    private              Reseau                            leReseau;
    private              Transporteur                      leTransporteur;
    private              Map<String, Ligne>                lignesParRegistration; /// Ligne par registration

    @Override
    public void reinit() {
        lignesParRegistration = new HashMap<String, Ligne>();
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
	    throw new ServiceException(CodeIncident.INVALIDE_LONGUEUR_LIGNE, "FATAL01003 : Mauvais nombre de champs dans la section '03'.");
	if ((ligneCSV[1] == null) || (ligneCSV[1].trim().length() <= 0))
	    throw new ServiceException(CodeIncident.NULL_RESEAU_LIGNE, "ERROR03001 : Le second champs de la section '03' doit etre non null.");
	if ((ligneCSV[2] == null) || (ligneCSV[2].trim().length() <= 0))
	    throw new ServiceException(CodeIncident.NULL_TRANSPORTEUR_LIGNE, "ERROR03002 : Le troisieme champs de la section '03' doit etre non null.");
	if ((ligneCSV[3] == null) || (ligneCSV[3].trim().length() <= 0))
	    throw new ServiceException(CodeIncident.NULL_NAME_LINE, "ERROR03003 : Le quatrieme champs de la section '03' doit etre non null.");
	if ((ligneCSV[5] == null) || (ligneCSV[5].trim().length() <= 0))
	    throw new ServiceException(CodeIncident.NULL_NAME_LINE, "ERROR03004 : Le sixieme champs de la section '03' doit etre non null.");

        if (leReseau == null) {
	    logger.debug("CREATION DU RESEAU "+ligneCSV[1].trim());
	    leReseau = new Reseau();
	    leReseau.setName(ligneCSV[1].trim());
	    leReseau.setObjectId(getIdentificationManager().getIdFonctionnel(getHastusCode(), "PtNetwork", toTrident(ligneCSV[1].trim())));
	    leReseau.setObjectVersion(1);
	    leReseau.setCreationTime(new Date(System.currentTimeMillis()));
	    leReseau.setRegistrationNumber(leReseau.getName());
	    leReseau.setVersionDate(Calendar.getInstance().getTime());
	}
        else
            if (!ligneCSV[1].trim().equals(leReseau.getName()))
                throw new ServiceException(CodeIncident.DUPLICATE_NAME_RESEAU, "FATAL01013 : Tous les seconds champs de la section '03' doivent etre egaux.");
	if (leTransporteur == null) {
	    logger.debug("CREATION DU TRANSPORTEUR "+ligneCSV[2].trim());
	    leTransporteur = new Transporteur();
	    leTransporteur.setName(ligneCSV[2].trim());
	    leTransporteur.setObjectId(getIdentificationManager().getIdFonctionnel(getHastusCode(), "Company", toTrident(ligneCSV[2].trim())));
	    leTransporteur.setObjectVersion(1);
	    leTransporteur.setCreationTime(new Date(System.currentTimeMillis()));
	    leTransporteur.setRegistrationNumber(leTransporteur.getName());
	}
        else
            if (!ligneCSV[2].trim().equals(leTransporteur.getName()))
                throw new ServiceException(CodeIncident.DUPLICATE_NAME_TRANSPORTEUR, "FATAL01023 : Tous les troisiemes champs de la section '03' doivent etre egaux.");

	logger.debug("CREATION DE LIGNE "+ligneCSV[3].trim());
        Ligne ligne = new Ligne();
        ligne.setName(ligneCSV[3].trim());
	ligne.setObjectVersion(1);
	ligne.setCreationTime(new Date(System.currentTimeMillis()));
	if ((ligneCSV[4] != null) && (ligneCSV[4].trim().length() > 0))
            ligne.setPublishedName(ligneCSV[4].trim());
        else
            ligne.setPublishedName(ligne.getName());
	ligne.setObjectId(getIdentificationManager().getIdFonctionnel(getHastusCode(), "Line", toTrident(ligneCSV[5].trim())));
	ligne.setRegistrationNumber(ligneCSV[5].trim());
        if ((ligneCSV[7] != null) && (ligneCSV[7].trim().length() > 0))
            ligne.setNumber(ligneCSV[7].trim());
        else
            ligne.setNumber(ligneCSV[5].trim());
        boolean invalideTransportMode = false;
        ligne.setTransportModeName(TransportModeNameType.BUS);
        if ((ligneCSV[6] == null) || (ligneCSV[6].trim().length() <= 0))
            invalideTransportMode = true;
        else if(ligneCSV[6].trim().equals("Autocar"))
            ligne.setTransportModeName(TransportModeNameType.COACH);
        else if(ligneCSV[6].trim().equals("Avion"))
            ligne.setTransportModeName(TransportModeNameType.AIR);
        else if(ligneCSV[6].trim().equals("Bac"))
            ligne.setTransportModeName(TransportModeNameType.WATERBORNE);
        else if(ligneCSV[6].trim().equals("Bus"))
            ligne.setTransportModeName(TransportModeNameType.BUS);
        else if(ligneCSV[6].trim().equals("Ferry"))
            ligne.setTransportModeName(TransportModeNameType.FERRY);
        else if(ligneCSV[6].trim().equals("Marche à pied"))
            ligne.setTransportModeName(TransportModeNameType.WALK);
        else if(ligneCSV[6].trim().equals("Métro"))
            ligne.setTransportModeName(TransportModeNameType.METRO);
        else if(ligneCSV[6].trim().equals("Navette"))
            ligne.setTransportModeName(TransportModeNameType.SHUTTLE);
        else if(ligneCSV[6].trim().equals("RER"))
            ligne.setTransportModeName(TransportModeNameType.LOCALTRAIN);
        else if(ligneCSV[6].trim().equals("TAXI"))
            ligne.setTransportModeName(TransportModeNameType.TAXI);
        else if(ligneCSV[6].trim().equals("TER"))
            ligne.setTransportModeName(TransportModeNameType.LOCALTRAIN);
        else if(ligneCSV[6].trim().equals("Train"))
            ligne.setTransportModeName(TransportModeNameType.TRAIN);
        else if(ligneCSV[6].trim().equals("Train grande ligne"))
            ligne.setTransportModeName(TransportModeNameType.LONGDISTANCETRAIN);
        else if(ligneCSV[6].trim().equals("Tramway"))
            ligne.setTransportModeName(TransportModeNameType.TRAMWAY);
        else if(ligneCSV[6].trim().equals("Trolleybus"))
            ligne.setTransportModeName(TransportModeNameType.TROLLEYBUS);
        else if(ligneCSV[6].trim().equals("Voiture particulière"))
            ligne.setTransportModeName(TransportModeNameType.PRIVATEVEHICLE);
        else if(ligneCSV[6].trim().equals("Autre"))
            ligne.setTransportModeName(TransportModeNameType.OTHER);
        else
            invalideTransportMode = true;
        addLigne(ligne);
        if (invalideTransportMode)
            throw new ServiceException(CodeIncident.INVALIDE_TRANSPORTMODE_LIGNE, "ERROR03101 : Le septieme champs de la section '03' doit etre parmi \"Autocar\", \"Avion\", \"Bac\", \"Bus\", \"Ferry\", \"Marche à pied\", \"Métro\", \"Navette\", \"RER\", \"Taxi\", \"TER\", \"Train\", \"Train grande ligne\", \"Tramway\", \"Trolleybus\", \"Voiture particulière\", \"Vélo\", \"Autre\".");
    }

    private void addLigne(Ligne ligne) {
        Ligne tmpLigne = lignesParRegistration.get(ligne.getRegistrationNumber());
        if (tmpLigne != null)
            if (theSameLigne(ligne, tmpLigne))
                return;
            else
                throw new ServiceException(CodeIncident.DUPLICATE_REGISTRATION_LINE, "ERROR03102 : Une autre ligne de la section '03' a le même deuxième champs que celle-ci..");
	lignesParRegistration.put(ligne.getRegistrationNumber(), ligne);
    }

    private boolean theSameLigne(Ligne ligne1, Ligne ligne2) {
        if ((ligne1 == null) || (ligne2 == null))
            if ((ligne1 == null) && (ligne2 == null))
                return true;
            else
                return false;
        if (((ligne1.getRegistrationNumber() == null) || (ligne2.getRegistrationNumber() == null)) &&
            ((ligne1.getRegistrationNumber() != null) || (ligne2.getRegistrationNumber() != null)))
            return false;
        if (ligne1.getRegistrationNumber() != null)
            if (!ligne1.getRegistrationNumber().equals(ligne2.getRegistrationNumber()))
                return false;
        if (((ligne1.getName() == null) || (ligne2.getName() == null)) &&
            ((ligne1.getName() != null) || (ligne2.getName() != null)))
            return false;
        if (ligne1.getName() != null)
            if (!ligne1.getName().equals(ligne2.getName()))
                return false;
        if (((ligne1.getNumber() == null) || (ligne2.getNumber() == null)) &&
            ((ligne1.getNumber() != null) || (ligne2.getNumber() != null)))
            return false;
        if (ligne1.getNumber() != null)
            if (!ligne1.getNumber().equals(ligne2.getNumber()))
                return false;
        if (((ligne1.getPublishedName() == null) || (ligne2.getPublishedName() == null)) &&
            ((ligne1.getPublishedName() != null) || (ligne2.getPublishedName() != null)))
            return false;
        if (ligne1.getPublishedName() != null)
            if (!ligne1.getPublishedName().equals(ligne2.getPublishedName()))
                return false;
        if (((ligne1.getTransportModeName() == null) || (ligne2.getTransportModeName() == null)) &&
            ((ligne1.getTransportModeName() != null) || (ligne2.getTransportModeName() != null)))
            return false;
        if (ligne1.getTransportModeName() != null)
            if (ligne1.getTransportModeName().compareTo(ligne2.getTransportModeName()) != 0)
                return false;
        
        return true;
    }
    
    public String getCleBus() {
	return cleBus;
    }
    
    public void setCleBus(String cleBus) {
	this.cleBus = cleBus;
    }
    
    @Override
    public Transporteur getTransporteur() {
	return leTransporteur;
    }
    
    @Override
    public Reseau getReseau() {
	return leReseau;
    }
    
    @Override
    public Map<String, Ligne> getLigneParRegistration() {
	return lignesParRegistration;
    }

    @Override
    public void completion() {
    }
}
