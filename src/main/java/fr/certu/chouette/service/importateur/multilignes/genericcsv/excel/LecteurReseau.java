package fr.certu.chouette.service.importateur.multilignes.genericcsv.excel;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.Logger;
import fr.certu.chouette.modele.Reseau;
import fr.certu.chouette.service.commun.CodeIncident;
import fr.certu.chouette.service.commun.ServiceException;
import fr.certu.chouette.service.identification.IIdentificationManager;
import fr.certu.chouette.service.importateur.multilignes.genericcsv.ILecteurReseau;

public class LecteurReseau implements ILecteurReseau {
	
	private static final Logger                 logger                 = Logger.getLogger(LecteurReseau.class);
	private              int                    colonneDesTitres;      // 7
	private              IIdentificationManager identificationManager; // 
	private              String                 cleNom;                // "Nom du réseau"
	private              String                 cleCode;               // "Code Réseau"
	private              String                 cleDescription;        // "Description du réseau"
	private              Reseau                 reseau;
	private              Set<String>            cellulesNonRenseignees;
	private              Set<String>            titres;
	
	public void reinit() {
		titres = new HashSet<String>();
		reseau = null;
		titres.add(cleNom);
		titres.add(cleCode);
		titres.add(cleDescription);
		cellulesNonRenseignees = new HashSet<String>(titres);
	}
	
	public Reseau getReseau() {
		return reseau;
	}
	
	private boolean isTitreNouvelleDonnee(String titre) {
		return cleNom.equals(titre);
	}
	
	private void validerCompletudeDonneeEnCours() {
		if (reseau != null)
			validerCompletude();
	}
	
	public void validerCompletude() {
		if (cellulesNonRenseignees.size() > 0)
			throw new ServiceException(CodeIncident.ERR_CSV_FORMAT_INVALIDE, "Il manque les données suivantes pour définir un réseau:"+cellulesNonRenseignees);
		logger.debug("FIN DE LECTURE RESEAU.");
	}
	
	public void lire(String[] ligneCSV) {
		if (ligneCSV.length < colonneDesTitres+2)
			throw new ServiceException(CodeIncident.ERR_CSV_FORMAT_INVALIDE, "Le nombre de colonnes "+ligneCSV.length+" est invalide ( < "+(colonneDesTitres+2));
		String titre = ligneCSV[colonneDesTitres];
		String valeur = ligneCSV[colonneDesTitres+1];
		if (isTitreNouvelleDonnee(titre)) {
			logger.debug("DEBUT DE LECTURE RESEAU.");
			validerCompletudeDonneeEnCours();
			cellulesNonRenseignees = new HashSet<String>(titres);
			reseau = new Reseau();
			reseau.setObjectId(identificationManager.getIdFonctionnel("PtNetwork", "1"));
			reseau.setObjectVersion(1);
			reseau.setCreationTime(new Date());
			reseau.setVersionDate(new Date());
		}
		if (!cellulesNonRenseignees.remove(titre))
			throw new ServiceException(CodeIncident.ERR_CSV_FORMAT_INVALIDE, "La ligne "+titre+" apparait plusieurs fois dans ce réseau.");
		if (cleNom.equals(titre))
			reseau.setName(valeur);
		else if (cleCode.equals(titre))
			reseau.setRegistrationNumber(valeur);
		else if (cleDescription.equals(titre))
			reseau.setDescription(valeur);
		//reseau.setComment(comment);
		//reseau.setId(id);
		//reseau.setCreatorId(creatorId);
		//reseau.setSourceIdentifier(sourceIdentifier);
		//reseau.setSourceName(sourceName);
		//reseau.setSourceType(sourceType);
	}
	
	public boolean isTitreReconnu(String[] ligneCSV) {
		if ((ligneCSV == null) || (ligneCSV.length < colonneDesTitres+1))
			return false;
		String titre = ligneCSV[colonneDesTitres];
		if (titre == null)
			return false;
		return titres.contains(titre);
	}
	
	public IIdentificationManager getIdentificationManager() {
		return identificationManager;
	}

	public void setIdentificationManager(IIdentificationManager identificationManager) {
		this.identificationManager = identificationManager;
	}
	
	public String getCleNom() {
		return cleNom;
	}

	public void setCleNom(String cleNom) {
		this.cleNom = cleNom;
	}

	public String getCleCode() {
		return cleCode;
	}

	public void setCleCode(String cleCode) {
		this.cleCode = cleCode;
	}

	public String getCleDescription() {
		return cleDescription;
	}

	public void setCleDescription(String cleDescription) {
		this.cleDescription = cleDescription;
	}

	public int getColonneDesTitres() {
		return colonneDesTitres;
	}

	public void setColonneDesTitres(int colonneDesTitres) {
		this.colonneDesTitres = colonneDesTitres;
	}
}
