package mobi.chouette.exchange.neptune.validation;


import mobi.chouette.common.Context;
import mobi.chouette.exchange.neptune.Constant;
import mobi.chouette.exchange.neptune.model.AreaCentroid;
import mobi.chouette.exchange.validation.ValidationConstraints;
import mobi.chouette.exchange.validation.ValidationException;
import mobi.chouette.exchange.validation.Validator;
import mobi.chouette.exchange.validation.ValidatorFactory;
import mobi.chouette.exchange.validation.report.Detail;
import mobi.chouette.exchange.validation.report.FileLocation;
import mobi.chouette.exchange.validation.report.Location;
import mobi.chouette.model.type.LongLatTypeEnum;

public class AreaCentroidValidator extends AbstractValidator implements Validator<AreaCentroid> , Constant{

	public static final String LONG_LAT_TYPE = "longLatType";

	public static final String CONTAINED_IN = "containedIn";

	public static String NAME = "AreaCentroidValidator";

	private static final String AREA_CENTROID_1 = "2-NEPTUNE-AreaCentroid-1";
	private static final String AREA_CENTROID_2 = "2-NEPTUNE-AreaCentroid-2";

	public static final String LOCAL_CONTEXT = "AreaCentroid";


    @Override
	protected void initializeCheckPoints(Context context)
	{
		addItemToValidation( context, prefix, "AreaCentroid", 2,
				"E", "E");
	}

	public void addLocation(Context context, String objectId, int lineNumber, int columnNumber)
	{
		Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
		objectContext.put(LINE_NUMBER, Integer.valueOf(lineNumber));
		objectContext.put(COLUMN_NUMBER, Integer.valueOf(columnNumber));

	}

	public void addContainedIn(Context  context, String objectId, String containedIn)
	{
		Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
		objectContext.put(CONTAINED_IN, containedIn);
	}

	public void addLongLatType(Context  context, String objectId, LongLatTypeEnum longLatType)
	{
		Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
		objectContext.put(LONG_LAT_TYPE, longLatType);
	}


	@Override
	public ValidationConstraints validate(Context context, AreaCentroid target) throws ValidationException
	{
		Context validationContext = (Context) context.get(VALIDATION_CONTEXT);
		Context localContext = (Context) validationContext.get(LOCAL_CONTEXT);
		Context stopAreaContext = (Context) validationContext.get(StopAreaValidator.LOCAL_CONTEXT);

		String fileName = (String) context.get(FILE_NAME);

		if (localContext == null || localContext.isEmpty())
			return new ValidationConstraints();


		// 2-NEPTUNE-AreaCentroid-1 : check reference to stoparea
		prepareCheckPoint(context, AREA_CENTROID_1);
		for (String objectId : localContext.keySet()) 
		{

			Context objectContext = (Context) localContext.get(objectId);
			int lineNumber = ((Integer) objectContext.get(LINE_NUMBER)).intValue();
			int columnNumber = ((Integer) objectContext.get(COLUMN_NUMBER)).intValue();
			FileLocation sourceLocation = new FileLocation(fileName, lineNumber, columnNumber);

			String containedIn = (String) objectContext.get(CONTAINED_IN);
			if (containedIn == null)
				continue;
			if (!stopAreaContext.containsKey(containedIn))
			{
				Detail errorItem = new Detail(
						AREA_CENTROID_1,
						new Location(sourceLocation,objectId), containedIn);
				addValidationError(context, AREA_CENTROID_1, errorItem);
			}
		}
		// 2-NEPTUNE-AreaCentroid-2 : check centroid projection type as WSG84
		prepareCheckPoint(context,AREA_CENTROID_2);
		for (String objectId : localContext.keySet()) 
		{
			Context objectContext = (Context) localContext.get(objectId);
			int lineNumber = (int) objectContext.get(LINE_NUMBER);
			int columnNumber = (int) objectContext.get(COLUMN_NUMBER);
			FileLocation sourceLocation = new FileLocation(fileName, lineNumber, columnNumber);

			if (objectContext.get(LONG_LAT_TYPE).equals(LongLatTypeEnum.WGS84))
				continue;
			Detail errorItem = new Detail(
					AREA_CENTROID_2,
					new Location(sourceLocation,objectId), objectContext.get(LONG_LAT_TYPE).toString());
			addValidationError(context, AREA_CENTROID_2, errorItem);
		}



		return new ValidationConstraints();
	}

	public static class DefaultValidatorFactory extends ValidatorFactory {

		@Override
		protected Validator<AreaCentroid> create(Context context) {
			AreaCentroidValidator instance = (AreaCentroidValidator) context.get(NAME);
			if (instance == null) {
				instance = new AreaCentroidValidator();
				context.put(NAME, instance);
			}
			return instance;
		}

	}

	static {
		ValidatorFactory.factories
		.put(AreaCentroidValidator.class.getName(), new DefaultValidatorFactory());
	}



}
