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
import mobi.chouette.model.Line;

public class LineValidator extends AbstractValidator implements Validator<Line> , Constant{

	public static final String ROUTE_ID = "routeId";

	public static final String LINE_END = "lineEnd";

	public static final String PT_NETWORK_ID_SHORTCUT = "ptNetworkIdShortcut";

	public static String NAME = "LineValidator";

	private static final String LINE_1 = "2-NEPTUNE-Line-1";
	private static final String LINE_2 = "2-NEPTUNE-Line-2";
	private static final String LINE_3 = "2-NEPTUNE-Line-3";
	private static final String LINE_4 = "2-NEPTUNE-Line-4";
	private static final String LINE_5 = "2-NEPTUNE-Line-5";

	public static final String LOCAL_CONTEXT = "NeptuneLine";


    @Override
	protected void initializeCheckPoints(Context context)
	{
		addItemToValidation(context, prefix, "Line", 5, "E", "W", "W", "E", "E");

	}

	public void addLocation(Context context, String objectId, int lineNumber, int columnNumber)
	{
		Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
		objectContext.put(LINE_NUMBER, Integer.valueOf(lineNumber));
		objectContext.put(COLUMN_NUMBER, Integer.valueOf(columnNumber));

	}

	public void addPtNetworkIdShortcut(Context  context, String objectId, String ptNetworkIdShortcut)
	{
		Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
		objectContext.put(PT_NETWORK_ID_SHORTCUT, ptNetworkIdShortcut);

	}

	@SuppressWarnings("unchecked")
	public void addLineEnd(Context context, String objectId, String lineEnd) {
		Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
		List<String> contains = (List<String>) objectContext.get(LINE_END);
		if (contains == null)
		{
			contains = new ArrayList<>();
			objectContext.put(LINE_END, contains);
		}
		contains.add(lineEnd);

	}

	@SuppressWarnings("unchecked")
	public void addRouteId(Context context, String objectId, String routeId) {
		Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
		List<String> contains = (List<String>) objectContext.get(ROUTE_ID);
		if (contains == null)
		{
			contains = new ArrayList<>();
			objectContext.put(ROUTE_ID, contains);
		}
		contains.add(routeId);

	}



