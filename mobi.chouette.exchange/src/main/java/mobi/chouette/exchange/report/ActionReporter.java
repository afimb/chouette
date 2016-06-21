package mobi.chouette.exchange.report;

import mobi.chouette.common.Context;

public interface ActionReporter {
	
	public enum OBJECT_TYPE {
		NETWORK,
		COMPANY,
		STOP_AREA,
		LINE,
		ROUTE,
		STOP_POINT,
		JOURNEY_PATTERN
	};
	
	public enum FILE_STATE {
		IGNORED, OK, ERROR
	};
	
	public enum ERROR_CODE 
	{
		INVALID_PARAMETERS,
		NO_DATA_FOUND,
		NO_DATA_PROCEEDED,
		INVALID_DATA,
		INTERNAL_ERROR
	};
	
	public enum OBJECT_STATE 
	{
		OK,
		WARNING,
		ERROR
	};
	
	public enum FILE_ERROR_CODE 
	{
		FILE_NOT_FOUND,
		READ_ERROR,
		WRITE_ERROR,
		INVALID_FORMAT,
		INTERNAL_ERROR
	};
	
	void addFileReport(Context context, String fileInfoName, boolean isZipFile);
	void addFileErrorInReport(Context context, String fileInfoName, FILE_ERROR_CODE code, String message, boolean isZipFile);
	void setActionError(Context context, ERROR_CODE code, String description);
	void addObjectReport(Context context, String objectId, String type, String description, OBJECT_STATE status, IO_TYPE ioType);
	void addObjectReportToSpecificCollection(Context context, String objectId, String type, String description, OBJECT_STATE status, IO_TYPE ioType);
	void addErrorToObjectReport(Context context, String objectId, String type, String description, OBJECT_STATE status, IO_TYPE ioType, ERROR_CODE code, String descriptionError);
	void addStatToObjectReport(Context context, String objectId, String type, String description, OBJECT_STATE status, IO_TYPE ioType, String statType);
	
	
	
	/**
	 * Factory for using action reporter instance
	 * @author gjamot
	 *
	 */
	public class Factory {
		private static ActionReporter actionReporter;
		
		public static synchronized ActionReporter getInstance() {
			if(actionReporter == null) {
				actionReporter = new ActionReporterImpl();
			}
			
			return actionReporter;
		}
	}
}
