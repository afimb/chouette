package unit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import chouette.schema.ChouettePTNetworkTypeType;
import chouette.schema.Period;
import chouette.schema.Timetable;

import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.manager.SingletonManager;
import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.modele.TableauMarche;
import fr.certu.chouette.service.commun.ServiceException;
import fr.certu.chouette.service.database.ILigneManager;
import fr.certu.chouette.service.database.IPositionGeographiqueManager;
import fr.certu.chouette.service.exportateur.impl.MassiveExportManager;
import fr.certu.chouette.service.fichier.IImportateur;
import fr.certu.chouette.service.importateur.multilignes.ILecteurPrincipal;
import fr.certu.chouette.service.xml.ILecteurFichierXML;

public class TableauMarcheTest {
	private static final Calendar cal = Calendar.getInstance();
	
	private Timetable dateTimetable;
	private Timetable periodTimetable;
	private Timetable mixedTimetable;
	
	private Date firstDateInDateTimetable;
	private Date firstDateInPeriodTimetable;
	
	private Date lastDateInDateTimetable;
	private Date lastDateInPeriodTimetable;
	
	@BeforeSuite
	public void initialisation() throws Exception {
		mixedTimetable = new Timetable();
		
		dateTimetable = new Timetable();
		firstDateInDateTimetable = cal.getTime();
		for(int i=0 ; i< 5 ; i++){
			dateTimetable.addCalendarDay(new org.exolab.castor.types.Date(cal.getTime()));
			mixedTimetable.addCalendarDay(new org.exolab.castor.types.Date(cal.getTime()));
			lastDateInDateTimetable = cal.getTime();
			cal.add(Calendar.DATE, 2);
		}
		
		
		periodTimetable = new Timetable();
		firstDateInPeriodTimetable = cal.getTime();
		for(int i=0 ; i< 5 ; i++){
			Period p = new Period();
			p.setStartOfPeriod(new org.exolab.castor.types.Date(cal.getTime()));
			cal.add(Calendar.DATE, 2);
			p.setEndOfPeriod(new org.exolab.castor.types.Date(cal.getTime()));
			lastDateInPeriodTimetable = cal.getTime();
			cal.add(Calendar.DATE, 2);
			periodTimetable.addPeriod(p);
			mixedTimetable.addPeriod(p);
		}
	}

	@Test(groups = "tests unitaires", description = "modèle tableau marche - isInPeriod")
	public void isInTmPeriod() {
		testTimetable(dateTimetable, firstDateInDateTimetable, lastDateInDateTimetable);
		testTimetable(periodTimetable, firstDateInPeriodTimetable, lastDateInPeriodTimetable);
		testTimetable(mixedTimetable, firstDateInDateTimetable, lastDateInPeriodTimetable);
	}
	
	private void testTimetable(Timetable timetable, Date timetableStartDate, Date timetableEndDate){
		TableauMarche tm = new TableauMarche();
		tm.setTimetable(timetable);
		
		Date periodStartDate = null;
		Date periodEndDate = null;
		
		//case 1 : period totally before
		cal.setTime(timetableStartDate);
		cal.add(Calendar.DATE, -10);
		periodStartDate = cal.getTime();
		cal.add(Calendar.DATE, 5);
		periodEndDate = cal.getTime();
		assert !tm.isTimetableInPeriod(periodStartDate, periodEndDate);
		
		//case 2 : period over timetableStartDate
		cal.setTime(timetableStartDate);
		cal.add(Calendar.DATE, -5);
		periodStartDate = cal.getTime();
		cal.setTime(timetableStartDate);
		cal.add(Calendar.DATE, 5);
		periodEndDate = cal.getTime();
		assert tm.isTimetableInPeriod(periodStartDate, periodEndDate);
		
		//case 3 : period inside timetable
		cal.setTime(timetableStartDate);
		cal.add(Calendar.DATE, 2);
		periodStartDate = cal.getTime();
		cal.setTime(timetableEndDate);
		cal.add(Calendar.DATE, -2);
		periodEndDate = cal.getTime();
		assert tm.isTimetableInPeriod(periodStartDate, periodEndDate);
		
		//case 4 : period over timetableEndDate
		cal.setTime(timetableEndDate);
		cal.add(Calendar.DATE, -5);
		periodStartDate = cal.getTime();
		cal.setTime(timetableEndDate);
		cal.add(Calendar.DATE, 5);
		periodEndDate = cal.getTime();
		assert tm.isTimetableInPeriod(periodStartDate, periodEndDate);
		
		//case 5 : period totally after timetable
		cal.setTime(timetableEndDate);
		cal.add(Calendar.DATE, 5);
		periodStartDate = cal.getTime();
		cal.add(Calendar.DATE, 5);
		periodEndDate = cal.getTime();
		assert !tm.isTimetableInPeriod(periodStartDate, periodEndDate);
		
		//case 6 : period over timetable
		cal.setTime(timetableStartDate);
		cal.add(Calendar.DATE, -2);
		periodStartDate = cal.getTime();
		cal.setTime(timetableEndDate);
		cal.add(Calendar.DATE, 2);
		periodEndDate = cal.getTime();
		assert tm.isTimetableInPeriod(periodStartDate, periodEndDate);
		
		//case 7 : periodStartDate before timetable / periodEndDate null
		cal.setTime(timetableStartDate);
		cal.add(Calendar.DATE, -5);
		periodStartDate = cal.getTime();
		periodEndDate = null;
		assert tm.isTimetableInPeriod(periodStartDate, periodEndDate);
		
		//case 8 : periodStartDate inside timetable / periodEndDate null
		cal.setTime(timetableStartDate);
		cal.add(Calendar.DATE, 5);
		periodStartDate = cal.getTime();
		periodEndDate = null;
		assert tm.isTimetableInPeriod(periodStartDate, periodEndDate);
		
		//case 9 : periodStartDate after timetable / periodEndDate null
		cal.setTime(timetableEndDate);
		cal.add(Calendar.DATE, 5);
		periodStartDate = cal.getTime();
		periodEndDate = null;
		assert !tm.isTimetableInPeriod(periodStartDate, periodEndDate);

		//case 10 : periodEndDate before timetable / periodStartDate null
		cal.setTime(timetableStartDate);
		cal.add(Calendar.DATE, -5);
		periodStartDate = null;
		periodEndDate = cal.getTime();
		assert !tm.isTimetableInPeriod(periodStartDate, periodEndDate);
		
		//case 11 : periodEndDate inside timetable / periodStartDate null
		cal.setTime(timetableStartDate);
		cal.add(Calendar.DATE, 5);
		periodStartDate = null;
		periodEndDate = cal.getTime();
		assert tm.isTimetableInPeriod(periodStartDate, periodEndDate);
		
		//case 12 : periodEndDate after timetable / periodStartDate null
		cal.setTime(timetableEndDate);
		cal.add(Calendar.DATE, 5);
		periodStartDate = null;
		periodEndDate = cal.getTime();
		assert tm.isTimetableInPeriod(periodStartDate, periodEndDate);
		
		//case 13 : periodStartDate and periodEndDate null
		assert tm.isTimetableInPeriod(null, null);
	}

}
