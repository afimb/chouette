package mobi.chouette.exchange.neptune.validation;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.neptune.Constant;
import mobi.chouette.exchange.validation.ValidationConstraints;
import mobi.chouette.exchange.validation.ValidationException;
import mobi.chouette.exchange.validation.Validator;
import mobi.chouette.exchange.validation.ValidatorFactory;
import mobi.chouette.exchange.validation.report.Detail;
import mobi.chouette.exchange.validation.report.FileLocation;
import mobi.chouette.exchange.validation.report.Location;
import mobi.chouette.model.Line;
import mobi.chouette.model.PTNetwork;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.util.Referential;

public class StopPointValidator extends AbstractValidator implements Validator<StopPoint> , Constant{

	public static String NAME = "StopPointValidator";
	
	private static final String STOP_POINT_1 = "2-NEPTUNE-StopPoint-1";
	private static final String STOP_POINT_2 = "2-NEPTUNE-StopPoint-2";
	private static final String STOP_POINT_3 = "2-NEPTUNE-StopPoint-3";
	private static final String STOP_POINT_4 = "2-NEPTUNE-StopPoint-4";

	public static final String LOCAL_CONTEXT = "StopPoint";


	public StopPointValidator(Context context) 
	{
		addItemToValidation(context, prefix, "StopPoint", 4, "E", "E", "E", "E");

	}

	public void addLocation(Context context, String objectId, int lineNumber, int columnNumber)
	{
		Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
		objectContext.put(LINE_NUMBER, Integer.valueOf(lineNumber));
		objectContext.put(COLUMN_NUMBER, Integer.valueOf(columnNumber));
		
	}
	
	@SuppressWarnings("unchecked")
	public void addLineId(Context  context, String objectId, String lineId)
	{
		Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
		List<String> lineIds = (List<String>) objectContext.get("lineId");
		if (lineIds == null)
		{
			lineIds = new ArrayList<>();
			objectContext.put("lineId", lineIds);
		}
		lineIds.add(lineId);
	}
	
	

	@SuppressWarnings("unchecked")
	@Override
	public ValidationConstraints validate(Context context, StopPoint target) throws ValidationException
	{
		Context validationContext = (Context) context.get(VALIDATION_CONTEXT);
		Context localContext = (Context) validationContext.get(LOCAL_CONTEXT);
		if (localContext == null || localContext.isEmpty()) return new ValidationConstraints();

		Referential referential = (Referential) context.get(REFERENTIAL);
		String fileName = (String) context.get(FILE_NAME);
		Line line = referential.getLines().values().iterator().next(); 

		for (String objectId : localContext.keySet()) 
		{
			Context objectContext = (Context) localContext.get(objectId);
			int lineNumber = ((Integer) objectContext.get(LINE_NUMBER)).intValue();
			int columnNumber = ((Integer) objectContext.get(COLUMN_NUMBER)).intValue();
			FileLocation sourceLocation = new FileLocation(fileName, lineNumber, columnNumber);

		}
		return new ValidationConstraints();
	}

	public static class DefaultValidatorFactory extends ValidatorFactory {

		

		@Override
		protected Validator<StopPoint> create(Context context) {
			StopPointValidator instance = (StopPointValidator) context.get(NAME);
			if (instance == null) {
				instance = new StopPointValidator(context);
				context.put(NAME, instance);
			}
			return instance;
		}

	}

	static {
		ValidatorFactory.factories
		.put(StopPointValidator.class.getName(), new DefaultValidatorFactory());
	}



}
