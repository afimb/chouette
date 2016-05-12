package mobi.chouette.exchange.neptune.validation;


import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import mobi.chouette.exchange.validation.report.FileLocation;
import mobi.chouette.exchange.validation.report.Location;
import mobi.chouette.model.NeptuneIdentifiedObject;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.util.Referential;

public class VehicleJourneyValidator extends AbstractValidator implements Validator<VehicleJourney> , Constant{

	public static final String TIME_SLOT_ID = "timeSlotId";

	public static final String ORDER = "order";

	public static final String STOP_POINT_ID = "stopPointId";

	public static final String VEHICLE_JOURNEY_ID = "vehicleJourneyId";

	public static final String VEHICLE_JOURNEY_AT_STOPS = "vehicleJourneyAtStops";

	public static final String OPERATOR_ID = "operatorId";

	public static final String ROUTE_ID = "routeId";

	public static final String JOURNEY_PATTERN_ID = "journeyPatternId";

	public static final String LINE_ID_SHORTCUT = "lineIdShortcut";
	
	public static final String WAITING_TIME = "waitingTime";
	
	public static final String ELAPSE_DURATION = "elapseDuration";
	
	public static final String HEADWAY_FREQUENCY = "headwayFrequency";

	public static String NAME = "VehicleJourneyValidator";

	private static final String VEHICLE_JOURNEY_1 = "2-NEPTUNE-VehicleJourney-1";
	private static final String VEHICLE_JOURNEY_2 = "2-NEPTUNE-VehicleJourney-2";
	private static final String VEHICLE_JOURNEY_3 = "2-NEPTUNE-VehicleJourney-3";
	private static final String VEHICLE_JOURNEY_4 = "2-NEPTUNE-VehicleJourney-4";
	private static final String VEHICLE_JOURNEY_5 = "2-NEPTUNE-VehicleJourney-5";
	private static final String VEHICLE_JOURNEY_6 = "2-NEPTUNE-VehicleJourney-6";
	private static final String VEHICLE_JOURNEY_7 = "2-NEPTUNE-VehicleJourney-7";
	private static final String VEHICLE_JOURNEY_8 = "2-NEPTUNE-VehicleJourney-8";
	private static final String VEHICLE_JOURNEY_AT_STOP_1 = "2-NEPTUNE-VehicleJourneyAtStop-1";
	private static final String VEHICLE_JOURNEY_AT_STOP_2 = "2-NEPTUNE-VehicleJourneyAtStop-2";
	private static final String VEHICLE_JOURNEY_AT_STOP_3 = "2-NEPTUNE-VehicleJourneyAtStop-3";
	private static final String VEHICLE_JOURNEY_AT_STOP_4 = "2-NEPTUNE-VehicleJourneyAtStop-4";

	public static final String LOCAL_CONTEXT = "VehicleJourney";


    @Override
	protected void initializeCheckPoints(Context context)
	{
		addItemToValidation(context, prefix, "VehicleJourney", 8, "E", "E", "E",
				"E", "E", "E", "W", "E");
		addItemToValidation(context, prefix, "VehicleJourneyAtStop", 4, "E",
				"E", "E", "E");

	}

	public void addLocation(Context context, NeptuneIdentifiedObject object, int lineNumber, int columnNumber)
	{
		addLocation( context,LOCAL_CONTEXT,  object,  lineNumber,  columnNumber);
	}


	public void addLineIdShortcut(Context  context, String objectId, String lineIdShortcut)
	{
		Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
		objectContext.put(LINE_ID_SHORTCUT, lineIdShortcut);

	}
	public void addOperatorId(Context  context, String objectId, String operatorId)
	{
		Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
		objectContext.put(OPERATOR_ID, operatorId);

	}

	public void addRouteId(Context  context, String objectId, String routeId)
	{
		Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
		objectContext.put(ROUTE_ID, routeId);

	}

	public void addTimeSlotId(Context  context, String objectId, String timeSlotId)
	{
		Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
		objectContext.put(TIME_SLOT_ID, timeSlotId);

	}


	public void addJourneyPatternId(Context  context, String objectId, String journeyPatternId)
	{
		Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
		objectContext.put(JOURNEY_PATTERN_ID, journeyPatternId);

	}

	@SuppressWarnings("unchecked")
	public Context addVehicleJourneyAtStopContext(Context context, String objectId) 
	{
		Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
		List<Context> lvjasCtx = (List<Context>) objectContext.get(VEHICLE_JOURNEY_AT_STOPS);
		if (lvjasCtx == null) 
		{
			lvjasCtx = new ArrayList<>();
			objectContext.put(VEHICLE_JOURNEY_AT_STOPS, lvjasCtx);
		}
		Context vjasCtx = new Context();
		lvjasCtx.add(vjasCtx);
		return vjasCtx;
	}

