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
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.util.Referential;

public class StopAreaValidator extends AbstractValidator implements Validator<StopArea> , Constant{

	public static String NAME = "StopAreaValidator";

	private static final String STOP_AREA_1 = "2-NEPTUNE-StopArea-1";
	private static final String STOP_AREA_2 = "2-NEPTUNE-StopArea-2";
	private static final String STOP_AREA_3 = "2-NEPTUNE-StopArea-3";
	private static final String STOP_AREA_4 = "2-NEPTUNE-StopArea-4";
	private static final String STOP_AREA_5 = "2-NEPTUNE-StopArea-5";
	private static final String STOP_AREA_6 = "2-NEPTUNE-StopArea-6";
	private static final String ITL_1 = "2-NEPTUNE-ITL-1";
	private static final String ITL_2 = "2-NEPTUNE-ITL-2";

	static final String LOCAL_CONTEXT = "StopArea";


	public StopAreaValidator(Context context) 
	{
		addItemToValidation(context, prefix, "StopArea", 6, "E", "E", "E", "E", "E", "E");

	}

	public void addLocation(Context context, String objectId, int lineNumber, int columnNumber)
	{
		Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
		objectContext.put(LINE_NUMBER, lineNumber);
		objectContext.put(COLUMN_NUMBER, columnNumber);

	}

	public void addAreaCentroidId(Context  context, String objectId, String centroidId)
	{
		Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
		objectContext.put("centroidOfArea", centroidId);

	}

	@SuppressWarnings("unchecked")
	public void addContains(Context context, String objectId, String containsId) {
		Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
		List<String> contains = (List<String>) objectContext.get("contains");
		if (contains == null)
		{
			contains = new ArrayList<>();
			objectContext.put("contains", contains);
		}
		contains.add(containsId);
		
	}



