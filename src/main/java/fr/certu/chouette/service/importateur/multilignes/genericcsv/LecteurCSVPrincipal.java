package fr.certu.chouette.service.importateur.multilignes.genericcsv;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;
import au.com.bytecode.opencsv.CSVReader;
import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.echange.LectureEchange;
import fr.certu.chouette.modele.ArretItineraire;
import fr.certu.chouette.modele.Course;
import fr.certu.chouette.modele.Horaire;
import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.modele.Reseau;
import fr.certu.chouette.modele.TableauMarche;
import fr.certu.chouette.modele.Transporteur;
import fr.certu.chouette.service.commun.CodeDetailIncident;
import fr.certu.chouette.service.commun.CodeIncident;
import fr.certu.chouette.service.commun.ServiceException;
import fr.certu.chouette.service.importateur.multilignes.ILecteurPrincipal;

public class LecteurCSVPrincipal implements ILecteurPrincipal {
    
    private static final Logger               logger              = Logger.getLogger(LecteurCSVPrincipal.class);
    private static final String               JeuCaracteres       = "UTF-8";//ISO-8859-1";
    private              String               repertoire;         // "."
    private              char                 separateur;         // ';'
    private              int                  colonneDesTitres;   // 7
    private              ILecteurCalendrier   lecteurCalendrier;  // 
    private              ILecteurReseau       lecteurReseau;      // 
    private              ILecteurTransporteur lecteurTransporteur;// 
    private              ILecteurLigne        lecteurLigne;       // 
    private              ILecteurCourse       lecteurCourse;      // 
    private              ILecteurHoraire      lecteurHoraire;     // 
    private              ILecteurZone         lecteurZone;        // 
    private              ILecteurMission      lecteurMission;     // 
    private              ILecteurItineraire   lecteurItineraire;  // 
    private              ILecteurArret        lecteurArret;       // 
    
    public void lire(String nom) {
	lireCheminFichier(getCheminfichier(nom));
    }
    
