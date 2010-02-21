package fr.certu.chouette.service.importateur.multilignes.genericcsv.excel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import chouette.schema.types.DayTypeType;
import fr.certu.chouette.modele.Periode;
import fr.certu.chouette.modele.TableauMarche;
import fr.certu.chouette.service.commun.CodeIncident;
import fr.certu.chouette.service.commun.ServiceException;
import fr.certu.chouette.service.identification.IIdentificationManager;
import fr.certu.chouette.service.importateur.multilignes.genericcsv.ILecteurCalendrier;

public class LecteurCalendrier implements ILecteurCalendrier {
	
	private static final Logger                     logger                = Logger.getLogger(LecteurCalendrier.class);
	private              Map<String, TableauMarche> caldendriersParRef;
	private static final SimpleDateFormat           sdf                   = new SimpleDateFormat("dd/MM/yy");
	private              int                        colonneDesTitres;     // 7
	private              IIdentificationManager     identificationManager;// 
	private              String                     cleAlias;             // "Alias"
	private              String                     cleJour;              // "Jour d'application"
	private              String                     cleDebut;             // "Date de début d'application"
	private              String                     cleFin;               // "Date de fin d'application"
	private              String                     cleLundi;             // "Lundi (O/N)"
	private              String                     cleMardi;             // "Mardi (O/N)"
	private              String                     cleMercredi;          // "Mercredi (O/N)"
	private              String                     cleJeudi;             // "Jeudi (O/N)"
	private              String                     cleVendredi;          // "Vendredi (O/N)"
	private              String                     cleSamedi;            // "Samedi (O/N)"
	private              String                     cleDimanche;          // "Dimanche (O/N)"
	private              String                     cleCommentaire;       // "Libellé du tableau de marche"
	private              TableauMarche              calendrierEnCours;
	private              Set<String>                cellulesNonRenseignees;
	private              Set<String>                titres;
	
	public void reinit() {
		calendrierEnCours = null;
		caldendriersParRef = new Hashtable<String, TableauMarche>();
		titres = new HashSet<String>();
		titres.add(cleCommentaire);
		titres.add(cleAlias);
		titres.add(cleJour);
		titres.add(cleDebut);
		titres.add(cleFin);
		titres.add(cleLundi);
		titres.add(cleMardi);
		titres.add(cleMercredi);
		titres.add(cleJeudi);
		titres.add(cleVendredi);
		titres.add(cleSamedi);
		titres.add(cleDimanche);
		cellulesNonRenseignees = new HashSet<String>(titres);
	}

	private boolean isTitreNouvelleDonnee(String titre) {
		return cleCommentaire.equals(titre);
	}
	
