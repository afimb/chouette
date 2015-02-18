package mobi.chouette.exchange.neptune.validation;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.neptune.Constant;
import mobi.chouette.exchange.validation.ValidationConstraints;
import mobi.chouette.exchange.validation.ValidationException;
import mobi.chouette.exchange.validation.Validator;
import mobi.chouette.exchange.validation.ValidatorFactory;
import mobi.chouette.exchange.validation.report.FileLocation;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.util.Referential;

public class TimetableValidator extends AbstractValidator implements Validator<Timetable> , Constant{

	public static String NAME = "TimetableValidator";

	private static final String TIMETABLE_1 = "2-NEPTUNE-Timetable-1";
	private static final String TIMETABLE_2 = "2-NEPTUNE-Timetable-2";

	public static final String LOCAL_CONTEXT = "Timetable";


	public TimetableValidator(Context context) 
	{
		addItemToValidation(context, prefix, "Timetable", 2, "W", "W");

	}

	public void addLocation(Context context, String objectId, int lineNumber, int columnNumber)
	{
		Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
		objectContext.put(LINE_NUMBER, Integer.valueOf(lineNumber));
		objectContext.put(COLUMN_NUMBER, Integer.valueOf(columnNumber));

	}

	public void addAreaCentroidId(Context  context, String objectId, String centroidId)
	{
		Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
		objectContext.put("centroidOfArea", centroidId);

	}

	@SuppressWarnings("unchecked")
	public void addContains(Context context, String objectId, String containsId) {
		Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
		List<String> contains = (List<String>) objectContext.get("contains");
		if (contains == null)
		{
			contains = new ArrayList<>();
			objectContext.put("contains", contains);
		}
		contains.add(containsId);
		
	}



	@Override
	public ValidationConstraints validate(Context context, Timetable target) throws ValidationException
	{
		Context validationContext = (Context) context.get(VALIDATION_CONTEXT);
		Context localContext = (Context) validationContext.get(LOCAL_CONTEXT);
		if (localContext == null || localContext.isEmpty()) return new ValidationConstraints();
		Context stopPointContext = (Context) validationContext.get(StopPointValidator.LOCAL_CONTEXT);
		Context itlContext = (Context) validationContext.get(ITLValidator.LOCAL_CONTEXT);
		Context areaCentroidContext = (Context) validationContext.get(AreaCentroidValidator.LOCAL_CONTEXT);
		Referential referential = (Referential) context.get(REFERENTIAL);
		Map<String, StopArea> stopAreas = referential.getStopAreas();
		String fileName = (String) context.get(FILE_NAME);

		for (String objectId : localContext.keySet()) 
		{
			
	         // TODO 2-NEPTUNE-StopArea-1 : check if StopArea refers in field contains
	         // only stopareas or stoppoints

			
			Context objectContext = (Context) localContext.get(objectId);
			StopArea stopArea = stopAreas.get(objectId);
			int lineNumber = ((Integer) objectContext.get(LINE_NUMBER)).intValue();
			int columnNumber = ((Integer) objectContext.get(COLUMN_NUMBER)).intValue();
			FileLocation sourceLocation = new FileLocation(fileName, lineNumber, columnNumber);

		}
		return new ValidationConstraints();
	}

	public static class DefaultValidatorFactory extends ValidatorFactory {



		@Override
		protected Validator<Timetable> create(Context context) {
			TimetableValidator instance = (TimetableValidator) context.get(NAME);
			if (instance == null) {
				instance = new TimetableValidator(context);
				context.put(NAME, instance);
			}
			return instance;
		}

	}

	static {
		ValidatorFactory.factories
		.put(TimetableValidator.class.getName(), new DefaultValidatorFactory());
	}



}
