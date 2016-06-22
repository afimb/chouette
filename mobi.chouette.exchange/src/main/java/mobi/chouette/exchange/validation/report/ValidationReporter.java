package mobi.chouette.exchange.validation.report;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.model.DataLocation;

public interface ValidationReporter {
	
	public enum RESULT {
		UNCHECK, OK, NOK
	};
		
	/**
	 * add a single checkpoint entry
	 * @param context
	 * @param key
	 * @param severity (W or E)
	 */
	void addItemToValidationReport(Context context, String key, String severity);
	/**
	 * Add one or more checkpoints with severity to validation report
	 * Note : you can add more checkpoints using severities arg (W, E, W, ...)
	 * @param context
	 * @param prefix
	 * @param name
	 * @param count
	 * @param severities
	 */
	void addItemToValidationReport(Context context, String prefix, String name, int count, String... severities);
	/**
	 * Bind error and location to checkpoint in validation report
	 * @param context
	 * @param checkPointName
	 * @param location
	 */
	void addCheckPointReportError(Context context, String checkPointName, DataLocation location);
	/**
	 * Bind error and location to checkpoint in validation report
	 * @param context
	 * @param checkPointName
	 * @param location
	 */
	void addCheckPointReportError(Context context, String checkPointName, String detail, DataLocation location);
	/**
	 * Bind error and location to checkpoint in validation report
	 * @param context
	 * @param checkPointName
	 * @param location
	 * @param value
	 */
	void addCheckPointReportError(Context context, String checkPointName, DataLocation location, String value);
	/**
	 * Bind error and location to checkpoint in validation report
	 * @param context
	 * @param checkPointName
	 * @param detail
	 * @param location
	 * @param value
	 */
	void addCheckPointReportError(Context context, String checkPointName, String detail, DataLocation location, String value);
	/**
	 * Bind error and location to checkpoint in validation report
	 * @param context
	 * @param checkPointName
	 * @param location
	 * @param value
	 * @param refValue
	 */
	void addCheckPointReportError(Context context, String checkPointName,  DataLocation location, String value, String refValue);
	/**
	 * Bind error and location to checkpoint in validation report
	 * @param context
	 * @param checkPointName
	 * @param location
	 * @param value
	 * @param refValue
	 */
	void addCheckPointReportError(Context context, String checkPointName, String detail, DataLocation location, String value, String refValue);
	/**
	 * Bind error and location to checkpoint in validation report
	 * @param context
	 * @param checkPointName
	 * @param sourceLocation
	 * @param value
	 * @param refValue
	 * @param targetLocations
	 */
	void addCheckPointReportError(Context context, String checkPointName, DataLocation sourceLocation, String value, String refValue, DataLocation... targetLocations);
	/**
	 * Bind error and location to checkpoint in validation report
	 * @param context
	 * @param checkPointName
	 * @param sourceLocation
	 * @param value
	 * @param refValue
	 * @param targetLocations
	 */
	void addCheckPointReportError(Context context, String checkPointName, String detail, DataLocation sourceLocation, String value, String refValue, DataLocation... targetLocations);
	/**
	 * Bind error and location to checkpoint in validation report
	 * @param context
	 * @param checkPointName
	 * @param locations
	 * @param value
	 */
	void addCheckPointReportError(Context context, String checkPointName, DataLocation[] locations, String value);
	/**
	 * Report if test is successful for specific checkpoint
	 * @param context
	 * @param checkpointName
	 * @param filenameInfo
	 */
	void reportSuccess(Context context, String checkpointName, String filenameInfo);
	/**
	 * Suppose that test for specific checkpoint is successful
	 * @param context
	 * @param checkPointName
	 */
	void prepareCheckPointReport(Context context, String checkPointName);
	/**
	 * Set state (OK, NOK, UNCHECK) to specific checkpoint
	 * @param checkPoint
	 * @param state
	 */
	void updateCheckPointReportState(Context context, String checkPointName, RESULT state);
	
	/**
	 * Update checkpoint severity
	 * @param context
	 * @param checkPointName
	 * @param severity
	 */
	void updateCheckPointReportSeverity(Context context, String checkPointName, CheckPointReport.SEVERITY severity);
	
	/**
	 * Check if validation report validity
	 * @param context
	 * @return
	 */
	boolean checkValidationReportValidity(Context context);
	
	/**
	 * Check if checkpoint exists
	 * @param context
	 * @param checkPointName
	 * @return
	 */
	boolean checkIfCheckPointExists(Context context, String checkPointName);
	/**
	 * Clear all validation report checkpoints
	 * @param context
	 */
	void clearValidationReport(Context context);
	
	/**
	 * Factory for using validation reporter instance
	 * @author gjamot
	 *
	 */
	public class Factory {
		private static ValidationReporter validationReporter;
		
		public static synchronized ValidationReporter getInstance() {
			if(validationReporter == null) {
				validationReporter = new ValidationReporterImpl();
			}
			
			return validationReporter;
		}
	}

}
