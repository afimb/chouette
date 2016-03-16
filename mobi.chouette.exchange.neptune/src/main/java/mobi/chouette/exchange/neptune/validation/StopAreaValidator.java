package mobi.chouette.exchange.neptune.validation;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.extern.log4j.Log4j;
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
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.util.Referential;
@Log4j
public class StopAreaValidator extends AbstractValidator implements Validator<StopArea> , Constant{

	public static final String CONTAINS2 = "contains";

	public static final String CENTROID_OF_AREA = "centroidOfArea";

	public static String NAME = "StopAreaValidator";

	private static final String STOP_AREA_1 = "2-NEPTUNE-StopArea-1";
	private static final String STOP_AREA_2 = "2-NEPTUNE-StopArea-2";
	private static final String STOP_AREA_3 = "2-NEPTUNE-StopArea-3";
	private static final String STOP_AREA_4 = "2-NEPTUNE-StopArea-4";
	private static final String STOP_AREA_5 = "2-NEPTUNE-StopArea-5";
	private static final String STOP_AREA_6 = "2-NEPTUNE-StopArea-6";

	// TODO move tests to ITLValidator
	private static final String ITL_1 = "2-NEPTUNE-ITL-1";
	private static final String ITL_2 = "2-NEPTUNE-ITL-2";

	public static final String LOCAL_CONTEXT = "StopArea";


    @Override
	protected void initializeCheckPoints(Context context)
	{
		addItemToValidation(context, prefix, "StopArea", 6, "E", "E", "E", "E", "E", "E");

		try {
			ITLValidator validator = (ITLValidator) ValidatorFactory.create(ITLValidator.class.getName(), context);
			validator.initializeCheckPoints(context);
		} catch (ClassNotFoundException e) {
		}


	}

	public void addLocation(Context context, NeptuneIdentifiedObject object, int lineNumber, int columnNumber)
	{
		if (object == null) throw new NullPointerException("null object");
		addLocation( context,LOCAL_CONTEXT,  object,  lineNumber,  columnNumber);
	}

	public void addAreaCentroidId(Context  context, String objectId, String centroidId)
	{
		if (objectId == null) throw new NullPointerException("null objectId");
		Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
		objectContext.put(CENTROID_OF_AREA, centroidId);

	}

	@SuppressWarnings("unchecked")
	public void addContains(Context context, String objectId, String containsId) {
		if (objectId == null) throw new NullPointerException("null objectId");
		Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
		List<String> contains = (List<String>) objectContext.get(CONTAINS2);
		if (contains == null)
		{
			contains = new ArrayList<>();
			objectContext.put(CONTAINS2, contains);
		}
		contains.add(containsId);

	}



