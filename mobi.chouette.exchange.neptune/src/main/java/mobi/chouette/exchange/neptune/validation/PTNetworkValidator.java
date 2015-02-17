package mobi.chouette.exchange.neptune.validation;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.neptune.Constant;
import mobi.chouette.exchange.validation.ValidationConstraints;
import mobi.chouette.exchange.validation.ValidationException;
import mobi.chouette.exchange.validation.Validator;
import mobi.chouette.exchange.validation.ValidatorFactory;
import mobi.chouette.exchange.validation.report.Detail;
import mobi.chouette.exchange.validation.report.FileLocation;
import mobi.chouette.exchange.validation.report.Location;
import mobi.chouette.model.Line;
import mobi.chouette.model.PTNetwork;
import mobi.chouette.model.util.Referential;

public class PTNetworkValidator extends AbstractValidator implements Validator<PTNetwork> , Constant{

	public static String NAME = "PTNetworkValidator";
	
	private static final String NETWORK_1 = "2-NEPTUNE-Network-1";

    static final String LOCAL_CONTEXT = "PTNetwork";


	public PTNetworkValidator(Context context) 
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
	public ValidationConstraints validate(Context context, PTNetwork target) throws ValidationException
	{
		Context validationContext = (Context) context.get(VALIDATION_CONTEXT);
		Context localContext = (Context) validationContext.get(LOCAL_CONTEXT);
		if (localContext == null || localContext.isEmpty()) return new ValidationConstraints();

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
					int lineNumber = ((Integer) objectContext.get(LINE_NUMBER)).intValue();
					int columnNumber = ((Integer) objectContext.get(COLUMN_NUMBER)).intValue();
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
		protected Validator<PTNetwork> create(Context context) {
			PTNetworkValidator instance = (PTNetworkValidator) context.get(NAME);
			if (instance == null) {
				instance = new PTNetworkValidator(context);
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
