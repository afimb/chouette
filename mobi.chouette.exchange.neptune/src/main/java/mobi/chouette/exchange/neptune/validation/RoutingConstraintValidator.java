package mobi.chouette.exchange.neptune.validation;

import java.util.Map;

import mobi.chouette.common.ChouetteId;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.neptune.Constant;
import mobi.chouette.exchange.neptune.NeptuneChouetteIdGenerator;
import mobi.chouette.exchange.neptune.NeptuneObjectIdTypes;
import mobi.chouette.exchange.neptune.importer.NeptuneImportParameters;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.ValidationException;
import mobi.chouette.exchange.validation.Validator;
import mobi.chouette.exchange.validation.ValidatorFactory;
import mobi.chouette.exchange.validation.report.DataLocation;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import mobi.chouette.model.Line;
import mobi.chouette.model.NeptuneIdentifiedObject;
import mobi.chouette.model.RoutingConstraint;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.util.Referential;

public class RoutingConstraintValidator extends AbstractValidator implements Validator<RoutingConstraint>, Constant {

	public static final String ITL_NAME = "name";

	public static final String LINE_ID = "lineId";

	public static String NAME = "RoutingConstraintValidator";

	// TODO move tests from StopAreaValidator
	private static final String ITL_1 = "2-NEPTUNE-ITL-1";
	private static final String ITL_2 = "2-NEPTUNE-ITL-2";
	private static final String ITL_3 = "2-NEPTUNE-ITL-3";
	private static final String ITL_4 = "2-NEPTUNE-ITL-4";
	private static final String ITL_5 = "2-NEPTUNE-ITL-5";

	public static final String ITL_LOCAL_CONTEXT = "ITL";
	public static final String SA_LOCAL_CONTEXT = "SA_ITL";

	@Override
	protected void initializeCheckPoints(Context context) {
		addItemToValidation(context, prefix, "ITL", 5, "E", "E", "E", "E", "E");

	}

	public void addITLLocation(Context context, NeptuneIdentifiedObject object, int lineNumber, int columnNumber) {
		addLocation(context, ITL_LOCAL_CONTEXT, object, lineNumber, columnNumber);

	}

	public void addStopAreaLocation(Context context, NeptuneIdentifiedObject object, int lineNumber, int columnNumber) {
		addLocation(context, SA_LOCAL_CONTEXT, object, lineNumber, columnNumber);

	}

	public void addLineId(Context context, String objectId, String lineId) {
		Context objectContext = getObjectContext(context, ITL_LOCAL_CONTEXT, objectId);
		objectContext.put(LINE_ID, lineId);
	}

	public void addName(Context context, String objectId, String name) {
		Context objectContext = getObjectContext(context, ITL_LOCAL_CONTEXT, objectId);
		objectContext.put(ITL_NAME, name);
	}

	@Override
	public void validate(Context context, RoutingConstraint target) throws ValidationException {
		validateStopAreaTag(context, target);
		validateITLTag(context, target);

		return;
	}

	/**
	 * Validate ITL tag in xml file
	 * 
	 * @param context
	 * @param target
	 */
	private void validateITLTag(Context context, RoutingConstraint target) {

		Context validationContext = (Context) context.get(VALIDATION_CONTEXT);
		Context itlLocalContext = (Context) validationContext.get(ITL_LOCAL_CONTEXT);
		NeptuneImportParameters parameters = (NeptuneImportParameters) context.get(CONFIGURATION);
		NeptuneChouetteIdGenerator neptuneChouetteIdGenerator = (NeptuneChouetteIdGenerator) context
				.get(CHOUETTEID_GENERATOR);

		if (itlLocalContext == null || itlLocalContext.isEmpty())
			return;
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
		Map<ChouetteId, DataLocation> fileLocations = data.getDataLocations();
		Context routingConstraintLocalContext = (Context) validationContext
				.get(RoutingConstraintValidator.SA_LOCAL_CONTEXT);
		Context lineContext = (Context) validationContext.get(LineValidator.LOCAL_CONTEXT);
		Referential referential = (Referential) context.get(REFERENTIAL);
		Line line = getLine(referential);
		Map<ChouetteId, RoutingConstraint> routingConstraints = referential.getRoutingConstraints();

		String fileName = (String) context.get(FILE_NAME);

		for (String objectId : itlLocalContext.keySet()) {
			Context objectContext = (Context) itlLocalContext.get(objectId);

			int lineNumber = ((Integer) objectContext.get(LINE_NUMBER)).intValue();
			int columnNumber = ((Integer) objectContext.get(COLUMN_NUMBER)).intValue();

			String routingConstraintId = objectId;
			ChouetteId routingConstraintChouetteId = neptuneChouetteIdGenerator.toChouetteId(routingConstraintId,
					parameters.getDefaultCodespace(), RoutingConstraint.class);

			// 2-NEPTUNE-ITL-3 : Check if ITL refers existing StopArea
			prepareCheckPoint(context, ITL_3);

			if (!routingConstraintLocalContext.containsKey(routingConstraintId)
					|| !routingConstraints.containsKey(routingConstraintChouetteId))

			{

				ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
				validationReporter.addCheckPointReportError(context, ITL_3, new DataLocation(fileName, lineNumber,
						columnNumber, (String) objectContext.get(ITL_NAME)), routingConstraintId);

			} else {
				// 2-NEPTUNE-ITL-4 : Check if ITL refers StopArea of ITL
				// type
				prepareCheckPoint(context, ITL_4);
				RoutingConstraint rc = routingConstraints.get(routingConstraintChouetteId);
				if (rc != null) {
					Context routingConstraintData = (Context) routingConstraintLocalContext.get(routingConstraintId);
					lineNumber = ((Integer) routingConstraintData.get(LINE_NUMBER)).intValue();
					columnNumber = ((Integer) routingConstraintData.get(COLUMN_NUMBER)).intValue();

					ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
					validationReporter.addCheckPointReportError(context, ITL_4, new DataLocation(fileName, lineNumber,
							columnNumber, (String) objectContext.get(ITL_NAME)),
							NeptuneObjectIdTypes.ROUTING_CONSTRAINT_KEY, null, fileLocations
									.get(routingConstraintChouetteId));
				}
			}

			// 2-NEPTUNE-ITL-5 : Check if ITL refers Line
			String lineId = (String) objectContext.get(LINE_ID);
			if (lineId != null) {
				String specificLineId = neptuneChouetteIdGenerator.toSpecificFormatId(line.getChouetteId(),
						parameters.getDefaultCodespace(), line);
				prepareCheckPoint(context, ITL_5);
				if (!lineId.equals(specificLineId)) {
					Context lineData = (Context) lineContext.get(specificLineId);
					lineNumber = ((Integer) lineData.get(LINE_NUMBER)).intValue();
					columnNumber = ((Integer) lineData.get(COLUMN_NUMBER)).intValue();

					ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
					validationReporter.addCheckPointReportError(context, ITL_5, new DataLocation(fileName, lineNumber,
							columnNumber, objectId), lineId, null, fileLocations.get(line.getChouetteId()));

				}
			}
		}

	}

