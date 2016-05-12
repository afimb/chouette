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
import mobi.chouette.exchange.validation.report.FileLocation;
import mobi.chouette.exchange.validation.report.Location;
import mobi.chouette.model.Line;
import mobi.chouette.model.NeptuneIdentifiedObject;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.util.Referential;

public class ITLValidator extends AbstractValidator implements Validator<StopArea> , Constant{

	public static final String ITL_NAME = "name";

	public static final String LINE_ID = "lineId";

	public static String NAME = "ITLValidator";

	// TODO move tests from StopAreaValidator
	private static final String ITL_1 = "2-NEPTUNE-ITL-1";
	private static final String ITL_2 = "2-NEPTUNE-ITL-2";
	private static final String ITL_3 = "2-NEPTUNE-ITL-3";
	private static final String ITL_4 = "2-NEPTUNE-ITL-4";
	private static final String ITL_5 = "2-NEPTUNE-ITL-5";

	public static final String LOCAL_CONTEXT = "ITL";


    @Override
	protected void initializeCheckPoints(Context context)
	{
		addItemToValidation(context, prefix, "ITL", 5, "E",
				"E", "E", "E", "E");

	}

	public void addLocation(Context context, NeptuneIdentifiedObject object, int lineNumber, int columnNumber)
	{
		addLocation( context,LOCAL_CONTEXT,  object,  lineNumber,  columnNumber);

	}

	public void addLineId(Context  context, String objectId, String lineId)
	{
		Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
		objectContext.put(LINE_ID, lineId);
	}

	public void addName(Context  context, String objectId, String name)
	{
		Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
		objectContext.put(ITL_NAME, name);
	}


	@Override
	public ValidationConstraints validate(Context context, StopArea target) throws ValidationException
	{
		Context validationContext = (Context) context.get(VALIDATION_CONTEXT);
		Context localContext = (Context) validationContext.get(LOCAL_CONTEXT);
		if (localContext == null || localContext.isEmpty()) return new ValidationConstraints();
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
		Map<String, Location> fileLocations = data.getFileLocations();
		Context stopAreaContext = (Context) validationContext.get(StopAreaValidator.LOCAL_CONTEXT);
		Context lineContext = (Context) validationContext.get(LineValidator.LOCAL_CONTEXT);
		Referential referential = (Referential) context.get(REFERENTIAL);
		Line line = getLine(referential);
		Map<String, StopArea> stopAreas = referential.getStopAreas();

		String fileName = (String) context.get(FILE_NAME);

		// 2-NEPTUNE-ITL-3 : Check if ITL refers existing StopArea
		prepareCheckPoint(context, ITL_3);

		for (String objectId : localContext.keySet()) 
		{
			Context objectContext = (Context) localContext.get(objectId);
			int lineNumber = ((Integer) objectContext.get(LINE_NUMBER)).intValue();
			int columnNumber = ((Integer) objectContext.get(COLUMN_NUMBER)).intValue();
			FileLocation sourceLocation = new FileLocation(fileName, lineNumber, columnNumber);

			String stopAreaId = objectId;
			
			if (!stopAreaContext.containsKey(stopAreaId) || !stopAreas.containsKey(stopAreaId))

			{
				Detail errorItem = new Detail(
						ITL_3,
						new Location(sourceLocation,(String) objectContext.get(ITL_NAME)), stopAreaId);
				addValidationError(context, ITL_3, errorItem);
			} else
			{
				// 2-NEPTUNE-ITL-4 : Check if ITL refers StopArea of ITL
				// type
				prepareCheckPoint(context, ITL_4);
				StopArea stopArea = stopAreas.get(stopAreaId);
				if (!stopArea.getAreaType().equals(ChouetteAreaEnum.ITL))
				{
					Detail errorItem = new Detail(
							ITL_4,
							new Location(sourceLocation,(String) objectContext.get(ITL_NAME)), stopArea.getAreaType().toString());
					Context stopAreaData = (Context) stopAreaContext.get(stopAreaId);
					lineNumber = ((Integer) stopAreaData.get(LINE_NUMBER)).intValue();
					columnNumber = ((Integer) stopAreaData.get(COLUMN_NUMBER)).intValue();
					FileLocation targetLocation = new FileLocation(fileName, lineNumber, columnNumber);
					errorItem.getTargets().add(fileLocations.get( stopAreaId));
					addValidationError(context, ITL_4, errorItem);

				}
			}

			// 2-NEPTUNE-ITL-5 : Check if ITL refers Line
			String lineId = (String) objectContext.get(LINE_ID);
			if (lineId != null)
			{
				prepareCheckPoint(context, ITL_5);
				if (!lineId.equals(line.getObjectId()))
				{
					Detail errorItem = new Detail(
							ITL_5,
							new Location(sourceLocation,objectId), lineId);
					Context lineData = (Context) lineContext.get(line.getObjectId());
					lineNumber = ((Integer) lineData.get(LINE_NUMBER)).intValue();
					columnNumber = ((Integer) lineData.get(COLUMN_NUMBER)).intValue();
					FileLocation targetLocation = new FileLocation(fileName, lineNumber, columnNumber);
					errorItem.getTargets().add(fileLocations.get( line.getObjectId()));
					addValidationError(context, ITL_5, errorItem);
				}

			}



		}
		return new ValidationConstraints();
	}

	public static class DefaultValidatorFactory extends ValidatorFactory {



		@Override
		protected Validator<StopArea> create(Context context) {
			ITLValidator instance = (ITLValidator) context.get(NAME);
			if (instance == null) {
				instance = new ITLValidator();
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
