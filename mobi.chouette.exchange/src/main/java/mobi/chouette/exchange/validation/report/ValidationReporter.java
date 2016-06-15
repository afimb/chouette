package mobi.chouette.exchange.validation.report;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.model.DataLocation;
import mobi.chouette.exchange.validation.report.CheckPointReport.RESULT;

public interface ValidationReporter {
	void addItemToValidationReport(Context context, String prefix, String name, int count, String... severities);
	void addCheckPointReportError(Context context, String checkPointName, DataLocation location, String value, RESULT result);
	void addCheckPointReportError(Context context, String checkPointName, String detail, DataLocation location, String value, RESULT result);
	void addCheckPointReportError(Context context, String checkPointName, DataLocation location, String value, String refValue, RESULT result);
	void addCheckPointReportError(Context context, String checkPointName, DataLocation[] locations, String value, RESULT result);
	void reportSuccess(Context context, String checkpointName, String filenameInfo);
	void prepareCheckPointReport(Context context, String checkPointKey);
	void updateCheckPointReportState(CheckPointReport checkPoint, CheckPointReport.RESULT state);
	CheckPointReport findCheckPointReportByName(Context context, String name);
	void clearValidationReport(Context context);
}
