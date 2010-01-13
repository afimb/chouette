package fr.certu.chouette.service.importateur.multilignes.hastus.impl;

import chouette.schema.types.PTDirectionType;
import fr.certu.chouette.modele.Itineraire;
import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.service.identification.IIdentificationManager;
import fr.certu.chouette.service.importateur.multilignes.hastus.ILecteurItineraire;
import fr.certu.chouette.service.importateur.multilignes.hastus.commun.CodeIncident;
import fr.certu.chouette.service.importateur.multilignes.hastus.commun.ServiceException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;

public class LecteurItineraire implements ILecteurItineraire {
	
	private static final Logger                            logger                 = Logger.getLogger(LecteurItineraire.class);
	private              int                               counter;
	private              IIdentificationManager            identificationManager; // 
	private              String                            cleCode;               // "04"
	private              String                            cleAller;              // "Aller"
	private              String                            cleRetour;             // "Retour"
	private              String                            hastusCode;            // "HastusTUR"
	private              String                            special;               // "SPECIAL"
	private              String                            space;                 // "SPACE"
	private              Map<String, Ligne>                ligneParRegistration;  /// Ligne par registration (LecteurLigne)
	private              Set<String>                       itineraireNames;       /// Ensemble des name des Itineraire (<Ligne.registrationNumber>-<zone.registrationNumber>-<zone.registrationNumber>)
	private              Map<String, Itineraire>           itineraireParNom;      /// Itineraire par number (<Ligne.registrationNumber>-<integer>)
	private              Map<Itineraire, Ligne>            ligneParItineraire;    /// Ligne par Itineraire
	private              Map<String, PositionGeographique> zones;                 /// PositionGeographique (non arrêt physique) par name | registrationNumber(LecteurZone)
	private              Set<String>                       messages;
	
	public boolean isTitreReconnu(String[] ligneCSV) {
		if ((ligneCSV == null) || (ligneCSV.length == 0))
			return false;
		return ligneCSV[0].equals(getCleCode());
	}
	
