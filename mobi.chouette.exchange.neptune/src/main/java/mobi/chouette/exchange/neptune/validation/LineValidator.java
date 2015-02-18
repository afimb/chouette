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
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.util.Referential;

public class LineValidator extends AbstractValidator implements Validator<Line> , Constant{

	public static String NAME = "LineValidator";

	private static final String LINE_1 = "2-NEPTUNE-Line-1";
	private static final String LINE_2 = "2-NEPTUNE-Line-2";
	private static final String LINE_3 = "2-NEPTUNE-Line-3";
	private static final String LINE_4 = "2-NEPTUNE-Line-4";
	private static final String LINE_5 = "2-NEPTUNE-Line-5";

	public static final String LOCAL_CONTEXT = "NeptuneLine";


	public LineValidator(Context context) 
	{
		addItemToValidation(context, prefix, "Line", 5, "E", "W", "W", "E", "E");

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
	public ValidationConstraints validate(Context context, Line target) throws ValidationException
	{
		Context validationContext = (Context) context.get(VALIDATION_CONTEXT);
		Context localContext = (Context) validationContext.get(LOCAL_CONTEXT);
		if (localContext == null || localContext.isEmpty()) return new ValidationConstraints();
		Context stopPointContext = (Context) validationContext.get(StopPointValidator.LOCAL_CONTEXT);
		Context itlContext = (Context) validationContext.get(ITLValidator.LOCAL_CONTEXT);
		Context areaCentroidContext = (Context) validationContext.get(AreaCentroidValidator.LOCAL_CONTEXT);
		Referential referential = (Referential) context.get(REFERENTIAL);
		Map<String, StopArea> stopAreas = referential.getStopAreas();
		String fileName = (String) context.get(FILE_URL);

		for (String objectId : localContext.keySet()) 
		{
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
		protected Validator<Line> create(Context context) {
			LineValidator instance = (LineValidator) context.get(NAME);
			if (instance == null) {
				instance = new LineValidator(context);
				context.put(NAME, instance);
			}
			return instance;
		}

	}

	static {
		ValidatorFactory.factories
		.put(LineValidator.class.getName(), new DefaultValidatorFactory());
	}



}
