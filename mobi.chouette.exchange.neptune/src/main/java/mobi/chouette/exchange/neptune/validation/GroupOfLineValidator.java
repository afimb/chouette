package mobi.chouette.exchange.neptune.validation;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.neptune.Constant;
import mobi.chouette.exchange.validation.ValidationConstraints;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.ValidationException;
import mobi.chouette.exchange.validation.Validator;
import mobi.chouette.exchange.validation.ValidatorFactory;
import mobi.chouette.exchange.validation.report.Detail;
import mobi.chouette.exchange.validation.report.Location;
import mobi.chouette.model.GroupOfLine;
import mobi.chouette.model.NeptuneIdentifiedObject;

public class GroupOfLineValidator extends AbstractValidator implements Validator<GroupOfLine> , Constant{

	public static final String LINE_ID = "lineId";

	public static String NAME = "GroupOfLineValidator";
	
	private static final String GROUP_OF_LINE_1 = "2-NEPTUNE-GroupOfLine-1";

	public static final String LOCAL_CONTEXT = "GroupOfLine";


    @Override
	protected void initializeCheckPoints(Context context)
	{
		addItemToValidation( context, prefix, "GroupOfLine", 1, "W");

	}

	public void addLocation(Context context, NeptuneIdentifiedObject object, int lineNumber, int columnNumber)
	{
		addLocation( context,LOCAL_CONTEXT,  object,  lineNumber,  columnNumber);
		
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
	public ValidationConstraints validate(Context context, GroupOfLine target) throws ValidationException
	{
		Context validationContext = (Context) context.get(VALIDATION_CONTEXT);
		Context localContext = (Context) validationContext.get(LOCAL_CONTEXT);
		if (localContext == null || localContext.isEmpty()) return new ValidationConstraints();
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
		Map<String, Location> fileLocations = data.getFileLocations();
		
		
		Context lineContext = (Context) validationContext.get(LineValidator.LOCAL_CONTEXT);
		String lineId = lineContext.keySet().iterator().next();

		for (String objectId : localContext.keySet()) 
		{
			// 2-NEPTUNE-GroupOfLine-1 : check if lineId of line is present in list
			Context objectContext = (Context) localContext.get(objectId);
			List<String> lineIds = (List<String>) objectContext.get(LINE_ID);
			if (lineIds != null)
			{
				prepareCheckPoint(context, GROUP_OF_LINE_1);
				if (!lineIds.contains(lineId))
				{
					Detail errorItem = new Detail(
							GROUP_OF_LINE_1,
							fileLocations.get(objectId), lineId);
					addValidationError(context, GROUP_OF_LINE_1, errorItem);
				}
			}

		}
		return new ValidationConstraints();
	}

	public static class DefaultValidatorFactory extends ValidatorFactory {

		

		@Override
		protected Validator<GroupOfLine> create(Context context) {
			GroupOfLineValidator instance = (GroupOfLineValidator) context.get(NAME);
			if (instance == null) {
				instance = new GroupOfLineValidator();
				context.put(NAME, instance);
			}
			return instance;
		}

	}

	static {
		ValidatorFactory.factories
		.put(GroupOfLineValidator.class.getName(), new DefaultValidatorFactory());
	}



}
