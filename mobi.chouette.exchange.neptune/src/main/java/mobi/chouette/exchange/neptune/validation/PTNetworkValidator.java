package mobi.chouette.exchange.neptune.validation;


import java.util.ArrayList;
import java.util.List;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.neptune.Constant;
import mobi.chouette.exchange.validator.ValidationConstraints;
import mobi.chouette.exchange.validator.ValidationException;
import mobi.chouette.exchange.validator.Validator;
import mobi.chouette.exchange.validator.ValidatorFactory;
import mobi.chouette.exchange.validator.report.Detail;
import mobi.chouette.exchange.validator.report.FileLocation;
import mobi.chouette.exchange.validator.report.Location;
import mobi.chouette.model.Network;

public class PTNetworkValidator extends AbstractValidator implements Validator<Network> , Constant{

	public static final String LINE_ID = "lineId";

	public static String NAME = "PTNetworkValidator";
	
	private static final String NETWORK_1 = "2-NEPTUNE-Network-1";

	public static final String LOCAL_CONTEXT = "PTNetwork";

    @Override
	protected void initializeCheckPoints(Context context)
	{
		addItemToValidation(context, prefix, "Network", 1, "W");

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
		List<String> lineIds = (List<String>) objectContext.get(LINE_ID);
		if (lineIds == null)
		{
			lineIds = new ArrayList<>();
			objectContext.put(LINE_ID, lineIds);
		}
		lineIds.add(lineId);
	}
	
	

	@SuppressWarnings("unchecked")
	@Override
	public ValidationConstraints validate(Context context, Network target) throws ValidationException
	{
		Context validationContext = (Context) context.get(VALIDATION_CONTEXT);
		Context localContext = (Context) validationContext.get(LOCAL_CONTEXT);
		if (localContext == null || localContext.isEmpty()) return new ValidationConstraints();
		Context lineContext = (Context) validationContext.get(LineValidator.LOCAL_CONTEXT);

		String fileName = (String) context.get(FILE_NAME);
		String lineId = lineContext.keySet().iterator().next(); 

		for (String objectId : localContext.keySet()) 
		{
			// 2-NEPTUNE-PtNetwork-1 : check if lineId of line is present in list
			Context objectContext = (Context) localContext.get(objectId);
			List<String> lineIds = (List<String>) objectContext.get(LINE_ID);
			if (lineIds != null)
			{
				prepareCheckPoint(context, NETWORK_1);
				if (!lineIds.contains(lineId))
				{
					int lineNumber = ((Integer) objectContext.get(LINE_NUMBER)).intValue();
					int columnNumber = ((Integer) objectContext.get(COLUMN_NUMBER)).intValue();
					FileLocation sourceLocation = new FileLocation(fileName, lineNumber, columnNumber);
					Detail errorItem = new Detail(
							NETWORK_1,
							new Location(sourceLocation ,objectId), lineId);
					addValidationError(context, NETWORK_1, errorItem);
				}
			}

		}
		return new ValidationConstraints();
	}

	public static class DefaultValidatorFactory extends ValidatorFactory {

		

		@Override
		protected Validator<Network> create(Context context) {
			PTNetworkValidator instance = (PTNetworkValidator) context.get(NAME);
			if (instance == null) {
				instance = new PTNetworkValidator();
				context.put(NAME, instance);
			}
			return instance;
		}

	}

	static {
		ValidatorFactory.factories
		.put(PTNetworkValidator.class.getName(), new DefaultValidatorFactory());
	}



}
