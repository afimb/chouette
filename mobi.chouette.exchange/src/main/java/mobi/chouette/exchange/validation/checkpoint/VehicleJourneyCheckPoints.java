package mobi.chouette.exchange.validation.checkpoint;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.Validator;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;
import mobi.chouette.exchange.validation.report.DataLocation;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.VehicleJourneyAtStop;
import mobi.chouette.model.type.TransportModeNameEnum;

/**
 * check a group of coherent vehicle journeys (i.e. on the same journey pattern)
 * <ul>
 * <li>3-VehicleJourney-1 : check if time progress correctly on each stop</li>
 * <li>3-VehicleJourney-2 : check speed progression</li>
 * <li>3-VehicleJourney-3 : check if two journeys progress similarly</li>
 * <li>3-VehicleJourney-4 : check if each journey has minimum one timetable</li>
 * <li>3-VehicleJourney-5 : check if time progress correctly with offset on each stop and between two stops</li>
 * </ul>
 * 
 * 
 * @author michel
 * 
 */
@Log4j
public class VehicleJourneyCheckPoints extends AbstractValidation<VehicleJourney> implements Validator<VehicleJourney> {

	private static Comparator<VehicleJourneyAtStop> VEHICLE_JOURNEY_AT_STOP_SORTER = new Comparator<VehicleJourneyAtStop>() {

		@Override
		public int compare(VehicleJourneyAtStop o1, VehicleJourneyAtStop o2) {
			return o1.getStopPoint().getPosition() - o2.getStopPoint().getPosition();
		}

	};

	@Override
	public void validate(Context context, VehicleJourney target) {
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
		List<VehicleJourney> beans = new ArrayList<>(data.getVehicleJourneys());
		ValidationParameters parameters = (ValidationParameters) context.get(VALIDATION);
		if (isEmpty(beans))
			return;
		// 3-VehicleJourney-1 : check if time progress correctly on each stop
		// 3-VehicleJourney-2 : check speed progression
		// 3-VehicleJourney-3 : check if two journeys progress similarly
		// 3-VehicleJourney-4 : check if each journey has minimum one timetable
		// 3-VehicleJourney-5 : check if time progress correctly with offset on each stop and between two stops
		// 4-VehicleJourney-2 : (optional) check transport modes
		boolean test4_1 = (parameters.getCheckVehicleJourney() != 0);
		boolean test4_2 = parameters.getCheckAllowedTransportModes() == 1;

		initCheckPoint(context, VEHICLE_JOURNEY_1, SEVERITY.W);
		initCheckPoint(context, VEHICLE_JOURNEY_2, SEVERITY.W);
		initCheckPoint(context, VEHICLE_JOURNEY_3, SEVERITY.W);
		initCheckPoint(context, VEHICLE_JOURNEY_4, SEVERITY.W);
		initCheckPoint(context, VEHICLE_JOURNEY_5, SEVERITY.E);
		
		// checkPoint is applicable
		prepareCheckPoint(context, VEHICLE_JOURNEY_1);
		prepareCheckPoint(context, VEHICLE_JOURNEY_2);
		prepareCheckPoint(context, VEHICLE_JOURNEY_4);
		prepareCheckPoint(context, VEHICLE_JOURNEY_5);

		//
		if (test4_2) {
			initCheckPoint(context, L4_VEHICLE_JOURNEY_2, SEVERITY.E);
			prepareCheckPoint(context, L4_VEHICLE_JOURNEY_2);
		}

		if (test4_1) {
			initCheckPoint(context, L4_VEHICLE_JOURNEY_1, SEVERITY.E);
			prepareCheckPoint(context, L4_VEHICLE_JOURNEY_1);
		}

		for (VehicleJourney vj : beans) {
			List<VehicleJourneyAtStop> lvjas = vj.getVehicleJourneyAtStops();
			Collections.sort(lvjas, VEHICLE_JOURNEY_AT_STOP_SORTER);
		}

		for (int i = 0; i < beans.size(); i++) {
			VehicleJourney vj = beans.get(i);

			// 3-VehicleJourney-1 : check if time progress correctly on each
			// stop
			check3VehicleJourney1(context, vj, parameters);

			// 3-VehicleJourney-2 : check speed progression
			check3VehicleJourney2(context, vj, parameters);

			// 3-VehicleJourney-3 : check if two journeys progress similarly
			check3VehicleJourney3(context, beans, i, vj, parameters);

			// 3-VehicleJourney-4 : check if each journey has minimum one
			// timetable
			check3VehicleJourney4(context, vj);
			
			// 3-VehicleJourney-5 : check if time progress correctly with offset on each stop and between two stops
			check3VehicleJourney5(context, vj);

			// 4-VehicleJourney-1 : (optionnal) check columns constraints
			if (test4_1)
				check4Generic1(context, vj, L4_VEHICLE_JOURNEY_1, parameters, log);

			// 4-VehicleJourney-2 : (optionnal) check transport modes
			if (test4_2)
				check4VehicleJourney2(context, vj, parameters);

		}
		return ;
	}

	
	/**
	 * Time between two time values with offset handling
	 * @param first
	 * @param firstTimeOffset
	 * @param last
	 * @param lastTimeOffset
	 * @return
	 */
	private long diffTime(Time first, int firstTimeOffset, Time last, int lastTimeOffset) {
		if (first == null || last == null)
			return Long.MIN_VALUE; // TODO
		
		long firstOffset = firstTimeOffset * 86400L;
		long lastOffset = lastTimeOffset * 86400L;
		
		long lastTime = (last.getTime() / 1000L) + lastOffset;
		long firstTime = (first.getTime() / 1000L) + firstOffset;
		
		long diff = lastTime - firstTime;
		
		return diff;
	}
	

