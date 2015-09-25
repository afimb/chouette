package mobi.chouette.exchange.neptune.validation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.neptune.Constant;
import mobi.chouette.exchange.validation.ValidationConstraints;
import mobi.chouette.exchange.validation.ValidationException;
import mobi.chouette.exchange.validation.Validator;
import mobi.chouette.exchange.validation.ValidatorFactory;
import mobi.chouette.exchange.validation.report.Detail;
import mobi.chouette.exchange.validation.report.FileLocation;
import mobi.chouette.exchange.validation.report.Location;
import mobi.chouette.model.Line;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.util.Referential;

@Log4j
public class LineValidator extends AbstractValidator implements Validator<Line>, Constant {

	public static final String ROUTE_ID = "routeId";

	public static final String LINE_END = "lineEnd";

	public static final String PT_NETWORK_ID_SHORTCUT = "ptNetworkIdShortcut";

	public static String NAME = "LineValidator";

	private static final String LINE_1 = "2-NEPTUNE-Line-1";
	private static final String LINE_2 = "2-NEPTUNE-Line-2";
	private static final String LINE_3 = "2-NEPTUNE-Line-3";
	private static final String LINE_4 = "2-NEPTUNE-Line-4";
	private static final String LINE_5 = "2-NEPTUNE-Line-5";
	private static final String LINE_6 = "2-NEPTUNE-Line-5";

	public static final String LOCAL_CONTEXT = "NeptuneLine";

	@Override
	protected void initializeCheckPoints(Context context) {
		addItemToValidation(context, prefix, "Line", 6, "E", "W", "W", "E", "E", "E");

	}

	public void addLocation(Context context, String objectId, int lineNumber, int columnNumber) {
		addLocation(context, LOCAL_CONTEXT, objectId, lineNumber, columnNumber);

	}

	public void addPtNetworkIdShortcut(Context context, String objectId, String ptNetworkIdShortcut) {
		Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
		objectContext.put(PT_NETWORK_ID_SHORTCUT, ptNetworkIdShortcut);

	}

	@SuppressWarnings("unchecked")
	public void addLineEnd(Context context, String objectId, String lineEnd) {
		Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
		List<String> contains = (List<String>) objectContext.get(LINE_END);
		if (contains == null) {
			contains = new ArrayList<>();
			objectContext.put(LINE_END, contains);
		}
		contains.add(lineEnd);

	}

	@SuppressWarnings("unchecked")
	public void addRouteId(Context context, String objectId, String routeId) {
		Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
		List<String> contains = (List<String>) objectContext.get(ROUTE_ID);
		if (contains == null) {
			contains = new ArrayList<>();
			objectContext.put(ROUTE_ID, contains);
		}
		contains.add(routeId);

	}

