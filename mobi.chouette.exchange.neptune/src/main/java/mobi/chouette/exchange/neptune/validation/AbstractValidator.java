package mobi.chouette.exchange.neptune.validation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.neptune.Constant;
import mobi.chouette.exchange.neptune.model.AreaCentroid;
import mobi.chouette.exchange.neptune.model.PTLink;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.report.DataLocation;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import mobi.chouette.model.Line;
import mobi.chouette.model.NeptuneIdentifiedObject;
import mobi.chouette.model.util.Referential;

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
		Context validationContext = (Context) context.get(VALIDATION_CONTEXT);
		if (validationContext == null) {
			validationContext = new Context();
			context.put(VALIDATION_CONTEXT, validationContext);
			validationContext.put(OBJECT_IDS, new HashSet<String>());
		}

		Set<String> objectIds = (Set<String>) validationContext.get(OBJECT_IDS);
		objectIds.add(objectId);

		Context localContext = (Context) validationContext.get(localContextName);
		if (localContext == null) {
			localContext = new Context();
			validationContext.put(localContextName, localContext);
		}
		Context objectContext = (Context) localContext.get(objectId);
		if (objectContext == null) {
			objectContext = new Context();
			localContext.put(objectId, objectContext);
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
		String objectId = object.getObjectId();
		Context objectContext = getObjectContext(context, localContext, objectId);
		objectContext.put(LINE_NUMBER, Integer.valueOf(lineNumber));
		objectContext.put(COLUMN_NUMBER, Integer.valueOf(columnNumber));
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
		if (data == null) 
		{
			data = new ValidationData();
			context.put(VALIDATION_DATA,data);
		}
		String fileName = (String) context.get(FILE_NAME);
		if (data != null && fileName != null) {
			DataLocation loc = new DataLocation(fileName, lineNumber, columnNumber, object);
			// manage neptune specific model
			if (object instanceof PTLink) {
				try {
					List<DataLocation.Path> path = loc.getPath();
					path.add(loc.new Path(object));
					path.add(loc.new Path(((PTLink) object).getRoute()));
					path.add(loc.new Path(((PTLink) object).getRoute().getLine()));

//					Line line = ((PTLink) object).getRoute().getLine();
//					if (line != null)
//						loc.setLine(line);
				} catch (NullPointerException e) {

				}
			} else if (object instanceof AreaCentroid) {
				try {
					List<DataLocation.Path> path = loc.getPath();
					path.add(loc.new Path(object));
					path.add(loc.new Path(((AreaCentroid) object).getContainedIn()));
				} catch (NullPointerException e) {

				}
			}{
//				DataLocation.addLineLocation(loc, object);
				loc.setName(DataLocation.buildName(object));
			}
			data.getDataLocations().put(objectId, loc);
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
