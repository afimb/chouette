package mobi.chouette.exchange.neptune.validation;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.neptune.Constant;
import mobi.chouette.exchange.neptune.model.TimeSlot;
import mobi.chouette.exchange.validation.ValidationConstraints;
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
	public ValidationConstraints validate(Context context, TimeSlot target) throws ValidationException
	{
		Context validationContext = (Context) context.get(VALIDATION_CONTEXT);
		Context localContext = (Context) validationContext.get(LOCAL_CONTEXT);
		
		if (localContext == null || localContext.isEmpty())
			return new ValidationConstraints();
		
		if (target != null &&
				target.getBeginningSlotTime() != null &&
				target.getFirstDepartureTimeInSlot() != null &&
				target.getLastDepartureTimeInSlot() != null &&
				target.getEndSlotTime() != null)
			if (target.getBeginningSlotTime().after(target.getFirstDepartureTimeInSlot()) ||
					target.getFirstDepartureTimeInSlot().after(target.getLastDepartureTimeInSlot()) ||
					target.getLastDepartureTimeInSlot().after(target.getEndSlotTime()))
				; // addValidationError
//		Context stopPointsContext = (Context) validationContext.get(StopPointValidator.LOCAL_CONTEXT);
//
//		String fileName = (String) context.get(FILE_NAME);
//
//		// 2-NEPTUNE-PtLink-1 : check existence of start and end of links
//		prepareCheckPoint(context, PT_LINK_1);
//		for (String objectId : localContext.keySet()) 
//		{
//			Context objectContext = (Context) localContext.get(objectId);
//			int lineNumber = ((Integer) objectContext.get(LINE_NUMBER)).intValue();
//			int columnNumber = ((Integer) objectContext.get(COLUMN_NUMBER)).intValue();
//			FileLocation sourceLocation = new FileLocation(fileName, lineNumber, columnNumber);
//
//			String start = (String) objectContext.get(START_OF_LINK_ID);
//			if (!stopPointsContext.containsKey(start))
//			{
//				Detail errorItem = new Detail(
//						PT_LINK_1,
//						new Location(sourceLocation,objectId), start, "startOfLink");
//				addValidationError(context,PT_LINK_1, errorItem);
//			}
//			String end = (String) objectContext.get(END_OF_LINK_ID);
//			if (!stopPointsContext.containsKey(end))
//			{
//				Detail errorItem = new Detail(
//						PT_LINK_1,
//						new Location(sourceLocation,objectId), end, "endOfLink");
//				addValidationError(context,PT_LINK_1, errorItem);
//			}
//
//
//		}
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
