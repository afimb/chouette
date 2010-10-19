package fr.certu.chouette.service.importateur.multilignes.hastus.impl;

import chouette.schema.types.ChouetteAreaType;
import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.service.importateur.multilignes.hastus.ILecteurZone;
import fr.certu.chouette.service.importateur.multilignes.hastus.commun.CodeIncident;
import fr.certu.chouette.service.importateur.multilignes.hastus.commun.ServiceException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

public class LecteurZone extends Lecteur implements ILecteurZone {

    private static final Logger                            logger                 = Logger.getLogger(LecteurZone.class);
    private              int                               lineLength;
    private              String                            cleAreaType;           // "CommercialStopPoint"
    private              Map<String, PositionGeographique> zones;                 /// PositionGeographique (non arrêt physique) par registrationNumber
    private              Map<String, PositionGeographique> zonesParObjectId;      /// PositionGeographique (non arrêt physique) par objectId

    @Override
    public void reinit() {
        super.reinit();
        lineLength       = 0;
        zones            = new HashMap<String, PositionGeographique>();
        zonesParObjectId = new HashMap<String, PositionGeographique>();
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
        if ((lineLength == 0) && ((ligneCSV.length == 4) || (ligneCSV.length == 5)))
            lineLength = ligneCSV.length;
        if (ligneCSV.length != lineLength)
            throw new ServiceException(CodeIncident.INVALIDE_LONGUEUR_ZONE, "FATAL01001 : Mauvais nombre de champs dans la section '01'.");
        if ((ligneCSV[1] == null) || (ligneCSV[1].trim().length() == 0))
            throw new ServiceException(CodeIncident.INVALIDE_REGISTRATION_ZONE, "ERROR01001 : Le second champs de la section '01' doit etre non null.");
        if ((ligneCSV[2] == null) || (ligneCSV[2].trim().length() == 0))
            throw new ServiceException(CodeIncident.INVALIDE_REGISTRATION_ZONE, "ERROR01002 : Le troisième champs de la section '01' doit etre non null.");
        logger.debug("CREATION DE ZONE : "+ligneCSV[1]);
        PositionGeographique zone = new PositionGeographique();
        zone.setRegistrationNumber(ligneCSV[1].trim());
        zone.setObjectId(getIdentificationManager().getIdFonctionnel(getHastusCode(), "StopArea", "COM"+toTrident(zone.getRegistrationNumber())));
        zone.setObjectVersion(1);
        zone.setCreationTime(new Date(System.currentTimeMillis()));
        zone.setName(ligneCSV[2].trim());
        int index = 3;
        boolean invalideType = false;
        if (lineLength == 5) {
            if ((ligneCSV[index] == null) || (!ligneCSV[index].trim().equals(getCleAreaType())))
                invalideType = true;
            index++;
        }
        zone.setAreaType(ChouetteAreaType.COMMERCIALSTOPPOINT);
        if ((ligneCSV[index] != null) && (ligneCSV[index].trim().length() > 0))
            zone.setCountryCode(ligneCSV[index].trim());
        addZone(zone);
        if (invalideType)
            throw new ServiceException(CodeIncident.INVALIDE_TYPE_ZONE, "WARN01001 : Le quatrieme champs de la section '01' doit etre '"+getCleAreaType()+"'.");
    }

    public String getCleAreaType() {
        return cleAreaType;
    }
    
    public void setCleAreaType(String cleAreaType) {
        this.cleAreaType = cleAreaType;
    }

    @Override
    public Map<String, PositionGeographique> getZones() {
        return zones;
    }
    
    @Override
    public Map<String, PositionGeographique> getZonesParObjectId() {
        return zonesParObjectId;
    }
    
    public void setZonesParObjectId(Map<String, PositionGeographique> zonesParObjectId) {
        this.zonesParObjectId = zonesParObjectId;
    }
    
    public void setZones(Map<String, PositionGeographique> zones) {
        this.zones = zones;
    }

    public PositionGeographique getZone(String registrationNumber) {
        if (zones == null)
            return null;
        return zones.get(registrationNumber);
    }

    public void addZone(PositionGeographique zone) {
        if (zones == null)
            zones = new HashMap<String, PositionGeographique>();
        PositionGeographique tmpZone = zones.get(zone.getRegistrationNumber());
        if (tmpZone != null)
            if (theSameZone(zone, tmpZone))
                return;
            else
                throw new ServiceException(CodeIncident.DUPLICATE_NAME_ZONE, "ERROR01101 : Une autre ligne de la section '01' a le même deuxième champs que celle-ci.");
        zones.put(zone.getRegistrationNumber(), zone);
        zonesParObjectId.put(zone.getObjectId(), zone);
    }

    private boolean theSameZone(PositionGeographique zone1, PositionGeographique zone2) {
        if ((zone1 == null) || (zone2 == null))
            if ((zone1 == null) && (zone2 == null))
                return true;
            else
                return false;
        if (((zone1.getRegistrationNumber() == null) || (zone2.getRegistrationNumber() == null)) &&
            ((zone1.getRegistrationNumber() != null) || (zone2.getRegistrationNumber() != null)))
            return false;
        if (zone1.getRegistrationNumber() != null)
            if (!zone1.getRegistrationNumber().equals(zone2.getRegistrationNumber()))
                return false;
        if (((zone1.getName() == null) || (zone2.getName() == null)) &&
            ((zone1.getName() != null) || (zone2.getName() != null)))
            return false;
        if (zone1.getName() != null)
            if (!zone1.getName().equals(zone2.getName()))
                return false;
        if (((zone1.getCountryCode() == null) || (zone2.getCountryCode() == null)) &&
            ((zone1.getCountryCode() != null) || (zone2.getCountryCode() != null)))
            return false;
        if (zone1.getCountryCode() != null)
            if (!zone1.getCountryCode().equals(zone2.getCountryCode()))
                return false;
        return true;
    }

    @Override
    public void completion() {
    }
}
