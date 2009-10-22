package fr.certu.chouette.service.importateur.multilignes.pegase;

import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.service.importateur.multilignes.ILecteurPrincipal;
import java.io.File;
import org.apache.log4j.Logger;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import au.com.bytecode.opencsv.CSVReader;
import fr.certu.chouette.echange.LectureEchange;
import fr.certu.chouette.modele.Reseau;
import fr.certu.chouette.modele.Transporteur;
import fr.certu.chouette.service.identification.IIdentificationManager;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class LecteurPrincipal implements ILecteurPrincipal {
	
	private              String                     repertoire;        // "."
	private static final Logger                     logger        = Logger.getLogger(LecteurPrincipal.class);
    private static final int                        len           = 19;
    private              char                       separateur;
    private static final String                     JeuCaracteres = "UTF-8";
    private              Map<String, Ligne>         lignes;
    private              Map<String, TableauMarche> tms;
    private              Map<String, Arret>         arrets;
    private              Map<String, Transporteur>  transporteurs;
    private              int                        ligneNumber;
	private              String                     logFileName; 
	private              IIdentificationManager     identificationManager;
	public  static       int                        counter;
	private              DriverManagerDataSource    managerDataSource;
	
	public List<ILectureEchange> getLecturesEchange() {
		Connection connexion = null;
		try {
			Class.forName(managerDataSource.getDriverClassName());
			Properties props = new Properties();
			props.setProperty("user",managerDataSource.getUsername());
			props.setProperty("password",managerDataSource.getPassword());
			props.setProperty("allowEncodingChanges","true");
			connexion = DriverManager.getConnection(managerDataSource.getUrl(), props);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
			
		List<ILectureEchange> lectureEchanges = new ArrayList<ILectureEchange>();
		Reseau                reseau          = new Reseau();
		counter       = 1;
		transporteurs = new HashMap<String, Transporteur>();
		//((IdentificationManager)identificationManager).setSystemId("PEGASE");
		reseau.setName("Reseau de Transport Allier");
		reseau.setRegistrationNumber("RESEAU_ALLIER");
		reseau.setDescription("Reseau de Transport Allier : Donnees PEGASE");
		reseau.setCreationTime(new Date());
		reseau.setVersionDate(new Date());
		reseau.setObjectId(identificationManager.getIdFonctionnel("PtNetwork", "NEW_"+String.valueOf(counter++)));
		reseau.setObjectVersion(1);
		for (Ligne lig : lignes.values()) {
			LectureEchange lectureEchange = new LectureEchange();
			Transporteur   transporteur   = transporteurs.get(lig.getCodeItineraire());
			if (transporteur == null) {
				transporteur = new Transporteur();
				transporteur.setName(lig.getCodeItineraire().substring(0, lig.getCodeItineraire().length()-3));
				transporteur.setRegistrationNumber(lig.getShortName());
				transporteur.setShortName(lig.getShortName());
				transporteur.setOrganisationalUnit("Transdev");
				//transporteur.setCode("cleCodePostal");
				//transporteur.setPhone("cleTelephone");
				//transporteur.setFax("cleFax");
				//transporteur.setEmail("cleEmail");
				transporteur.setObjectId(identificationManager.getIdFonctionnel("Company", "NEW_"+String.valueOf(counter++)));
				transporteur.setObjectVersion(1);
				transporteur.setCreationTime(new Date());
				transporteurs.put(lig.getCodeItineraire(), transporteur);
			}
			lectureEchange.setTransporteur(transporteur);
			lectureEchange.setReseau(reseau);
			lectureEchange.setLigne(lig.getLigne());
			lectureEchange.setTableauxMarche(lig.getTableauxMarche());
			lectureEchange.setZonesCommerciales(lig.getZonesCommerciales(connexion));
			lectureEchange.setArretsPhysiques(lig.getArretsPhysiques());
			lectureEchange.setZoneParenteParObjectId(lig.getZoneParenteParObjectId());
			lectureEchange.setObjectIdZonesGeneriques(lig.getObjectIdZonesGeneriques());
			lectureEchange.setItineraires(lig.getChouetteItineraires());
			lectureEchange.setMissions(lig.getMissions());
			lectureEchange.setCourses(lig.getCourses());
			lectureEchange.setArrets(lig.getArretsItineraires());
			lectureEchange.setItineraireParArret(lig.getItineraireParArret());
			lectureEchange.setHoraires(lig.getHoraires());
			
			//lectureEchange.setZonesPlaces(zonesPlaces);
			//lectureEchange.setCorrespondances(correspondances);
			//lectureEchange.setInterdictionTraficLocal(interdictionsTraficLocal);
			//lectureEchange.setPhysiquesParITLId(physiquesParITLId);
			
		    lectureEchanges.add(lectureEchange);
		}
		return lectureEchanges;
	}
	
	public void lire(String nom) {
		lireCheminFichier(getCheminFichier(nom));
	}
	
	private String getCheminFichier(String nom) {
		return repertoire + File.separator + nom;		
	}
	
	@SuppressWarnings("unchecked")
	public void lireCheminFichier(String chemin) {
		lignes = new HashMap<String, Ligne>();
	    tms = new HashMap<String, TableauMarche>();
	    arrets = new HashMap<String, Arret>();
	    logger.debug("LECTURE DE DONNEES CSV : "+chemin);
		try {
			InputStreamReader 	inputSR = new InputStreamReader(new FileInputStream(chemin), JeuCaracteres);
			CSVReader         lecteur = new CSVReader(inputSR, separateur);
			List<String[]>    contenu = lecteur.readAll();
			for (String[] text : contenu) {
				ligneNumber++;
				if (text.length != len)
					logger.error(text.length+" ");
				else
					for (int i = 0; i < len; i++)
						if ((text[i] == null) || (text[i].trim().length() == 0))
							logger.error(ligneNumber+" : CHAMPS N°"+i+" EST VIDE.");
				String regLigne             = text[ 0].trim();
				String dateValiditeLigne    = text[ 1].trim();
				String shortNameLigne       = text[ 2].trim();
				String nameLigne            = text[ 3].trim();
				String codeItineraire       = text[ 4].trim();
				String dateValiditeCourse   = text[ 5].trim();
				String shortNameItineraire  = text[ 6].trim();
				String nameItineraire       = text[ 7].trim();
				String serviceCode          = text[ 8].trim();
				String ordreArretDansCourse = text[ 9].trim();
				String serviceType          = text[10].trim();
				String codeArret            = text[11].trim();
				String heureDePassage       = text[12].trim();
				String shortNameArret       = text[13].trim();
				String nameArret            = text[14].trim();
				String joursValidite        = text[15].trim();
				String communeDeArret       = text[16].trim();
				@SuppressWarnings("unused")
				String tarif                = text[17].trim();
				String sensCourse           = text[18].trim();
				if (joursValidite.length() != 7)
					throw new TableauMarcheException("ERROR POUR JOURS_VALIDITE : "+joursValidite);//ligneNumber+"  : joursValidite.length() != 7");
				String        key;
				if (codeItineraire.length() >= 3+regLigne.length())
					key = codeItineraire.substring(0, 3+regLigne.length());
				else
					key = codeItineraire;
				Ligne         lig     = getLigne(regLigne, shortNameLigne, nameLigne, key);
				TableauMarche tm      = getTM(dateValiditeLigne, joursValidite);
				if (!dateValiditeLigne.equals(dateValiditeCourse))
					tm = getTM(dateValiditeCourse, joursValidite);
				Itineraire    it      = lig.getItineraire(codeItineraire, shortNameItineraire, nameItineraire);
				Course        co      = it.getCourse(serviceCode, serviceType, sensCourse);
				Arret         arret   = getArret(codeArret, shortNameArret, nameArret, communeDeArret);
				Horaire       ho      = co.getHoraire(arret, ordreArretDansCourse, heureDePassage);
				co.addTM(tm);
				/*
				logger.error(lig.getReg()+";"+tm.getDateDebut()+";"+lig.getShortName()+";"+lig.getName()+";"+
						it.getCode()+";"+dateValiditeCourse+";"+it.getShortName()+";"+it.getName()+";"+
						co.getServiceCode()+";"+ho.getOrdre()+";"+co.getServiceType()+";"+arret.getCode()+";"+ho.getHeure()+";"+
						arret.getShortName()+";"+arret.getName()+";"+tm.getJoursApplication()+";"+arret.getCommune()+";"+
						tarif+";"+co.getSens());
				 */
			}
			// FUSION DES COURSES EQUIVALENTES :
			for (Ligne lig : lignes.values()) {
				for (Itineraire it : lig.getItineraires().values()) {
					Map<Course, List<Course>> coursesAFusionner = new HashMap<Course, List<Course>>();
					int size = it.getCourses().values().size();
					Course[] courses = it.getCourses().values().toArray(new Course[size]);
					for (int i = 0; i < size; i++) {
						for (int j = i+1; j < size; j++) {
							Course co1 = courses[i];
							Course co2 = courses[j];
							Set<Course> dejaVu = new HashSet<Course>();
							Collection<Horaire> horaires1 = co1.getHoraires().values();
							Collection<Horaire> horaires2 = co2.getHoraires().values();
							if (horaires1.size() != horaires2.size())
								;//logger.error("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
							boolean areEquals = true;
							for (Horaire ho1 : horaires1) {
								for (Horaire ho2 : horaires2)
									if (ho1.getOrdre() == ho2.getOrdre())
										if (ho1.getArret() != ho2.getArret())
											;//logger.error("YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY LI : "+lig.getReg()+". IT : "+it.getCode()+". CO : "+co1.getServiceCode()+" .. "+co2.getServiceCode()+
											//		". AR : "+ho1.getArret().getCode()+" .. "+ho2.getArret().getCode()+".");
										else
											if (!ho1.getHeure().equals(ho2.getHeure()))
												areEquals = false;
							}
							if (areEquals && dejaVu.add(co1)) {
								dejaVu.add(co2);
								if (coursesAFusionner.get(co1) == null)
									coursesAFusionner.put(co1, new ArrayList<Course>());
								coursesAFusionner.get(co1).add(co2);
							}
						}
					}
					//FUSIONNER LES COURSES...
					for (Course co1 : coursesAFusionner.keySet()) {
						List<Course> coursesADetruire = coursesAFusionner.get(co1);
						co1.setServiceType(co1.getServiceType()+"_"+co1.getServiceCode());
						for (Course co2 : coursesADetruire) {
							co1.addTMs(co2.getTMs());
							co1.setServiceType(co1.getServiceType()+"_"+co2.getServiceCode());
						}
						it.removeCourses(coursesADetruire);
					}
				}
			}
			/*
			for (Ligne lig : lignes.values()) {
				logger.error("LIGNE : "+lig.getReg()+". NOM : "+lig.getName()+". NOM PUBLIC : "+lig.getShortName()+".");
				for (Itineraire it : lig.getItineraires().values()) {
					logger.error("\tITINERAIRE : "+it.getCode()+". NOM : "+it.getName()+". NOM PUBLIC : "+it.getShortName()+".");
					for (Course co : it.getCourses().values()) {
						logger.error("\t\tCOURSE : "+co.getServiceCode()+". SENS : "+co.getSens());
						for (TableauMarche tm : co.getTMs())
							logger.error("\t\t\tCALENDRIER : "+tm.getDateDebut()+" TO "+tm.getDateFin()+". JOURS D4APPLICATIONS : "+tm.getJoursApplication());
						for (Horaire ho : co.getHoraires().values())
							logger.error("\t\t\tHORAIRE : "+ho.increment+". HEURE DE PASSAGE : "+ho.getHeure()+". AT : "+ho.getArret().getCode()+". NAME : "+ho.getArret().getName());
					}
				}
			}
			*/
		}
		catch(Throwable e) {
			String msg = "LIGNE NUMERO "+ligneNumber+" : "+e.getMessage();
			logger.error(msg);
			throw new RuntimeException(msg);
		}
	}
	
	private Ligne getLigne(String reg, String shortName, String name, String codeItineraire) throws LigneException {
		Ligne ligne = lignes.get(codeItineraire);
		if (ligne != null) {
			if (!ligne.getShortName().equals(shortName))
				throw new LigneException("SHORTNAME MUST BE \""+ligne.getShortName()+"\" AND NOT \""+shortName+"\".");
			if (!ligne.getName().equals(name))
				throw new LigneException("NAME MUST BE \""+ligne.getName()+"\" AND NOT \""+name+"\".");
			return ligne;
		}
		ligne = new Ligne(identificationManager, reg, shortName, name, codeItineraire);
		lignes.put(codeItineraire, ligne);
		return ligne;
	}
	
	private TableauMarche getTM(String dateV, String joursAppli) {
		TableauMarche tm = tms.get(dateV+joursAppli);
		if (tm != null)
			return tm;
		try {
			tm = new TableauMarche(identificationManager, dateV, joursAppli);
		}
		catch(Exception e) {
		}
		tms.put(dateV+joursAppli, tm);
		return tm;
	}
	
	private Arret getArret(String code, String shortName, String name, String commune) throws ArretException {
		Arret arret = arrets.get(code);
		if (arret != null) {
			if (!arret.getShortName().equals(shortName))
				throw new ArretException("ERREUR POUR ARRET SHORTNAME : "+shortName + " : "+arret.getShortName());
			if (!arret.getName().equals(name))
				throw new ArretException("ERREUR POUR ARRET NAME : "+name + " : "+arret.getName());
			if (!arret.getCommune().equals(commune))
				throw new ArretException("ERREUR POUR ARRET COMMUNE : "+commune + " : "+arret.getCommune());
			return arret;
		}
		arret = new Arret(identificationManager, code, shortName, name, commune);
		arrets.put(code, arret);
		return arret;
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
	
	public int getLigneNumber() {
		return ligneNumber;
	}
	
	public void setLigneNumber(int ligneNumber) {
		this.ligneNumber = ligneNumber;
	}
	
	public String getLogFileName() {
		return logFileName;
	}
	
	public void setLogFileName(String logFileName) {
		this.logFileName = logFileName;
	}
	
	public IIdentificationManager getIdentificationManager() {
		return identificationManager;
	}
	
	public void setIdentificationManager(IIdentificationManager identificationManager) {
		this.identificationManager = identificationManager;
	}
	
	public void setManagerDataSource(DriverManagerDataSource managerDataSource) {
		this.managerDataSource = managerDataSource;
	}
}
