package mobi.chouette.exchange.neptune.validation;


import java.util.ArrayList;
import java.util.List;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.neptune.Constant;
import mobi.chouette.exchange.validation.ValidationConstraints;
import mobi.chouette.exchange.validation.ValidationException;
import mobi.chouette.exchange.validation.Validator;
import mobi.chouette.exchange.validation.ValidatorFactory;
import mobi.chouette.exchange.validation.report.Detail;
import mobi.chouette.exchange.validation.report.FileLocation;
import mobi.chouette.exchange.validation.report.Location;
import mobi.chouette.model.Timetable;

public class TimetableValidator extends AbstractValidator implements Validator<Timetable> , Constant{

	public static final String VEHICLE_JOURNEY_ID = "vehicleJourneyId";

	public static String NAME = "TimetableValidator";

	private static final String TIMETABLE_1 = "2-NEPTUNE-Timetable-1";
	private static final String TIMETABLE_2 = "2-NEPTUNE-Timetable-2";

	public static final String LOCAL_CONTEXT = "Timetable";


    @Override
	protected void initializeCheckPoints(Context context)
	{
		addItemToValidation(context, prefix, "Timetable", 2, "W", "W");

	}

	public void addLocation(Context context, String objectId, int lineNumber, int columnNumber)
	{
		addLocation( context,LOCAL_CONTEXT,  objectId,  lineNumber,  columnNumber);

	}

	@SuppressWarnings("unchecked")
	public void addVehicleJourneyId(Context context, String objectId, String vjId) {
		Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
		List<String> contains = (List<String>) objectContext.get(VEHICLE_JOURNEY_ID);
		if (contains == null)
		{
			contains = new ArrayList<>();
			objectContext.put(VEHICLE_JOURNEY_ID, contains);
		}
		contains.add(vjId);
		
	}



	@SuppressWarnings("unchecked")
	@Override
	public ValidationConstraints validate(Context context, Timetable target) throws ValidationException
	{
		Context validationContext = (Context) context.get(VALIDATION_CONTEXT);
		Context localContext = (Context) validationContext.get(LOCAL_CONTEXT);
		if (localContext == null || localContext.isEmpty()) return new ValidationConstraints();
		Context vehicleJourneyContext = (Context) validationContext.get(VehicleJourneyValidator.LOCAL_CONTEXT);

		String fileName = (String) context.get(FILE_NAME);

	      // 2-NEPTUNE-Timetable-1 : check if timetable refers at least one
	      // existing vehiclejourney (w)
	      prepareCheckPoint(context, TIMETABLE_1);
	      // 2-NEPTUNE-Timetable-2 : check if vehiclejourney is referred by at
	      // least one timetable (w)
	      prepareCheckPoint(context, TIMETABLE_2);
	      
	      List<String> unreferencedVehicleJourneys = new ArrayList<String>(vehicleJourneyContext.keySet());

	      for (String objectId : localContext.keySet()) 
		{
			Context objectContext = (Context) localContext.get(objectId);
			int lineNumber = ((Integer) objectContext.get(LINE_NUMBER)).intValue();
			int columnNumber = ((Integer) objectContext.get(COLUMN_NUMBER)).intValue();
			FileLocation sourceLocation = new FileLocation(fileName, lineNumber, columnNumber);

	         boolean vjFound = false;
	         for (String vjId : (List<String>) objectContext.get(VEHICLE_JOURNEY_ID))
	         {
	            if (vehicleJourneyContext.containsKey(vjId))
	               vjFound = true;
	            unreferencedVehicleJourneys.remove(vjId);
	         }
	         if (!vjFound)
	         {
				Detail errorItem = new Detail(
						TIMETABLE_1,
						new Location(sourceLocation,objectId));
				addValidationError(context,TIMETABLE_1, errorItem);

	         }

		}
	      if (!unreferencedVehicleJourneys.isEmpty())
	      {
	         for (String vjId : unreferencedVehicleJourneys)
	         {
	            Context vjctx = (Context) vehicleJourneyContext.get(vjId);
	            int lineNumber = ((Integer) vjctx.get(LINE_NUMBER)).intValue();
			    int columnNumber = ((Integer) vjctx.get(COLUMN_NUMBER)).intValue();
				FileLocation sourceLocation = new FileLocation(fileName, lineNumber, columnNumber);
				Detail errorItem = new Detail(
						TIMETABLE_2,
						new Location(sourceLocation,vjId));
				addValidationError(context,TIMETABLE_2, errorItem);
	         }
	      }
		return new ValidationConstraints();
	}

	public static class DefaultValidatorFactory extends ValidatorFactory {



		@Override
		protected Validator<Timetable> create(Context context) {
			TimetableValidator instance = (TimetableValidator) context.get(NAME);
			if (instance == null) {
				instance = new TimetableValidator();
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
