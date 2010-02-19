package fr.certu.chouette.service.importateur.multilignes.hastus.impl;

import fr.certu.chouette.modele.Course;
import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.modele.TableauMarche;
import fr.certu.chouette.service.identification.IIdentificationManager;
import fr.certu.chouette.service.importateur.multilignes.hastus.ILecteurCourse;
import fr.certu.chouette.service.importateur.multilignes.hastus.commun.CodeIncident;
import fr.certu.chouette.service.importateur.multilignes.hastus.commun.ServiceException;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.apache.log4j.Logger;

public class LecteurCourse implements ILecteurCourse {
	
	private static final Logger                            logger                  = Logger.getLogger(LecteurCourse.class);
	private              int                               counter;
	private              IIdentificationManager            identificationManager;  // 
	private              String                            cleCode;                // "05"
	private              String                            hastusCode;             // "HastusTUR"
	private              String                            special;                // "SPECIAL"
	private              String                            space;                  // "SPACE"
	private              Map<String, Ligne>                ligneParRegistration;   /// Ligne par registration (LecteurLigne)
	private              Map<Course, Ligne>                ligneParCourse;         /// Ligne par Course
	private              Map<String, Course>               courseParNom;           /// 
	private              Map<String, TableauMarche>        tableauMarcheParValeur; /// 
	private              Map<Ligne,List<TableauMarche>>    tableauxMarchesParLigne;/// 
	private static final SimpleDateFormat                  sdf1                    = new SimpleDateFormat("ddMMyy");
	private static final SimpleDateFormat                  sdf2                    = new SimpleDateFormat("dd/MM/yyyy");
	private static final SimpleDateFormat                  sdf3                    = new SimpleDateFormat("yyyy-MM-dd");
	
	public boolean isTitreReconnu(String[] ligneCSV) {
		if ((ligneCSV == null) || (ligneCSV.length == 0))
			return false;
		return ligneCSV[0].equals(getCleCode());
	}
	
