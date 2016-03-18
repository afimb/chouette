package mobi.chouette.exchange.neptune.validation;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import mobi.chouette.model.Route;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.util.Referential;

public class ChouetteRouteValidator extends AbstractValidator implements Validator<Route> , Constant{

	public static final String JOURNEY_PATTERN_ID = "journeyPatternId";

	protected static final String SEQUENCE_OF_ROUTE = "sequenceOfRoute";

	public static final String PT_LINK_ID = "ptLinkId";

	public static final String WAY_BACK_ROUTE_ID = "wayBackRouteId";

	public static String NAME = "ChouetteRouteValidator";

	private static final String ROUTE_1 = "2-NEPTUNE-Route-1";
	private static final String ROUTE_2 = "2-NEPTUNE-Route-2";
	private static final String ROUTE_3 = "2-NEPTUNE-Route-3";
	private static final String ROUTE_4 = "2-NEPTUNE-Route-4";
	private static final String ROUTE_5 = "2-NEPTUNE-Route-5";
	private static final String ROUTE_6 = "2-NEPTUNE-Route-6";
	private static final String ROUTE_7 = "2-NEPTUNE-Route-7";
	private static final String ROUTE_8 = "2-NEPTUNE-Route-8";
	private static final String ROUTE_9 = "2-NEPTUNE-Route-9";
	private static final String ROUTE_10 = "2-NEPTUNE-Route-10";
	private static final String ROUTE_11 = "2-NEPTUNE-Route-11";
	private static final String ROUTE_12 = "2-NEPTUNE-Route-12";

	public static final String LOCAL_CONTEXT = "Route";


    @Override
	protected void initializeCheckPoints(Context context)
	{
		addItemToValidation(context, prefix, "Route", 12, "E", "E", "E", "E",
				"E", "E", "E", "E", "W", "E", "W", "W");

	}

	public void addLocation(Context context, NeptuneIdentifiedObject object, int lineNumber, int columnNumber)
	{
		addLocation( context,LOCAL_CONTEXT,  object,  lineNumber,  columnNumber);

	}

	public void addWayBackRouteId(Context  context, String objectId, String waybackId)
	{
		Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
		objectContext.put(WAY_BACK_ROUTE_ID, waybackId);

	}

	@SuppressWarnings("unchecked")
	public void addPtLinkId(Context context, String objectId, String linkId) {
		Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
		List<String> contains = (List<String>) objectContext.get(PT_LINK_ID);
		if (contains == null)
		{
			contains = new ArrayList<>();
			objectContext.put(PT_LINK_ID, contains);
		}
		contains.add(linkId);

	}

	@SuppressWarnings("unchecked")
	public void addJourneyPatternId(Context context, String objectId, String jpId) {
		Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
		List<String> contains = (List<String>) objectContext.get(JOURNEY_PATTERN_ID);
		if (contains == null)
		{
			contains = new ArrayList<>();
			objectContext.put(JOURNEY_PATTERN_ID, contains);
		}
		contains.add(jpId);

	}


	@Override
	public ValidationConstraints validate(Context context, Route target) throws ValidationException
	{
		Context validationContext = (Context) context.get(VALIDATION_CONTEXT);
		Context localContext = (Context) validationContext.get(LOCAL_CONTEXT);
		if (localContext == null || localContext.isEmpty()) return new ValidationConstraints();

		boolean routeok = phase1(context);

		// prepare maps for following tests
		Map<String, List<String>> mapPTLinksByStartId = new HashMap<>();
		Map<String, List<String>> mapPTLinksByEndId = new HashMap<>();

		routeok = phase2(context, routeok,
				mapPTLinksByStartId, mapPTLinksByEndId);

		phase3(context);
		// if routes are sufficiently filled 
		if (routeok)
		{
			for (String objectId : localContext.keySet())
			{
				// prepare object context

				boolean route4ok =  phase4(context, mapPTLinksByStartId, mapPTLinksByEndId, objectId);
				// 
				if (route4ok)
				{
					phase5(context,  objectId);
				}
			}

		}
		phase6(context);


		return new ValidationConstraints();
	}