    public List<ILectureEchange> getLecturesEchange() {
	List<Ligne> lignes = lecteurLigne.getLignes();
	if ((lignes == null) || (lignes.size() == 0))
	    return null;
	List<TableauMarche> tableauxMarche = new ArrayList<TableauMarche>(lecteurCalendrier.getTableauxMarchesParRef().values());
	Reseau reseau = lecteurReseau.getReseau();
	Transporteur transporteur = lecteurTransporteur.getTransporteur();
	List<ILectureEchange> lecturesEchange = new ArrayList<ILectureEchange>();
	for (Ligne ligne : lignes) {
	    LectureEchange lectureEchange = new LectureEchange();
	    lectureEchange.setReseau(reseau);
	    lectureEchange.setTransporteur(transporteur);
	    lectureEchange.setLigne(ligne);
	    // Les Zones et les arrets physique  // SE RESTREINDRE AUX ZONES / ARRETS PHYSIQUE DE LA LIGNE
	    List<String> objectIdZonesGeneriques = new ArrayList<String>();
	    lectureEchange.setZonesCommerciales(lecteurZone.getZones(ligne));
	    lectureEchange.setArretsPhysiques(lecteurZone.getArretsPhysiques(ligne));
	    for (PositionGeographique zone : lectureEchange.getZonesCommerciales())
		objectIdZonesGeneriques.add(zone.getObjectId());
	    for (PositionGeographique arretPhysique : lectureEchange.getArretsPhysiques())
		objectIdZonesGeneriques.add(arretPhysique.getObjectId());
	    lectureEchange.setObjectIdZonesGeneriques(objectIdZonesGeneriques);
	    lectureEchange.setZoneParenteParObjectId(lecteurZone.getZoneParenteParObjectId(ligne));
	    //for (String key : lectureEchange.getZoneParenteParObjectId().keySet())
	    //logger.error("HHHHHH "+ligne.getPublishedName()+"\t:\t"+lectureEchange.getZoneParenteParObjectId().get(key)+"\t:\t"+key);
	    for (PositionGeographique zone : lectureEchange.getZonesCommerciales())
		logger.debug("ZONES : "+zone.getName());
	    for (PositionGeographique arretPhysique : lectureEchange.getArretsPhysiques())
		logger.debug("ARRETPHYSIQUE : "+arretPhysique.getName());
	    // Missions
	    lectureEchange.setMissions(lecteurMission.getMissions().get(ligne)); 
	    // COURSES
	    List<Course> courses = lecteurCourse.getCourses(ligne);
	    lectureEchange.setCourses(courses);
	    // Horaires et TABLEAUX DE MARCHE
	    List<Horaire> horaires = new ArrayList<Horaire>();
	    List<TableauMarche> _tableauxMarche = new ArrayList<TableauMarche>();
	    Set<TableauMarche> _tableauxM = new HashSet<TableauMarche>();
	    for (Course course : courses) {
		if (lecteurHoraire.getHoraires() != null)
		    if (lecteurHoraire.getHoraires().get(course) != null)
			horaires.addAll(lecteurHoraire.getHoraires().get(course));
		for (TableauMarche tableauMarche : tableauxMarche)
		    for (int i = 0; i < tableauMarche.getVehicleJourneyIdCount(); i++)
			if (tableauMarche.getVehicleJourneyId(i).equals(course.getObjectId()))
			    if (_tableauxM.add(tableauMarche))
				_tableauxMarche.add(tableauMarche);
	    }
	    lectureEchange.setHoraires(horaires);
	    lectureEchange.setTableauxMarche(_tableauxMarche);
	    // ITINERAIRES
	    lectureEchange.setItineraires(lecteurItineraire.getItineraires().get(ligne));
	    // ARRETS ITINERAIRES
	    List<ArretItineraire> arretsItineraires = lecteurArret.getArretsItinerairesParLigne().get(ligne);
	    lectureEchange.setArrets(arretsItineraires);
	    lectureEchange.setItineraireParArret(lecteurArret.getItineraireParArret().get(ligne));
	    
	    lecturesEchange.add(lectureEchange);
	}
	return lecturesEchange;
    }
    
    private void initialisation() {
	lecteurCalendrier.reinit();
	lecteurReseau.reinit();
	lecteurTransporteur.reinit();
	lecteurLigne.reinit();
	lecteurCourse.reinit();
	lecteurHoraire.reinit();
	lecteurZone.reinit();
	lecteurMission.reinit();
	lecteurItineraire.reinit();
	lecteurArret.reinit();
    }
    
