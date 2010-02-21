package fr.certu.chouette.service.importateur.multilignes.hastus.impl;

import chouette.schema.types.ChouetteAreaType;
import chouette.schema.types.LongLatTypeType;
import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.service.identification.IIdentificationManager;
import fr.certu.chouette.service.importateur.multilignes.hastus.ILecteurArret;
import fr.certu.chouette.service.importateur.multilignes.hastus.commun.CodeIncident;
import fr.certu.chouette.service.importateur.multilignes.hastus.commun.ServiceException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;

public class LecteurArret implements ILecteurArret {
	
	private static final Logger                            logger                      = Logger.getLogger(LecteurArret.class);
	private              int                               counter;
	private              IIdentificationManager            identificationManager;      // 
	private              String                            cleCode;                    // "02"
	private              String                            cleAreaType;                // "BoardingPosition"
	private              String                            cleLambert1;                // "LAMBERT I"
	private              String                            cleLambert2;                // "LAMBERT II"
	private              String                            cleLambert3;                // "LAMBERT III"
	private              String                            cleLambert4;                // "LAMBERT IV"
	private              String                            cleWGS84;                   // "WGS84"
	private              String                            hastusCode;                 // "HastusTUR"
	private              String                            special;                    // "SPECIAL"
	private              String                            space;                      // "SPACE"
	private              Map<String, PositionGeographique> zones;                      /// PositionGeographique (non arrêt physique) par name (LecteurZone)
	private              Map<String, PositionGeographique> arretsPhysiques;            /// PositionGeographique (arrêt physique) par name
	private              Map<String, PositionGeographique> arretsPhysiquesParObjectId; /// PositionGeographique (arrêt physique) par objectId
	private              Map<String, String>               objectIdParParentObjectId;  /// objectId (pêre : non arrêt physique) par objectId (fils : arrêt physique)
	private              Set<String>                       arretsNames;
	
	public boolean isTitreReconnu(String[] ligneCSV) {
		if ((ligneCSV == null) || (ligneCSV.length == 0))
			return false;
		return ligneCSV[0].equals(getCleCode());
	}
	
