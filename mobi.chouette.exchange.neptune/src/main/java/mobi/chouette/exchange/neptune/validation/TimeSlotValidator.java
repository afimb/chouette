package mobi.chouette.exchange.neptune.validation;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.neptune.Constant;
import mobi.chouette.exchange.neptune.model.TimeSlot;
import mobi.chouette.exchange.validation.ValidationException;
import mobi.chouette.exchange.validation.Validator;
import mobi.chouette.exchange.validation.ValidatorFactory;
import mobi.chouette.model.NeptuneIdentifiedObject;

public class TimeSlotValidator extends AbstractValidator implements Validator<TimeSlot> , Constant{
	
	public static String NAME = "TimeSlotValidator";

	public static final String LOCAL_CONTEXT = "TimeSlot";


    @Override
	protected void initializeCheckPoints(Context context)
	{
	}

	public void addLocation(Context context, NeptuneIdentifiedObject object, int lineNumber, int columnNumber)
	{
		addLocation( context,LOCAL_CONTEXT,  object,  lineNumber,  columnNumber);

	}

	@Override
	public void validate(Context context, TimeSlot target) throws ValidationException
	{
		Context validationContext = (Context) context.get(VALIDATION_CONTEXT);
		Context localContext = (Context) validationContext.get(LOCAL_CONTEXT);
		
		if (localContext == null || localContext.isEmpty())
			return ;
		
		if (target != null &&
				target.getBeginningSlotTime() != null &&
				target.getFirstDepartureTimeInSlot() != null &&
				target.getLastDepartureTimeInSlot() != null &&
				target.getEndSlotTime() != null)
			if (target.getBeginningSlotTime().isAfter(target.getFirstDepartureTimeInSlot()) ||
					target.getFirstDepartureTimeInSlot().isAfter(target.getLastDepartureTimeInSlot()) ||
					target.getLastDepartureTimeInSlot().isAfter(target.getEndSlotTime()))
				; 
		return ;
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
