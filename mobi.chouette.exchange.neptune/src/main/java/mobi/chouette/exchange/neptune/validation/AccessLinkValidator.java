package mobi.chouette.exchange.neptune.validation;


import mobi.chouette.common.Context;
import mobi.chouette.exchange.neptune.Constant;
import mobi.chouette.exchange.validation.ValidationConstraints;
import mobi.chouette.exchange.validation.ValidationException;
import mobi.chouette.exchange.validation.Validator;
import mobi.chouette.exchange.validation.ValidatorFactory;
import mobi.chouette.exchange.validation.report.FileLocation;
import mobi.chouette.model.AccessLink;
import mobi.chouette.model.util.Referential;

public class AccessLinkValidator extends AbstractValidator implements Validator<AccessLink> , Constant{

	public static String NAME = "AccessLinkValidator";

	private static final String ACCESS_LINK_1 = "2-NEPTUNE-AccessLink-1";
	private static final String ACCESS_LINK_2 = "2-NEPTUNE-AccessLink-2";

	public static final String LOCAL_CONTEXT = "AccessLink";


	public AccessLinkValidator(Context context) 
	{
		addItemToValidation( context, prefix, "AccessLink", 2,
				"E", "E");

	}

	public void addLocation(Context context, String objectId, int lineNumber, int columnNumber)
	{
		Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
		objectContext.put(LINE_NUMBER, Integer.valueOf(lineNumber));
		objectContext.put(COLUMN_NUMBER, Integer.valueOf(columnNumber));

	}

	public void addStartOfLinkId(Context  context, String objectId, String linkId)
	{
		Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
		
			objectContext.put("startOfLinkId", linkId);
		
	}

	public void addEndOfLinkId(Context  context, String objectId, String linkId)
	{
		Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
		
			objectContext.put("endOfLinkId", linkId);
	}



	@Override
	public ValidationConstraints validate(Context context, AccessLink target) throws ValidationException
	{
		Context validationContext = (Context) context.get(VALIDATION_CONTEXT);
		Context localContext = (Context) validationContext.get(LOCAL_CONTEXT);
		Context stopAreaContext = (Context) validationContext.get(StopAreaValidator.LOCAL_CONTEXT);
		if (localContext == null || localContext.isEmpty()) return new ValidationConstraints();

		Referential referential = (Referential) context.get(REFERENTIAL);
		String fileName = (String) context.get(FILE_NAME);

		for (String objectId : localContext.keySet()) 
		{
			Context objectContext = (Context) localContext.get(objectId);
			AccessLink accessLink = referential.getAccessLinks().get(objectId);
			int lineNumber = ((Integer) objectContext.get(LINE_NUMBER)).intValue();
			int columnNumber = ((Integer) objectContext.get(COLUMN_NUMBER)).intValue();
			FileLocation sourceLocation = new FileLocation(fileName, lineNumber, columnNumber);

//			if (stopAreaContext.containsKey(connectionLink.getStartOfLink().getObjectId()) 
//					|| stopAreaContext.containsKey(connectionLink.getEndOfLink().getObjectId()))
//				continue;
//			Map<String, Object> map = new HashMap<String, Object>();
//			map.put("startOfLink", connectionLink.getStartOfLink().getObjectId());
//			map.put("endOfLink", connectionLink.getEndOfLink().getObjectId());
//			Detail errorItem = new Detail(
//					CONNECTION_LINK_1,
//					new Location(sourceLocation,connectionLink.getObjectId()), map);
//			addValidationError(context, CONNECTION_LINK_1, errorItem);

	         boolean step1 = true;
	         // TODO refactor or move in parser
//	         if (!stopAreaContext.containsKey(link.getStopArea())
//	               && !accessPoints.containsKey(link.getAccessPoint()))
	         {
//	            Locator trdLocation = link.sourceLocation();
//	            Map<String, Object> map = new HashMap<String, Object>();
//	            map.put("link", "startOfLink");
//	            map.put("target", link.getStartOfLink());
//	            ReportLocation location = new ReportLocation(sourceFile,
//	                  trdLocation.getLineNumber(), trdLocation.getColumnNumber());
//	            DetailReportItem errorItem = new DetailReportItem(ACCESS_LINK_1,
//	                  link.getObjectId(), Report.STATE.ERROR, location, map);
//	            addValidationError(ACCESS_LINK_1, errorItem);
//	            step1 = false;
	         }
	         if (!step1)
	            continue;
	         // 2-NEPTUNE-AccessLink-2 : check one target as accesspoint and
	         // other as stoparea
	         prepareCheckPoint(context, ACCESS_LINK_2);
	         // TODO refactor or move in parser

//	         if (startObject instanceof StopArea
//	               && endObject instanceof PTAccessPointType)
//	            continue;
//	         if (startObject instanceof PTAccessPointType
//	               && endObject instanceof StopArea)
//	            continue;
//	         Locator trdLocation = link.sourceLocation();
//	         Map<String, Object> map = new HashMap<String, Object>();
//	         map.put("startOfLink", link.getStartOfLink());
//	         map.put("endOfLink", link.getEndOfLink());
//	         map.put("type", startObject.getClass().getSimpleName());
//	         ReportLocation location = new ReportLocation(sourceFile,
//	               trdLocation.getLineNumber(), trdLocation.getColumnNumber());
//	         DetailReportItem errorItem = new DetailReportItem(ACCESS_LINK_2,
//	               link.getObjectId(), Report.STATE.ERROR, location, map);
//	         addValidationError(ACCESS_LINK_2, errorItem);

		}
		return new ValidationConstraints();
	}

	public static class DefaultValidatorFactory extends ValidatorFactory {



		@Override
		protected Validator<AccessLink> create(Context context) {
			AccessLinkValidator instance = (AccessLinkValidator) context.get(NAME);
			if (instance == null) {
				instance = new AccessLinkValidator(context);
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
