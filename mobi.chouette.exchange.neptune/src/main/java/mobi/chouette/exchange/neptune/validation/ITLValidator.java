package mobi.chouette.exchange.neptune.validation;


import java.util.Map;

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
import mobi.chouette.model.ChouetteId;
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
	public void validate(Context context, StopArea target) throws ValidationException
	{
		Context validationContext = (Context) context.get(VALIDATION_CONTEXT);
		Context localContext = (Context) validationContext.get(LOCAL_CONTEXT);
		if (localContext == null || localContext.isEmpty()) return ;
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
		Map<ChouetteId, DataLocation> fileLocations = data.getDataLocations();
		Context stopAreaContext = (Context) validationContext.get(StopAreaValidator.LOCAL_CONTEXT);
		Context lineContext = (Context) validationContext.get(LineValidator.LOCAL_CONTEXT);
		Referential referential = (Referential) context.get(REFERENTIAL);
		Line line = getLine(referential);
		Map<ChouetteId, StopArea> stopAreas = referential.getStopAreas();
		NeptuneImportParameters parameters = (NeptuneImportParameters) context.get(CONFIGURATION);
		NeptuneChouetteIdGenerator neptuneChouetteIdGenerator = (NeptuneChouetteIdGenerator) context.get(CHOUETTEID_GENERATOR);

		String fileName = (String) context.get(FILE_NAME);

		// 2-NEPTUNE-ITL-3 : Check if ITL refers existing StopArea
		prepareCheckPoint(context, ITL_3);

		for (String objectId : localContext.keySet()) 
		{
			Context objectContext = (Context) localContext.get(objectId);
			int lineNumber = ((Integer) objectContext.get(LINE_NUMBER)).intValue();
			int columnNumber = ((Integer) objectContext.get(COLUMN_NUMBER)).intValue();

			String stopAreaId = objectId;
			
			if (!stopAreaContext.containsKey(stopAreaId) || !stopAreas.containsKey(stopAreaId))

			{
				
				ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
				validationReporter.addCheckPointReportError(context, ITL_3, new DataLocation(fileName, lineNumber, columnNumber, (String) objectContext.get(ITL_NAME)), stopAreaId);
				
			} else
			{
				// 2-NEPTUNE-ITL-4 : Check if ITL refers StopArea of ITL
				// type
				prepareCheckPoint(context, ITL_4);
				StopArea stopArea = stopAreas.get(stopAreaId);
				if (!stopArea.getAreaType().equals(ChouetteAreaEnum.ITL))
				{
					Context stopAreaData = (Context) stopAreaContext.get(stopAreaId);
					lineNumber = ((Integer) stopAreaData.get(LINE_NUMBER)).intValue();
					columnNumber = ((Integer) stopAreaData.get(COLUMN_NUMBER)).intValue();
					
					ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
					validationReporter.addCheckPointReportError(context, ITL_4, new DataLocation(fileName, lineNumber, columnNumber, (String) objectContext.get(ITL_NAME)),
							stopArea.getAreaType().toString(),null, fileLocations.get( stopAreaId));
				}
			}

			// 2-NEPTUNE-ITL-5 : Check if ITL refers Line
			String lineId = (String) objectContext.get(LINE_ID);
			ChouetteId lineChouetteId = neptuneChouetteIdGenerator.toChouetteId(lineId, parameters.getDefaultCodespace());
			if (lineId != null)
			{
				prepareCheckPoint(context, ITL_5);
				if (!lineChouetteId.equals(line.getChouetteId()))
				{
					Context lineData = (Context) lineContext.get(neptuneChouetteIdGenerator.toSpecificFormatId(line.getChouetteId(), parameters.getDefaultCodespace(), line));
					lineNumber = ((Integer) lineData.get(LINE_NUMBER)).intValue();
					columnNumber = ((Integer) lineData.get(COLUMN_NUMBER)).intValue();
					

					ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
					validationReporter.addCheckPointReportError(context, ITL_5, new DataLocation(fileName, lineNumber, columnNumber, objectId), lineId,null, fileLocations.get(line.getChouetteId()));
				}

			}



		}
		return ;
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
