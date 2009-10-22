package fr.certu.chouette.service.importateur.multilignes.hastus.impl;

import chouette.schema.types.TransportModeNameType;
import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.modele.Reseau;
import fr.certu.chouette.modele.Transporteur;
import fr.certu.chouette.service.identification.IIdentificationManager;
import fr.certu.chouette.service.importateur.multilignes.hastus.ILecteurLigne;
import fr.certu.chouette.service.importateur.multilignes.hastus.commun.CodeIncident;
import fr.certu.chouette.service.importateur.multilignes.hastus.commun.ServiceException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

public class LecteurLigne implements ILecteurLigne {
	
	private static final Logger                            logger                 = Logger.getLogger(LecteurLigne.class);
	private              int                               counter;
	private              IIdentificationManager            identificationManager; // 
	private              String                            cleCode;               // "03"
	private              String                            cleBus;                // "BUS"
	private              String                            hastusCode;            // "HastusTUR"
	private              String                            special;               // "SPECIAL"
	private              String                            space;                 // "SPACE"
	private              Reseau                            leReseau;
	private              Transporteur                      leTransporteur;
	private              Map<String, Ligne>                lignesParRegistration; /// Ligne par registration
	
	public boolean isTitreReconnu(String[] ligneCSV) {
		if ((ligneCSV == null) || (ligneCSV.length == 0))
			return false;
		return ligneCSV[0].equals(getCleCode());
	}
	
	public void lire(String[] ligneCSV) {
		if ((ligneCSV == null) || (ligneCSV.length == 0))
			return;
		if (ligneCSV.length != 7)
			throw new ServiceException(CodeIncident.INVALIDE_LONGUEUR_LIGNE, "La longeur des lignes dans \"Ligne\" est 7 : "+ligneCSV.length);
		if ((ligneCSV[1] == null) || (ligneCSV[1].trim().length() <= 0))
			throw new ServiceException(CodeIncident.NULL_RESEAU_LIGNE, "Pas de \"Reseau\" pour cette \"Ligne\".");
		if (leReseau == null) {
			logger.debug("CREATION DU RESEAU "+ligneCSV[1].trim());
			leReseau = new Reseau();
			leReseau.setName(ligneCSV[1].trim());
			leReseau.setObjectId(identificationManager.getIdFonctionnel(hastusCode, "PtNetwork", toTrident(ligneCSV[1].trim())));
			leReseau.setObjectVersion(1);
			leReseau.setCreationTime(new Date(System.currentTimeMillis()));
			leReseau.setRegistrationNumber("TUR-HASTUS-"+leReseau.getName());
		}
		if ((ligneCSV[2] == null) || (ligneCSV[2].trim().length() <= 0))
			throw new ServiceException(CodeIncident.NULL_TRANSPORTEUR_LIGNE, "Pas de \"Transporteur\" pour cette \"Ligne\".");
		if (leTransporteur == null) {
			logger.debug("CREATION DU TRANSPORTEUR "+ligneCSV[2].trim());
			leTransporteur = new Transporteur();
			leTransporteur.setName(ligneCSV[2].trim());
			leTransporteur.setObjectId(identificationManager.getIdFonctionnel(hastusCode, "Company", toTrident(ligneCSV[2].trim())));
			leTransporteur.setObjectVersion(1);
			leTransporteur.setCreationTime(new Date(System.currentTimeMillis()));
			leTransporteur.setRegistrationNumber("TUR-HASTUS-"+leTransporteur.getName());
		}
		if ((ligneCSV[3] == null) || (ligneCSV[3].trim().length() <= 0))
			throw new ServiceException(CodeIncident.NULL_NAME_LINE, "Cette \"Ligne\" ne possède de nom.");
		logger.debug("CREATION DE LIGNE "+ligneCSV[3].trim());
		Ligne ligne = new Ligne();
		ligne.setName(ligneCSV[3].trim());
		ligne.setObjectVersion(1);
		ligne.setCreationTime(new Date(System.currentTimeMillis()));
		if ((ligneCSV[4] != null) && (ligneCSV[4].trim().length() > 0))
			ligne.setPublishedName(ligneCSV[4].trim());
		if ((ligneCSV[5] == null) || (ligneCSV[5].trim().length() <= 0))
			throw new ServiceException(CodeIncident.NULL_REGISTRATION_LINE, "Pas de \"Registration\" pour cette \"Ligne\".");
		ligne.setObjectId(identificationManager.getIdFonctionnel(hastusCode, "Line", toTrident(ligneCSV[5].trim())));
		ligne.setRegistrationNumber(ligneCSV[5].trim());
		ligne.setNumber(ligneCSV[5].trim());
		if (lignesParRegistration.get(ligne.getRegistrationNumber()) != null)
			throw new ServiceException(CodeIncident.DUPLICATE_REGISTRATION_LINE, "Il existe déjà une \"Ligne\" avec ce \"RegistrationNumber\" : "+ligne.getRegistrationNumber());
		lignesParRegistration.put(ligne.getRegistrationNumber(), ligne);
		if ((ligneCSV[6] == null) || (ligneCSV[6].trim().length() <= 0))
			throw new ServiceException(CodeIncident.NULL_TRANSPORTMODE_LIGNE, "Le mode de transport d'une \"Ligne\" ne peut être null.");
		if (!ligneCSV[6].trim().equals(getCleBus()))
			throw new ServiceException(CodeIncident.INVALIDE_TRANSPORTMODE_LIGNE, "Le mode de transport \""+ligneCSV[6].trim()+"\" est invalide.");
		ligne.setTransportModeName(TransportModeNameType.BUS);
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
		lignesParRegistration = new HashMap<String, Ligne>();
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
	
	public String getCleBus() {
		return cleBus;
	}
	
	public void setCleBus(String cleBus) {
		this.cleBus = cleBus;
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
	
	public Transporteur getTransporteur() {
		return leTransporteur;
	}
	
	public Reseau getReseau() {
		return leReseau;
	}
	
	public Map<String, Ligne> getLigneParRegistration() {
		return lignesParRegistration;
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
