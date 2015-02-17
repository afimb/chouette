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
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.util.Referential;

public class VehicleJourneyValidator extends AbstractValidator implements Validator<VehicleJourney> , Constant{

	public static String NAME = "VehicleJourneyValidator";

	private static final String VEHICLE_JOURNEY_1 = "2-NEPTUNE-VehicleJourney-1";
	private static final String VEHICLE_JOURNEY_2 = "2-NEPTUNE-VehicleJourney-2";
	private static final String VEHICLE_JOURNEY_3 = "2-NEPTUNE-VehicleJourney-3";
	private static final String VEHICLE_JOURNEY_4 = "2-NEPTUNE-VehicleJourney-4";
	private static final String VEHICLE_JOURNEY_5 = "2-NEPTUNE-VehicleJourney-5";
	private static final String VEHICLE_JOURNEY_6 = "2-NEPTUNE-VehicleJourney-6";
	private static final String VEHICLE_JOURNEY_7 = "2-NEPTUNE-VehicleJourney-7";
	private static final String VEHICLE_JOURNEY_AT_STOP_1 = "2-NEPTUNE-VehicleJourneyAtStop-1";
	private static final String VEHICLE_JOURNEY_AT_STOP_2 = "2-NEPTUNE-VehicleJourneyAtStop-2";
	private static final String VEHICLE_JOURNEY_AT_STOP_3 = "2-NEPTUNE-VehicleJourneyAtStop-3";
	private static final String VEHICLE_JOURNEY_AT_STOP_4 = "2-NEPTUNE-VehicleJourneyAtStop-4";

	static final String LOCAL_CONTEXT = "VehicleJourney";


	public VehicleJourneyValidator(Context context) 
	{
		addItemToValidation(context, prefix, "VehicleJourney", 7, "E", "E", "E",
				"E", "E", "E", "W");
		addItemToValidation(context, prefix, "VehicleJourneyAtStop", 4, "E",
				"E", "E", "E");

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
	public ValidationConstraints validate(Context context, VehicleJourney target) throws ValidationException
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
		protected Validator<VehicleJourney> create(Context context) {
			VehicleJourneyValidator instance = (VehicleJourneyValidator) context.get(NAME);
			if (instance == null) {
				instance = new VehicleJourneyValidator(context);
				context.put(NAME, instance);
			}
			return instance;
		}

	}

	static {
		ValidatorFactory.factories
		.put(VehicleJourneyValidator.class.getName(), new DefaultValidatorFactory());
	}



}