	public void lire(String[] ligneCSV) {
		if ((ligneCSV == null) || (ligneCSV.length == 0))
			return;
		if (ligneCSV.length != 15)
			throw new ServiceException(CodeIncident.INVALIDE_LONGUEUR_COURSE, "La longeur des lignes dans \"Course\" est 15 : "+ligneCSV.length);
		logger.debug("CREATION DE COURSES "+ligneCSV[1].trim());
		Course course = new Course();
		course.setObjectVersion(1);
		course.setCreationTime(new Date(System.currentTimeMillis()));
		String ligneRegistration = "";
		if ((ligneCSV[13] != null) && (ligneCSV[13].trim().length() > 0) && (ligneCSV[14] != null) && (ligneCSV[14].trim().length() > 0)) {
			course.setObjectId(identificationManager.getIdFonctionnel(hastusCode, "VehicleJourney", toTrident(ligneCSV[14].trim()+"-"+ligneCSV[13].trim())));
			course.setPublishedJourneyName(ligneCSV[14].trim()+"-"+ligneCSV[13].trim());
			course.setComment("Course n° "+ligneCSV[13].trim()+" du parcours "+ligneCSV[14].trim());
			courseParNom.put(ligneCSV[14].trim()+"-"+ligneCSV[13].trim(), course);
			int index = ligneCSV[14].trim().indexOf('-');
			if (index <= 0)
				throw new ServiceException(CodeIncident.INVALIDE_NAME_COURSE, "Le \"Nom\" de la \"Course\" doit contenir \"-\" : "+ligneCSV[14].trim());
			ligneRegistration = ligneCSV[14].trim().substring(0, index);
			if (ligneParRegistration.get(ligneRegistration) == null)
				throw new ServiceException(CodeIncident.INVALIDE_NAME_COURSE, "Le \"Nom\" de la \"Course\" doit commencer par le nom d'une ligne suivi de \"-\" : "+ligneCSV[14].trim());
			ligneParCourse.put(course, ligneParRegistration.get(ligneRegistration));
			if (tableauxMarchesParLigne.get(ligneParRegistration.get(ligneRegistration)) == null)
				tableauxMarchesParLigne.put(ligneParRegistration.get(ligneRegistration), new ArrayList<TableauMarche>());
			try {
				int courseNumber1 = Integer.parseInt(ligneCSV[14].trim().substring(index+1));
				int courseNumber2 = Integer.parseInt(ligneCSV[13].trim());
				course.setNumber(courseNumber1*courseNumber2);
			}
			catch(NumberFormatException e) {
				throw new ServiceException(CodeIncident.INVALIDE_NAME_COURSE, "Le \"Nom\" de la \"Course\" doit être de la forme <ligne registration number>-<course number> : "+ligneCSV[1].trim());
			}
		}
		else
			throw new ServiceException(CodeIncident.NULL_NAME_COURSE, "Le \"Nom\" de la \"Course\" ne peut être null.");
		if ((ligneCSV[10] == null) || (ligneCSV[10].trim().length() == 0))
			throw new ServiceException(CodeIncident.NULL_JOURSVALIDITE_COURSE, "Les jours de validitês de \"TableauMarche\" ne doit pas être null.");
		if ((ligneCSV[11] == null) || (ligneCSV[11].trim().length() == 0))
			throw new ServiceException(CodeIncident.NULL_DATEDEBUT_COURSE, "La date de dêbut de \"TableauMarche\" ne doit pas être null.");
		if ((ligneCSV[12] == null) || (ligneCSV[12].trim().length() == 0))
			throw new ServiceException(CodeIncident.NULL_DATEFIN_COURSE, "La date de fin de \"TableauMarche\" ne doit pas être null.");
		
		String joursValides = ligneCSV[10].trim();
		if (!joursValides.matches("[01]+"))
			throw new ServiceException(CodeIncident.INVALIDE_JOURSVALIDITE_COURSE, "Le codage des jours de validitês de \"TableauMarche\" est une suite de \'0\' et de \'1\' : "+joursValides);
		BigInteger bigInt = new BigInteger(joursValides, 2);
		Date debut = null;
		Date fin = null;
		try {
			debut = sdf2.parse(ligneCSV[11].trim());
		}
		catch(ParseException pe) {
			throw new ServiceException(CodeIncident.INVALIDE_DATEDEBUT_COURSE, "La date de début de course est invalide : "+ligneCSV[11]);
		}
		try {
			fin = sdf2.parse(ligneCSV[12].trim());
		}
		catch(ParseException pe) {
			throw new ServiceException(CodeIncident.INVALIDE_DATEFIN_COURSE, "La date de fin de course est invalide : "+ligneCSV[12]);
		}
		if (debut.after(fin))
			throw new ServiceException(CodeIncident.INVALIDE_DATES_COURSE, "La date de dêbut de \"TableauMarche\" doit être postêrieure ê la date de fin : "+ligneCSV[11]+" < "+ligneCSV[12]);
		String key = sdf1.format(debut)+bigInt.toString()+sdf1.format(fin);
		TableauMarche tableauMarche = tableauMarcheParValeur.get(key);
		if (tableauMarche == null) {
			long numberOfDays = (long)1 + (fin.getTime()/(24l*60l*60l*1000l) - debut.getTime()/(24l*60l*60l*1000l));
			if (joursValides.length() != (int)numberOfDays)
				throw new ServiceException(CodeIncident.INVALIDE_DATESJOURSVALIDITE_COURSE, "Le champs jours valides ("+joursValides+") n'est pas conforme aux dates de debut ("+ligneCSV[11].trim()+") est de fin ("+ligneCSV[12].trim()+") : "+(int)numberOfDays+" jours.");
			long time = debut.getTime();
			Date first = null;
			Date last = null;
			String value = null;
			tableauMarche = new TableauMarche();
			for (int i = 0; i < numberOfDays; i++) 
			{
				Date date = new Date(time);
				if (joursValides.charAt(i) == '1') 
				{
					if (first == null) 
					{
						first = new Date(time);
						value = "1";
					}
					last = new Date(time);
					tableauMarche.ajoutDate(date);
				}
				if (value != null)
					value += joursValides.charAt(i);
				time += (long)24*60*60*1000;
				
			}
			if (value == null)
				return; // TM vide
			tableauMarche.setObjectVersion(1);
			tableauMarche.setCreationTime(new Date(System.currentTimeMillis()));
			tableauMarcheParValeur.put(key, tableauMarche);
			int index = value.length()-1;
			while (value.charAt(index) == '0') 
			{
				value = value.substring(0, index);
				index = value.length()-1;
			}
			BigInteger bigInt2 = new BigInteger(value, 2);
			String firstDate = sdf1.format(first);
			String lastDate = sdf1.format(last);
			String key2 = firstDate+bigInt2.toString()+lastDate;
			tableauMarche.setObjectId(identificationManager.getIdFonctionnel(hastusCode, "Timetable", key2));
			tableauMarche.setComment("FROM "+sdf3.format(first)+" TO "+sdf3.format(last));
		}
		tableauMarche.addVehicleJourneyId(course.getObjectId());
		if (!tableauxMarchesParLigne.get(ligneParRegistration.get(ligneRegistration)).contains(tableauMarche))
			tableauxMarchesParLigne.get(ligneParRegistration.get(ligneRegistration)).add(tableauMarche);
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
		ligneParCourse = new HashMap<Course, Ligne>();
		tableauMarcheParValeur = new HashMap<String, TableauMarche>();
		courseParNom = new HashMap<String, Course>();
		tableauxMarchesParLigne = new HashMap<Ligne, List<TableauMarche>>();
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
	
	public Map<Course, Ligne> getLigneParCourse() {
		return ligneParCourse;
	}
	
	public Map<String, Course> getCourseParNom() {
		return courseParNom;
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

	public List<TableauMarche> getTableauxMarches(Ligne ligne) {
		return tableauxMarchesParLigne.get(ligne);
	}
}
