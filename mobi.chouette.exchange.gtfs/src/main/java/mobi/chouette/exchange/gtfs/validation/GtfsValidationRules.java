package mobi.chouette.exchange.gtfs.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import mobi.chouette.exchange.gtfs.importer.GtfsImportParameters;
import mobi.chouette.exchange.validation.report.CheckPoint;

public class GtfsValidationRules implements Constant {
	
	public List<CheckPoint> checkPoints(GtfsImportParameters parameters) {
		List<CheckPoint> checkPoints = new ArrayList<CheckPoint>();		
		checkPoints.addAll(csvCheckPoints());
		checkPoints.addAll(commonCheckPoints());
		return checkPoints;
	}

	private Collection<? extends CheckPoint> csvCheckPoints() {
		List<CheckPoint> checkPoints = new ArrayList<CheckPoint>();
		
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_CSV_1, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_CSV_2, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_CSV_3, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_CSV_4, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_CSV_5, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_CSV_6, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_CSV_7, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		return checkPoints;
	}

	private Collection<? extends CheckPoint> commonCheckPoints() {
		List<CheckPoint> checkPoints = new ArrayList<CheckPoint>();
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Common_1   , CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Common_1_1 , CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Common_1_2 , CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		// TODO. A faire apparaitre seulement si des fichiers parasites
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Common_1_3 , CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));

		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Common_2   , CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Common_2_1 , CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Common_2_2 , CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Common_3   , CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Common_3_1 , CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Common_3_2 , CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Common_3_3 , CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Common_3_4 , CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Common_4   , CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Common_4_1 , CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		// checkPoints.add(new CheckPoint(GTFS_1_GTFS_Common_4_2 , CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Common_4_3 , CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Common_4_4 , CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		// checkPoints.add(new CheckPoint(GTFS_1_GTFS_Common_4_5 , CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Common_4_6 , CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		// checkPoints.add(new CheckPoint(GTFS_1_GTFS_Common_4_7 , CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));

		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Common_5   , CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));

		checkPoints.add(new CheckPoint(GTFS_2_GTFS_Common_1   , CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_2_GTFS_Common_2   , CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(GTFS_2_GTFS_Common_3   , CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(GTFS_2_GTFS_Common_4   , CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_2_GTFS_Common_5   , CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(GTFS_2_GTFS_Common_6   , CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_2_GTFS_Common_7   , CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(GTFS_2_GTFS_Common_8   , CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_2_GTFS_Stop_4   , CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		return checkPoints;
	}
}
