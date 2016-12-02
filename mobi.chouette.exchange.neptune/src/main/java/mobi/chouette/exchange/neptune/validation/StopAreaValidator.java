package mobi.chouette.exchange.neptune.validation;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.ChouetteId;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.neptune.Constant;
import mobi.chouette.exchange.neptune.NeptuneChouetteIdGenerator;
import mobi.chouette.exchange.neptune.importer.NeptuneImportParameters;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.ValidationException;
import mobi.chouette.exchange.validation.Validator;
import mobi.chouette.exchange.validation.ValidatorFactory;
import mobi.chouette.exchange.validation.report.DataLocation;
import mobi.chouette.exchange.validation.report.ValidationReporter;
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

	public static final String LOCAL_CONTEXT = "StopArea";


    @Override
	protected void initializeCheckPoints(Context context)
	{
		addItemToValidation(context, prefix, "StopArea", 6, "E", "E", "E", "E", "E", "E");

		try {
			RoutingConstraintValidator validator = (RoutingConstraintValidator) ValidatorFactory.create(RoutingConstraintValidator.class.getName(), context);
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
	public void validate(Context context, StopArea target) throws ValidationException
	{
		Context validationContext = (Context) context.get(VALIDATION_CONTEXT);
		Context localContext = (Context) validationContext.get(LOCAL_CONTEXT);
		NeptuneImportParameters parameters = (NeptuneImportParameters) context.get(CONFIGURATION);
		NeptuneChouetteIdGenerator neptuneChouetteIdGenerator = (NeptuneChouetteIdGenerator) context.get(CHOUETTEID_GENERATOR);
		Set<String> objectIds = (Set<String>) validationContext.get(OBJECT_IDS);

		if (localContext == null || localContext.isEmpty()) return ;
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
		Map<ChouetteId, DataLocation> fileLocations = data.getDataLocations();

		Context stopPointContext = (Context) validationContext.get(StopPointValidator.LOCAL_CONTEXT);
		Context itlContext = (Context) validationContext.get(RoutingConstraintValidator.ITL_LOCAL_CONTEXT);
		if (itlContext == null) itlContext = new Context(); 
		Referential referential = (Referential) context.get(REFERENTIAL);
		Map<ChouetteId, StopArea> stopAreas = referential.getStopAreas();

		prepareCheckPoint(context,STOP_AREA_1);
		
		for (String objectId : localContext.keySet()) 
		{
				Context objectContext = (Context) localContext.get(objectId);
				
				ChouetteId objectChouetteId = neptuneChouetteIdGenerator.toChouetteId(objectId, parameters.getDefaultCodespace(),StopArea.class);
				
				List<String> contains = (List<String>) objectContext.get(CONTAINS2);
				
				if( contains != null ) {
				//  2-NEPTUNE-StopArea-1 : check if StopArea refers in field contains
					for (String containedId : contains) 

					{
						// only stopareas or stoppoints or external ref
						
						if (objectIds.contains(containedId) && !localContext.containsKey(containedId) && !stopPointContext.containsKey(containedId))
						{
							// wrong or unknown reference type
							ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
							validationReporter.addCheckPointReportError(context, STOP_AREA_1, fileLocations.get(objectChouetteId), containedId);
							
						}
					}

				}
	
				StopArea stopArea = stopAreas.get(objectChouetteId);
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
						String specificChildId = neptuneChouetteIdGenerator.toSpecificFormatId(child.getChouetteId(), parameters.getDefaultCodespace(), child);
						if (localContext.containsKey(specificChildId))
						{
							if (!child.getAreaType().equals(ChouetteAreaEnum.StopPlace) && 
									!child.getAreaType().equals(ChouetteAreaEnum.CommercialStopPoint))
							{
								// wrong reference type
								ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
								validationReporter.addCheckPointReportError(context, STOP_AREA_2, fileLocations.get(child.getChouetteId()), child.getAreaType().toString(),ChouetteAreaEnum.StopPlace.toString());
							}
						}
						else if (stopPointContext.containsKey(specificChildId))
						{
							// wrong reference type
							ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
							validationReporter.addCheckPointReportError(context, STOP_AREA_2, fileLocations.get(stopArea.getChouetteId()), "StopPoint",ChouetteAreaEnum.StopPlace.toString());
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
						if (localContext.containsKey(neptuneChouetteIdGenerator.toSpecificFormatId(child.getChouetteId(), parameters.getDefaultCodespace(), child))) 
						{
							if (!child.getAreaType().equals(ChouetteAreaEnum.Quay) && 
									!child.getAreaType().equals(ChouetteAreaEnum.BoardingPosition))
							{
								// wrong reference type
								ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
								validationReporter.addCheckPointReportError(context, STOP_AREA_3, fileLocations.get(stopArea.getChouetteId()), child.getAreaType().toString(),ChouetteAreaEnum.CommercialStopPoint.toString());
							}
						}
						else if (stopPointContext.containsKey(neptuneChouetteIdGenerator.toSpecificFormatId(child.getChouetteId(), parameters.getDefaultCodespace(), child)))
						{
							// wrong reference type
							ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
							validationReporter.addCheckPointReportError(context, STOP_AREA_3, fileLocations.get(stopArea.getChouetteId()), "StopPoint",ChouetteAreaEnum.CommercialStopPoint.toString());
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
						if (localContext.containsKey(neptuneChouetteIdGenerator.toSpecificFormatId(child.getChouetteId(), parameters.getDefaultCodespace(), child)))
						{
							StopArea area = stopAreas.get(neptuneChouetteIdGenerator.toSpecificFormatId(child.getChouetteId(), parameters.getDefaultCodespace(), child));
							// wrong reference type
							ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
							validationReporter.addCheckPointReportError(context, STOP_AREA_4, fileLocations.get(stopArea.getChouetteId()), area.getAreaType().toString(),stopArea.getAreaType().toString(),fileLocations.get(stopArea.getChouetteId()));
						}
					}
				}
				break;		
				}
		}
		
		return ;
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
