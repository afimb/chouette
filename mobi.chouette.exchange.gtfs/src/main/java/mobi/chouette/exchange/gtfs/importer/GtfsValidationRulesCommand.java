package mobi.chouette.exchange.gtfs.importer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.naming.InitialContext;

import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.validation.report.CheckPoint;
import mobi.chouette.exchange.validation.report.ValidationReport;

public class GtfsValidationRulesCommand implements Command, Constant {

	public static final String COMMAND = "GtfsValidationRulesCommand";

	@Override
	public boolean execute(Context context) throws Exception {
		ValidationReport validationReport = (ValidationReport)context.get(VALIDATION_REPORT);
		validationReport.setCheckPoints(checkPoints());
		return SUCCESS;
	}
	
	private List<CheckPoint> checkPoints() {
		List<CheckPoint> checkPoints = new ArrayList<CheckPoint>();
		checkPoints.addAll(csvCheckPoints());
		checkPoints.addAll(commonCheckPoints());
		checkPoints.addAll(agencyCheckPoints());
		checkPoints.addAll(stopCheckPoints());
		checkPoints.addAll(routeCheckPoints());
		checkPoints.addAll(tripCheckPoints());
		checkPoints.addAll(stopTimeCheckPoints());
		checkPoints.addAll(calendarCheckPoints());
		checkPoints.addAll(calendarDateCheckPoints());
		checkPoints.addAll(frequencyCheckPoints());
		checkPoints.addAll(transferCheckPoints());
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
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_CSV_8, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_CSV_9, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		return checkPoints;
	}