	@Override
	public ValidationConstraints validate(Context context, StopArea target) throws ValidationException
	{
		Context validationContext = (Context) context.get(VALIDATION_CONTEXT);
		Context localContext = (Context) validationContext.get(LOCAL_CONTEXT);
		Context stopPointContext = (Context) validationContext.get(StopPointValidator.LOCAL_CONTEXT);
		Context itlContext = (Context) validationContext.get(ITLValidator.LOCAL_CONTEXT);
		Context areaCentroidContext = (Context) validationContext.get(AreaCentroidValidator.LOCAL_CONTEXT);
		Referential referential = (Referential) context.get(REFERENTIAL);
		Map<String, StopArea> stopAreas = referential.getStopAreas();
		String fileName = (String) context.get(FILE_URL);

		for (String objectId : localContext.keySet()) 
		{
			
	         // TODO 2-NEPTUNE-StopArea-1 : check if StopArea refers in field contains
	         // only stopareas or stoppoints

			
			Context objectContext = (Context) localContext.get(objectId);
			StopArea stopArea = stopAreas.get(objectId);
			int lineNumber = (int) objectContext.get(LINE_NUMBER);
			int columnNumber = (int) objectContext.get(COLUMN_NUMBER);
			FileLocation sourceLocation = new FileLocation(fileName, lineNumber, columnNumber);

			switch (stopArea.getAreaType())
			{
			case StopPlace:
			{
				prepareCheckPoint(context, STOP_AREA_2);
				// 2-NEPTUNE-StopArea-2 : if stoparea is StopPlace :
				// check if it refers only stopareas of type stopplace
				// or commercialstoppoints
				for (StopArea child : stopArea.getContainedStopAreas()) 
				{
					if (!localContext.containsKey(child.getObjectId())) continue;
					if (!child.getAreaType().equals(ChouetteAreaEnum.StopPlace) && 
							!child.getAreaType().equals(ChouetteAreaEnum.CommercialStopPoint))
					{
						// wrong reference type
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("contains", child.getObjectId());
						map.put("type", child.getAreaType().toString());
						map.put("parentType",
								ChouetteAreaEnum.StopPlace.toString());
						Detail errorItem = new Detail(
								STOP_AREA_2,
								new Location(sourceLocation,stopArea.getObjectId()), map);
						addValidationError(context,STOP_AREA_2, errorItem);
					}
				}
				for (StopPoint child : stopArea.getContainedStopPoints()) 
				{
					if (!stopPointContext.containsKey(child.getObjectId())) continue;
					// wrong reference type
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("contains", child.getObjectId());
					map.put("type", "StopPoint");
					map.put("parentType",
							ChouetteAreaEnum.StopPlace.toString());
					Detail errorItem = new Detail(
							STOP_AREA_2,
							new Location(sourceLocation,stopArea.getObjectId()), map);
					addValidationError(context,STOP_AREA_2, errorItem);
				}
			}
			break;
			case CommercialStopPoint:
			{
				// 2-NEPTUNE-StopArea-3 : if stoparea is
				// commercialStopPoint : check if it refers only
				// stopareas of type quay or boardingPosition
				prepareCheckPoint(context,STOP_AREA_3);
				for (StopArea child : stopArea.getContainedStopAreas()) 
				{
					if (!localContext.containsKey(child.getObjectId())) continue;
					if (!child.getAreaType().equals(ChouetteAreaEnum.Quay) && 
							!child.getAreaType().equals(ChouetteAreaEnum.BoardingPosition))
					{
						// wrong reference type
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("contains", child.getObjectId());
						map.put("type", child.getAreaType().toString());
						map.put("parentType",
								ChouetteAreaEnum.StopPlace.toString());
						Detail errorItem = new Detail(
								STOP_AREA_3,
								new Location(sourceLocation,stopArea.getObjectId()), map);
						addValidationError(context,STOP_AREA_3, errorItem);
					}
				}
				for (StopPoint child : stopArea.getContainedStopPoints()) 
				{
					if (!stopPointContext.containsKey(child.getObjectId())) continue;
					// wrong reference type
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("contains", child.getObjectId());
					map.put("type", "StopPoint");
					map.put("parentType",
							ChouetteAreaEnum.StopPlace.toString());
					Detail errorItem = new Detail(
							STOP_AREA_3,
							new Location(sourceLocation,stopArea.getObjectId()), map);
					addValidationError(context,STOP_AREA_3, errorItem);
				}
			}
			break;
			case Quay:
			case BoardingPosition:
			{
				prepareCheckPoint(context,STOP_AREA_4);
				// 2-NEPTUNE-StopArea-4 : if stoparea is quay or
				// boardingPosition : check if it refers only StopPoints
				for (StopArea child : stopArea.getContainedStopAreas()) 
				{
					if (!localContext.containsKey(child.getObjectId())) continue;
					// wrong reference type
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("contains", child.getObjectId());
					map.put("type", child.getAreaType().toString());
					map.put("parentType",
							ChouetteAreaEnum.StopPlace.toString());
					Detail errorItem = new Detail(
							STOP_AREA_4,
							new Location(sourceLocation,stopArea.getObjectId()), map);
					addValidationError(context,STOP_AREA_4, errorItem);

				}
			}
			break;
			case ITL:
			{
				// 2-NEPTUNE-ITL-1 : if stoparea is ITL : check if it
				// refers only non ITL stopAreas
				prepareCheckPoint(context,ITL_1);
				for (StopArea child : stopArea.getContainedStopAreas()) 
				{
					if (!localContext.containsKey(child.getObjectId())) continue;
					if (child.getAreaType().equals(ChouetteAreaEnum.ITL))
					{
						// wrong reference type
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("contains", child.getObjectId());
						map.put("type", child.getAreaType().toString());
						map.put("parentType",
								ChouetteAreaEnum.StopPlace.toString());
						Detail errorItem = new Detail(
								ITL_1,
								new Location(sourceLocation,stopArea.getObjectId()), map);
						addValidationError(context,ITL_1, errorItem);
					}
				}
				for (StopPoint child : stopArea.getContainedStopPoints()) 
				{
					if (!stopPointContext.containsKey(child.getObjectId())) continue;
					// wrong reference type
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("contains", child.getObjectId());
					map.put("type", "StopPoint");
					map.put("parentType",
							ChouetteAreaEnum.StopPlace.toString());
					Detail errorItem = new Detail(
							ITL_1,
							new Location(sourceLocation,stopArea.getObjectId()), map);
					addValidationError(context,ITL_1, errorItem);
				}
				// 2-NEPTUNE-ITL-2 : if stoparea is ITL : check if a ITLType
				// object refers it
				prepareCheckPoint(context,ITL_2);
				Context itlData = (Context) itlContext.get(stopArea.getObjectId());
				if (itlData == null)
				{
					// unused ITL Stop
					Detail errorItem = new Detail(
							ITL_2,
							new Location(sourceLocation,stopArea.getObjectId()));
					addValidationError(context,ITL_2, errorItem);
				}
			}


			}
			if (!stopArea.getAreaType().equals(ChouetteAreaEnum.ITL))
			{
				prepareCheckPoint(context,STOP_AREA_5);
				prepareCheckPoint(context,STOP_AREA_6);
				String centroidId = (String) objectContext.get("centroidOfArea");
				if (centroidId != null)
				{
					// 2-NEPTUNE-StopArea-5 : if stoparea is not ITL : check if
					// it refers an existing areacentroid (replace test
					// fk_centroid_stoparea from XSD)
					Context areaCentroidData = (Context) areaCentroidContext.get(centroidId);
					if (areaCentroidData == null)
					{
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("centroidOfArea", centroidId );
						Detail errorItem = new Detail(
								STOP_AREA_5,
								new Location(sourceLocation,stopArea.getObjectId()), map);
						addValidationError(context,STOP_AREA_5, errorItem);
					} 
					else
					{
						// 2-NEPTUNE-StopArea-6 : if stoparea is not ITL : check
						// if it refers an existing areacentroid which refers
						// the good stoparea.
						String containedIn = (String) areaCentroidData.get("containedIn");
						if (containedIn != null)
						{
							if (!containedIn.equals(
									stopArea.getObjectId()))
							{
								Map<String, Object> map = new HashMap<String, Object>();
								map.put("centroidOfArea", centroidId);
								map.put("containedIn",containedIn);
								Detail errorItem = new Detail(
										STOP_AREA_6,
										new Location(sourceLocation,stopArea.getObjectId()), map);
								addValidationError(context,STOP_AREA_6, errorItem);
							}
						}
					}
				}

			}
		}
		return new ValidationConstraints();
	}

	public static class DefaultValidatorFactory extends ValidatorFactory {



		@Override
		protected Validator<StopArea> create(Context context) {
			StopAreaValidator instance = (StopAreaValidator) context.get(NAME);
			if (instance == null) {
				instance = new StopAreaValidator(context);
				context.put(NAME, instance);
			}
			return instance;
		}

	}

	static {
		ValidatorFactory.factories
		.put(StopAreaValidator.class.getName(), new DefaultValidatorFactory());
	}



}
