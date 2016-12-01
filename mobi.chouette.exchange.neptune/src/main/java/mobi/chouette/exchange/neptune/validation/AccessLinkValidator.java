package mobi.chouette.exchange.neptune.validation;


import java.util.Map;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.neptune.Constant;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.ValidationException;
import mobi.chouette.exchange.validation.Validator;
import mobi.chouette.exchange.validation.ValidatorFactory;
import mobi.chouette.exchange.validation.report.DataLocation;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import mobi.chouette.model.AccessLink;
import mobi.chouette.model.NeptuneIdentifiedObject;

public class AccessLinkValidator extends AbstractValidator implements Validator<AccessLink> , Constant{

	public static final String END_OF_LINK_ID = "endOfLinkId";

	public static final String START_OF_LINK_ID = "startOfLinkId";

	public static String NAME = "AccessLinkValidator";

	private static final String ACCESS_LINK_1 = "2-NEPTUNE-AccessLink-1";
	private static final String ACCESS_LINK_2 = "2-NEPTUNE-AccessLink-2";

	public static final String LOCAL_CONTEXT = "AccessLink";


    @Override
	protected void initializeCheckPoints(Context context)
	{
		addItemToValidation( context, prefix, "AccessLink", 2,
				"E", "E");

	}

    @Override
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
	public void validate(Context context, AccessLink target) throws ValidationException
	{
		Context validationContext = (Context) context.get(VALIDATION_CONTEXT);
		Context localContext = (Context) validationContext.get(LOCAL_CONTEXT);
		Context stopAreasContext = (Context) validationContext.get(StopAreaValidator.LOCAL_CONTEXT);
		Context accessPointsContext = (Context) validationContext.get(AccessPointValidator.LOCAL_CONTEXT);
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
		Map<String, DataLocation> fileLocations = data.getDataLocations();

		if (localContext == null || localContext.isEmpty()) return ;


		// 2-NEPTUNE-AccessLink-1 : check existence of start and end of links
		prepareCheckPoint(context, ACCESS_LINK_1);

		for (String objectId : localContext.keySet()) 
		{
			Context objectContext = (Context) localContext.get(objectId);
			DataLocation sourceLocation = fileLocations.get(objectId);

			boolean step1 = true;

			String start = (String) objectContext.get(START_OF_LINK_ID);
			if (!stopAreasContext.containsKey(start)
					&& !accessPointsContext.containsKey(start))
			{
				ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
				validationReporter.addCheckPointReportError(context, ACCESS_LINK_1, sourceLocation, start);
				step1 = false;
			}
			String end = (String) objectContext.get(END_OF_LINK_ID);
			if (!stopAreasContext.containsKey(end)
					&& !accessPointsContext.containsKey(end))
			{
				
				ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
				validationReporter.addCheckPointReportError(context, ACCESS_LINK_1, sourceLocation, end);
				
				step1 = false;
			}
	         if (!step1)
	             continue;
	         // 2-NEPTUNE-AccessLink-2 : check one target as accesspoint and
	         // other as stoparea
	         prepareCheckPoint(context, ACCESS_LINK_2);
	         if (stopAreasContext.containsKey(start)
	               && accessPointsContext.containsKey(end))
	            continue;
	         if (accessPointsContext.containsKey(start)
	               && stopAreasContext.containsKey(end))
	            continue;
				
				ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
				validationReporter.addCheckPointReportError(context, ACCESS_LINK_2, sourceLocation, start, end);

		}
		return ;
	}

	public static class DefaultValidatorFactory extends ValidatorFactory {



		@Override
		protected Validator<AccessLink> create(Context context) {
			AccessLinkValidator instance = (AccessLinkValidator) context.get(NAME);
			if (instance == null) {
				instance = new AccessLinkValidator();
				context.put(NAME, instance);
			}
			return instance;
		}

	}

	static {
		ValidatorFactory.factories
		.put(AccessLinkValidator.class.getName(), new DefaultValidatorFactory());
	}



}
