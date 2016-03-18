package mobi.chouette.exchange.neptune.validation;


import java.util.Map;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.neptune.Constant;
import mobi.chouette.exchange.neptune.model.PTLink;
import mobi.chouette.exchange.validation.ValidationConstraints;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.ValidationException;
import mobi.chouette.exchange.validation.Validator;
import mobi.chouette.exchange.validation.ValidatorFactory;
import mobi.chouette.exchange.validation.report.Detail;
import mobi.chouette.exchange.validation.report.Location;
import mobi.chouette.model.NeptuneIdentifiedObject;

public class PtLinkValidator extends AbstractValidator implements Validator<PTLink> , Constant{

	public static final String END_OF_LINK_ID = "endOfLinkId";

	public static final String START_OF_LINK_ID = "startOfLinkId";

	public static String NAME = "PtLinkValidator";

	private static final String PT_LINK_1 = "2-NEPTUNE-PtLink-1";

	public static final String LOCAL_CONTEXT = "PTLink";


    @Override
	protected void initializeCheckPoints(Context context)
	{
		addItemToValidation( context, prefix, "PtLink", 1, "E");

	}

	public void addLocation(Context context, NeptuneIdentifiedObject object, int lineNumber, int columnNumber)
	{
		addLocation( context,LOCAL_CONTEXT,  object,  lineNumber,  columnNumber);

	}
	public void addStartOfLinkId(Context  context, String objectId, String linkId)
	{
		Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);

		objectContext.put(START_OF_LINK_ID, linkId);

	}

	public void addEndOfLinkId(Context  context, String objectId, String linkId)
	{
		Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);

		objectContext.put(END_OF_LINK_ID, linkId);
	}


	@Override
	public ValidationConstraints validate(Context context, PTLink target) throws ValidationException
	{
		Context validationContext = (Context) context.get(VALIDATION_CONTEXT);
		Context localContext = (Context) validationContext.get(LOCAL_CONTEXT);
		if (localContext == null || localContext.isEmpty()) return new ValidationConstraints();
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
		Map<String, Location> fileLocations = data.getFileLocations();
		
		Context stopPointsContext = (Context) validationContext.get(StopPointValidator.LOCAL_CONTEXT);


		// 2-NEPTUNE-PtLink-1 : check existence of start and end of links
		prepareCheckPoint(context, PT_LINK_1);
		for (String objectId : localContext.keySet()) 
		{
			Context objectContext = (Context) localContext.get(objectId);

			String start = (String) objectContext.get(START_OF_LINK_ID);
			if (!stopPointsContext.containsKey(start))
			{
				Detail errorItem = new Detail(
						PT_LINK_1,
						fileLocations.get(objectId), start, "startOfLink");
				addValidationError(context,PT_LINK_1, errorItem);
			}
			String end = (String) objectContext.get(END_OF_LINK_ID);
			if (!stopPointsContext.containsKey(end))
			{
				Detail errorItem = new Detail(
						PT_LINK_1,
						fileLocations.get(objectId), end, "endOfLink");
				addValidationError(context,PT_LINK_1, errorItem);
			}


		}
		return new ValidationConstraints();
	}

	public static class DefaultValidatorFactory extends ValidatorFactory {



		@Override
		protected Validator<PTLink> create(Context context) {
			PtLinkValidator instance = (PtLinkValidator) context.get(NAME);
			if (instance == null) {
				instance = new PtLinkValidator();
				context.put(NAME, instance);
			}
			return instance;
		}

	}

	static {
		ValidatorFactory.factories
		.put(PtLinkValidator.class.getName(), new DefaultValidatorFactory());
	}



}