	@SuppressWarnings("unchecked")
	@Override
	public ValidationConstraints validate(Context context, StopArea target) throws ValidationException
	{
		Context validationContext = (Context) context.get(VALIDATION_CONTEXT);
		Context localContext = (Context) validationContext.get(LOCAL_CONTEXT);
		Set<String> objectIds = (Set<String>) validationContext.get(OBJECT_IDS);

		if (localContext == null || localContext.isEmpty()) return new ValidationConstraints();
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
		Map<String, Location> fileLocations = data.getFileLocations();
		Context stopPointContext = (Context) validationContext.get(StopPointValidator.LOCAL_CONTEXT);
		Context itlContext = (Context) validationContext.get(ITLValidator.LOCAL_CONTEXT);
		if (itlContext == null) itlContext = new Context(); 
		Context areaCentroidContext = (Context) validationContext.get(AreaCentroidValidator.LOCAL_CONTEXT);
		Referential referential = (Referential) context.get(REFERENTIAL);
		Map<String, StopArea> stopAreas = referential.getStopAreas();

		prepareCheckPoint(context,STOP_AREA_1);

		for (String objectId : localContext.keySet()) 
		{
			Context objectContext = (Context) localContext.get(objectId);

			List<String> contains = (List<String>) objectContext.get(CONTAINS2);
			//  2-NEPTUNE-StopArea-1 : check if StopArea refers in field contains
			for (String containedId : contains) 
			{
				// only stopareas or stoppoints or external ref
				
				if (objectIds.contains(containedId) && !localContext.containsKey(containedId) && !stopPointContext.containsKey(containedId))
				{
					// wrong or unknown reference type
					Detail errorItem = new Detail(
							STOP_AREA_1,
							fileLocations.get(objectId), containedId);
					addValidationError(context,STOP_AREA_1, errorItem);
				}
			}

			StopArea stopArea = stopAreas.get(objectId);
			if (stopArea == null) 
			{
				log.error("null area " +objectId);
				continue;
			}
			if (stopArea.getAreaType() == null)
			{
				log.error("null area type " +stopArea);
				continue;
			}

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
					if (localContext.containsKey(child.getObjectId()))
					{
						if (!child.getAreaType().equals(ChouetteAreaEnum.StopPlace) && 
								!child.getAreaType().equals(ChouetteAreaEnum.CommercialStopPoint))
						{
							// wrong reference type
							Detail errorItem = new Detail(
									STOP_AREA_2,
									fileLocations.get(stopArea.getObjectId()), child.getAreaType().toString(),ChouetteAreaEnum.StopPlace.toString());
							errorItem.getTargets().add(fileLocations.get( child.getObjectId()));

							addValidationError(context,STOP_AREA_2, errorItem);
						}
					}
					else if (stopPointContext.containsKey(child.getObjectId()))
					{
						// wrong reference type
						Detail errorItem = new Detail(
								STOP_AREA_2,
								fileLocations.get(stopArea.getObjectId()), "StopPoint",ChouetteAreaEnum.StopPlace.toString());
						errorItem.getTargets().add(fileLocations.get(child.getObjectId()));
						addValidationError(context,STOP_AREA_2, errorItem);

					}
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
					if (localContext.containsKey(child.getObjectId())) 
					{
						if (!child.getAreaType().equals(ChouetteAreaEnum.Quay) && 
								!child.getAreaType().equals(ChouetteAreaEnum.BoardingPosition))
						{
							// wrong reference type
							Detail errorItem = new Detail(
									STOP_AREA_3,
									fileLocations.get(stopArea.getObjectId()), child.getAreaType().toString(),ChouetteAreaEnum.CommercialStopPoint.toString());
							errorItem.getTargets().add(fileLocations.get( child.getObjectId()));
							addValidationError(context,STOP_AREA_3, errorItem);
						}
					}
					else if (stopPointContext.containsKey(child.getObjectId()))
					{
						// wrong reference type
						Detail errorItem = new Detail(
								STOP_AREA_3,
								fileLocations.get(stopArea.getObjectId()), "StopPoint",ChouetteAreaEnum.CommercialStopPoint.toString());
						errorItem.getTargets().add(fileLocations.get( child.getObjectId()));
						addValidationError(context,STOP_AREA_3, errorItem);

					}
				}
			}
			break;
			case Quay:
			case BoardingPosition:
			{
				prepareCheckPoint(context,STOP_AREA_4);
				// 2-NEPTUNE-StopArea-4 : if stoparea is quay or
				// boardingPosition : check if it refers only StopPoints
				for (StopPoint child : stopArea.getContainedStopPoints()) 
				{
					if (localContext.containsKey(child.getObjectId()))
					{
						StopArea area = stopAreas.get(child.getObjectId());
						// wrong reference type
						Detail errorItem = new Detail(
								STOP_AREA_4,
								fileLocations.get(stopArea.getObjectId()), area.getAreaType().toString(),stopArea.getAreaType().toString());
						errorItem.getTargets().add(fileLocations.get( child.getObjectId()));
						addValidationError(context,STOP_AREA_4, errorItem);
					}
				}
			}
			break;
			case ITL:
			{
				// 2-NEPTUNE-ITL-1 : if stoparea is ITL : check if it
				// refers only non ITL stopAreas
				prepareCheckPoint(context,ITL_1);
				for (StopArea child : stopArea.getRoutingConstraintAreas()) 
				{
					if (localContext.containsKey(child.getObjectId())) 
					{
						if (child.getAreaType().equals(ChouetteAreaEnum.ITL))
						{
							// wrong reference type

							Detail errorItem = new Detail(
									ITL_1,
									fileLocations.get(stopArea.getObjectId()), child.getAreaType().toString(),ChouetteAreaEnum.ITL.toString());
							errorItem.getTargets().add(fileLocations.get(child.getObjectId()));
							addValidationError(context,ITL_1, errorItem);
						}
					}
					else if (stopPointContext.containsKey(child.getObjectId()))
					{
						// wrong reference type
						Detail errorItem = new Detail(
								ITL_1,
								fileLocations.get(stopArea.getObjectId()), "StopPoint",ChouetteAreaEnum.ITL.toString());
						errorItem.getTargets().add(fileLocations.get( child.getObjectId()));
						addValidationError(context,ITL_1, errorItem);

					}
					
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
							fileLocations.get(stopArea.getObjectId()));
					addValidationError(context,ITL_2, errorItem);
				}

			}


			}
			if (!stopArea.getAreaType().equals(ChouetteAreaEnum.ITL))
			{
				prepareCheckPoint(context,STOP_AREA_5);
				prepareCheckPoint(context,STOP_AREA_6);
				String centroidId = (String) objectContext.get(CENTROID_OF_AREA);
				if (centroidId != null)
				{
					// 2-NEPTUNE-StopArea-5 : if stoparea is not ITL : check if
					// it refers an existing areacentroid (replace test
					// fk_centroid_stoparea from XSD)
					Context areaCentroidData = (Context) areaCentroidContext.get(centroidId);
					if (areaCentroidData == null)
					{
						Detail errorItem = new Detail(
								STOP_AREA_5,
								fileLocations.get(stopArea.getObjectId()), centroidId);
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
								Detail errorItem = new Detail(
										STOP_AREA_6,
										fileLocations.get(stopArea.getObjectId()), containedIn);
								errorItem.getTargets().add(fileLocations.get( centroidId));
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
				instance = new StopAreaValidator();
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