	private Collection<? extends CheckPoint> commonCheckPoints() {
		List<CheckPoint> checkPoints = new ArrayList<CheckPoint>();
		checkPoints.add(new CheckPoint(GTFS_2_GTFS_Common_1, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(GTFS_2_GTFS_Common_2, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(GTFS_2_GTFS_Common_3, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		return checkPoints;
	}
	
	private Collection<? extends CheckPoint> agencyCheckPoints() {
		List<CheckPoint> checkPoints = new ArrayList<CheckPoint>();
		checkPoints.addAll(agencyCheckPoints1());
		checkPoints.addAll(agencyCheckPoints2());
		return checkPoints;
	}
	
	private Collection<? extends CheckPoint> agencyCheckPoints1() {
		List<CheckPoint> checkPoints = new ArrayList<CheckPoint>();
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Agency_1, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Agency_2, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Agency_3, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Agency_4, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Agency_5, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Agency_6, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Agency_7, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Agency_8, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Agency_9, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Agency_10, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		return checkPoints;
	}
	
	private Collection<? extends CheckPoint> agencyCheckPoints2() {
		List<CheckPoint> checkPoints = new ArrayList<CheckPoint>();
		checkPoints.add(new CheckPoint(GTFS_2_GTFS_Agency_1, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(GTFS_2_GTFS_Agency_2, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		return checkPoints;
	}
	
	private Collection<? extends CheckPoint> stopCheckPoints() {
		List<CheckPoint> checkPoints = new ArrayList<CheckPoint>();
		checkPoints.addAll(stopCheckPoints1());
		checkPoints.addAll(stopCheckPoints2());
		return checkPoints;
	}
	
	private Collection<? extends CheckPoint> stopCheckPoints1() {
		List<CheckPoint> checkPoints = new ArrayList<CheckPoint>();
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Stop_1, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Stop_2, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Stop_3, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Stop_4, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Stop_5, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Stop_6, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Stop_7, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Stop_8, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Stop_9, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Stop_10, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Stop_11, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		return checkPoints;
	}
	
	private Collection<? extends CheckPoint> stopCheckPoints2() {
		List<CheckPoint> checkPoints = new ArrayList<CheckPoint>();
		checkPoints.add(new CheckPoint(GTFS_2_GTFS_Stop_1, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_2_GTFS_Stop_2, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_2_GTFS_Stop_3, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(GTFS_2_GTFS_Stop_4, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_2_GTFS_Stop_5, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(GTFS_2_GTFS_Stop_6, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		return checkPoints;
	}
	
	private Collection<? extends CheckPoint> routeCheckPoints() {
		List<CheckPoint> checkPoints = new ArrayList<CheckPoint>();
		checkPoints.addAll(routeCheckPoints1());
		checkPoints.addAll(routeCheckPoints2());
		return checkPoints;
	}
	
	private Collection<? extends CheckPoint> routeCheckPoints1() {
		List<CheckPoint> checkPoints = new ArrayList<CheckPoint>();
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Route_1, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Route_2, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Route_3, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Route_4, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Route_5, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Route_6, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Route_7, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Route_8, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Route_9, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Route_10, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		return checkPoints;
	}
	
	private Collection<? extends CheckPoint> routeCheckPoints2() {
		List<CheckPoint> checkPoints = new ArrayList<CheckPoint>();
		checkPoints.add(new CheckPoint(GTFS_2_GTFS_Route_1, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(GTFS_2_GTFS_Route_2, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_2_GTFS_Route_3, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_2_GTFS_Route_4, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_2_GTFS_Route_5, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(GTFS_2_GTFS_Route_6, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(GTFS_2_GTFS_Route_8, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(GTFS_2_GTFS_Route_9, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(GTFS_2_GTFS_Route_10, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_2_GTFS_Route_11, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(GTFS_2_GTFS_Route_12, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		return checkPoints;
	}
	
	private Collection<? extends CheckPoint> tripCheckPoints() {
		List<CheckPoint> checkPoints = new ArrayList<CheckPoint>();
		checkPoints.addAll(tripCheckPoints1());
		checkPoints.addAll(tripCheckPoints2());
		return checkPoints;
	}
	
	private Collection<? extends CheckPoint> tripCheckPoints1() {
		List<CheckPoint> checkPoints = new ArrayList<CheckPoint>();
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Trip_1, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Trip_2, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Trip_3, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Trip_4, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Trip_5, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Trip_6, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Trip_7, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Trip_8, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		return checkPoints;
	}
	
	private Collection<? extends CheckPoint> tripCheckPoints2() {
		List<CheckPoint> checkPoints = new ArrayList<CheckPoint>();
		checkPoints.add(new CheckPoint(GTFS_2_GTFS_Trip_1, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_2_GTFS_Trip_2, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_2_GTFS_Trip_3, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(GTFS_2_GTFS_Trip_4, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(GTFS_2_GTFS_Trip_5, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_2_GTFS_Trip_6, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_2_GTFS_Trip_7, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		return checkPoints;
	}
	
	private Collection<? extends CheckPoint> stopTimeCheckPoints() {
		List<CheckPoint> checkPoints = new ArrayList<CheckPoint>();
		checkPoints.addAll(stopTimeCheckPoints1());
		checkPoints.addAll(stopTimeCheckPoints2());
		return checkPoints;
	}
	
	private Collection<? extends CheckPoint> stopTimeCheckPoints1() {
		List<CheckPoint> checkPoints = new ArrayList<CheckPoint>();
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_StopTime_1, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_StopTime_2, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_StopTime_3, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_StopTime_4, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_StopTime_5, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_StopTime_6, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_StopTime_7, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_StopTime_8, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_StopTime_9, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_StopTime_10, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_StopTime_11, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_StopTime_12, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		return checkPoints;
	}
	
	private Collection<? extends CheckPoint> stopTimeCheckPoints2() {
		List<CheckPoint> checkPoints = new ArrayList<CheckPoint>();
		checkPoints.add(new CheckPoint(GTFS_2_GTFS_StopTime_1, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_2_GTFS_StopTime_2, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_2_GTFS_StopTime_3, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_2_GTFS_StopTime_4, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_2_GTFS_StopTime_5, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_2_GTFS_StopTime_6, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_2_GTFS_StopTime_7, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(GTFS_2_GTFS_StopTime_8, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_2_GTFS_StopTime_9, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		return checkPoints;
	}
	
	private Collection<? extends CheckPoint> calendarCheckPoints() {
		List<CheckPoint> checkPoints = new ArrayList<CheckPoint>();
		checkPoints.addAll(calendarCheckPoints1());
		checkPoints.addAll(calendarCheckPoints2());
		return checkPoints;
	}
	
	private Collection<? extends CheckPoint> calendarCheckPoints1() {
		List<CheckPoint> checkPoints = new ArrayList<CheckPoint>();
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Calendar_1, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Calendar_2, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Calendar_3, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Calendar_4, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Calendar_5, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Calendar_6, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Calendar_7, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Calendar_8, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Calendar_9, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Calendar_10, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Calendar_11, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Calendar_12, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Calendar_13, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Calendar_14, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		return checkPoints;
	}
	
	private Collection<? extends CheckPoint> calendarCheckPoints2() {
		List<CheckPoint> checkPoints = new ArrayList<CheckPoint>();
		checkPoints.add(new CheckPoint(GTFS_2_GTFS_Calendar_1, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_2_GTFS_Calendar_2, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(GTFS_2_GTFS_Calendar_3, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(GTFS_2_GTFS_Calendar_4, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(GTFS_2_GTFS_Calendar_5, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(GTFS_2_GTFS_Calendar_6, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		return checkPoints;
	}
	
	private Collection<? extends CheckPoint> calendarDateCheckPoints() {
		List<CheckPoint> checkPoints = new ArrayList<CheckPoint>();
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_CalendarDate_1, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_CalendarDate_2, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_CalendarDate_3, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_CalendarDate_4, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_CalendarDate_5, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_CalendarDate_6, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_CalendarDate_7, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		return checkPoints;
	}
	
	private Collection<? extends CheckPoint> frequencyCheckPoints() {
		List<CheckPoint> checkPoints = new ArrayList<CheckPoint>();
		checkPoints.addAll(frequencyCheckPoints1());
		checkPoints.addAll(frequencyCheckPoints2());
		return checkPoints;
	}
	
	private Collection<? extends CheckPoint> frequencyCheckPoints1() {
		List<CheckPoint> checkPoints = new ArrayList<CheckPoint>();
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Frequency_1, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Frequency_2, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Frequency_3, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Frequency_4, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Frequency_5, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Frequency_6, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Frequency_7, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		return checkPoints;
	}
	
	private Collection<? extends CheckPoint> frequencyCheckPoints2() {
		List<CheckPoint> checkPoints = new ArrayList<CheckPoint>();
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Frequency_1, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Frequency_2, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Frequency_3, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Frequency_4, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Frequency_5, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Frequency_6, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Frequency_7, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		return checkPoints;
	}
	
	private Collection<? extends CheckPoint> transferCheckPoints() {
		List<CheckPoint> checkPoints = new ArrayList<CheckPoint>();
		checkPoints.addAll(transferCheckPoints1());
		checkPoints.addAll(transferCheckPoints2());
		return checkPoints;
	}
	
	private Collection<? extends CheckPoint> transferCheckPoints1() {
		List<CheckPoint> checkPoints = new ArrayList<CheckPoint>();
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Transfer_1, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Transfer_2, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Transfer_3, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Transfer_4, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Transfer_5, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_1_GTFS_Transfer_6, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		return checkPoints;
	}
	
	private Collection<? extends CheckPoint> transferCheckPoints2() {
		List<CheckPoint> checkPoints = new ArrayList<CheckPoint>();
		checkPoints.add(new CheckPoint(GTFS_2_GTFS_Transfer_1, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		checkPoints.add(new CheckPoint(GTFS_2_GTFS_Transfer_2, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
		return checkPoints;
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = new GtfsValidationRulesCommand();
			return result;
		}
	}

	static {
		CommandFactory.factories.put(GtfsValidationRulesCommand.class.getName(), new DefaultCommandFactory());
	}

}
