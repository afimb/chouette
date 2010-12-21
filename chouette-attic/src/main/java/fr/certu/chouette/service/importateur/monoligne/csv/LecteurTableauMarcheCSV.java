package fr.certu.chouette.service.importateur.monoligne.csv;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import chouette.schema.ChouettePTNetworkTypeType;
import chouette.schema.Timetable;
import chouette.schema.types.DayTypeType;
import fr.certu.chouette.modele.Periode;
import fr.certu.chouette.modele.TableauMarche;
import fr.certu.chouette.service.commun.CodeDetailIncident;
import fr.certu.chouette.service.commun.CodeIncident;
import fr.certu.chouette.service.commun.ServiceException;
import fr.certu.chouette.service.identification.IIdentificationManager;

public class LecteurTableauMarcheCSV {
	
    private static final Logger logger = Logger.getLogger( LecteurTableauMarcheCSV.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
	
	private IIdentificationManager identificationManager;
	private String                 cleDebut;        // "Date de d�but d'application"
	private String                 cleFin;          // "Date de fin d'application"
	private String                 cleLundi;        // "Lundi (O/N)"
	private String                 cleMardi;        // "Mardi (O/N)"
	private String                 cleMercredi;     // "Mercredi (O/N)"
	private String                 cleJeudi;        // "Jeudi (O/N)"
	private String                 cleVendredi;     // "Vendredi (O/N)"
	private String                 cleSamedi;       // "Samedi (O/N)"
	private String                 cleDimanche;     // "Dimanche (O/N)"
	private String                 cleFerie;        // "Jour f�ri� (O/N)"
	private String                 cleCommentaire;  // "Libell� du tableau de marche"
	
	private Map<String, List<String>> contenuParTitre;
	private int total;
	
	public LecteurTableauMarcheCSV() {
		super();
		contenuParTitre = new Hashtable<String, List<String>>();
	}
	
	public void initialiser(int total) {
		contenuParTitre.clear();
		this.total = total;
	}

	public void ajouter(String titre, List<String> contenu) {
		assert contenu.size()==total*2: "total attendu "+(total*2)+", total obtenu "+contenu.size();
		if (isCle(titre))
			contenuParTitre.put(titre, contenu);
	}
	
	public Set<String> getTitresIntrouvables() {
		Set<String> cles = new HashSet<String>();
		cles.add(cleCommentaire);
		cles.add(cleDebut);
		cles.add(cleFin);
		cles.add(cleLundi);
		cles.add(cleMardi);
		cles.add(cleMercredi);
		cles.add(cleJeudi);
		cles.add(cleVendredi);
		cles.add(cleSamedi);
		cles.add(cleDimanche);
		cles.add(cleFerie);
		Set<String> clesLues = contenuParTitre.keySet();
		cles.removeAll( clesLues);
		return cles;
	}
	
	public List<TableauMarche> lire() {
		List<TableauMarche> tableauxMarches = new ArrayList<TableauMarche>(total);
		List<String> contenuCommentaire = contenuParTitre.get(cleCommentaire);
		List<String> contenuDebut = contenuParTitre.get(cleDebut);
		List<String> contenuFin = contenuParTitre.get(cleFin);
		List<String> contenuLundi = contenuParTitre.get(cleLundi);
		List<String> contenuMardi = contenuParTitre.get(cleMardi);
		List<String> contenuMercredi = contenuParTitre.get(cleMercredi);
		List<String> contenuJeudi = contenuParTitre.get(cleJeudi);
		List<String> contenuVendredi = contenuParTitre.get(cleVendredi);
		List<String> contenuSamedi = contenuParTitre.get(cleSamedi);
		List<String> contenuDimanche = contenuParTitre.get(cleDimanche);
		List<String> contenuFerie = contenuParTitre.get(cleFerie);
		Map<DayTypeType, List<String>> etatsParJourType = new Hashtable<DayTypeType, List<String>>();
		etatsParJourType.put(DayTypeType.MONDAY, contenuLundi);
		etatsParJourType.put(DayTypeType.TUESDAY, contenuMardi);
		etatsParJourType.put(DayTypeType.WEDNESDAY, contenuMercredi);
		etatsParJourType.put(DayTypeType.THURSDAY, contenuJeudi);
		etatsParJourType.put(DayTypeType.FRIDAY, contenuVendredi);
		etatsParJourType.put(DayTypeType.SATURDAY, contenuSamedi);
		etatsParJourType.put(DayTypeType.SUNDAY, contenuDimanche);
		etatsParJourType.put(DayTypeType.PUBLICHOLLIDAY, contenuFerie);
		for (int i = 0; i < total; i++) {
			TableauMarche tableauMarche = new TableauMarche();
			tableauxMarches.add(tableauMarche);
			tableauMarche.setObjectId(identificationManager.getIdFonctionnel("Timetable", String.valueOf(i*2)));
			tableauMarche.setObjectVersion(1);
			tableauMarche.addVehicleJourneyId(identificationManager.getIdFonctionnel("VehicleJourney", String.valueOf(i*2)));
			tableauMarche.setComment(contenuCommentaire.get( i*2));
			Periode periode = new Periode();
			tableauMarche.ajoutPeriode(periode);
			try {
				periode.setDebut(lireDate(contenuDebut.get(i*2)));
			}
			catch(ParseException e) {
				throw new ServiceException( CodeIncident.ERR_CSV_FORMAT_INVALIDE, CodeDetailIncident.DATE_TYPE,contenuDebut.get(i*2));
			}
         try {
            periode.setFin(lireDate(contenuFin.get(i*2)));
         }
         catch(ParseException e) {
            throw new ServiceException( CodeIncident.ERR_CSV_FORMAT_INVALIDE, CodeDetailIncident.DATE_TYPE,contenuFin.get(i*2));
         }
			Set<DayTypeType> jours = new HashSet<DayTypeType>();
			for (DayTypeType jourType : etatsParJourType.keySet())
				if (etatsParJourType.get(jourType).get(i*2).equals("O"))
					jours.add(jourType);
			tableauMarche.setDayTypes(jours);
			tableauMarche.setCreationTime(new Date());
		}
		return tableauxMarches;
	}
	
	public List<String[]> ecrire(ChouettePTNetworkTypeType chouettePTNetworkType, int length, int colonneTitrePartieFixe, LecteurCourseCSV lecteurCourseCSV) {
		List<String[]> donneesTableauxMarche = new ArrayList<String[]>();
		String[] donneesTableauxMarche1 = new String[length];
		String[] donneesTableauxMarche2 = new String[length];
		String[] donneesTableauxMarche4 = new String[length];
		String[] donneesTableauxMarche5 = new String[length];
		String[] donneesTableauxMarche6 = new String[length];
		String[] donneesTableauxMarche7 = new String[length];
		String[] donneesTableauxMarche8 = new String[length];
		String[] donneesTableauxMarche9 = new String[length];
		String[] donneesTableauxMarche10 = new String[length];
		String[] donneesTableauxMarche11 = new String[length];
		String[] donneesTableauxMarche12 = new String[length];
		donneesTableauxMarche1[colonneTitrePartieFixe] = getCleDebut();
		donneesTableauxMarche2[colonneTitrePartieFixe] = getCleFin();
		donneesTableauxMarche4[colonneTitrePartieFixe] = getCleLundi();
		donneesTableauxMarche5[colonneTitrePartieFixe] = getCleMardi();
		donneesTableauxMarche6[colonneTitrePartieFixe] = getCleMercredi();
		donneesTableauxMarche7[colonneTitrePartieFixe] = getCleJeudi();
		donneesTableauxMarche8[colonneTitrePartieFixe] = getCleVendredi();
		donneesTableauxMarche9[colonneTitrePartieFixe] = getCleSamedi();
		donneesTableauxMarche10[colonneTitrePartieFixe] = getCleDimanche();
		donneesTableauxMarche11[colonneTitrePartieFixe] = getCleFerie();
		donneesTableauxMarche12[colonneTitrePartieFixe] = getCleCommentaire();
		Timetable[] timetables = chouettePTNetworkType.getTimetable();
		for (int i = 0; i < chouettePTNetworkType.getChouetteLineDescription().getVehicleJourneyCount(); i++) {
			donneesTableauxMarche4[colonneTitrePartieFixe+1+2*i] = "N";
			donneesTableauxMarche5[colonneTitrePartieFixe+1+2*i] = "N";
			donneesTableauxMarche6[colonneTitrePartieFixe+1+2*i] = "N";
			donneesTableauxMarche7[colonneTitrePartieFixe+1+2*i] = "N";
			donneesTableauxMarche8[colonneTitrePartieFixe+1+2*i] = "N";
			donneesTableauxMarche9[colonneTitrePartieFixe+1+2*i] = "N";
			donneesTableauxMarche10[colonneTitrePartieFixe+1+2*i] = "N";
			donneesTableauxMarche11[colonneTitrePartieFixe+1+2*i] = "N";
			Timetable timetable = null;
			for (int j = 0; j < timetables.length; j++) {
				for (int k = 0; k < timetables[j].getVehicleJourneyIdCount(); k++)
					if (timetables[j].getVehicleJourneyId(k).equals(chouettePTNetworkType.getChouetteLineDescription().getVehicleJourney(i).getObjectId())) {
						timetable = timetables[j];
						if (timetable.getPeriodCount() > 0) {
							donneesTableauxMarche1[colonneTitrePartieFixe+1+2*i] = new SimpleDateFormat("dd/MM/yy").format(timetable.getPeriod(0).getStartOfPeriod().toDate());
							donneesTableauxMarche2[colonneTitrePartieFixe+1+2*i] = new SimpleDateFormat("dd/MM/yy").format(timetable.getPeriod(0).getEndOfPeriod().toDate());
						}
						for (int l = 0; l < timetable.getDayTypeCount(); l++)
							switch (timetable.getDayType(l))
							{
								case MONDAY:
									donneesTableauxMarche4[colonneTitrePartieFixe+1+2*i] = "O";
									break;
								case TUESDAY:
									donneesTableauxMarche5[colonneTitrePartieFixe+1+2*i] = "O";
									break;
								case WEDNESDAY:
									donneesTableauxMarche6[colonneTitrePartieFixe+1+2*i] = "O";
									break;
								case THURSDAY:
									donneesTableauxMarche7[colonneTitrePartieFixe+1+2*i] = "O";
									break;
								case FRIDAY:
									donneesTableauxMarche8[colonneTitrePartieFixe+1+2*i] = "O";
									break;
								case SATURDAY:
									donneesTableauxMarche9[colonneTitrePartieFixe+1+2*i] = "O";
									break;
								case SUNDAY:
									donneesTableauxMarche10[colonneTitrePartieFixe+1+2*i] = "O";
									break;
								case PUBLICHOLLIDAY:
									donneesTableauxMarche11[colonneTitrePartieFixe+1+2*i] = "O";
									break;
							}
						donneesTableauxMarche12[colonneTitrePartieFixe+1+2*i] = timetable.getComment();
					}
			}
		}
		String[] donneesCours = lecteurCourseCSV.ecrire(chouettePTNetworkType.getChouetteLineDescription(), length, colonneTitrePartieFixe);
		donneesTableauxMarche.add(donneesTableauxMarche1);
		donneesTableauxMarche.add(donneesTableauxMarche2);
		donneesTableauxMarche.add(donneesCours);
		donneesTableauxMarche.add(donneesTableauxMarche4);
		donneesTableauxMarche.add(donneesTableauxMarche5);
		donneesTableauxMarche.add(donneesTableauxMarche6);
		donneesTableauxMarche.add(donneesTableauxMarche7);
		donneesTableauxMarche.add(donneesTableauxMarche8);
		donneesTableauxMarche.add(donneesTableauxMarche9);
		donneesTableauxMarche.add(donneesTableauxMarche10);
		donneesTableauxMarche.add(donneesTableauxMarche11);
		donneesTableauxMarche.add(donneesTableauxMarche12);
		return donneesTableauxMarche;
	}
	
	private Date lireDate(String champ) throws ParseException {
		if (champ == null)
			return null;
		return sdf.parse(champ);
	}
	
	public boolean isCle(String titre) {
		if (titre == null)
			return false;
		return titre.equals(cleCommentaire) || titre.equals(cleDebut) || titre.equals(cleFin) || titre.equals(cleLundi)
		|| titre.equals(cleMardi) || titre.equals(cleMercredi) || titre.equals(cleJeudi) || titre.equals( cleVendredi)
		|| titre.equals(cleSamedi) || titre.equals(cleDimanche) || titre.equals(cleFerie);
	}
	
	public void setCleCommentaire(String cleCommentaire) {
		this.cleCommentaire = cleCommentaire;
	}
	
	public String getCleCommentaire() {
		return cleCommentaire;
	}
	
	public void setCleDebut(String cleDebut) {
		this.cleDebut = cleDebut;
	}
	
	public String getCleDebut() {
		return cleDebut;
	}

	public void setCleFin(String cleFin) {
		this.cleFin = cleFin;
	}
	
	public String getCleFin() {
		return cleFin;
	}

	public void setCleLundi(String cleLundi) {
		this.cleLundi = cleLundi;
	}
	
	public String getCleLundi() {
		return cleLundi;
	}

	public void setCleMardi(String cleMardi) {
		this.cleMardi = cleMardi;
	}
	
	public String getCleMardi() {
		return cleMardi;
	}

	public void setCleMercredi(String cleMercredi) {
		this.cleMercredi = cleMercredi;
	}
	
	public String getCleMercredi() {
		return cleMercredi;
	}

	public void setCleJeudi(String cleJeudi) {
		this.cleJeudi = cleJeudi;
	}
	
	public String getCleJeudi() {
		return cleJeudi;
	}

	public void setCleVendredi(String cleVendredi) {
		this.cleVendredi = cleVendredi;
	}
	
	public String getCleVendredi() {
		return cleVendredi;
	}

	public void setCleSamedi(String cleSamedi) {
		this.cleSamedi = cleSamedi;
	}
	
	public String getCleSamedi() {
		return cleSamedi;
	}

	public void setCleDimanche(String cleDimanche) {
		this.cleDimanche = cleDimanche;
	}
	
	public String getCleDimanche() {
		return cleDimanche;
	}

	public void setCleFerie(String cleFerie) {
		this.cleFerie = cleFerie;
	}
	
	public String getCleFerie() {
		return cleFerie;
	}

	public void setIdentificationManager(IIdentificationManager identificationManager) {
		this.identificationManager = identificationManager;
	}
	
	public IIdentificationManager getIdentificationManager() {
		return identificationManager;
	}
}