	public void lire(String[] ligneCSV) {
		if ((ligneCSV == null) || (ligneCSV.length == 0))
			return;
		if ((ligneCSV.length != 9) && (ligneCSV.length != 8))
			throw new ServiceException(CodeIncident.INVALIDE_LONGUEUR_ARRET, "La longeur des lignes dans \"ArretPhysique\" est 8 ou 9 : "+ligneCSV.length);
		if ((ligneCSV[1] == null) || (ligneCSV[1].trim().length() == 0))
			throw new ServiceException(CodeIncident.INVALIDE_REGISTRATION_ZONE, "Le numéro d'enregistrement d'un \"Arret Physique\" doit être non null.");
		logger.debug("CREATION D'ARRET PHYSIQUE : "+ligneCSV[1]);
		PositionGeographique arretPhysique = new PositionGeographique();
		arretPhysique.setName(ligneCSV[1].trim());
		arretPhysique.setRegistrationNumber(ligneCSV[1].trim());
		arretPhysique.setObjectId(identificationManager.getIdFonctionnel(hastusCode, "StopArea", "PHY"+toTrident(ligneCSV[1].trim())));
		arretPhysique.setObjectVersion(1);
		arretPhysique.setCreationTime(new Date(System.currentTimeMillis()));
		if (ligneCSV[2] != null)
			arretPhysique.setComment(ligneCSV[2].trim());
		if ((ligneCSV[3] == null) || (!ligneCSV[3].trim().equals(getCleAreaType())))
			throw new ServiceException(CodeIncident.INVALIDE_TYPE_ZONE, "Le type de \"Zone\" est \""+getCleAreaType()+"\" : "+ligneCSV[3]);
		arretPhysique.setAreaType(ChouetteAreaType.BOARDINGPOSITION);
		addArretPhysique(arretPhysique);
		if (ligneCSV[4] != null) {
			if (!((ligneCSV[4].equals(getCleLambert1())) || (ligneCSV[4].equals(getCleLambert2())) || (ligneCSV[4].equals(getCleLambert3())) || (ligneCSV[4].equals(getCleLambert4())) || (ligneCSV[4].equals(getCleWGS84()))))
				throw new ServiceException(CodeIncident.INVALIDE_CORDONEES_TYPE, "Le type de coordonnêes \""+ligneCSV[4].trim()+"\" est invalide.");
			if (ligneCSV[4].equals(getCleLambert1()))
				arretPhysique.setProjectionType(cleLambert1);
			else if (ligneCSV[4].equals(getCleLambert2()))
				arretPhysique.setProjectionType(cleLambert2);
			else if (ligneCSV[4].equals(getCleLambert3()))
				arretPhysique.setProjectionType(cleLambert3);
			else if (ligneCSV[4].equals(getCleLambert4()))
				arretPhysique.setProjectionType(cleLambert4);
			else if (ligneCSV[4].equals(getCleWGS84()))
				arretPhysique.setLongLatType(LongLatTypeType.WGS84);
			if ((ligneCSV[5] == null) || (ligneCSV[5].trim().length() == 0) || (ligneCSV[6] == null) || (ligneCSV[6].trim().length() == 0))
				if (arretsNames.add("Les coordonnêes sont indispensables lorsque leur type est donnê."))
					;//throw new ServiceException(CodeIncident.INVALIDE_CORDONEES_TYPE, "Les coordonnêes sont indispensables lorsque leur type est donnê.");
			try {
				if ((ligneCSV[5] != null) && (ligneCSV[5].trim().length() > 0)) {
					String latNumber = ligneCSV[5].trim();
					latNumber = latNumber.replace(',', '.');
					if ((ligneCSV[4].equals(getCleLambert1())) || (ligneCSV[4].equals(getCleLambert2())) ||
							(ligneCSV[4].equals(getCleLambert3())) || (ligneCSV[4].equals(getCleLambert4())))
						arretPhysique.setX(new BigDecimal(latNumber));
					else if (ligneCSV[4].equals(getCleWGS84()))
						arretPhysique.setLongitude(new BigDecimal(latNumber));
				}
			}
			catch(NumberFormatException e) {
				throw new ServiceException(CodeIncident.INVALIDE_NUMBER_FORMAT, "L'expression \""+ligneCSV[5].trim()+"\" ne represente pas un \"X\" valide.");
			}
			try {
				if ((ligneCSV[6] != null) && (ligneCSV[6].trim().length() > 0)) {
					String longNumber = ligneCSV[6].trim();
					longNumber = longNumber.replace(',', '.');
					if ((ligneCSV[4].equals(getCleLambert1())) || (ligneCSV[4].equals(getCleLambert2())) ||
							(ligneCSV[4].equals(getCleLambert3())) || (ligneCSV[4].equals(getCleLambert4())))
						arretPhysique.setY(new BigDecimal(longNumber));
					else if (ligneCSV[4].equals(getCleWGS84()))
						arretPhysique.setLatitude(new BigDecimal(longNumber));
				}
			}
			catch(NumberFormatException e) {
				throw new ServiceException(CodeIncident.INVALIDE_NUMBER_FORMAT, "L'expression \""+ligneCSV[6].trim()+"\" ne represente pas un \"Y\" valide.");
			}
		}
		else
			if ((ligneCSV[5] != null) || (ligneCSV[6] != null))
				;//throw new ServiceException(CodeIncident.INVALIDE_CORDONEES_TYPE, "Il ne peut y avoir de coordonnêes sans type.");
		if ((ligneCSV[7] == null) || (ligneCSV[7].trim().length() == 0))
			;//throw new ServiceException(CodeIncident.INVALIDE_PARENT_ARRET, "L'arrêt \""+ligneCSV[2].trim()+"\" n'est contenu dans aucune zone.");
		PositionGeographique zone = getZones().get(ligneCSV[7].trim());
		if (zone == null)
			throw new ServiceException(CodeIncident.INVALIDE_PARENT_ARRET, "Il n'y a pas de \"Zone\" avec le nom : "+ligneCSV[7].trim());
		logger.debug("\t"+arretPhysique.getObjectId()+" ("+arretPhysique.getName()+") est contenu dans de "+zone.getObjectId()+" ("+zone.getName()+").");
		addObjectIdParParentObjectId(arretPhysique.getObjectId(), zone.getObjectId());
		if (ligneCSV.length == 9)
			if ((ligneCSV[8] != null) || (ligneCSV[8].trim().length() != 0))
				arretPhysique.setComment(ligneCSV[8].trim());
		//arretPhysique.setCountryCode("51100");
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
	
	public void completion() {
		for (String name : zones.keySet()) {
			PositionGeographique zone = zones.get(name);
			int count = 0;
			double x = (double)0.0;
			double y = (double)0.0;
			
			for (String arretPhysiqueObjectId : objectIdParParentObjectId.keySet())
				if (objectIdParParentObjectId.get(arretPhysiqueObjectId).equals(zone.getObjectId())) {
					PositionGeographique arretPhysique = arretsPhysiquesParObjectId.get(arretPhysiqueObjectId);
					if (arretPhysique == null)
						throw new ServiceException(CodeIncident.DONNEE_INVALIDE, "Il doit y avoir un ARRET PHYSIQUE : "+arretPhysiqueObjectId);
					if ((arretPhysique.getX() != null) && (arretPhysique.getY() != null)) {
						count++;
						x = x + arretPhysique.getX().doubleValue();
						y = y + arretPhysique.getY().doubleValue();
					}
				}
			if (count > 0) {
				x = x / (double)count; 
				y = y / (double)count; 
				zone.setX(new BigDecimal(x));
				zone.setY(new BigDecimal(y));
			}
		}
		arretsNames.clear();
	}

	public void reinit() {
		arretsPhysiques = new HashMap<String, PositionGeographique>();
		arretsPhysiquesParObjectId = new HashMap<String, PositionGeographique>();
		objectIdParParentObjectId = new HashMap<String, String>();
		arretsNames = new HashSet<String>();
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
	
	public String getCleLambert1() {
		return cleLambert1;
	}
	
	public void setCleLambert1(String cleLambert1) {
		this.cleLambert1 = cleLambert1;
	}
	
	public String getCleLambert2() {
		return cleLambert2;
	}
	
	public void setCleLambert2(String cleLambert2) {
		this.cleLambert2 = cleLambert2;
	}
	
	public String getCleLambert3() {
		return cleLambert3;
	}
	
	public void setCleLambert3(String cleLambert3) {
		this.cleLambert3 = cleLambert3;
	}
	
	public String getCleLambert4() {
		return cleLambert4;
	}
	
	public void setCleLambert4(String cleLambert4) {
		this.cleLambert4 = cleLambert4;
	}
	
	public String getCleWGS84() {
		return cleWGS84;
	}
	
	public void setCleWGS84(String cleWGS84) {
		this.cleWGS84 = cleWGS84;
	}
	
	public Map<String, PositionGeographique> getZones() {
		return zones;
	}
	
	public void setZones(Map<String, PositionGeographique> zones) {
		this.zones = zones;
	}
	
	public Map<String, PositionGeographique> getArretsPhysiques() {
		return arretsPhysiques;
	}
	
	public Map<String, PositionGeographique> getArretsPhysiquesParObjectId() {
		return arretsPhysiquesParObjectId;
	}
	
	public void setArretsPhysiques(Map<String, PositionGeographique> arretsPhysiques) {
		this.arretsPhysiques = arretsPhysiques;
	}
	
	public void addArretPhysique(PositionGeographique arretPhysique) {
		if (arretsPhysiques == null)
			arretsPhysiques = new HashMap<String, PositionGeographique>();
		if (arretsPhysiques.get(arretPhysique.getName()) != null)
			throw new ServiceException(CodeIncident.DUPLICATE_NAME_ARRETPHYSIQUE, "Il ne peut y avoir deux \"Zones\" avec le même nom : "+arretPhysique.getName()); 
		arretsPhysiques.put(arretPhysique.getName(), arretPhysique);
		logger.debug("ADDING ARRET PHYSIQUE : \""+arretPhysique.getName()+"\".");
		arretsPhysiquesParObjectId.put(arretPhysique.getObjectId(), arretPhysique);
	}
	
	public PositionGeographique getArretPhysique(String name) {
		return arretsPhysiques.get(name);
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
	
	public void setObjectIdParParentObjectId(Map<String, String> objectIdParParentObjectId) {
		this.objectIdParParentObjectId = objectIdParParentObjectId;
	}
	
	public Map<String, String> getObjectIdParParentObjectId() {
		return objectIdParParentObjectId;
	}
	
	public void addObjectIdParParentObjectId(String objectId, String parentObjectId) {
		if (objectIdParParentObjectId == null)
			objectIdParParentObjectId = new HashMap<String, String>();
		if (objectIdParParentObjectId.get(objectId) != null)
			throw new ServiceException(CodeIncident.DUPLICATE_NAME_PARENTARRETPHYSIQUE, "Il ne peut y avoir deux \"Zones\" parentes d'un même \"ArretPhysique\" : "+parentObjectId+" et "+objectIdParParentObjectId.get(objectId)+" sont parent de "+objectId); 
		objectIdParParentObjectId.put(objectId, parentObjectId);
	}
	
	public String getObjectIdParParentObjectId(String objectId) {
		if (objectIdParParentObjectId == null)
			return null;
		return objectIdParParentObjectId.get(objectId);
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