	public void lire(String[] ligneCSV) {
		if ((ligneCSV == null) || (ligneCSV.length == 0))
			return;
		if (ligneCSV.length != 7)
			throw new ServiceException(CodeIncident.INVALIDE_LONGUEUR_ITINERAIRE, "La longeur des lignes dans \"Itineraire\" est 7 : "+ligneCSV.length);
		logger.debug("CREATION D'ITINERAIRE "+ligneCSV[1].trim());
		Itineraire itineraire = new Itineraire();
		itineraire.setObjectVersion(1);
		itineraire.setCreationTime(new Date(System.currentTimeMillis()));
		if ((ligneCSV[1] == null) || (ligneCSV[1].trim().length() <= 0))
			throw new ServiceException(CodeIncident.NULL_NAME_ITINERAIRE, "Le \"Nom\" de l'\"Itineraire\" ne peut être null.");
		itineraire.setName(ligneCSV[1].trim());
		if ((ligneCSV[2] != null) && (ligneCSV[2].trim().length() > 0)) {
			itineraire.setPublishedName(ligneCSV[2].trim());
			if (!ligneCSV[1].trim().equals(itineraire.getPublishedName()))
				throw new ServiceException(CodeIncident.INVALIDE_PUBLISHEDNAME_ITINERAIRE, "La ligne \""+cleCode+":"+itineraire.getName()+":"+itineraire.getPublishedName()+":...\" est invalide.");
		}
		if ((ligneCSV[3] != null) && (ligneCSV[3].trim().length() > 0))
			if (ligneCSV[3].trim().equals(getCleAller())) {
				itineraire.setDirection(PTDirectionType.A);
				itineraire.setWayBack("A");
			}
			else if (ligneCSV[3].trim().equals(getCleRetour())) {
				itineraire.setDirection(PTDirectionType.R);
				itineraire.setWayBack("R");
			}
			else
				throw new ServiceException(CodeIncident.INVALIDE_DIRECTION_ITINERAIRE, "La \"Direction\" d'un \"Itineraire\" ne peut être : "+ligneCSV[3].trim());
		if ((ligneCSV[4] != null) && (ligneCSV[4].trim().length() > 0)) {
			itineraire.setNumber(ligneCSV[4].trim());
			itineraire.setName(ligneCSV[1].trim()+"-"+ligneCSV[4].trim());
			itineraire.setObjectId(identificationManager.getIdFonctionnel(hastusCode, "ChouetteRoute", toTrident(ligneCSV[1].trim()+"-"+ligneCSV[4].trim())));
		}
		else
			itineraire.setObjectId(identificationManager.getIdFonctionnel(hastusCode, "ChouetteRoute", toTrident(ligneCSV[1].trim())));
		
		if (!itineraireNames.add(itineraire.getName()))
			throw new ServiceException(CodeIncident.DUPLICATE_NAME_ITINERAIRE, "Il ne peut y avoir deux \"Itineraire\" avec le meme nom : "+itineraire.getName());
		if (itineraire.getNumber() != null)
			if (itineraireParNom.get(itineraire.getNumber()) == null)
				itineraireParNom.put(itineraire.getNumber(), itineraire);
		if ((ligneCSV[5] == null) || (ligneCSV[5].trim().length() <= 0)) {
			if (itineraire.getDirection() != null)
				if (itineraire.getDirection().getType() != PTDirectionType.A_TYPE)
					throw new ServiceException(CodeIncident.NULL_DIRECTION_ITINERAIRE, "La \"Direction\" de l'\"Itineraire\" ("+itineraire.getName()+") est par défaut \"Aller\".");
		}
		else
			if (ligneCSV[5].trim().equals(getCleAller())) {
				if ((itineraire.getDirection() != null) && (itineraire.getDirection().getType() != PTDirectionType.A_TYPE))
					throw new ServiceException(CodeIncident.DIRECTIONS_CONTRADICTOIRES_ITINERAIRE, "La \"Direction\" d'un \"Itineraire\" est soit "+ligneCSV[3].trim()+" soit "+ligneCSV[5].trim()+".");
				itineraire.setDirection(PTDirectionType.A);
				itineraire.setWayBack("A");
			}
			else if (ligneCSV[5].trim().equals(getCleRetour())) {
				if ((itineraire.getDirection() != null) && (itineraire.getDirection().getType() != PTDirectionType.R_TYPE))
					throw new ServiceException(CodeIncident.DIRECTIONS_CONTRADICTOIRES_ITINERAIRE, "La \"Direction\" d'un \"Itineraire\" est soit "+ligneCSV[3].trim()+" soit "+ligneCSV[5].trim()+".");
				itineraire.setDirection(PTDirectionType.R);
				itineraire.setWayBack("R");
			}
			else
				throw new ServiceException(CodeIncident.INVALIDE_DIRECTION_ITINERAIRE, "La \"Direction\" d'un \"Itineraire\" ne peut être : "+ligneCSV[5].trim());
		if ((ligneCSV[6] == null) || (ligneCSV[6].trim().length() <= 0))
			throw new ServiceException(CodeIncident.NULL_LIGNE_ITINERAIRE, "Le \"RegistrationNumber\" de \"Ligne\" indiqué pour cet \"Itineraire\" ("+itineraire.getName()+") est null.");
		Ligne ligne = ligneParRegistration.get(ligneCSV[6].trim());
		if (ligne == null)
			throw new ServiceException(CodeIncident.INVALIDE_LIGNE_ITINERAIRE, "Le \"RegistrationNumber\" ("+ligneCSV[6].trim()+") de \"Ligne\" indiqué pour cet \"Itineraire\" ("+itineraire.getName()+") est inconnu.");
		ligneParItineraire.put(itineraire, ligne);
		verifierNumberItineraire(itineraire.getNumber());
		verifierNomItineraire(ligneCSV[1].trim());
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
	
	private void verifierNumberItineraire(String number) {
		// <Ligne.registrationNumber>-<integer>
		if (number != null) {
			String[] tab = number.split("-");
			if ((tab != null) && (tab.length == 2))
				if (ligneParRegistration.get(tab[0]) != null) {
					try {
						Integer.parseInt(tab[1]);
						return;
					}
					catch(Exception e) {
						if (messages.add("Le \"number\" ("+number+") est invalide."))
							throw new ServiceException(CodeIncident.INVALIDE_NUMBER_ITINERAIRE, "Le champ \"number\" ("+number+") est invalide, il devrait commencer par un numéro d'enregistrement d'une ligne suivit par le symbol \"-\" et par un entier.");
					}
				}
				else {
					if (messages.add("Le \"number\" ("+number+") est invalide."))
						throw new ServiceException(CodeIncident.INVALIDE_NUMBER_ITINERAIRE, "Le champ \"number\" ("+number+") est invalide, il devrait commencer par un numéro d'enregistrement d'une ligne suivit par le symbol \"-\".");
				}
			else {
				if (messages.add("Le \"number\" ("+number+") est invalide."))
					throw new ServiceException(CodeIncident.INVALIDE_NUMBER_ITINERAIRE, "Le champ \"number\" ("+number+") est invalide, il devrait contenir un et un seul symbol \"-\".");
			}
		}
		else
			if (messages.add("Le \"number\" ("+number+") est invalide."))
				throw new ServiceException(CodeIncident.INVALIDE_NUMBER_ITINERAIRE, "Le champ \"number\" doit être renseigné.");
	}
	
	private void verifierNomItineraire(String name) {
		// <Ligne.registrationNumber>-<zone.registrationNumber>-<zone.registrationNumber>
		if (name != null) {
			String[] tab = name.split("-");
			if ((tab != null) && (tab.length == 3))
				if ((ligneParRegistration.get(tab[0]) != null) && (zones.get(tab[1]) != null) && (zones.get(tab[2]) != null))
					return;
				else {
					String message = "";
					if (ligneParRegistration.get(tab[0]) == null)
						message += " Il n' y a pas de ligne avec le numéro d'enregistrement "+tab[0]+".";
					if (zones.get(tab[1]) == null)
						message += " Il n' y a pas de lieu nommé "+tab[1]+".";
					if (zones.get(tab[2]) == null)
						message += " Il n' y a pas de lieu nommé "+tab[2]+".";
					if (messages.add("Le \"name\" ("+name+") est invalide."))
						throw new ServiceException(CodeIncident.INVALIDE_NAME_ITINERAIRE, "Le champ \"name\" ("+name+") est invalide." + message);
				}
			else
				if (messages.add("Le \"name\" ("+name+") est invalide."))
					throw new ServiceException(CodeIncident.INVALIDE_NAME_ITINERAIRE, "Le champ \"name\" ("+name+") est invalide, il doit contenir deux fois le symbol \"-\".");
		}
		else
			if (messages.add("Le \"name\" ("+name+") est invalide."))
				throw new ServiceException(CodeIncident.INVALIDE_NAME_ITINERAIRE, "Le champ \"name\" doit être renseigné.");
	}
	
	public void reinit() {
		ligneParItineraire = new HashMap<Itineraire, Ligne>();
		itineraireParNom = new HashMap<String, Itineraire>();
		itineraireNames = new HashSet<String>();
		messages = new HashSet<String>();
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
	
	public void setLigneParRegistration(Map<String, Ligne> ligneParRegistration) {
		this.ligneParRegistration = ligneParRegistration;
	}
	
	public Map<Itineraire, Ligne> getLigneParItineraire() {
		return ligneParItineraire;
	}
	
	public Map<String, Itineraire> getItineraireParNom() {
		return itineraireParNom;
	}

	public void setZones(Map<String, PositionGeographique> zones) {
		this.zones = zones;
	}
	
	public void completion() {
		this.zones.clear();
		this.itineraireNames.clear();
		this.messages.clear();
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