	@SuppressWarnings("unchecked")
	@Override
	public ValidationConstraints validate(Context context, Line target) throws ValidationException {
		Context validationContext = (Context) context.get(VALIDATION_CONTEXT);
		Context localContext = (Context) validationContext.get(LOCAL_CONTEXT);
		if (localContext == null || localContext.isEmpty())
			return new ValidationConstraints();
		Context networkContext = (Context) validationContext.get(PTNetworkValidator.LOCAL_CONTEXT);
		Context stopAreaContext = (Context) validationContext.get(StopAreaValidator.LOCAL_CONTEXT);
		Context routeContext = (Context) validationContext.get(ChouetteRouteValidator.LOCAL_CONTEXT);
		String fileName = (String) context.get(FILE_NAME);
		Referential referential = (Referential) context.get(REFERENTIAL);
		Map<String, Line> lines = referential.getLines();

		for (String objectId : localContext.keySet()) {
			Context objectContext = (Context) localContext.get(objectId);
			int lineNumber = ((Integer) objectContext.get(LINE_NUMBER)).intValue();
			int columnNumber = ((Integer) objectContext.get(COLUMN_NUMBER)).intValue();
			FileLocation sourceLocation = new FileLocation(fileName, lineNumber, columnNumber);

			Line line = lines.get(objectId);
			// 2-NEPTUNE-Line-1 : check ptnetworkIdShortcut
			String ptnetworkIdShortcut = (String) objectContext.get(PT_NETWORK_ID_SHORTCUT);

			if (ptnetworkIdShortcut != null) {
				prepareCheckPoint(context, LINE_1);
				if (!networkContext.containsKey(ptnetworkIdShortcut)) {
					Detail errorItem = new Detail(LINE_1, new Location(sourceLocation, objectId), ptnetworkIdShortcut);
					addValidationError(context, LINE_1, errorItem);
				}
			}

			// 2-NEPTUNE-Line-2 : check existence of ends of line
			List<String> lineEnds = (List<String>) objectContext.get(LINE_END);
			if (lineEnds != null) {
				prepareCheckPoint(context, LINE_2);
				Set<String> endAreas = new HashSet<>();
				for (Route route : line.getRoutes()) {
					if (route.getStopPoints().size() > 0) {
						StopArea area = route.getStopPoints().get(0).getContainedInStopArea();
						if (area == null) {
							log.error("missing stoparea for "
									+ route.getStopPoints().get(0).getObjectId());
						} else {
							endAreas.add(area.getObjectId());
							if (area.getParent() != null)
								endAreas.add(area.getParent().getObjectId());
						}
						area = route.getStopPoints().get(route.getStopPoints().size() - 1).getContainedInStopArea();
						if (area == null) {
							log.error("missing stoparea for "
									+ route.getStopPoints().get(route.getStopPoints().size() - 1).getObjectId());
						} else {
							endAreas.add(area.getObjectId());
							if (area.getParent() != null)
								endAreas.add(area.getParent().getObjectId());
						}
					}
				}

				for (String endId : lineEnds) {
					// endId must exists as stopArea ?
					if (!stopAreaContext.containsKey(endId)) {
						Detail errorItem = new Detail(LINE_2, new Location(sourceLocation, objectId), endId);
						addValidationError(context, LINE_2, errorItem);
					} else {
						// 2-NEPTUNE-Line-3 : check ends of line
						prepareCheckPoint(context, LINE_3);
						if (!endAreas.contains(endId)) {
							Detail errorItem = new Detail(LINE_3, new Location(sourceLocation, objectId), endId);
							addValidationError(context, LINE_3, errorItem);
						}
					}
				}

			}

			// 2-NEPTUNE-Line-4 : check routes references
			prepareCheckPoint(context, LINE_4);
			List<String> routeIds = (List<String>) objectContext.get(ROUTE_ID);
			for (String routeId : routeIds) {
				if (!routeContext.containsKey(routeId)) {
					Detail errorItem = new Detail(LINE_4, new Location(sourceLocation, objectId), routeId);
					addValidationError(context, LINE_4, errorItem);
				}
			}

			// 2-NEPTUNE-Line-5 : check routes references
			prepareCheckPoint(context, LINE_5);
			for (String routeId : routeContext.keySet()) {
				if (!routeIds.contains(routeId)) {
					Detail errorItem = new Detail(LINE_5, new Location(sourceLocation, objectId), routeId);
					Context routeCtx = (Context) routeContext.get(routeId);
					lineNumber = ((Integer) routeCtx.get(LINE_NUMBER)).intValue();
					columnNumber = ((Integer) routeCtx.get(COLUMN_NUMBER)).intValue();
					FileLocation targetLocation = new FileLocation(fileName, lineNumber, columnNumber);
					errorItem.getTargets().add(new Location(targetLocation, routeId));
					addValidationError(context, LINE_5, errorItem);
				}
			}

			// 2-NEPTUNE-Line-6 : check presence of Name or Number or
			// publishedName
			prepareCheckPoint(context, LINE_6);

			if (isEmpty(line.getName()) && isEmpty(line.getNumber()) && isEmpty(line.getPublishedName())) {
				Detail errorItem = new Detail(LINE_6, new Location(sourceLocation, objectId));
				addValidationError(context, LINE_6, errorItem);
			}

		}
		return new ValidationConstraints();
	}

	public static class DefaultValidatorFactory extends ValidatorFactory {

		@Override
		protected Validator<Line> create(Context context) {
			LineValidator instance = (LineValidator) context.get(NAME);
			if (instance == null) {
				instance = new LineValidator();
				context.put(NAME, instance);
			}
			return instance;
		}

	}

	static {
		ValidatorFactory.factories.put(LineValidator.class.getName(), new DefaultValidatorFactory());
	}

}
