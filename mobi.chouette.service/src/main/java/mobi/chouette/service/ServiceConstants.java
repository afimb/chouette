package mobi.chouette.service;

import mobi.chouette.common.Constant;

public interface ServiceConstants extends Constant{
	// errors
	public static final String UNKNOWN_REFERENTIAL = "unknown_referential";
	public static final String UNKNOWN_ACTION = "unknown_action";
	public static final String DUPPLICATE_PARAMETERS = "dupplicate_parameters";
	public static final String DUPPLICATE_DATA = "dupplicate_data";
	public static final String MISSING = "dupplicate_data";

	// link rel values
//	public static final String LOCATION_REL = "location";
//	public static final String CANCEL_REL = "cancel";
//	public static final String DELETE_REL = "delete";
//	
//	public static final String PARAMETERS_REL = "parameters";
//	public static final String ACTION_PARAMETERS_REL = "action_params";
//	public static final String VALIDATION_PARAMETERS_REL = "validation_params";
//	public static final String DATA_REL = "data";
//	public static final String INPUT_REL = "input";
//	public static final String OUTPUT_REL = "output";
//	public static final String VALIDATION_REL = "validation_report";
//	public static final String REPORT_REL = "action_report";

	// link methods
	public static final String GET_METHOD = "get";
	public static final String POST_METHOD = "post";
	public static final String PUT_METHOD = "put";
	public static final String DELETE_METHOD = "delete";


}