	public void addVehicleJourneyId(Context vjasCtx, String vehicleJourneyId)
	{
		vjasCtx.put(VEHICLE_JOURNEY_ID, vehicleJourneyId);
	}
	public void addStopPointId(Context vjasCtx, String stopPointId)
	{
		vjasCtx.put(STOP_POINT_ID, stopPointId);
	}
	public void addOrder(Context vjasCtx, Integer order)
	{
		vjasCtx.put(ORDER, order);
	}
	public void addVehicleJourneyAtStopLocation(Context vjasCtx, int lineNumber, int columnNumber)
	{
		vjasCtx.put(LINE_NUMBER, Integer.valueOf(lineNumber));
		vjasCtx.put(COLUMN_NUMBER, Integer.valueOf(columnNumber));

	}

	public void addWaitingTime(Context vjasCtx, Time value) {
		vjasCtx.put(WAITING_TIME, value);
	}

	public void addElapseDuration(Context vjasCtx, Time value) {
		vjasCtx.put(ELAPSE_DURATION, value);
	}

	public void addHeadwayFrequency(Context vjasCtx, Time value) {
		vjasCtx.put(HEADWAY_FREQUENCY, value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ValidationConstraints validate(Context context, VehicleJourney target) throws ValidationException
	{
		Context validationContext = (Context) context.get(VALIDATION_CONTEXT);
		Context localContext = (Context) validationContext.get(LOCAL_CONTEXT);
		if (localContext == null || localContext.isEmpty()) return new ValidationConstraints();
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
		Map<String, Location> fileLocations = data.getFileLocations();
		Context stopPointsContext = (Context) validationContext.get(StopPointValidator.LOCAL_CONTEXT);
		Context routesContext = (Context) validationContext.get(ChouetteRouteValidator.LOCAL_CONTEXT);
		Context journeyPatternsContext = (Context) validationContext.get(JourneyPatternValidator.LOCAL_CONTEXT);
		Context linesContext = (Context) validationContext.get(LineValidator.LOCAL_CONTEXT);
		Context companiesContext = (Context) validationContext.get(CompanyValidator.LOCAL_CONTEXT);
		Context timeSlotsContext = (Context) validationContext.get(TimeSlotValidator.LOCAL_CONTEXT);
		if (timeSlotsContext == null) timeSlotsContext = new Context();
		String fileName = (String) context.get(FILE_NAME);
		Referential referential = (Referential) context.get(REFERENTIAL);
		Map<String, VehicleJourney> vehicleJourneys = referential.getVehicleJourneys();
		
		// 2-NEPTUNE-VehicleJourney-1 : check existence of route
		prepareCheckPoint(context, VEHICLE_JOURNEY_1);
		// 2-NEPTUNE-VehicleJourneyAtStop-1 : check existence of stopPoint
		prepareCheckPoint(context, VEHICLE_JOURNEY_AT_STOP_1);

		if (!journeyPatternsContext.isEmpty())
		{
			// 2-NEPTUNE-VehicleJourney-6 : check if route and JourneyPattern
			// are coherent (e)
			prepareCheckPoint(context,VEHICLE_JOURNEY_6);
			// 2-NEPTUNE-VehicleJourney-7 : check if journeypatterns have at
			// least one vehiclejourney (w)
			prepareCheckPoint(context,VEHICLE_JOURNEY_7);
			// 2-NEPTUNE-VehicleJourneyAtStop-4 : check if stoppoints are
			// coherent with journeyPattern(e)
			prepareCheckPoint(context,VEHICLE_JOURNEY_AT_STOP_4);
		}

		List<String> unreferencedJourneyPatterns = new ArrayList<String>(
				journeyPatternsContext.keySet());


		for (String objectId : localContext.keySet()) 
		{
			boolean fkOK = true;
			Context objectContext = (Context) localContext.get(objectId);

			// 2-NEPTUNE-VehicleJourney-1 : check if route and JourneyPattern
			// are coherent (e)
			String routeId = (String) objectContext.get(ROUTE_ID);
			if (!routesContext.containsKey(routeId))
			{
				Detail errorItem = new Detail(
						VEHICLE_JOURNEY_1,
						fileLocations.get(objectId), routeId.toString());
				addValidationError(context,VEHICLE_JOURNEY_1, errorItem);
				fkOK = false;
			}
			
			String timeSlotId = (String) objectContext.get(TIME_SLOT_ID);
			if (timeSlotId != null && !timeSlotsContext.containsKey(timeSlotId)) {
				;// TODO. Report error
			}

			String journeyPatternId = (String) objectContext.get(JOURNEY_PATTERN_ID);
			if (journeyPatternId != null)
			{
				// 2-NEPTUNE-VehicleJourney-2 : check existence of
				// journeyPattern
				prepareCheckPoint(context,VEHICLE_JOURNEY_2);
				if (!journeyPatternsContext.containsKey(objectContext.get(JOURNEY_PATTERN_ID)))
				{
					Detail errorItem = new Detail(
							VEHICLE_JOURNEY_2,
							fileLocations.get(objectId), objectContext.get(JOURNEY_PATTERN_ID).toString());
					addValidationError(context,VEHICLE_JOURNEY_2, errorItem);
					fkOK = false;
				}
			}
			else
			{
				// 2-NEPTUNE-VehicleJourney-8 : check if only one journey pattern exists in route
				// journeyPattern
				prepareCheckPoint(context,VEHICLE_JOURNEY_8);
				VehicleJourney vj = vehicleJourneys.get(objectId);
				if (vj.getRoute().getJourneyPatterns().size() != 1)
				{
					Detail errorItem = new Detail(
							VEHICLE_JOURNEY_8,
							fileLocations.get(objectId));
					addValidationError(context,VEHICLE_JOURNEY_8, errorItem);
					fkOK = false;					
				}
				else
				{
					// affect journeyPattern for following tests
					vj.setJourneyPattern(vj.getRoute().getJourneyPatterns().get(0));
					journeyPatternId = vj.getJourneyPattern().getObjectId();
				}
			}
			if (objectContext.containsKey(LINE_ID_SHORTCUT))
			{
				// 2-NEPTUNE-VehicleJourney-3 : check existence of line
				prepareCheckPoint(context,VEHICLE_JOURNEY_3);
				if (!linesContext.containsKey(objectContext.get(LINE_ID_SHORTCUT)))
				{
					Detail errorItem = new Detail(
							VEHICLE_JOURNEY_3,
							fileLocations.get(objectId), objectContext.get(LINE_ID_SHORTCUT).toString());
					addValidationError(context,VEHICLE_JOURNEY_3, errorItem);
				}
			}
			if (objectContext.containsKey(OPERATOR_ID))
			{
				// 2-NEPTUNE-VehicleJourney-4 : check existence of operator
				prepareCheckPoint(context,VEHICLE_JOURNEY_4);
				if (!companiesContext.containsKey(objectContext.get(OPERATOR_ID)))
				{
					Detail errorItem = new Detail(
							VEHICLE_JOURNEY_4,
							fileLocations.get(objectId), objectContext.get(OPERATOR_ID).toString());
					addValidationError(context,VEHICLE_JOURNEY_4, errorItem);
				}
			}
			if (objectContext.containsKey(TIME_SLOT_ID))
			{
				// 2-NEPTUNE-VehicleJourney-5 : check existence of timeslot
				prepareCheckPoint(context, VEHICLE_JOURNEY_5);
				if (!timeSlotsContext.containsKey(objectContext.get(TIME_SLOT_ID)))
				{
					Detail errorItem = new Detail(
							VEHICLE_JOURNEY_5,
							fileLocations.get(objectId), objectContext.get(TIME_SLOT_ID).toString());
					addValidationError(context,VEHICLE_JOURNEY_5, errorItem);
				}
			}

			List<String> stopsOfVj = new ArrayList<String>();
			List<Context> vjass = (List<Context>) objectContext.get(VEHICLE_JOURNEY_AT_STOPS);
			if (vjass.get(0).containsKey(ORDER))
			{
				Collections.sort(vjass, new OrderContextSorter());
			}

			for (Context vjas : vjass)
			{
				Integer lineNumber = ((Integer) objectContext.get(LINE_NUMBER)).intValue();
				Integer columnNumber = ((Integer) objectContext.get(COLUMN_NUMBER)).intValue();
				Location vjasLocation = new Location(new FileLocation(fileName, lineNumber, columnNumber),objectId);
				vjasLocation.setLine(fileLocations.get(objectId).getLine());

				String stopPointId = (String) vjas.get(STOP_POINT_ID);
				if (!stopPointsContext.containsKey(stopPointId))
				{
					Detail errorItem = new Detail(
							VEHICLE_JOURNEY_AT_STOP_1,
							vjasLocation, stopPointId);
					addValidationError(context,VEHICLE_JOURNEY_AT_STOP_1, errorItem);
					fkOK = false;
				} else
				{
					stopsOfVj.add(stopPointId);
				}
				if (vjas.containsKey(VEHICLE_JOURNEY_ID))
				{
					// 2-NEPTUNE-VehicleJourneyAtStop-2 : check existence of
					// vehicleJourney
					String vehicleJourneyId = (String) vjas.get(VEHICLE_JOURNEY_ID);
					prepareCheckPoint(context, VEHICLE_JOURNEY_AT_STOP_2);
					if (!objectId.equals(vehicleJourneyId))
					{
						Detail errorItem = new Detail(
								VEHICLE_JOURNEY_AT_STOP_2,
								vjasLocation, vehicleJourneyId);
						addValidationError(context,VEHICLE_JOURNEY_AT_STOP_2, errorItem);
					}
				}
			}
			if (!journeyPatternsContext.isEmpty())
			{
				if (journeyPatternId != null && journeyPatternsContext.containsKey(journeyPatternId))
				{
					Context journeyPatternCtx = (Context) journeyPatternsContext.get(journeyPatternId);
					String jpRouteId = (String) journeyPatternCtx.get(JourneyPatternValidator.ROUTE_ID);

					unreferencedJourneyPatterns.remove(journeyPatternId);
					if (!jpRouteId.equals(routeId))
					{
						Detail errorItem = new Detail(
								VEHICLE_JOURNEY_6,
								fileLocations.get(objectId), journeyPatternId,routeId);
						addValidationError(context,VEHICLE_JOURNEY_6, errorItem);
					} else
					{
						List<String> stops = (List<String>) journeyPatternCtx.get(JourneyPatternValidator.STOP_POINT_LIST);

						if (stopsOfVj.size() != stops.size()
								|| !stops.containsAll(stopsOfVj))
						{
							Detail errorItem = new Detail(
									VEHICLE_JOURNEY_AT_STOP_4,
									fileLocations.get(objectId), journeyPatternId);
							addValidationError(context,VEHICLE_JOURNEY_AT_STOP_4, errorItem);
						}
					}

				}


			}
			if (!fkOK)
				continue; // if fk are not valid, it is impossible to proceed
			// these checkpoints
			Context routeCtx = (Context) routesContext.get(routeId);
			if (routeCtx.containsKey(ChouetteRouteValidator.SEQUENCE_OF_ROUTE))
			{
				List<String> sequence = new ArrayList<String>((List<String>) routeCtx.get(ChouetteRouteValidator.SEQUENCE_OF_ROUTE));
				sequence.retainAll(stopsOfVj);
				if (!sequence.equals(stopsOfVj))
				{
					Detail errorItem = new Detail(
							VEHICLE_JOURNEY_AT_STOP_3,
							fileLocations.get(objectId), routeId);
					addValidationError(context,VEHICLE_JOURNEY_AT_STOP_3, errorItem);
				}
			}
		}
		if (!unreferencedJourneyPatterns.isEmpty())
		{
			// unused journeyPatterns : warning
			for (String jpId : unreferencedJourneyPatterns)
			{

				Detail errorItem = new Detail(
						VEHICLE_JOURNEY_7,
						fileLocations.get(jpId));
				addValidationError(context,VEHICLE_JOURNEY_7, errorItem);

			}

		}
		return new ValidationConstraints();
	}

	public static class DefaultValidatorFactory extends ValidatorFactory {



		@Override
		protected Validator<VehicleJourney> create(Context context) {
			VehicleJourneyValidator instance = (VehicleJourneyValidator) context.get(NAME);
			if (instance == null) {
				instance = new VehicleJourneyValidator();
				context.put(NAME, instance);
			}
			return instance;
		}

	}

	static {
		ValidatorFactory.factories
		.put(VehicleJourneyValidator.class.getName(), new DefaultValidatorFactory());
	}

	/**
	 * compare 2 vehicleJourneyAtStop on order field
	 * 
	 * @author michel
	 * 
	 */
	private class OrderContextSorter implements
	Comparator<Context>
	{

		@Override
		public int compare(Context o1,
				Context o2)
		{
			int order1 = o1.containsKey(ORDER)? ((Integer) o1.get(ORDER)).intValue():1;
			int order2 = o2.containsKey(ORDER)? ((Integer) o2.get(ORDER)).intValue():1;
			return order1 - order2;
		}

	}

}
