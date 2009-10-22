package fr.certu.chouette.service.importateur.multilignes.hastus.impl;

import chouette.schema.types.ChouetteAreaType;
import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.service.identification.IIdentificationManager;
import fr.certu.chouette.service.importateur.multilignes.hastus.ILecteurZone;
import fr.certu.chouette.service.importateur.multilignes.hastus.commun.CodeIncident;
import fr.certu.chouette.service.importateur.multilignes.hastus.commun.ServiceException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

public class LecteurZone implements ILecteurZone {
	
	private static final Logger                            logger                 = Logger.getLogger(LecteurZone.class);
	private              int                               counter;
	private              IIdentificationManager            identificationManager; // 
	private              String                            cleCode;               // "01"
	private              String                            cleAreaType;           // "CommercialStopPoint"
	private              String                            hastusCode;            // "HastusTUR"
	private              String                            special;               // "SPECIAL"
	private              String                            space;                 // "SPACE"
	private              Map<String, PositionGeographique> zones;                 /// PositionGeographique (non arrêt physique) par registrationNumber
	private              Map<String, PositionGeographique> zonesParObjectId;      /// PositionGeographique (non arrêt physique) par objectId
	
	public boolean isTitreReconnu(String[] ligneCSV) {
		if ((ligneCSV == null) || (ligneCSV.length == 0))
			return false;
		return ligneCSV[0].equals(getCleCode());
	}
	
	public void lire(String[] ligneCSV) {
		if ((ligneCSV == null) || (ligneCSV.length == 0))
			return;
		if (ligneCSV.length != 4)
			throw new ServiceException(CodeIncident.INVALIDE_LONGUEUR_ZONE, "La longeur des lignes dans \"Zone\" est 4 : "+ligneCSV.length);
		logger.debug("CREATION DE ZONE : "+ligneCSV[1]);
		if ((ligneCSV[1] == null) || (ligneCSV[1].trim().length() == 0))
			throw new ServiceException(CodeIncident.INVALIDE_NAME_ZONE, "Le nom d'une \"Zone\" doit être non null.");
		PositionGeographique zone = new PositionGeographique();
		zone.setObjectId(identificationManager.getIdFonctionnel(hastusCode, "StopArea", "COM"+toTrident(ligneCSV[1].trim())));//String.valueOf(counter++)));
		zone.setObjectVersion(1);
		zone.setCreationTime(new Date(System.currentTimeMillis()));
		zone.setRegistrationNumber(ligneCSV[1].trim());
		if (ligneCSV[2] != null)
			zone.setName(ligneCSV[2].trim());
		if ((ligneCSV[3] == null) || (!ligneCSV[3].trim().equals(getCleAreaType())))
			throw new ServiceException(CodeIncident.INVALIDE_TYPE_ZONE, "Le type de \"Zone\" est \""+getCleAreaType()+"\" : "+ligneCSV[3]);
		if ((ligneCSV[3] != null) && (ligneCSV[3].trim().equals(getCleAreaType())))
			zone.setAreaType(ChouetteAreaType.COMMERCIALSTOPPOINT);
		addZone(zone);
	}
	
	private String toTrident(String str) {
		if ((str == null) || (str.length() == 0))
			return "";
		String result = "";
		for (int i = 0; i < str.length(); i++)
			if (('a' <= str.charAt(i)) && (str.charAt(i) <= 'z') ||
				('A' <= str.charAt(i)) && (str.charAt(i) <= 'Z') ||
				('0' <= str.charAt(i)) && (str.charAt(i) <= '9'))
				result += str.charAt(i);
			else if ((str.charAt(i) == ' ') || (str.charAt(i) == '\t'))
				result += space;
			else
				result += special;
		return result;
	}

	public void reinit() {
		counter = 0;
		zones = new HashMap<String, PositionGeographique>();
		zonesParObjectId = new HashMap<String, PositionGeographique>();
	}
	
	public IIdentificationManager getIdentificationManager() {
		return identificationManager;
	}
	
	public void setIdentificationManager(IIdentificationManager identificationManager) {
		this.identificationManager = identificationManager;
	}
	
	public String getCleCode() {
		return cleCode;
	}
	
	public void setCleCode(String cleCode) {
		this.cleCode = cleCode;
	}
	
	public String getCleAreaType() {
		return cleAreaType;
	}
	
	public void setCleAreaType(String cleAreaType) {
		this.cleAreaType = cleAreaType;
	}
	
	public Map<String, PositionGeographique> getZones() {
		return zones;
	}
	
	public Map<String, PositionGeographique> getZonesParObjectId() {
		return zonesParObjectId;
	}
	
	public void setZonesParObjectId(Map<String, PositionGeographique> zonesParObjectId) {
		this.zonesParObjectId = zonesParObjectId;
	}
	
	public void setZones(Map<String, PositionGeographique> zones) {
		this.zones = zones;
	}
	
	public void addZone(PositionGeographique zone) {
		if (zones == null)
			zones = new HashMap<String, PositionGeographique>();
		if (zones.get(zone.getName()) != null)
			throw new ServiceException(CodeIncident.DUPLICATE_NAME_ZONE, "Il ne peut y avoir deux \"Zones\" avec le même nom : "+zone.getName()); 
		zones.put(zone.getRegistrationNumber(), zone);
		zonesParObjectId.put(zone.getObjectId(), zone);
	}
	
	public PositionGeographique getZone(String name) {
		if (zones == null)
			return null;
		return zones.get(name);
	}
	
	public int getCounter() {
		return counter;
	}
	
	public void setCounter(int counter) {
		this.counter = counter;
	}
	
	public String getHastusCode() {
		return hastusCode;
	}
	
	public void setHastusCode(String hastusCode) {
		this.hastusCode = hastusCode;
	}
	
	public String getSpecial() {
		return special;
	}
	
	public void setSpecial(String special) {
		this.special = special;
	}
	
	public String getSpace() {
		return space;
	}
	
	public void setSpace(String space) {
		this.space = space;
	}
}