	public void lire(String[] ligneCSV) {
		if (ligneCSV.length < colonneDesTitres+2)
			throw new ServiceException(CodeIncident.ERR_CSV_FORMAT_INVALIDE, "Le nombre de colonnes "+ligneCSV.length+" est invalide ( < "+(colonneDesTitres+2));
		String titre = ligneCSV[colonneDesTitres];
		String valeur = ligneCSV[colonneDesTitres+1];
		if (isTitreNouvelleDonnee(titre)) {
			validerCompletudeDonneeEnCours();
			cellulesNonRenseignees = new HashSet<String>(titres);
			calendrierEnCours = new TableauMarche();
			calendrierEnCours.setObjectId(identificationManager.getIdFonctionnel("Timetable", String.valueOf(caldendriersParRef.size()+1)));
			calendrierEnCours.setObjectVersion(1);
			calendrierEnCours.setCreationTime(new Date());
			logger.debug("Nouveau calendrier");
		}
		if (!cellulesNonRenseignees.remove(titre))
			throw new ServiceException(CodeIncident.ERR_CSV_FORMAT_INVALIDE, "La ligne "+titre+" apparait plusieurs fois dans ce calendrier.");
		if (cleCommentaire.equals(titre))
			calendrierEnCours.setComment(valeur);
		else if (cleJour.equals(titre)) {
			boolean finDeLigne = false;
			for (int i = colonneDesTitres+1; i < ligneCSV.length; i++) {
				valeur = ligneCSV[i];
				if ((valeur == null) || (valeur.trim().length() == 0))
					finDeLigne = true;
				else {
					if (finDeLigne)
						throw new ServiceException(CodeIncident.ERR_CSV_FORMAT_INVALIDE, "La valeur "+valeur+" arrive après la fin de la ligne.");
					try {
						calendrierEnCours.ajoutDate(sdf.parse(valeur));
					}
					catch(Exception e) {
						throw new ServiceException(CodeIncident.ERR_CSV_FORMAT_INVALIDE, "Format de date invalide : "+valeur, e);
					}
				}
			}
		}
		else if (cleDebut.equals(titre)) {
			boolean finDeLigne = false;
			for (int i = colonneDesTitres+1; i < ligneCSV.length; i++) {
				valeur = ligneCSV[i];
				if ((valeur == null) || (valeur.trim().length() == 0))
					finDeLigne = true;
				else {
					if (finDeLigne)
						throw new ServiceException(CodeIncident.ERR_CSV_FORMAT_INVALIDE, "La valeur "+valeur+" arrive après la fin de la ligne.");
					Periode periode = new Periode();
					calendrierEnCours.ajoutPeriode(periode);
					try {
						periode.setDebut(sdf.parse(valeur));
					}
					catch(Exception e) {
						throw new ServiceException(CodeIncident.ERR_CSV_FORMAT_INVALIDE, "Format de date de debut de période invalide : "+valeur, e);
					}
				}
			}
		}
		else if (cleFin.equals(titre)) {
			boolean finDeLigne = false;
			List<Periode> periodes = calendrierEnCours.getPeriodes();
			for (int i = colonneDesTitres+1; i < ligneCSV.length; i++) {
				valeur = ligneCSV[i];
				if ((valeur == null) || (valeur.trim().length() == 0)) {
					finDeLigne = true;
					if ((periodes != null) && (periodes.size() > i-(colonneDesTitres+1)))
						throw new ServiceException(CodeIncident.ERR_CSV_FORMAT_INVALIDE, "La date de debut "+periodes.get(i-(colonneDesTitres+1))+" ne correspond à aucune periode.");
				}
				else {
					if (finDeLigne)
						throw new ServiceException(CodeIncident.ERR_CSV_FORMAT_INVALIDE, "La valeur "+valeur+" arrive après la fin de la ligne.");
					if ((periodes == null) || (periodes.size() <= i-(colonneDesTitres+1)))
						throw new ServiceException(CodeIncident.ERR_CSV_FORMAT_INVALIDE, "La date de fin "+valeur+" ne correspond à aucune periode.");
					Periode periode = periodes.get(i-(colonneDesTitres+1));
					try {
						periode.setFin(sdf.parse(valeur));
					}
					catch(Exception e) {
						throw new ServiceException(CodeIncident.ERR_CSV_FORMAT_INVALIDE, "Format de date de fin de période invalide : "+valeur, e);
					}
				}
			}
		}
		else if (cleLundi.equals(titre)) {
			if (isO(valeur)) {
				Set<DayTypeType> jours =  calendrierEnCours.getDayTypes();
				jours.add(DayTypeType.MONDAY);
				calendrierEnCours.setDayTypes(jours);
			}
		}
		else if (cleMardi.equals(titre)) {
			if (isO(valeur)) {
				Set<DayTypeType> jours =  calendrierEnCours.getDayTypes();
				jours.add(DayTypeType.TUESDAY);
				calendrierEnCours.setDayTypes(jours);
			}
		}
		else if (cleMercredi.equals(titre)) {
			if (isO(valeur)) {
				Set<DayTypeType> jours =  calendrierEnCours.getDayTypes();
				jours.add(DayTypeType.WEDNESDAY);
				calendrierEnCours.setDayTypes(jours);
			}
		}
		else if (cleJeudi.equals(titre)) {
			if (isO(valeur)) {
				Set<DayTypeType> jours =  calendrierEnCours.getDayTypes();
				jours.add(DayTypeType.THURSDAY);
				calendrierEnCours.setDayTypes(jours);
			}
		}
		else if (cleVendredi.equals(titre)) {
			if (isO(valeur)) {
				Set<DayTypeType> jours =  calendrierEnCours.getDayTypes();
				jours.add(DayTypeType.FRIDAY);
				calendrierEnCours.setDayTypes(jours);
			}
		}
		else if (cleSamedi.equals(titre)) {
			if (isO(valeur)) {
				Set<DayTypeType> jours =  calendrierEnCours.getDayTypes();
				jours.add(DayTypeType.SATURDAY);
				calendrierEnCours.setDayTypes(jours);
			}
		}
		else if (cleDimanche.equals(titre)) {
			if (isO(valeur)) {
				Set<DayTypeType> jours =  calendrierEnCours.getDayTypes();
				jours.add(DayTypeType.SUNDAY);
				calendrierEnCours.setDayTypes(jours);
			}
		}
		else if (cleAlias.equals(titre)) {
			logger.debug("\talias = "+valeur);
			if ((valeur == null) || (valeur.trim().length() == 0))
				throw new ServiceException(CodeIncident.ERR_CSV_FORMAT_INVALIDE, "Un alias ne peut être null.");
			if (caldendriersParRef.get(valeur.trim()) != null)
				throw new ServiceException(CodeIncident.ERR_CSV_FORMAT_INVALIDE, "Chaque calendrier doit avoir un alias propre.");
			caldendriersParRef.put(valeur.trim(), calendrierEnCours);
		}
		//calendrierEnCours.setCreatorId(creatorId);
		//calendrierEnCours.setId(id);
		//calendrierEnCours.setVersion(version);
	}
	
