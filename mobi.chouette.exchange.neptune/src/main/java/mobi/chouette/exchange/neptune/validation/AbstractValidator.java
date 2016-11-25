package mobi.chouette.exchange.neptune.validation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.neptune.Constant;
import mobi.chouette.exchange.neptune.NeptuneChouetteIdGenerator;
import mobi.chouette.exchange.neptune.importer.NeptuneImportParameters;
import mobi.chouette.exchange.neptune.model.AreaCentroid;
import mobi.chouette.exchange.neptune.model.PTLink;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.report.DataLocation;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import mobi.chouette.model.Line;
import mobi.chouette.model.NeptuneIdentifiedObject;
import mobi.chouette.model.util.Referential;

@Log4j
public abstract class AbstractValidator implements Constant {

	protected static final String prefix = "2-NEPTUNE-";

	protected static final String OBJECT_IDS = "encontered_ids";

	@SuppressWarnings("unchecked")
	public static void resetContext(Context context) {
		Context validationContext = (Context) context.get(VALIDATION_CONTEXT);
		if (validationContext != null) {
			for (String key : validationContext.keySet()) {
				if (key.equals(OBJECT_IDS)) {
					Set<String> objects = (Set<String>) validationContext.get(key);
					objects.clear();
				} else {
					Context localContext = (Context) validationContext.get(key);
					localContext.clear();
				}

			}
		}
	}

	@SuppressWarnings("unchecked")
	protected static Context getObjectContext(Context context, String localContextName, String objectId) {
		log.info("AbstractValidator object id : " + objectId);
		log.info("AbstractValidator OK1");
		Context objectContext = null;
		if (objectId != null) {
			Context validationContext = (Context) context.get(VALIDATION_CONTEXT);
			log.info("AbstractValidator OK2");
			if (validationContext == null) {
				validationContext = new Context();
				context.put(VALIDATION_CONTEXT, validationContext);
				validationContext.put(OBJECT_IDS, new HashSet<String>());
			}
			log.info("AbstractValidator OK3");
			Set<String> objectIds = (Set<String>) validationContext.get(OBJECT_IDS);
			log.info("AbstractValidator OK4");
			objectIds.add(objectId);
			log.info("AbstractValidator OK5");
			Context localContext = (Context) validationContext.get(localContextName);
			log.info("AbstractValidator OK6");
			if (localContext == null) {
				localContext = new Context();
				validationContext.put(localContextName, localContext);
			}
			log.info("AbstractValidator OK7");
			objectContext = (Context) localContext.get(objectId);
			if (objectContext == null) {
				objectContext = new Context();
				localContext.put(objectId, objectContext);
			}
			log.info("AbstractValidator OK8");
		}
		return objectContext;

	}

	protected static void addItemToValidation(Context context, String prefix, String name, int count,
			String... severities) {
//		ValidationReport validationReport = (ValidationReport) context.get(VALIDATION_REPORT);
//		for (int i = 1; i <= count; i++) {
//			String key = prefix + name + "-" + i;
//			if (validationReport.findCheckPointByName(key) == null) {
//				if (severities[i - 1].equals("W")) {
//					validationReport.addCheckPoint(
//							new CheckPoint(key, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
//				} else {
//					validationReport.addCheckPoint(
//							new CheckPoint(key, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
//				}
//			}
//		}
//			
		ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
		validationReporter.addItemToValidationReport(context, prefix, name, count, severities);
		return;
	}

	/**
	 * pass checkpoint to ok if uncheck
	 * 
	 * @param checkPointKey
	 */
	protected void prepareCheckPoint(Context context, String checkPointKey) {
//		ValidationReport validationReport = (ValidationReport) context.get(VALIDATION_REPORT);
//		CheckPoint checkPoint = validationReport.findCheckPointByName(checkPointKey);
//		if (checkPoint == null) {
//			initializeCheckPoints(context);
//			checkPoint = validationReport.findCheckPointByName(checkPointKey);
//		}
//		if (checkPoint.getDetails().isEmpty())
//			checkPoint.setState(CheckPoint.RESULT.OK);
		
		ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
		
		if (!validationReporter.checkIfCheckPointExists(context, checkPointKey)) {
			initializeCheckPoints(context);
			validationReporter.prepareCheckPointReport(context, checkPointKey);
		}
	}