	/**
	 * Validate stop area tag in xml file
	 * 
	 * @param context
	 * @param target
	 */
	private void validateStopAreaTag(Context context, RoutingConstraint target) {

		Context validationContext = (Context) context.get(VALIDATION_CONTEXT);
		Context itlLocalContext = (Context) validationContext.get(ITL_LOCAL_CONTEXT);
		NeptuneImportParameters parameters = (NeptuneImportParameters) context.get(CONFIGURATION);
		NeptuneChouetteIdGenerator neptuneChouetteIdGenerator = (NeptuneChouetteIdGenerator) context
				.get(CHOUETTEID_GENERATOR);

		if (itlLocalContext == null || itlLocalContext.isEmpty())
			return;
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
		Map<ChouetteId, DataLocation> fileLocations = data.getDataLocations();
		Context routingConstraintLocalContext = (Context) validationContext
				.get(RoutingConstraintValidator.SA_LOCAL_CONTEXT);
		Context stopPointContext = (Context) validationContext.get(StopPointValidator.LOCAL_CONTEXT);
		Referential referential = (Referential) context.get(REFERENTIAL);
		Map<ChouetteId, RoutingConstraint> routingConstraints = referential.getRoutingConstraints();

		for (String objectId : routingConstraintLocalContext.keySet()) {

			String routingConstraintId = objectId;
			ChouetteId routingConstraintChouetteId = neptuneChouetteIdGenerator.toChouetteId(routingConstraintId,
					parameters.getDefaultCodespace(), RoutingConstraint.class);
			RoutingConstraint routingConstraint = routingConstraints.get(routingConstraintChouetteId);

			// 2-NEPTUNE-ITL-1 : if stoparea is ITL : check if it
			// refers only non ITL stopAreas
			prepareCheckPoint(context, ITL_1);

			// @TODO à vérifier après suppression du type ITL dans l'enum
			// ChouetteAreaType
			for (StopArea child : routingConstraint.getRoutingConstraintAreas()) {
				String specificChildId = neptuneChouetteIdGenerator.toSpecificFormatId(child.getChouetteId(),
						parameters.getDefaultCodespace(), child);
				if (routingConstraintLocalContext.containsKey(specificChildId)) {

					ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
					validationReporter.addCheckPointReportError(context, ITL_1,
							fileLocations.get(routingConstraint.getChouetteId()), "ITL", "ITL",
							fileLocations.get(child.getChouetteId()));
				} else if (stopPointContext.containsKey(specificChildId)) {
					ChouetteId stopPointId = neptuneChouetteIdGenerator.toChouetteId(specificChildId, parameters.getDefaultCodespace(), StopPoint.class);
					ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
					validationReporter.addCheckPointReportError(context, ITL_1,
							fileLocations.get(routingConstraint.getChouetteId()), "StopPoint", "ITL",
							fileLocations.get(stopPointId));
				}

			}

			Context itlData = (Context) itlLocalContext.get(neptuneChouetteIdGenerator.toSpecificFormatId(
					routingConstraint.getChouetteId(), parameters.getDefaultCodespace(), routingConstraint));
			// 2-NEPTUNE-ITL-2 : if stoparea is ITL : check if a ITLType
			// object refers it
			prepareCheckPoint(context, ITL_2);
			if (itlData == null) {
				ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
				validationReporter.addCheckPointReportError(context, ITL_2,
						fileLocations.get(routingConstraint.getChouetteId()));
			}

		}
	}

	public static class DefaultValidatorFactory extends ValidatorFactory {

		@Override
		protected Validator<RoutingConstraint> create(Context context) {
			RoutingConstraintValidator instance = (RoutingConstraintValidator) context.get(NAME);
			if (instance == null) {
				instance = new RoutingConstraintValidator();
				context.put(NAME, instance);
			}
			return instance;
		}

	}

	static {
		ValidatorFactory.factories.put(RoutingConstraintValidator.class.getName(), new DefaultValidatorFactory());
	}

	@Override
	public void addLocation(Context context, NeptuneIdentifiedObject object, int lineNumber, int columnNumber) {
		// TODO Auto-generated method stub

	}

}