	@SuppressWarnings("unchecked")
	private boolean phase1(Context context) 
	{
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
		Map<String, Location> fileLocations = data.getFileLocations();
		Context validationContext = (Context) context.get(VALIDATION_CONTEXT);
		Context localContext = (Context) validationContext.get(LOCAL_CONTEXT);
		Context journeyPatternsContext = (Context) validationContext.get(JourneyPatternValidator.LOCAL_CONTEXT);
		Context ptLinksContext = (Context) validationContext.get(PtLinkValidator.LOCAL_CONTEXT);
		boolean routeok = true;

		// 2-NEPTUNE-Route-2 : check existence of ptlink
		prepareCheckPoint(context,ROUTE_2);
		Map<String, String> ptLinkInRoute = new HashMap<String, String>();
		for (String objectId : localContext.keySet()) 
		{
			// prepare object context
			Context objectContext = (Context) localContext.get(objectId);

			List<String> ptLinks = (List<String>) objectContext.get(PT_LINK_ID);

			for (String ptLinkId : ptLinks)
			{
				if (!ptLinksContext.containsKey(ptLinkId))
				{
					Detail errorItem = new Detail(
							ROUTE_2,
							fileLocations.get(objectId), ptLinkId);
					addValidationError(context,ROUTE_2, errorItem);
					routeok = false;
					continue;
				}
				// 2-NEPTUNE-Route-4 : check if ptlink is contained in only one
				// route
				prepareCheckPoint(context,ROUTE_4);
				if (ptLinkInRoute.containsKey(ptLinkId))
				{
					routeok = false;

					// ptlink is referenced by more than one route
					Detail errorItem = new Detail(
							ROUTE_4,
							fileLocations.get(objectId),ptLinkId );
					String routeId = ptLinkInRoute.get(ptLinkId);
					errorItem.getTargets().add(fileLocations.get(routeId));
					addValidationError(context,ROUTE_4, errorItem);
				} else
				{
					ptLinkInRoute.put(ptLinkId,objectId);
				}
			}
			if (objectContext.containsKey(JOURNEY_PATTERN_ID))
			{
				// 2-NEPTUNE-Route-1 : check existence of journeyPatterns
				prepareCheckPoint(context,ROUTE_1);
				List<String> journeyPatternIds = (List<String>) objectContext.get(JOURNEY_PATTERN_ID);
				for (String journeyPatternId : journeyPatternIds)
				{
					if (!journeyPatternsContext.containsKey(journeyPatternId))
					{
						Detail errorItem = new Detail(
								ROUTE_1,
								fileLocations.get(objectId), journeyPatternId);
						addValidationError(context,ROUTE_1, errorItem);
					}

				}
			}
		}
		return routeok;
	}