	private void check3VehicleJourney1(Context context,  VehicleJourney vj, ValidationParameters parameters) {
		// 3-VehicleJourney-1 : check if time progress correctly on each stop
		if (isEmpty(vj.getVehicleJourneyAtStops())) {
			log.error("vehicleJourney " + vj.getObjectId() + " has no vehicleJourneyAtStop");
			return;
		}
		long maxDiffTime = parameters.getInterStopDurationMax();
		List<VehicleJourneyAtStop> vjasList = vj.getVehicleJourneyAtStops();
		for (VehicleJourneyAtStop vjas : vjasList) {
			long diffTime = Math.abs(diffTime(vjas.getArrivalTime(), vjas.getArrivalDayOffset(), vjas.getDepartureTime(), vjas.getDepartureDayOffset())); /** GJT */
			if (diffTime > maxDiffTime) {
				DataLocation location = buildLocation(context,vj);
				DataLocation target = buildLocation(context,vjas.getStopPoint().getContainedInStopArea());
				ValidationReporter reporter = ValidationReporter.Factory.getInstance();
				reporter.addCheckPointReportError(context,VEHICLE_JOURNEY_1, location, Long.toString(diffTime),
						Long.toString(maxDiffTime), target);
			}
			
			/** GJT : Difference between two times on one stop cannot be negative 
			else if (diffTime < 0) {
				//TODO Créer un message de rapport spécifique à différence négative
				DataLocation location = buildLocation(context,vj);
				DataLocation target = buildLocation(context,vjas.getStopPoint().getContainedInStopArea());
				reporter.addCheckPointReportError(context,VEHICLE_JOURNEY_1, location, Long.toString(diffTime),
						Long.toString(maxDiffTime), target);
			}
			*/
		}

	}

