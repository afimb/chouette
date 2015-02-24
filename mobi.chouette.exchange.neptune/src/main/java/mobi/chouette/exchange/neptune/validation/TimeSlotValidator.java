package mobi.chouette.exchange.neptune.validation;


import mobi.chouette.common.Context;
import mobi.chouette.exchange.neptune.Constant;
import mobi.chouette.exchange.neptune.model.TimeSlot;
import mobi.chouette.exchange.validator.ValidationConstraints;
import mobi.chouette.exchange.validator.ValidationException;
import mobi.chouette.exchange.validator.Validator;
import mobi.chouette.exchange.validator.ValidatorFactory;

public class TimeSlotValidator extends AbstractValidator implements Validator<TimeSlot> , Constant{

	public static final String VEHICLE_JOURNEY_ID = "vehicleJourneyId";

	public static String NAME = "TimeSlotValidator";

	public static final String LOCAL_CONTEXT = "TimeSlot";


    @Override
	protected void initializeCheckPoints(Context context)
	{
	}

	public void addLocation(Context context, String objectId, int lineNumber, int columnNumber)
	{
		Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
		objectContext.put(LINE_NUMBER, Integer.valueOf(lineNumber));
		objectContext.put(COLUMN_NUMBER, Integer.valueOf(columnNumber));

	}

	@Override
	public ValidationConstraints validate(Context context, TimeSlot target) throws ValidationException
	{
		return new ValidationConstraints();
	}

	public static class DefaultValidatorFactory extends ValidatorFactory {



		@Override
		protected Validator<TimeSlot> create(Context context) {
			TimeSlotValidator instance = (TimeSlotValidator) context.get(NAME);
			if (instance == null) {
				instance = new TimeSlotValidator();
				context.put(NAME, instance);
			}
			return instance;
		}

	}

	static {
		ValidatorFactory.factories
		.put(TimeSlotValidator.class.getName(), new DefaultValidatorFactory());
	}



}
