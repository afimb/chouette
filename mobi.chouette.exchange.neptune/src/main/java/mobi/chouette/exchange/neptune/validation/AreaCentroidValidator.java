package mobi.chouette.exchange.neptune.validation;


import java.util.Map;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.ChouetteId;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.neptune.Constant;
import mobi.chouette.exchange.neptune.NeptuneChouetteIdGenerator;
import mobi.chouette.exchange.neptune.importer.NeptuneImportParameters;
import mobi.chouette.exchange.neptune.model.AreaCentroid;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.ValidationException;
import mobi.chouette.exchange.validation.Validator;
import mobi.chouette.exchange.validation.ValidatorFactory;
import mobi.chouette.exchange.validation.report.DataLocation;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import mobi.chouette.model.NeptuneIdentifiedObject;
import mobi.chouette.model.type.LongLatTypeEnum;

@Log4j
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

	public void addLocation(Context context, NeptuneIdentifiedObject object, int lineNumber, int columnNumber)
	{
		addLocation( context,LOCAL_CONTEXT,  object,  lineNumber,  columnNumber);

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
	public void validate(Context context, AreaCentroid target) throws ValidationException
	{
		Context validationContext = (Context) context.get(VALIDATION_CONTEXT);
		Context localContext = (Context) validationContext.get(LOCAL_CONTEXT);
		Context stopAreaContext = (Context) validationContext.get(StopAreaValidator.LOCAL_CONTEXT);
		NeptuneImportParameters parameters = (NeptuneImportParameters) context.get(CONFIGURATION);
		NeptuneChouetteIdGenerator neptuneChouetteIdGenerator = (NeptuneChouetteIdGenerator) context.get(CHOUETTEID_GENERATOR);
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
//		Map<String, Location> fileLocations = data.getFileLocations();
		Map<ChouetteId, DataLocation> fileLocations = data.getDataLocations();

		if (localContext == null || localContext.isEmpty())
			return ;


		// 2-NEPTUNE-AreaCentroid-1 : check reference to stoparea
		prepareCheckPoint(context, AREA_CENTROID_1);
		for (String objectId : localContext.keySet()) 
		{

			Context objectContext = (Context) localContext.get(objectId);
//			Location sourceLocation = fileLocations.get(objectId);
			DataLocation sourceLocation = fileLocations.get(neptuneChouetteIdGenerator.toChouetteId(objectId, parameters.getDefaultCodespace(),AreaCentroid.class));


			String containedIn = (String) objectContext.get(CONTAINED_IN);
			if (containedIn == null)
				continue;
			if (!stopAreaContext.containsKey(containedIn))
			{
				ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
				validationReporter.addCheckPointReportError(context, AREA_CENTROID_1, sourceLocation, containedIn);
			}
		}
		// 2-NEPTUNE-AreaCentroid-2 : check centroid projection type as WSG84
		prepareCheckPoint(context,AREA_CENTROID_2);
		for (String objectId : localContext.keySet()) 
		{
			Context objectContext = (Context) localContext.get(objectId);
			
			// log.warn("object id area centroid validator : " + objectId);
			
			if( objectContext == null)
				log.error("Object context is null in area centroid validator");
//			Location sourceLocation = fileLocations.get(objectId);
			// log.warn("Codespace area centroid : " + neptuneChouetteIdGenerator.toChouetteId(objectId, parameters.getDefaultCodespace(),AreaCentroid.class).getCodeSpace() + " technical id area centroid : " + neptuneChouetteIdGenerator.toChouetteId(objectId, parameters.getDefaultCodespace(),AreaCentroid.class).getTechnicalId());
			DataLocation sourceLocation = fileLocations.get(neptuneChouetteIdGenerator.toChouetteId(objectId, parameters.getDefaultCodespace(),AreaCentroid.class));
			
			if( objectContext.get(LONG_LAT_TYPE) != null) {
				if (objectContext.get(LONG_LAT_TYPE).equals(LongLatTypeEnum.WGS84))
					continue;
			
				ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
				validationReporter.addCheckPointReportError(context, AREA_CENTROID_2, sourceLocation, objectContext.get(LONG_LAT_TYPE).toString());
			}
		}



		return ;
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