	private void check3VehicleJourney2(Context context,  VehicleJourney vj, ValidationParameters parameters) {
		if (isEmpty(vj.getVehicleJourneyAtStops()))
			return;
		// 3-VehicleJourney-2 : check speed progression
		TransportModeNameEnum transportMode = getTransportMode(vj);
		long maxSpeed = getModeParameters(parameters, transportMode.toString(), log).getSpeedMax();
		long minSpeed = getModeParameters(parameters, transportMode.toString(), log).getSpeedMin();
		List<VehicleJourneyAtStop> vjasList = vj.getVehicleJourneyAtStops();
		for (int i = 1; i < vjasList.size(); i++) {
			VehicleJourneyAtStop vjas0 = vjasList.get(i - 1);
			VehicleJourneyAtStop vjas1 = vjasList.get(i);

			long diffTime = diffTime(vjas0.getDepartureTime(), vjas0.getDepartureDayOffset(), vjas1.getArrivalTime(), vjas1.getArrivalDayOffset()); /** GJT */
			if (diffTime < 0) {
				// chronologie inverse ou non définie
				DataLocation source = buildLocation(context,vj);
				DataLocation target1 = buildLocation(context,vjas0.getStopPoint().getContainedInStopArea());
				DataLocation target2 = buildLocation(context,vjas1.getStopPoint().getContainedInStopArea());

				ValidationReporter reporter = ValidationReporter.Factory.getInstance();
				reporter.addCheckPointReportError(context,VEHICLE_JOURNEY_2 , "1", source, null,null,target1, target2);
			} else {

				double distance = distance(vjas0.getStopPoint().getContainedInStopArea(), vjas1.getStopPoint()
						.getContainedInStopArea());
				if (distance < 1) {
					// arrêts superposés, vitesse non calculable
				} else {
					double speed = distance / (double) diffTime * 36 / 10; // (km/h)
					if (speed < minSpeed) {
						// trop lent
						DataLocation source = buildLocation(context,vj);
						DataLocation target1 = buildLocation(context,vjas0.getStopPoint().getContainedInStopArea());
						DataLocation target2 = buildLocation(context,vjas1.getStopPoint().getContainedInStopArea());

						ValidationReporter reporter = ValidationReporter.Factory.getInstance();
						reporter.addCheckPointReportError(context,VEHICLE_JOURNEY_2, "2", source, Integer.toString((int) speed),
								Integer.toString((int) minSpeed), target1, target2);
					} else if (speed > maxSpeed) {
						// trop rapide
						DataLocation source = buildLocation(context,vj);
						DataLocation target1 = buildLocation(context,vjas0.getStopPoint().getContainedInStopArea());
						DataLocation target2 = buildLocation(context,vjas1.getStopPoint().getContainedInStopArea());

						ValidationReporter reporter = ValidationReporter.Factory.getInstance();
						reporter.addCheckPointReportError(context,VEHICLE_JOURNEY_2, "3", source, Integer.toString((int) speed),
								Integer.toString((int) minSpeed), target1, target2);
					}
				}
			}
		}

	}

	/**
	 * @param vj
	 * @return
	 */
	private TransportModeNameEnum getTransportMode(VehicleJourney vj) {
		TransportModeNameEnum transportMode = vj.getTransportMode();
		if (transportMode == null) {
			transportMode = vj.getRoute().getLine().getTransportModeName();
			if (transportMode == null)
				transportMode = TransportModeNameEnum.Other;
		}
		return transportMode;
	}

	private void check3VehicleJourney3(Context context,  List<VehicleJourney> beans, int rank,
			VehicleJourney vj0, ValidationParameters parameters) {
		if (isEmpty(vj0.getVehicleJourneyAtStops()))
			return;
		// 3-VehicleJourney-3 : check if two journeys progress similarly

		TransportModeNameEnum transportMode0 = getTransportMode(vj0);

		prepareCheckPoint(context, VEHICLE_JOURNEY_3);
		long maxDuration = getModeParameters(parameters, transportMode0.toString(), log)
				.getInterStopDurationVariationMax();

		List<VehicleJourneyAtStop> vjas0 = vj0.getVehicleJourneyAtStops();
		for (int i = rank + 1; i < beans.size(); i++) {
			VehicleJourney vj1 = beans.get(i);
			if (vj1.getJourneyPattern().equals(vj0.getJourneyPattern())) {
				List<VehicleJourneyAtStop> vjas1 = vj1.getVehicleJourneyAtStops();
				if (vjas1.size() != vjas0.size()) {
					// FATAL ERROR : TODO
					log.error("vehicleJourney " + vj1.getObjectId() + " has different vehicleJourneyAtStop count "
							+ vjas1.size() + " than vehicleJourney " + vj0.getObjectId());
					break;
				}
				TransportModeNameEnum transportMode1 = getTransportMode(vj1);
				if (transportMode1.equals(transportMode0)) {
					for (int j = 1; j < vjas0.size(); j++) {
						long duration0 = diffTime(vjas0.get(j - 1).getDepartureTime(), vjas0.get(j - 1).getDepartureDayOffset(), vjas0.get(j).getArrivalTime(), vjas0.get(j).getArrivalDayOffset()); /** GJT */
						long duration1 = diffTime(vjas1.get(j - 1).getDepartureTime(), vjas1.get(j - 1).getDepartureDayOffset(), vjas1.get(j).getArrivalTime(), vjas1.get(j).getArrivalDayOffset()); /** GJT */
						if (Math.abs(duration0 - duration1) > maxDuration) {
							DataLocation source = buildLocation(context,vj0);
							DataLocation target1 = buildLocation(context,vj1);
							DataLocation target2 = buildLocation(context,vjas0.get(j - 1).getStopPoint().getContainedInStopArea());
							DataLocation target3 = buildLocation(context,vjas0.get(j).getStopPoint().getContainedInStopArea());

							ValidationReporter reporter = ValidationReporter.Factory.getInstance();
							reporter.addCheckPointReportError(context,VEHICLE_JOURNEY_3, source, Long.toString(Math.abs(duration0
									- duration1)), Long.toString(maxDuration), target1, target2, target3);
						}
					}
				}
			}
		}

	}