	private boolean isO(String valeur) {
		if ((valeur == null) || (!valeur.equals("O") && !valeur.equals("N")))
			throw new ServiceException(CodeIncident.ERR_CSV_FORMAT_INVALIDE, "Valeur du champ invalide "+valeur);
		if (valeur.equals("O"))
			return true;
		return false;
	}

	private void validerCompletudeDonneeEnCours() {
		if (calendrierEnCours != null)
			validerCompletude();
	}
	
	public void validerCompletude() {
		if (cellulesNonRenseignees.size() > 0)
			throw new ServiceException(CodeIncident.ERR_CSV_FORMAT_INVALIDE, "Il manque les données suivantes pour définir un calendrier d'application: " + cellulesNonRenseignees);
	}
	
	public Map<String, TableauMarche> getTableauxMarchesParRef() {
		return caldendriersParRef;
	}
	
	public void setTableauxMarchesParRef(Map<String, TableauMarche> caldendriersParRef) {
		this.caldendriersParRef = caldendriersParRef;
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

	public String getCleAlias() {
		return cleAlias;
	}

	public void setCleAlias(String cleAlias) {
		this.cleAlias = cleAlias;
	}

	public String getCleJour() {
		return cleJour;
	}

	public void setCleJour(String cleJour) {
		this.cleJour = cleJour;
	}

	public String getCleDebut() {
		return cleDebut;
	}

	public void setCleDebut(String cleDebut) {
		this.cleDebut = cleDebut;
	}

	public String getCleFin() {
		return cleFin;
	}

	public void setCleFin(String cleFin) {
		this.cleFin = cleFin;
	}

	public String getCleLundi() {
		return cleLundi;
	}

	public void setCleLundi(String cleLundi) {
		this.cleLundi = cleLundi;
	}

	public String getCleMardi() {
		return cleMardi;
	}

	public void setCleMardi(String cleMardi) {
		this.cleMardi = cleMardi;
	}

	public String getCleMercredi() {
		return cleMercredi;
	}

	public void setCleMercredi(String cleMercredi) {
		this.cleMercredi = cleMercredi;
	}

	public String getCleJeudi() {
		return cleJeudi;
	}

	public void setCleJeudi(String cleJeudi) {
		this.cleJeudi = cleJeudi;
	}

	public String getCleVendredi() {
		return cleVendredi;
	}

	public void setCleVendredi(String cleVendredi) {
		this.cleVendredi = cleVendredi;
	}

	public String getCleSamedi() {
		return cleSamedi;
	}

	public void setCleSamedi(String cleSamedi) {
		this.cleSamedi = cleSamedi;
	}

	public String getCleDimanche() {
		return cleDimanche;
	}

	public void setCleDimanche(String cleDimanche) {
		this.cleDimanche = cleDimanche;
	}

	public String getCleCommentaire() {
		return cleCommentaire;
	}

	public void setCleCommentaire(String cleCommentaire) {
		this.cleCommentaire = cleCommentaire;
	}

	public int getColonneDesTitres() {
		return colonneDesTitres;
	}

	public void setColonneDesTitres(int colonneDesTitres) {
		this.colonneDesTitres = colonneDesTitres;
	}
}
