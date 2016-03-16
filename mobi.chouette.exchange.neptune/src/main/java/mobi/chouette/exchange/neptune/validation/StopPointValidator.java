package mobi.chouette.exchange.neptune.validation;


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
import mobi.chouette.model.NeptuneIdentifiedObject;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.type.LongLatTypeEnum;

public class StopPointValidator extends AbstractValidator implements Validator<StopPoint> , Constant{

	public static final String PT_NETWORK_ID_SHORTCUT = "ptNetworkIdShortcut";

	public static final String LINE_ID_SHORTCUT = "lineIdShortcut";

	private static final String CONTAINED_ID = "containedIn";
	
	public static final String LONG_LAT_TYPE = "longLatType";


	public static String NAME = "StopPointValidator";
	
	private static final String STOP_POINT_1 = "2-NEPTUNE-StopPoint-1";
	private static final String STOP_POINT_2 = "2-NEPTUNE-StopPoint-2";
	private static final String STOP_POINT_3 = "2-NEPTUNE-StopPoint-3";
	private static final String STOP_POINT_4 = "2-NEPTUNE-StopPoint-4";

	public static final String LOCAL_CONTEXT = "StopPoint";



    @Override
	protected void initializeCheckPoints(Context context)
	{
		addItemToValidation(context, prefix, "StopPoint", 4, "E", "E", "E", "E");

	}

	public void addLocation(Context context, NeptuneIdentifiedObject object, int lineNumber, int columnNumber)
	{
		addLocation( context,LOCAL_CONTEXT,  object,  lineNumber,  columnNumber);
		
	}
	
	public void addLineIdShortcut(Context  context, String objectId, String lineIdShortcut)
	{
		Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
		objectContext.put(LINE_ID_SHORTCUT, lineIdShortcut);

	}
	public void addPtNetworkIdShortcut(Context  context, String objectId, String ptNetworkIdShortcut)
	{
		Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
		objectContext.put(PT_NETWORK_ID_SHORTCUT, ptNetworkIdShortcut);

	}

	public void addContainedIn(Context  context, String objectId, String containedIn)
	{
		Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
		objectContext.put(CONTAINED_ID, containedIn);

	}

	public void addLongLatType(Context  context, String objectId, LongLatTypeEnum longLatType)
	{
		Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
		objectContext.put(LONG_LAT_TYPE, longLatType);
	}
	

	@Override
	public ValidationConstraints validate(Context context, StopPoint target) throws ValidationException
	{
		Context validationContext = (Context) context.get(VALIDATION_CONTEXT);
		Context localContext = (Context) validationContext.get(LOCAL_CONTEXT);
		if (localContext == null || localContext.isEmpty()) return new ValidationConstraints();
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
		Map<String, Location> fileLocations = data.getFileLocations();
		Context stopAreasContext = (Context) validationContext.get(StopAreaValidator.LOCAL_CONTEXT);
		Context linesContext = (Context) validationContext.get(LineValidator.LOCAL_CONTEXT);
		Context networksContext = (Context) validationContext.get(PTNetworkValidator.LOCAL_CONTEXT);


	     // 2-NEPTUNE-StopPoint-3 : check existence of stoparea referred by
	      // containedIn
	      prepareCheckPoint(context,STOP_POINT_3);

	      // 2-NEPTUNE-StopPoint-4 : check stopPoint projection type as WSG84
	      prepareCheckPoint(context,STOP_POINT_4);

		for (String objectId : localContext.keySet()) 
		{
			Context objectContext = (Context) localContext.get(objectId);

	         if (objectContext.containsKey(LINE_ID_SHORTCUT))
	         {
	            // 2-NEPTUNE-StopPoint-1 : check existence of line referred by lineIdShortcut
	            prepareCheckPoint(context,STOP_POINT_1);
	            String lineIdShortCut = (String) objectContext.get(LINE_ID_SHORTCUT);
	            if (!linesContext.containsKey(lineIdShortCut))
	            {
					Detail errorItem = new Detail(
							STOP_POINT_1,
							fileLocations.get(objectId), lineIdShortCut);
					addValidationError(context,STOP_POINT_1, errorItem);
	            }

	         }
	         if (objectContext.containsKey(PT_NETWORK_ID_SHORTCUT))
	         {
	            // 2-NEPTUNE-StopPoint-2 : check existence of PTNetwork referred  by ptNetworkIdShortcut
	            prepareCheckPoint(context,STOP_POINT_2);
	            String ptNetworkIdShortcut = (String) objectContext.get(PT_NETWORK_ID_SHORTCUT);
	            if (!networksContext.containsKey(ptNetworkIdShortcut))
	            {
					Detail errorItem = new Detail(
							STOP_POINT_2,
							fileLocations.get(objectId), ptNetworkIdShortcut);
					addValidationError(context,STOP_POINT_2, errorItem);
	            }

	         }

	            String containedIn = (String) objectContext.get(CONTAINED_ID);
	         if (!stopAreasContext.containsKey(containedIn))
	         {
				Detail errorItem = new Detail(
							STOP_POINT_3,
							fileLocations.get(objectId), containedIn);
					addValidationError(context,STOP_POINT_3, errorItem);
	         }

	         if (!LongLatTypeEnum.WGS84.equals(objectContext.get(LONG_LAT_TYPE)))
	         {
					Detail errorItem = new Detail(
							STOP_POINT_4,
							fileLocations.get(objectId), objectContext.get(LONG_LAT_TYPE).toString());
					addValidationError(context,STOP_POINT_4, errorItem);
	         }
		
		}
		return new ValidationConstraints();
	}

	public static class DefaultValidatorFactory extends ValidatorFactory {

		

		@Override
		protected Validator<StopPoint> create(Context context) {
			StopPointValidator instance = (StopPointValidator) context.get(NAME);
			if (instance == null) {
				instance = new StopPointValidator();
				context.put(NAME, instance);
			}
			return instance;
		}

	}

	static {
		ValidatorFactory.factories
		.put(StopPointValidator.class.getName(), new DefaultValidatorFactory());
	}



}