	private void check3VehicleJourney4(Context context, VehicleJourney vj) {
		// 3-VehicleJourney-4 : check if each journey has minimum one timetable
		if (isEmpty(vj.getTimetables())) {
			DataLocation location = buildLocation(context,vj);
			ValidationReporter reporter = ValidationReporter.Factory.getInstance();
			reporter.addCheckPointReportError(context,VEHICLE_JOURNEY_4, location);

		}

	}
	
	private void check3VehicleJourney5(Context context,  VehicleJourney vj) {
		// 3-VehicleJourney-5 : check if time progress correctly on each stop including offset
		if (isEmpty(vj.getVehicleJourneyAtStops())) {
			log.error("vehicleJourney " + vj.getObjectId() + " has no vehicleJourneyAtStop");
			return;
		}
		
		
		VehicleJourneyAtStop previous_vjas = null;
		long diffTime = 0;
		
		List<VehicleJourneyAtStop> vjasList = vj.getVehicleJourneyAtStops();
		for (VehicleJourneyAtStop vjas : vjasList) {
			
			/** First stop */
			if(previous_vjas == null) {
				
				/** Difference between arrival and departure time for the first stop */
				diffTime = diffTime(vjas.getArrivalTime(), vjas.getArrivalDayOffset(), vjas.getDepartureTime(), vjas.getDepartureDayOffset());
				
				/** GJT : Difference between two times on one stop cannot be negative */
				if (diffTime < 0) {
					//TODO Créer un message de rapport spécifique à différence négative
					DataLocation location = buildLocation(context,vj);
					DataLocation target = buildLocation(context,vjas.getStopPoint().getContainedInStopArea());
					ValidationReporter reporter = ValidationReporter.Factory.getInstance();
					reporter.addCheckPointReportError(context,VEHICLE_JOURNEY_5, location, Long.toString(diffTime),
							Long.toString(diffTime), target);
				}
				
			} else {
				
				/** Difference between arrival times of two stops */
				diffTime = diffTime(previous_vjas.getArrivalTime(), previous_vjas.getArrivalDayOffset(), vjas.getArrivalTime(), vjas.getArrivalDayOffset());
				
				/** GJT : Difference between two times on one stop cannot be negative */
				if (diffTime < 0) {
					//TODO Créer un message de rapport spécifique à différence négative
					DataLocation location = buildLocation(context,vj);
					DataLocation target = buildLocation(context,vjas.getStopPoint().getContainedInStopArea());
					ValidationReporter reporter = ValidationReporter.Factory.getInstance();
					reporter.addCheckPointReportError(context,VEHICLE_JOURNEY_5, location, Long.toString(diffTime),
							Long.toString(diffTime), target);
				}
				
				/** Difference between departure times of two stops */
				diffTime = diffTime(previous_vjas.getDepartureTime(), previous_vjas.getDepartureDayOffset(), vjas.getDepartureTime(), vjas.getDepartureDayOffset());
				
				/** GJT : Difference between two times on one stop cannot be negative */
				if (diffTime < 0) {
					//TODO Créer un message de rapport spécifique à différence négative
					DataLocation location = buildLocation(context,vj);
					DataLocation target = buildLocation(context,vjas.getStopPoint().getContainedInStopArea());
					ValidationReporter reporter = ValidationReporter.Factory.getInstance();
					reporter.addCheckPointReportError(context,VEHICLE_JOURNEY_5, location, Long.toString(diffTime),
							Long.toString(diffTime), target);
				}
				
			}
			
			previous_vjas = vjas;
			
		}

	}

	private void check4VehicleJourney2(Context context, VehicleJourney vj, ValidationParameters parameters) {
		// 4-VehicleJourney-2 : (optional) check transport modes
		if (vj.getTransportMode() == null)
			return;
		if (getModeParameters(parameters, vj.getTransportMode().name(), log).getAllowedTransport() != 1) {
			// failure encountered, add line 1
			DataLocation location = buildLocation(context,vj);
			ValidationReporter reporter = ValidationReporter.Factory.getInstance();
			reporter.addCheckPointReportError(context,L4_VEHICLE_JOURNEY_2, location, vj.getTransportMode().name());
		}
	}

}
