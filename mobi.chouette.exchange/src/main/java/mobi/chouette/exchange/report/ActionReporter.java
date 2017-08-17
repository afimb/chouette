package mobi.chouette.exchange.report;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.validation.report.CheckPointReport.SEVERITY;

public interface ActionReporter {
	
	public enum OBJECT_TYPE {
		NETWORK("network"),
		COMPANY("company"),
		STOP_AREA("stop_area"),
		CONNECTION_LINK("connection_link"),
		ACCESS_POINT("access_point"),
		TIMETABLE("timetable"),
		LINE("line"),
		ROUTE("route"),
		JOURNEY_PATTERN("journey_pattern"),
		VEHICLE_JOURNEY("vehicle_journey");

		/**
		 * Allows building hash map keys when deserializing object from action_report.json
		 */
		@Getter
		private final String name;

		OBJECT_TYPE(String value) {
			name = value;
		}

		@JsonCreator
		public static OBJECT_TYPE fromValue(String v) {
			for (OBJECT_TYPE myEnum : values()) {
				if (myEnum.name.equals(v)) {
					return myEnum;
				}
			}
			throw new IllegalArgumentException("invalid string value passed: " + v);
		}

		public String toString() {
			return this.name;
		}
	}
	
	public enum FILE_STATE {
		IGNORED, OK, ERROR
	}
	
	public enum ERROR_CODE 
	{
		INVALID_PARAMETERS,
		NO_DATA_FOUND,
		NO_DATA_PROCEEDED,
		NO_DATA_ON_PERIOD,
		INVALID_DATA,
		INVALID_FORMAT,
		INTERNAL_ERROR,
		WRITE_ERROR
	};
	
	public enum OBJECT_STATE 
	{
		OK,
		WARNING,
		ERROR
	}
	
	public enum FILE_ERROR_CODE 
	{
		FILE_NOT_FOUND,
		READ_ERROR,
		WRITE_ERROR,
		INVALID_FORMAT,
		INTERNAL_ERROR
	}
	
	/**
	 * @param context
	 * @param fileInfoName
	 * @param ioType
	 */
	void addZipReport(Context context, String fileInfoName,IO_TYPE ioType);
	/**
	 * @param context
	 * @param fileInfoName
	 * @param code
	 * @param message
	 */
	void addZipErrorInReport(Context context, String fileInfoName, FILE_ERROR_CODE code, String message);
	/**
	 * @param context
	 * @param fileInfoName
	 * @param ioType
	 */
	void addFileReport(Context context, String fileInfoName,IO_TYPE ioType);
	/**
	 * @param context
	 * @param fileInfoName
	 * @param ioType
	 */
	void setFileState(Context context, String fileInfoName,IO_TYPE ioType,FILE_STATE state);
	/**
	 * @param context
	 * @param fileInfoName
	 * @param code
	 * @param message
	 */
	void addFileErrorInReport(Context context, String fileInfoName, FILE_ERROR_CODE code, String message);
	/**
	 * @param context
	 * @param fileInfoName
	 * @param code
	 * @return
	 */
	boolean addValidationErrorToFileReport(Context context, String fileInfoName, int code, SEVERITY severity);
	/**
	 * @param context
	 * @param code
	 * @param description
	 */
	void setActionError(Context context, ERROR_CODE code, String description);
    /**
     * @param context
     * @return
     */
    boolean hasActionError(Context context);
	/**
	 * @param context
	 * @param objectId
	 * @param type
	 * @param description
	 * @param status
	 * @param ioType
	 */
	void addObjectReport(Context context, String objectId, OBJECT_TYPE type, String description, OBJECT_STATE status, IO_TYPE ioType);
	/**
	 * @param context
	 * @param objectId
	 * @param type
	 * @param code
	 * @param descriptionError
	 */
	void addErrorToObjectReport(Context context, String objectId, OBJECT_TYPE type, ERROR_CODE code, String descriptionError);
	/**
	 * @param context
	 * @param objectId
	 * @param type
	 * @param code
	 * @return
	 */
	boolean addValidationErrorToObjectReport(Context context,  String objectId, OBJECT_TYPE type, int code, SEVERITY severity);
	/**
	 * add statistics value for object 
	 * @param context
	 * @param objectId
	 * @param type
	 * @param statType
	 * @param count value to add 
	 */
	void addStatToObjectReport(Context context, String objectId, OBJECT_TYPE type, OBJECT_TYPE statType, int count);
	/**
	 * set statistics value for object 
	 * @param context
	 * @param objectId
	 * @param type
	 * @param statType
	 * @param count value to set
	 */
	void setStatToObjectReport(Context context, String objectId, OBJECT_TYPE type, OBJECT_TYPE statType, int count);
	/**
	 * @param context
	 * @param line
	 * @return
	 */
	boolean hasInfo(Context context, OBJECT_TYPE line);

	boolean hasFileValidationErrors(Context context, String filename);
	
	
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