	@SuppressWarnings("unchecked")
	@Override
	public ValidationConstraints validate(Context context, Line target) throws ValidationException
	{
		Context validationContext = (Context) context.get(VALIDATION_CONTEXT);
		Context localContext = (Context) validationContext.get(LOCAL_CONTEXT);
		if (localContext == null || localContext.isEmpty()) return new ValidationConstraints();
		Context networkContext = (Context) validationContext.get(PTNetworkValidator.LOCAL_CONTEXT);
		Context stopPointContext = (Context) validationContext.get(StopPointValidator.LOCAL_CONTEXT);
		Context routeContext = (Context) validationContext.get(ChouetteRouteValidator.LOCAL_CONTEXT);
		String fileName = (String) context.get(FILE_NAME);

		for (String objectId : localContext.keySet()) 
		{
			Context objectContext = (Context) localContext.get(objectId);
			int lineNumber = ((Integer) objectContext.get(LINE_NUMBER)).intValue();
			int columnNumber = ((Integer) objectContext.get(COLUMN_NUMBER)).intValue();
			FileLocation sourceLocation = new FileLocation(fileName, lineNumber, columnNumber);

			// 2-NEPTUNE-Line-1 : check ptnetworkIdShortcut
			String ptnetworkIdShortcut = (String) objectContext.get(PT_NETWORK_ID_SHORTCUT);

			if (ptnetworkIdShortcut != null)
			{
				prepareCheckPoint(context, LINE_1);
				if (!networkContext.containsKey(ptnetworkIdShortcut))
				{
					Detail errorItem = new Detail(
							LINE_1,
							new Location(sourceLocation,objectId), ptnetworkIdShortcut);
					addValidationError(context,LINE_1, errorItem);
				}
			}

			// 2-NEPTUNE-Line-2 : check existence of ends of line
			List<String> lineEnds = (List<String>) objectContext.get(LINE_END);
			if (lineEnds != null)
			{
				prepareCheckPoint(context, LINE_2);
				Map<String, List<String>> mapPTLinksByStartId = new HashMap<>();
				Map<String, List<String>> mapPTLinksByEndId = new HashMap<>();
				Context ptLinkContext = (Context) validationContext.get(PtLinkValidator.LOCAL_CONTEXT);
				for (String ptLinkId : ptLinkContext.keySet()) 
				{
					Context ptlinkCtx = (Context) ptLinkContext.get(ptLinkId);
					String start = (String) ptlinkCtx.get(PtLinkValidator.START_OF_LINK_ID);
					String end = (String) ptlinkCtx.get(PtLinkValidator.END_OF_LINK_ID);
					List<String> startIds = mapPTLinksByStartId.get(start);
					if (startIds == null) 
					{
						startIds = new ArrayList<>();
						mapPTLinksByStartId.put(start, startIds);
					}
					startIds.add(ptLinkId);
					List<String> endIds = mapPTLinksByEndId.get(end);
					if (endIds == null) 
					{
						endIds = new ArrayList<>();
						mapPTLinksByEndId.put(end, endIds);
					}
					endIds.add(ptLinkId);
				}

				for (String endId : lineEnds)
				{
					// endId must exists as stopPoint ?
					if (!stopPointContext.containsKey(endId))
					{
						Detail errorItem = new Detail(
								LINE_2,
								new Location(sourceLocation,objectId), endId);
						addValidationError(context,LINE_2, errorItem);

					} else
					{
						// 2-NEPTUNE-Line-3 : check ends of line
						prepareCheckPoint(context,LINE_3);

						// endId must be referenced by one and only one ptLink
						List<String> startLinks = mapPTLinksByStartId.get(endId);
						List<String> endLinks = mapPTLinksByEndId.get(endId);
						boolean oneRef = true;
						// protect from null pointers
						if (startLinks == null)
							startLinks = new ArrayList<String>();
						if (endLinks == null)
							endLinks = new ArrayList<String>();

						if (startLinks.size() != 0 && endLinks.size() != 0)
						{
							oneRef = false;
						} else if (startLinks.size() > 1 || endLinks.size() > 1)
						{
							oneRef = false;
						}
						if (!oneRef)
						{
							Detail errorItem = new Detail(
									LINE_3,
									new Location(sourceLocation,objectId), endId);
							addValidationError(context,LINE_3, errorItem);

						}
					}
				}

			}
		
			// 2-NEPTUNE-Line-4 : check routes references
			prepareCheckPoint(context,LINE_4);
			List<String> routeIds = (List<String>) objectContext.get(ROUTE_ID);
			for (String routeId : routeIds)
			{
				if (!routeContext.containsKey(routeId))
				{
					Detail errorItem = new Detail(
							LINE_4,
							new Location(sourceLocation,objectId), routeId);
					addValidationError(context,LINE_4, errorItem);
				}
			}

			// 2-NEPTUNE-Line-5 : check routes references
			prepareCheckPoint(context,LINE_5);
			for (String routeId : routeContext.keySet())
			{
				if (!routeIds.contains(routeId))
				{
					Detail errorItem = new Detail(
							LINE_5,
							new Location(sourceLocation,objectId), routeId);
					Context routeCtx = (Context) routeContext.get(routeId);
					lineNumber = ((Integer) routeCtx.get(LINE_NUMBER)).intValue();
					columnNumber = ((Integer) routeCtx.get(COLUMN_NUMBER)).intValue();
					FileLocation targetLocation = new FileLocation(fileName, lineNumber, columnNumber);
					errorItem.getTargets().add(new Location(targetLocation,routeId));
					addValidationError(context,LINE_5, errorItem);
				}
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
		ValidatorFactory.factories
		.put(LineValidator.class.getName(), new DefaultValidatorFactory());
	}



}