	protected static Line getLine(Referential referential) {
		for (Line line : referential.getLines().values()) {
			if (line.isFilled())
				return line;
		}
		return null;
	}

	public abstract void addLocation(Context context, NeptuneIdentifiedObject object, int lineNumber, int columnNumber);

	/**
	 * add location for local validation (level 1 and 2) and for general
	 * validation (level 3 and more)
	 * 
	 * @param context
	 * @param localContext
	 * @param objectId
	 * @param lineNumber
	 * @param columnNumber
	 */
	protected void addLocation(Context context, String localContext, NeptuneIdentifiedObject object, int lineNumber,
			int columnNumber) {
		NeptuneImportParameters parameters = (NeptuneImportParameters) context.get(CONFIGURATION);
		NeptuneChouetteIdGenerator neptuneChouetteIdGenerator = (NeptuneChouetteIdGenerator) context.get(CHOUETTEID_GENERATOR);
		
		String objectId = neptuneChouetteIdGenerator.toSpecificFormatId(object.getChouetteId(), parameters.getDefaultCodespace(), object);
		if (object == null) 
			log.info("object in parsing is null ");
		
		if(object.getChouetteId() == null)
			log.info("object chouette id in parsing is null ");
		log.info("OK1dsvdsvd");
		if( context == null)
			log.info("context is null");
		if( localContext == null )
			log.info("local context is null");
		
		if (object.getChouetteId() == null)
			log.info("chouette id is null");
		if (objectId == null)
			log.info("objectId is null");
		if (objectId != null) {
			Context objectContext = getObjectContext(context, localContext, objectId);
			log.info("OK2");
			objectContext.put(LINE_NUMBER, Integer.valueOf(lineNumber));
			log.info("OK3");
			objectContext.put(COLUMN_NUMBER, Integer.valueOf(columnNumber));
			log.info("OK4");
			ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
			log.info("OK5");
			if (data == null) 
			{
				log.info("OK5BIS");
				data = new ValidationData();
				context.put(VALIDATION_DATA,data);
				log.info("OK6BIS");
			}
			String fileName = (String) context.get(FILE_NAME);
			log.info("OK6");
			if (data != null && fileName != null) {
				log.info("OK7");
				DataLocation loc = new DataLocation(context, fileName, lineNumber, columnNumber, object);
				// manage neptune specific model
				if (object instanceof PTLink) {
					try {
						List<DataLocation.Path> path = loc.getPath();
						path.add(loc.new Path(neptuneChouetteIdGenerator, parameters, object));
						path.add(loc.new Path(neptuneChouetteIdGenerator, parameters, ((PTLink) object).getRoute()));
						path.add(loc.new Path(neptuneChouetteIdGenerator, parameters, ((PTLink) object).getRoute().getLine()));
	
	//					Line line = ((PTLink) object).getRoute().getLine();
	//					if (line != null)
	//						loc.setLine(line);
					} catch (NullPointerException e) {
	
					}
				} else if (object instanceof AreaCentroid) {
					try {
						List<DataLocation.Path> path = loc.getPath();
						path.add(loc.new Path(neptuneChouetteIdGenerator, parameters, object));
						path.add(loc.new Path(neptuneChouetteIdGenerator, parameters, ((AreaCentroid) object).getContainedIn()));
					} catch (NullPointerException e) {
	
					}
				}{
					log.info("OK8");
	//				DataLocation.addLineLocation(loc, object);
					loc.setName(DataLocation.buildName(object));
				}
				log.info("OK9");
				data.getDataLocations().put(object.getChouetteId(), loc);
				log.info("OK10");
			}
		}

	}

	/**
	 * check if a list is null or empty
	 * 
	 * @param list
	 * @return
	 */
	protected boolean isListEmpty(List<?> list) {
		return list == null || list.isEmpty();
	}

	protected abstract void initializeCheckPoints(Context context);

	protected boolean isEmpty(String text) {
		return text == null || text.isEmpty();
	}
}