	private boolean phase2(Context context, boolean routeok,
			Map<String, List<String>> mapPTLinksByStartId,
			Map<String, List<String>> mapPTLinksByEndId) 
	{
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
		Map<String, Location> fileLocations = data.getFileLocations();
		Context validationContext = (Context) context.get(VALIDATION_CONTEXT);
		Context ptLinksContext = (Context) validationContext.get(PtLinkValidator.LOCAL_CONTEXT);
		for (String ptLinkId : ptLinksContext.keySet()) 
		{
			Context ptlinkCtx = (Context) ptLinksContext.get(ptLinkId);
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

		// 2-NEPTUNE-Route-5 : check stoppoint in no more than one start of
		// ptlink and one end
		prepareCheckPoint(context,ROUTE_5);
		for (String stopPointId : mapPTLinksByStartId.keySet())
		{
			List<String> ptLinkOfStop = mapPTLinksByStartId.get(stopPointId);
			if (ptLinkOfStop == null || ptLinkOfStop.size() == 1)
				continue;
			for (String ptLinkId : ptLinkOfStop)
			{
				routeok = false;
				Location linkLocation = fileLocations.get(ptLinkId);
				Detail errorItem = new Detail(
						ROUTE_5,
						linkLocation, stopPointId,"startOfLink");
				addValidationError(context,ROUTE_5, errorItem);

			}
		}
		for (String stopPointId : mapPTLinksByEndId.keySet())
		{
			List<String> ptLinkOfStop = mapPTLinksByEndId.get(stopPointId);
			if (ptLinkOfStop == null || ptLinkOfStop.size() == 1)
				continue;
			for (String ptLinkId  : ptLinkOfStop)
			{
				routeok = false;
				Location linkLocation = fileLocations.get(ptLinkId);
				Detail errorItem = new Detail(
						ROUTE_5,
						linkLocation, stopPointId,"endOfLink");
				addValidationError(context,ROUTE_5, errorItem);
			}
		}
		return routeok;
	}

	@SuppressWarnings("unchecked")
	private void phase3(Context context) 
	{
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
		Map<String, Location> fileLocations = data.getFileLocations();
		Context validationContext = (Context) context.get(VALIDATION_CONTEXT);
		Context localContext = (Context) validationContext.get(LOCAL_CONTEXT);
		Context journeyPatternsContext = (Context) validationContext.get(JourneyPatternValidator.LOCAL_CONTEXT);

		if (!journeyPatternsContext.isEmpty())
		{
			prepareCheckPoint(context, ROUTE_7);
			for (String journeyPatternId: journeyPatternsContext.keySet())
			{
				Context jpCtx = (Context) journeyPatternsContext.get(journeyPatternId);
				String routeId = (String) jpCtx.get(JourneyPatternValidator.ROUTE_ID);
				Context routeCtx = (Context) localContext.get(routeId);
				// 2-NEPTUNE-Route-7 : check cross reference between
				// journeypattern and route
				if (routeCtx != null)
				{
					List<String> jpsOfRoute = (List<String>) routeCtx.get(JOURNEY_PATTERN_ID);
					if (jpsOfRoute == null 
							|| !(jpsOfRoute.contains(journeyPatternId)))
					{
						Detail errorItem = new Detail(
								ROUTE_7,
								fileLocations.get(routeId), journeyPatternId);
						addValidationError(context,ROUTE_7, errorItem);
					}
				}
			}
		}
	}
	@SuppressWarnings("unchecked")
	private boolean phase4(Context context,  Map<String, List<String>> mapPTLinksByStartId,
			Map<String, List<String>> mapPTLinksByEndId, String objectId) {
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
		Map<String, Location> fileLocations = data.getFileLocations();
		Context validationContext = (Context) context.get(VALIDATION_CONTEXT);
		Context localContext = (Context) validationContext.get(LOCAL_CONTEXT);
		Context ptLinksContext = (Context) validationContext.get(PtLinkValidator.LOCAL_CONTEXT);
		boolean route3ok = true;

		Context objectContext = (Context) localContext.get(objectId);
		// 2-NEPTUNE-Route-6 : check if stoppoints build a linear route
		// checked
		// find first stop : does not appears as end of link

		List<String> ptLinks = (List<String>) objectContext.get(PT_LINK_ID);
		String startLink = null;
		for (String linkId : ptLinks)
		{
			Context ptlinkContext = (Context) ptLinksContext.get(linkId);
			String linkStart = (String) ptlinkContext.get(PtLinkValidator.START_OF_LINK_ID);
			if (!mapPTLinksByEndId.containsKey(linkStart))
			{
				if (startLink == null)
				{
					startLink = linkId;
				}
				else
				{
					// found 2 startLink = broken Route
					Detail errorItem = new Detail(
							ROUTE_6+ "_2",
							fileLocations.get(objectId),linkId );
					errorItem.getTargets().add(fileLocations.get(linkId));
					addValidationError(context,ROUTE_6, errorItem);
					route3ok = false;
				}
			}
		}
		if (startLink == null)
		{
			// no first id : circle route
			Detail errorItem = new Detail(
					ROUTE_6+ "_1",
					fileLocations.get(objectId) );
			addValidationError(context,ROUTE_6, errorItem);
			route3ok = false;
		} 
		else
		{
			List<String> pointIds = new ArrayList<String>();
			// build pointIds
			Context ptlinkContext = (Context) ptLinksContext.get(startLink);
			String linkStart = (String) ptlinkContext.get(PtLinkValidator.START_OF_LINK_ID);
			String linkEnd = (String) ptlinkContext.get(PtLinkValidator.END_OF_LINK_ID);
			pointIds.add(linkStart);
			pointIds.add(linkEnd);

			List<String> links = mapPTLinksByStartId.get(linkEnd);
			while (!isListEmpty(links))
			{
				String linkId = links.get(0);
				if (ptLinks.contains(linkId))
				{
					ptlinkContext = (Context) ptLinksContext.get(linkId);
					linkEnd = (String) ptlinkContext.get(PtLinkValidator.END_OF_LINK_ID);
					pointIds.add(linkEnd);
					links = mapPTLinksByStartId.get(linkEnd);
				} else
				{
					// broken route but ptlink exists
					Detail errorItem = new Detail(
							ROUTE_6+ "_2",
							fileLocations.get(objectId),linkId );
					errorItem.getTargets().add(fileLocations.get(linkId));
					addValidationError(context,ROUTE_6, errorItem);
					route3ok = false;
					break;
				}
			}

			if (route3ok)
			{
				objectContext.put(SEQUENCE_OF_ROUTE, pointIds);
			}
		}
		return route3ok;
	}

	@SuppressWarnings("unchecked")
	private void phase5(Context context, String objectId) {
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
		Map<String, Location> fileLocations = data.getFileLocations();
		Context validationContext = (Context) context.get(VALIDATION_CONTEXT);
		Context localContext = (Context) validationContext.get(LOCAL_CONTEXT);
		Context stopPointsContext = (Context) validationContext.get(StopPointValidator.LOCAL_CONTEXT);
		Context journeyPatternsContext = (Context) validationContext.get(JourneyPatternValidator.LOCAL_CONTEXT);

		Context objectContext = (Context) localContext.get(objectId);
		List<String> pointIds = (List<String>) objectContext.get(SEQUENCE_OF_ROUTE );

		if (!journeyPatternsContext.isEmpty() && objectContext.containsKey(JOURNEY_PATTERN_ID))
		{
			prepareCheckPoint(context,ROUTE_8);
			prepareCheckPoint(context,ROUTE_9);

			List<String> unusedPointIds = new ArrayList<String>(pointIds);

			List<String> jpIds = (List<String>) objectContext.get(JOURNEY_PATTERN_ID);

			for (String jpId : jpIds)
			{
				Context jpCtx = (Context) journeyPatternsContext.get(jpId);
				if (jpCtx != null)
				{
					// 2-NEPTUNE-Route-8 : check journey pattern
					// stoppoints included in route stoppoints
					List<String> stopsOnJp = (List<String>) jpCtx.get(JourneyPatternValidator.STOP_POINT_LIST);
					if (!pointIds.containsAll(stopsOnJp))
					{
						Detail errorItem = new Detail(
								ROUTE_8,
								fileLocations.get(objectId));
						errorItem.getTargets().add(fileLocations.get(jpId));
						addValidationError(context,ROUTE_8, errorItem);
					}
					unusedPointIds.removeAll(stopsOnJp);
				}
			}

			// 2-NEPTUNE-Route-9 : check usage of stoppoint in a
			// journeypattern of route (W)
			if (!unusedPointIds.isEmpty())
			{
				for (String stopPointId : unusedPointIds)
				{
					Detail errorItem = new Detail(
							ROUTE_9,
							fileLocations.get(objectId),stopPointId);
					Context stopCtx = (Context) stopPointsContext.get(stopPointId);
					if (stopCtx != null)
					{
						errorItem.getTargets().add(fileLocations.get(stopPointId));
					}
					addValidationError(context,ROUTE_9, errorItem);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void phase6(Context context) {
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
		Map<String, Location> fileLocations = data.getFileLocations();
		Context validationContext = (Context) context.get(VALIDATION_CONTEXT);
		Context localContext = (Context) validationContext.get(LOCAL_CONTEXT);
		Referential referential = (Referential) context.get(REFERENTIAL);
		for (String objectId : localContext.keySet())
		{
			// prepare object context
			Context objectContext = (Context) localContext.get(objectId);
			Route route = referential.getRoutes().get(objectId);
			if (objectContext.containsKey(WAY_BACK_ROUTE_ID))
			{
				// 2-NEPTUNE-Route-3 : check cross existence wayback routes
				prepareCheckPoint(context,ROUTE_3);

				String wayBackRouteId = (String) objectContext.get(WAY_BACK_ROUTE_ID);
				if (!localContext.containsKey(wayBackRouteId))
				{
					Detail errorItem = new Detail(
							ROUTE_3,
							fileLocations.get(objectId), wayBackRouteId);
					addValidationError(context,ROUTE_3, errorItem);
					continue;
				}

				prepareCheckPoint(context,ROUTE_10);
				// 2-NEPTUNE-Route-10 : check cross reference of wayback
				// routes
				Context wayBackCtx = (Context) localContext.get(wayBackRouteId);
				if (! wayBackCtx.containsKey(WAY_BACK_ROUTE_ID)
						|| ! wayBackCtx.get(WAY_BACK_ROUTE_ID).equals(objectId))
				{
					Detail errorItem = new Detail(
							ROUTE_10,
							fileLocations.get(objectId));
					errorItem.getTargets().add(fileLocations.get( wayBackRouteId));
					addValidationError(context,ROUTE_10, errorItem);
					continue;
				}

				// 2-NEPTUNE-Route-11 : check orientation of wayback routes
				// (W)
				Route wayBackRoute = referential.getRoutes().get(wayBackRouteId);
				if (route.getWayBack() != null
						&& wayBackRoute.getWayBack() != null)
				{
					prepareCheckPoint(context, ROUTE_11);
					String wk1 = route.getWayBack()
							.toLowerCase().substring(0, 1);
					String wk2 = wayBackRoute.getWayBack()
							.toLowerCase().substring(0, 1);
					if (wk1.equals(wk2))
					{
						Detail errorItem = new Detail(
								ROUTE_11,
								fileLocations.get(objectId),wayBackRoute.getWayBack(),route.getWayBack());
						errorItem.getTargets().add(fileLocations.get(wayBackRouteId));
						addValidationError(context,ROUTE_11, errorItem);
					}
				}
				// 2-NEPTUNE-Route-12 : check terminus of wayback routes (W)
				List<String> pointIds = (List<String>) objectContext.get(SEQUENCE_OF_ROUTE);
				List<String> wbPointIds = (List<String>) wayBackCtx.get(SEQUENCE_OF_ROUTE);
				if (pointIds != null && wbPointIds != null)
				{
					prepareCheckPoint(context,ROUTE_12);
					// check start of route (end will be tested on wayback
					// check)
					StopPoint start = referential.getStopPoints().get(pointIds.get(0));
					StopPoint end = referential.getStopPoints().get(wbPointIds.get(wbPointIds
							.size() - 1));
					if (end.getContainedInStopArea().equals(start.getContainedInStopArea()))
						continue;
					StopArea startParentCommercial = start.getContainedInStopArea().getParent();
					StopArea endParentCommercial = end.getContainedInStopArea().getParent();
					if (startParentCommercial == null
							|| endParentCommercial == null)
						continue;
					if (startParentCommercial.equals(endParentCommercial))
						continue;
					// warning
					Detail errorItem = new Detail(
							ROUTE_12,
							fileLocations.get(objectId),startParentCommercial.getObjectId(),endParentCommercial.getObjectId());
					errorItem.getTargets().add(fileLocations.get(wayBackRouteId));
					addValidationError(context,ROUTE_12, errorItem);

				}
			}

		}
	}


	public static class DefaultValidatorFactory extends ValidatorFactory {



		@Override
		protected Validator<Route> create(Context context) {
			ChouetteRouteValidator instance = (ChouetteRouteValidator) context.get(NAME);
			if (instance == null) {
				instance = new ChouetteRouteValidator();
				context.put(NAME, instance);
			}
			return instance;
		}

	}

	static {
		ValidatorFactory.factories
		.put(ChouetteRouteValidator.class.getName(), new DefaultValidatorFactory());
	}



}
