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
import mobi.chouette.model.Route;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.util.Referential;

public class ChouetteRouteValidator extends AbstractValidator implements Validator<Route> , Constant{

	public static String NAME = "ChouetteRouteValidator";

	private static final String ROUTE_1 = "2-NEPTUNE-Route-1";
	private static final String ROUTE_2 = "2-NEPTUNE-Route-2";
	private static final String ROUTE_3 = "2-NEPTUNE-Route-3";
	private static final String ROUTE_4 = "2-NEPTUNE-Route-4";
	private static final String ROUTE_5 = "2-NEPTUNE-Route-5";
	private static final String ROUTE_6 = "2-NEPTUNE-Route-6";
	private static final String ROUTE_7 = "2-NEPTUNE-Route-7";
	private static final String ROUTE_8 = "2-NEPTUNE-Route-8";
	private static final String ROUTE_9 = "2-NEPTUNE-Route-9";
	private static final String ROUTE_10 = "2-NEPTUNE-Route-10";
	private static final String ROUTE_11 = "2-NEPTUNE-Route-11";
	private static final String ROUTE_12 = "2-NEPTUNE-Route-12";

	public static final String LOCAL_CONTEXT = "Route";


	public ChouetteRouteValidator(Context context) 
	{
		addItemToValidation(context, prefix, "Route", 12, "E", "E", "E", "E",
				"E", "E", "E", "E", "W", "E", "W", "W");

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
	public ValidationConstraints validate(Context context, Route target) throws ValidationException
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
		protected Validator<Route> create(Context context) {
			ChouetteRouteValidator instance = (ChouetteRouteValidator) context.get(NAME);
			if (instance == null) {
				instance = new ChouetteRouteValidator(context);
				context.put(NAME, instance);
			}
			return instance;
		}

	}

	static {
		ValidatorFactory.factories
		.put(ChouetteRouteValidator.class.getName(), new DefaultValidatorFactory());
	}



}
