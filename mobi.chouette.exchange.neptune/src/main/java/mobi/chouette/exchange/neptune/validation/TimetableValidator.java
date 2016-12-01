package mobi.chouette.exchange.neptune.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.neptune.Constant;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.ValidationException;
import mobi.chouette.exchange.validation.Validator;
import mobi.chouette.exchange.validation.ValidatorFactory;
import mobi.chouette.exchange.validation.report.DataLocation;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import mobi.chouette.model.NeptuneIdentifiedObject;
import mobi.chouette.model.Period;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.util.Referential;

public class TimetableValidator extends AbstractValidator implements Validator<Timetable>, Constant {

	public static final String VEHICLE_JOURNEY_ID = "vehicleJourneyId";

	public static String NAME = "TimetableValidator";

	private static final String TIMETABLE_1 = "2-NEPTUNE-Timetable-1";
	private static final String TIMETABLE_2 = "2-NEPTUNE-Timetable-2";
	private static final String TIMETABLE_3 = "2-NEPTUNE-Timetable-3";

	public static final String LOCAL_CONTEXT = "Timetable";

	@Override
	protected void initializeCheckPoints(Context context) {
		addItemToValidation(context, prefix, "Timetable", 3, "W", "W", "E");

	}

	public void addLocation(Context context, NeptuneIdentifiedObject object, int lineNumber, int columnNumber) {
		addLocation(context, LOCAL_CONTEXT, object, lineNumber, columnNumber);

	}

	@SuppressWarnings("unchecked")
	public void addVehicleJourneyId(Context context, String objectId, String vjId) {
		Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
		List<String> contains = (List<String>) objectContext.get(VEHICLE_JOURNEY_ID);
		if (contains == null) {
			contains = new ArrayList<>();
			objectContext.put(VEHICLE_JOURNEY_ID, contains);
		}
		contains.add(vjId);

	}

	@SuppressWarnings("unchecked")
	@Override
	public void validate(Context context, Timetable target) throws ValidationException {
		Context validationContext = (Context) context.get(VALIDATION_CONTEXT);
		Context localContext = (Context) validationContext.get(LOCAL_CONTEXT);
		if (localContext == null || localContext.isEmpty())
			return;
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
		Map<String, DataLocation> fileLocations = data.getDataLocations();
		Context vehicleJourneyContext = (Context) validationContext.get(VehicleJourneyValidator.LOCAL_CONTEXT);
		Referential referential = (Referential) context.get(REFERENTIAL);
		Map<String, Timetable> timetables = referential.getTimetables();

		// 2-NEPTUNE-Timetable-1 : check if timetable refers at least one
		// existing vehiclejourney (w)
		prepareCheckPoint(context, TIMETABLE_1);
		// 2-NEPTUNE-Timetable-2 : check if vehiclejourney is referred by at
		// least one timetable (w)
		prepareCheckPoint(context, TIMETABLE_2);

		List<String> unreferencedVehicleJourneys = new ArrayList<String>(vehicleJourneyContext.keySet());

		for (String objectId : localContext.keySet()) {
			Context objectContext = (Context) localContext.get(objectId);

			boolean vjFound = false;
			if (objectContext.containsKey(VEHICLE_JOURNEY_ID)) {
				for (String vjId : (List<String>) objectContext.get(VEHICLE_JOURNEY_ID)) {
					if (vehicleJourneyContext.containsKey(vjId))
						vjFound = true;
					unreferencedVehicleJourneys.remove(vjId);
				}
			}
			if (!vjFound) {
				ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
				validationReporter.addCheckPointReportError(context, TIMETABLE_1, fileLocations.get(objectId));
			}

			Timetable timetable = timetables.get(objectId);
			if (timetable != null && !timetable.getPeriods().isEmpty()) {
				// 2-NEPTUNE-Timetable-3 : check if period end > period start
				// (e)
				prepareCheckPoint(context, TIMETABLE_3);
				for (Period period : timetable.getPeriods()) {
					if (period.getEndDate().after(period.getStartDate()))
						continue;
					ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
					validationReporter.addCheckPointReportError(context, TIMETABLE_3, fileLocations.get(objectId));
					break;
				}

			}
		}
		if (!unreferencedVehicleJourneys.isEmpty()) {
			for (String vjId : unreferencedVehicleJourneys) {
				ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
				validationReporter.addCheckPointReportError(context, TIMETABLE_2, fileLocations.get(vjId));
			}
		}
		return;
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
		ValidatorFactory.factories.put(TimetableValidator.class.getName(), new DefaultValidatorFactory());
	}

}
