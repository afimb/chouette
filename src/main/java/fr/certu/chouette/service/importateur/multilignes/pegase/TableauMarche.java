package fr.certu.chouette.service.importateur.multilignes.pegase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import chouette.schema.types.DayTypeType;
import fr.certu.chouette.modele.Periode;
import fr.certu.chouette.service.identification.IIdentificationManager;

public class TableauMarche {
	
	private              Date                                   dateDebut;
	private              Date                                   dateFin;
	private              int                                    joursApplication;
	private              String                                 joursApplicationCode;
	private static final SimpleDateFormat                       sdf              = new SimpleDateFormat("yyyyMMdd");
	private              Set<DayTypeType>                       dayTypes         = new HashSet<DayTypeType>();
	private              IIdentificationManager                 identificationManager;
	private              fr.certu.chouette.modele.TableauMarche tableauMarche;
	
	public TableauMarche(IIdentificationManager identificationManager, String dateV, String joursAppli) throws TableauMarcheException, ParseException {
		this.identificationManager = identificationManager;
		joursApplicationCode = joursAppli;
		try {
			dateDebut = sdf.parse(dateV);
		}
		catch(ParseException e) {
			throw e;
		}
		long time = (long)1000 * (long)60 * (long)60 * (long)24 * (long)365;
		dateFin = new Date(dateDebut.getTime()+time);
		//////////////////////////// AJOUT ....
		try {
			dateDebut = sdf.parse("20090818");
			dateFin = sdf.parse("20090831");
		}
		catch(ParseException e) {
			throw e;
		}
		////////////////////////////////FIN AJOUT ....
		joursApplication = 0;
		if (joursAppli.charAt(0) == 'l') {
			joursApplication += 1 >> DayTypeType.MONDAY_TYPE;
			dayTypes.add(DayTypeType.MONDAY);
		}
		else
			if (joursAppli.charAt(0) != '-')
				throw new TableauMarcheException("ERROR POUR JOURS_APPLICATION joursAppli.charAt(0) DOIT ETRE l OU - : "+joursAppli.charAt(0));
		if (joursAppli.charAt(1) == 'm') {
			joursApplication += 1 >> DayTypeType.TUESDAY_TYPE;
			dayTypes.add(DayTypeType.TUESDAY);
		}
		else
			if (joursAppli.charAt(1) != '-')
				throw new TableauMarcheException("ERROR POUR JOURS_APPLICATION joursAppli.charAt(1) DOIT ETRE m OU - : "+joursAppli.charAt(1));
		if (joursAppli.charAt(2) == 'm') {
			joursApplication += 1 >> DayTypeType.WEDNESDAY_TYPE;
			dayTypes.add(DayTypeType.WEDNESDAY);
		}
		else
			if (joursAppli.charAt(2) != '-')
				throw new TableauMarcheException("ERROR POUR JOURS_APPLICATION joursAppli.charAt(2) DOIT ETRE m OU - : "+joursAppli.charAt(2));
		if (joursAppli.charAt(3) == 'j') {
			joursApplication += 1 >> DayTypeType.THURSDAY_TYPE;
			dayTypes.add(DayTypeType.THURSDAY);
		}
		else
			if (joursAppli.charAt(3) != '-')
				throw new TableauMarcheException("ERROR POUR JOURS_APPLICATION joursAppli.charAt(3) DOIT ETRE j OU - : "+joursAppli.charAt(3));
		if (joursAppli.charAt(4) == 'v') {
			joursApplication += 1 >> DayTypeType.FRIDAY_TYPE;
			dayTypes.add(DayTypeType.FRIDAY);
		}
		else
			if (joursAppli.charAt(4) != '-')
				throw new TableauMarcheException("ERROR POUR JOURS_APPLICATION joursAppli.charAt(4) DOIT ETRE v OU - : "+joursAppli.charAt(4));
		if (joursAppli.charAt(5) == 's') {
			joursApplication += 1 >> DayTypeType.SATURDAY_TYPE;
			dayTypes.add(DayTypeType.SATURDAY);
		}
		else
			if (joursAppli.charAt(5) != '-')
				throw new TableauMarcheException("ERROR POUR JOURS_APPLICATION joursAppli.charAt(5) DOIT ETRE s OU - : "+joursAppli.charAt(5));
		if (joursAppli.charAt(6) == 'd') {
			joursApplication += 1 >> DayTypeType.SUNDAY_TYPE;
			dayTypes.add(DayTypeType.SUNDAY);
		}
		else
			if (joursAppli.charAt(6) != '-')
				throw new TableauMarcheException("ERROR POUR JOURS_APPLICATION joursAppli.charAt(6) DOIT ETRE d OU - : "+joursAppli.charAt(6));
	}
	
	public String getDateDebut() {
		return sdf.format(dateDebut);
	}
	
	public String getDateFin() {
		return sdf.format(dateFin);
	}
	
	public String getJoursApplication() {
		return joursApplicationCode;
	}
	
	public fr.certu.chouette.modele.TableauMarche getTableauxMarche() {
		if (tableauMarche == null)
			tableauMarche();
		return tableauMarche;
	}
	
	private void tableauMarche() {
		tableauMarche = new fr.certu.chouette.modele.TableauMarche();
		tableauMarche.setComment("E09 : Dep ("+joursApplicationCode+") 01/09/09 - 01/09/10");
		tableauMarche.setCreationTime(new Date());
		tableauMarche.setDayTypes(dayTypes);
		tableauMarche.setObjectId(identificationManager.getIdFonctionnel("Timetable", "NEW_"+String.valueOf(LecteurPrincipal.counter++)));
		tableauMarche.setObjectVersion(1);
		Periode periode = new Periode();
		periode.setDebut(dateDebut);
		periode.setFin(dateFin);
		tableauMarche.ajoutPeriode(periode);
	}
}