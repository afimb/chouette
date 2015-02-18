package mobi.chouette.exchange.neptune.validation;


import java.util.HashMap;
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
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.util.Referential;

public class ITLValidator extends AbstractValidator implements Validator<RoutingConstraint> , Constant{

	public static String NAME = "ITLValidator";

	// TODO move tests from StopAreaValidator
	private static final String ITL_1 = "2-NEPTUNE-ITL-1";
	private static final String ITL_2 = "2-NEPTUNE-ITL-2";
	private static final String ITL_3 = "2-NEPTUNE-ITL-3";
	private static final String ITL_4 = "2-NEPTUNE-ITL-4";
	private static final String ITL_5 = "2-NEPTUNE-ITL-5";

	public static final String LOCAL_CONTEXT = "ITL";


	public ITLValidator(Context context) 
	{
		addItemToValidation(context, prefix, "ITL", 5, "E",
				"E", "E", "E", "E");

	}

	public void addLocation(Context context, String objectId, int lineNumber, int columnNumber)
	{
		Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
		objectContext.put(LINE_NUMBER, Integer.valueOf(lineNumber));
		objectContext.put(COLUMN_NUMBER, Integer.valueOf(columnNumber));

	}

	public void addLineId(Context  context, String objectId, String lineId)
	{
		Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
		objectContext.put("lineId", lineId);
	}

	public void addName(Context  context, String objectId, String name)
	{
		Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
		objectContext.put("name", name);
	}

	@Override
	public ValidationConstraints validate(Context context, RoutingConstraint target) throws ValidationException
	{
		Context validationContext = (Context) context.get(VALIDATION_CONTEXT);
		Context localContext = (Context) validationContext.get(LOCAL_CONTEXT);
		if (localContext == null || localContext.isEmpty()) return new ValidationConstraints();
		Context stopAreaContext = (Context) validationContext.get(StopAreaValidator.LOCAL_CONTEXT);
		Referential referential = (Referential) context.get(REFERENTIAL);
		Line line = referential.getLines().values().iterator().next(); 
		Map<String, StopArea> stopAreas = referential.getStopAreas();

		String fileName = (String) context.get(FILE_URL);

		// 2-NEPTUNE-ITL-3 : Check if ITL refers existing StopArea
		prepareCheckPoint(context, ITL_3);

		for (String objectId : localContext.keySet()) 
		{
			Context objectContext = (Context) localContext.get(objectId);
			int lineNumber = ((Integer) objectContext.get(LINE_NUMBER)).intValue();
			int columnNumber = ((Integer) objectContext.get(COLUMN_NUMBER)).intValue();
			FileLocation sourceLocation = new FileLocation(fileName, lineNumber, columnNumber);

			String stopAreaId = (String) objectContext.get("areaId");

			if (!stopAreaContext.containsKey(stopAreaId) || !stopAreas.containsKey(stopAreaId))

			{
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("areaId", stopAreaId);
				map.put("name", (String) objectContext.get("name"));
				Detail errorItem = new Detail(
						ITL_3,
						new Location(sourceLocation,objectId), map);
				addValidationError(context, ITL_3, errorItem);
			} else
			{
				// 2-NEPTUNE-ITL-4 : Check if ITL refers StopArea of ITL
				// type
				prepareCheckPoint(context, ITL_4);
				StopArea stopArea = stopAreas.get(stopAreaId);
				if (!stopArea.getAreaType().equals(ChouetteAreaEnum.ITL))
				{
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("areaId", objectId);
					map.put("type", stopArea.getAreaType().toString());
					map.put("name", (String) objectContext.get("name"));
					Detail errorItem = new Detail(
							ITL_4,
							new Location(sourceLocation,objectId), map);
					addValidationError(context, ITL_4, errorItem);

				}
			}

			// 2-NEPTUNE-ITL-5 : Check if ITL refers Line
			String lineId = (String) objectContext.get("lineId");
			if (lineId != null)
			{
				prepareCheckPoint(context, ITL_5);
				if (lineId != line.getObjectId())
				{
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("name", (String) objectContext.get("name"));
					map.put("lineId", line.getObjectId());
					map.put("lineIdShortCut", lineId);
					Detail errorItem = new Detail(
							ITL_5,
							new Location(sourceLocation,objectId), map);
					addValidationError(context, ITL_5, errorItem);
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
