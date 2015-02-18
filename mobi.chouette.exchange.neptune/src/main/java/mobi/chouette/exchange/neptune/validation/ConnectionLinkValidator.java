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
import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.util.Referential;

public class ConnectionLinkValidator extends AbstractValidator implements Validator<ConnectionLink> , Constant{

	public static String NAME = "ConnectionLinkValidator";

	private static final String CONNECTION_LINK_1 = "2-NEPTUNE-ConnectionLink-1";

	public static final String LOCAL_CONTEXT = "ConnectionLink";


	public ConnectionLinkValidator(Context context) 
	{
		addItemToValidation( context, prefix, "ConnectionLink", 1,
				"E");

	}

	public void addLocation(Context context, String objectId, int lineNumber, int columnNumber)
	{
		Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
		objectContext.put(LINE_NUMBER, Integer.valueOf(lineNumber));
		objectContext.put(COLUMN_NUMBER, Integer.valueOf(columnNumber));

	}

	public void addStartOfLink(Context context, String objectId, String linkId)
	{
		Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
		objectContext.put("startOfLink", linkId);

	}

	public void addEndOfLink(Context context, String objectId, String linkId)
	{
		Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
		objectContext.put("endOfLink", linkId);

	}


	@Override
	public ValidationConstraints validate(Context context, ConnectionLink target) throws ValidationException
	{
		Context validationContext = (Context) context.get(VALIDATION_CONTEXT);
		Context localContext = (Context) validationContext.get(LOCAL_CONTEXT);
		Context stopAreaContext = (Context) validationContext.get(StopAreaValidator.LOCAL_CONTEXT);
		if (localContext == null || localContext.isEmpty()) return new ValidationConstraints();

		Referential referential = (Referential) context.get(REFERENTIAL);
		String fileName = (String) context.get(FILE_NAME);

		// 2-NEPTUNE-ConnectionLink-1 : check presence of start or end of link
		prepareCheckPoint(context, CONNECTION_LINK_1);
		for (String objectId : localContext.keySet()) 
		{
			Context objectContext = (Context) localContext.get(objectId);
			ConnectionLink connectionLink = referential.getConnectionLinks().get(objectId);
			int lineNumber = ((Integer) objectContext.get(LINE_NUMBER)).intValue();
			int columnNumber = ((Integer) objectContext.get(COLUMN_NUMBER)).intValue();
			FileLocation sourceLocation = new FileLocation(fileName, lineNumber, columnNumber);

			if (stopAreaContext.containsKey(connectionLink.getStartOfLink().getObjectId()) 
					|| stopAreaContext.containsKey(connectionLink.getEndOfLink().getObjectId()))
				continue;
			Detail errorItem = new Detail(
					CONNECTION_LINK_1,
					new Location(sourceLocation,connectionLink.getObjectId()));
			addValidationError(context, CONNECTION_LINK_1, errorItem);

		}
		return new ValidationConstraints();
	}

	public static class DefaultValidatorFactory extends ValidatorFactory {



		@Override
		protected Validator<ConnectionLink> create(Context context) {
			ConnectionLinkValidator instance = (ConnectionLinkValidator) context.get(NAME);
			if (instance == null) {
				instance = new ConnectionLinkValidator(context);
				context.put(NAME, instance);
			}
			return instance;
		}

	}

	static {
		ValidatorFactory.factories
		.put(ConnectionLinkValidator.class.getName(), new DefaultValidatorFactory());
	}



}
