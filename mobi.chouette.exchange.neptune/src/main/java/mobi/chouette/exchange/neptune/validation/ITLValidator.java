package mobi.chouette.exchange.neptune.validation;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.neptune.Constant;
import mobi.chouette.exchange.neptune.model.RoutingConstraint;
import mobi.chouette.exchange.validation.ValidationConstraints;
import mobi.chouette.exchange.validation.ValidationException;
import mobi.chouette.exchange.validation.Validator;
import mobi.chouette.exchange.validation.ValidatorFactory;
import mobi.chouette.exchange.validation.report.Detail;
import mobi.chouette.exchange.validation.report.FileLocation;
import mobi.chouette.exchange.validation.report.Location;
import mobi.chouette.model.Line;
import mobi.chouette.model.util.Referential;

public class ITLValidator extends AbstractValidator implements Validator<RoutingConstraint> , Constant{

	public static String NAME = "ITLValidator";
	
	private static final String NETWORK_1 = "2-NEPTUNE-Network-1";

	static final String LOCAL_CONTEXT = "ITL";


	public ITLValidator(Context context) 
	{
		addItemToValidation(context, prefix, "Network", 1, "W");

	}

	public void addLocation(Context context, String objectId, int lineNumber, int columnNumber)
	{
		Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
		objectContext.put(LINE_NUMBER, lineNumber);
		objectContext.put(COLUMN_NUMBER, columnNumber);
		
	}
	
	@SuppressWarnings("unchecked")
	public void addLineId(Context  context, String objectId, String lineId)
	{
		Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
		List<String> lineIds = (List<String>) objectContext.get("lineId");
		if (lineIds == null)
		{
			lineIds = new ArrayList<>();
			objectContext.put("lineId", lineIds);
		}
		lineIds.add(lineId);
	}
	
	

	@SuppressWarnings("unchecked")
	@Override
	public ValidationConstraints validate(Context context, RoutingConstraint target) throws ValidationException
	{
		Context validationContext = (Context) context.get(VALIDATION_CONTEXT);
		Context localContext = (Context) validationContext.get(LOCAL_CONTEXT);
		Referential referential = (Referential) context.get(REFERENTIAL);
		String fileName = (String) context.get(FILE_URL);
		Line line = referential.getLines().values().iterator().next(); 

		for (String objectId : localContext.keySet()) 
		{
			// 2-NEPTUNE-PtNetwork-1 : check if lineId of line is present in list
			Context objectContext = (Context) localContext.get(objectId);
			List<String> lineIds = (List<String>) objectContext.get("lineId");
			if (lineIds != null)
			{
				prepareCheckPoint(context, NETWORK_1);
				String lineId = line.getObjectId();
				if (!lineIds.contains(lineId))
				{
					int lineNumber = (int) objectContext.get(LINE_NUMBER);
					int columnNumber = (int) objectContext.get(COLUMN_NUMBER);
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("lineId", lineId);
					FileLocation sourceLocation = new FileLocation(fileName, lineNumber, columnNumber);
					Detail errorItem = new Detail(
							NETWORK_1,
							new Location(sourceLocation ,objectId), map);
					addValidationError(context, NETWORK_1, errorItem);
				}
			}

		}
		return new ValidationConstraints();
	}

	public static class DefaultValidatorFactory extends ValidatorFactory {

		

		@Override
		protected Validator<RoutingConstraint> create(Context context) {
			ITLValidator instance = (ITLValidator) context.get(NAME);
			if (instance == null) {
				instance = new ITLValidator(context);
				context.put(NAME, instance);
			}
			return instance;
		}

	}

	static {
		ValidatorFactory.factories
		.put(ITLValidator.class.getName(), new DefaultValidatorFactory());
	}



}