    public void lireCheminFichier(String chemin) {
	CSVReader lecteur = null;
	initialisation();
	int numLigne = 0;
	try {
	    InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(chemin), JeuCaracteres);
	    lecteur = new CSVReader(inputStreamReader, separateur);
	    List<String[]> contenu = lecteur.readAll();
	    contenu.add(new String[0]);
	    int counter = 0;
	    for (String[] ligneCSV : contenu) {
		numLigne++;
		if (lecteurCalendrier.isTitreReconnu(ligneCSV))
		    if (counter == 0)
			lecteurCalendrier.lire(ligneCSV);
		    else
			throw new ServiceException(CodeIncident.ERR_CSV_FORMAT_INVALIDE, CodeDetailIncident.TIMETABLE_POSITION);
		else if (lecteurReseau.isTitreReconnu(ligneCSV)) {
		    if (counter == 0) {
			lecteurCalendrier.validerCompletude();
			counter++;
		    }
		    if (counter == 1)
			lecteurReseau.lire(ligneCSV);
		    else
			throw new ServiceException(CodeIncident.ERR_CSV_FORMAT_INVALIDE, CodeDetailIncident.NETWORK_POSITION);
		}
		else if (lecteurTransporteur.isTitreReconnu(ligneCSV)) {
		    if (counter == 1) {
			lecteurReseau.validerCompletude();
			counter++;
		    }
		    if (counter == 2)
			lecteurTransporteur.lire(ligneCSV);
		    else
			throw new ServiceException(CodeIncident.ERR_CSV_FORMAT_INVALIDE, CodeDetailIncident.COMPANY_POSITION);
		}
		else if (lecteurLigne.isTitreReconnu(ligneCSV)) {
		    if (counter == 2) {
			lecteurTransporteur.validerCompletude();
			counter++;
		    }
		    if (counter == 3)
			lecteurLigne.lire(ligneCSV);
		    else
			throw new ServiceException(CodeIncident.ERR_CSV_FORMAT_INVALIDE, CodeDetailIncident.LINE_POSITION);
		}
		else if (lecteurCourse.isTitreReconnu(ligneCSV)) {
		    if (counter == 3) {
			//lecteurTransporteur.validerCompletude();
			lecteurCourse.setTableauxMarchesParRef(lecteurCalendrier.getTableauxMarchesParRef());
			counter++;
		    }
		    if (counter == 4)
			lecteurCourse.lire(ligneCSV, lecteurLigne.getLigneEnCours());
		    else
			;//
		}
		else if ((ligneCSV == null) || (ligneCSV.length == 0) || 
			 ((ligneCSV.length == 1) && ((ligneCSV[0] == null) || (ligneCSV[0].trim().length()==0))) ||
			 ((ligneCSV.length > colonneDesTitres) && ((ligneCSV[colonneDesTitres] == null) || (ligneCSV[colonneDesTitres].trim().length() == 0)))) {
		    if (counter == 5) {
			/*
			logger.error("LIGNE "+lecteurLigne.getLigneEnCours().getResistrationNumber());
			int coNum = 0;
			for (Course course : lecteurHoraire.getArretsPhysiques().keySet()) {
			    coNum++;
			    logger.error("\tCOURSE "+coNum);
			    for (String st : lecteurHoraire.getArretsPhysiques().get(course))
				if (st.length() > 0)
				    logger.error("\t\t "+st);
			}
			*/
			counter = 3;
			lecteurMission.lire(lecteurHoraire.getArretsPhysiques(), lecteurLigne.getLigneEnCours());
			lecteurItineraire.lire(lecteurCourse.getCourses(lecteurLigne.getLigneEnCours()), lecteurMission.getMissionByCode(), lecteurLigne.getLigneEnCours());
			lecteurArret.init(lecteurItineraire.getItineraires().get(lecteurLigne.getLigneEnCours()),
					  lecteurMission.getMissions().get(lecteurLigne.getLigneEnCours()),
					  lecteurCourse.getCourses(lecteurLigne.getLigneEnCours()),
					  lecteurHoraire.getHoraires(),
					  lecteurHoraire.getArretsPhysiques(),
					  lecteurZone.getArretsPhysiques());
			//logger.error("LIGNE "+lecteurLigne.getLigneEnCours().getPublishedName());
			for (PositionGeographique st : lecteurZone.getArretsPhysiques())
			    ;//logger.error("\t"+st.getName());
			lecteurArret.lire(lecteurLigne.getLigneEnCours(), lecteurCourse.getCoursesAller(), lecteurCourse.getCoursesRetour(), lecteurZone.getArretsPhysiques());
			;// VERIFIER QUE TOUTE LA LIGNE EST VIDE
		    }
		}
		else {
		    if (counter == 4) {
			lecteurCourse.validerCompletude();
			lecteurZone.init();
			lecteurHoraire.init();
			counter++;
		    }
		    if (counter == 5) {
			lecteurZone.lire(lecteurLigne.getLigneEnCours(), ligneCSV);
			lecteurHoraire.lire(ligneCSV, lecteurCourse.getCourses(lecteurLigne.getLigneEnCours()));
		    }
		    else {
			String ligneText = "";
			for (int i = 0; i < ligneCSV.length; i++)
			    ligneText += ligneCSV[i]+";";
			throw new ServiceException(CodeIncident.ERR_CSV_FORMAT_INVALIDE, CodeDetailIncident.INVALID_SYNTAX,ligneText);
		    }
		}
	    }
	} 
	catch (FileNotFoundException e) {
	    throw new ServiceException(CodeIncident.ERR_CSV_NON_TROUVE,  e);
	}
	catch (ServiceException e) {
	    throw e;
	}
	catch (Exception e) {
	    e.printStackTrace();
	    throw new ServiceException(CodeIncident.DONNEE_INVALIDE, e, numLigne);
	}
	finally {
	    if (lecteur != null) {
		try {
		    lecteur.close();
		}
		catch (Exception e) {
		    logger.error("Echec cloture du fichier "+chemin+", "+e.getMessage(), e);
		}
	    }
	}
    }
    
	private String getCheminfichier(String nom) {
		return repertoire + File.separator + nom;
	}
	
	public String getRepertoire() {
		return repertoire;
	}
	
	public void setRepertoire(String repertoire) {
		this.repertoire = repertoire;
	}
	
	public char getSeparateur() {
		return separateur;
	}
	
	public void setSeparateur(char separateur) {
		this.separateur = separateur;
	}

	public int getColonneDesTitres() {
		return colonneDesTitres;
	}

	public void setColonneDesTitres(int colonneDesTitres) {
		this.colonneDesTitres = colonneDesTitres;
	}
	
	public ILecteurCalendrier getLecteurCalendrier() {
		return lecteurCalendrier;
	}
	
	public void setLecteurCalendrier(ILecteurCalendrier lecteurCalendrier) {
		this.lecteurCalendrier = lecteurCalendrier;
	}
	
	public ILecteurReseau getLecteurReseau() {
		return lecteurReseau;
	}
	
	public void setLecteurReseau(ILecteurReseau lecteurReseau) {
		this.lecteurReseau = lecteurReseau;
	}
	
	public ILecteurTransporteur getLecteurTransporteur() {
		return lecteurTransporteur;
	}
	
	public void setLecteurTransporteur(ILecteurTransporteur lecteurTransporteur) {
		this.lecteurTransporteur = lecteurTransporteur;
	}
	
	public ILecteurLigne getLecteurLigne() {
		return lecteurLigne;
	}
	
	public void setLecteurLigne(ILecteurLigne lecteurLigne) {
		this.lecteurLigne = lecteurLigne;
	}
	
	public ILecteurCourse getLecteurCourse() {
		return lecteurCourse;
	}
	
	public void setLecteurCourse(ILecteurCourse lecteurCourse) {
		this.lecteurCourse = lecteurCourse;
	}
	
	public ILecteurHoraire getLecteurHoraire() {
		return lecteurHoraire;
	}
	
	public void setLecteurHoraire(ILecteurHoraire lecteurHoraire) {
		this.lecteurHoraire = lecteurHoraire;
	}
	
	public ILecteurZone getLecteurZone() {
		return lecteurZone;
	}
	
	public void setLecteurZone(ILecteurZone lecteurZone) {
		this.lecteurZone = lecteurZone;
	}
	
	public ILecteurMission getLecteurMission() {
		return lecteurMission;
	}
	
	public void setLecteurMission(ILecteurMission lecteurMission) {
		this.lecteurMission = lecteurMission;
	}
	
	public ILecteurItineraire getLecteurItineraire() {
		return lecteurItineraire;
	}
	
	public void setLecteurItineraire(ILecteurItineraire lecteurItineraire) {
		this.lecteurItineraire = lecteurItineraire;
	}
	
	public ILecteurArret getLecteurArret() {
		return lecteurArret;
	}
	
	public void setLecteurArret(ILecteurArret lecteurArret) {
		this.lecteurArret = lecteurArret;
	}
}
