package mobi.chouette.exchange.validation.checkpoint;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.validation.ValidationConstraints;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.Validator;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;
import mobi.chouette.exchange.validation.report.CheckPoint;
import mobi.chouette.exchange.validation.report.Detail;
import mobi.chouette.exchange.validation.report.Location;
import mobi.chouette.exchange.validation.report.ValidationReport;
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
	public ValidationConstraints validate(Context context, VehicleJourney target) {
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
		List<VehicleJourney> beans = new ArrayList<>(data.getVehicleJourneys());
		ValidationParameters parameters = (ValidationParameters) context.get(VALIDATION);
		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		if (isEmpty(beans))
			return null;
		// 3-VehicleJourney-1 : check if time progress correctly on each stop
		// 3-VehicleJourney-2 : check speed progression
		// 3-VehicleJourney-3 : check if two journeys progress similarly
		// 3-VehicleJourney-4 : check if each journey has minimum one timetable
		// 4-VehicleJourney-2 : (optional) check transport modes
		boolean test4_1 = (parameters.getCheckVehicleJourney() != 0);
		boolean test4_2 = parameters.getCheckAllowedTransportModes() == 1;

		initCheckPoint(report, VEHICLE_JOURNEY_1, CheckPoint.SEVERITY.WARNING);
		initCheckPoint(report, VEHICLE_JOURNEY_2, CheckPoint.SEVERITY.WARNING);
		initCheckPoint(report, VEHICLE_JOURNEY_3, CheckPoint.SEVERITY.WARNING);
		initCheckPoint(report, VEHICLE_JOURNEY_4, CheckPoint.SEVERITY.WARNING);

		// checkPoint is applicable
		prepareCheckPoint(report, VEHICLE_JOURNEY_1);
		prepareCheckPoint(report, VEHICLE_JOURNEY_2);
		prepareCheckPoint(report, VEHICLE_JOURNEY_4);

		//
		if (test4_2) {
			initCheckPoint(report, L4_VEHICLE_JOURNEY_2, CheckPoint.SEVERITY.ERROR);
			prepareCheckPoint(report, L4_VEHICLE_JOURNEY_2);
		}

		if (test4_1) {
			initCheckPoint(report, L4_VEHICLE_JOURNEY_1, CheckPoint.SEVERITY.ERROR);
			prepareCheckPoint(report, L4_VEHICLE_JOURNEY_1);
		}

		for (VehicleJourney vj : beans) {
			List<VehicleJourneyAtStop> lvjas = vj.getVehicleJourneyAtStops();
			Collections.sort(lvjas, VEHICLE_JOURNEY_AT_STOP_SORTER);
		}

		for (int i = 0; i < beans.size(); i++) {
			VehicleJourney vj = beans.get(i);

			// 3-VehicleJourney-1 : check if time progress correctly on each
			// stop
			check3VehicleJourney1(context,report, vj, parameters);

			// 3-VehicleJourney-2 : check speed progression
			check3VehicleJourney2(context,report, vj, parameters);

			// 3-VehicleJourney-3 : check if two journeys progress similarly
			check3VehicleJourney3(context,report, beans, i, vj, parameters);

			// 3-VehicleJourney-4 : check if each journey has minimum one
			// timetable
			check3VehicleJourney4(context,report, vj);

			// 4-VehicleJourney-1 : (optionnal) check columns constraints
			if (test4_1)
				check4Generic1(context,report, vj, L4_VEHICLE_JOURNEY_1, parameters, log);

			// 4-VehicleJourney-2 : (optionnal) check transport modes
			if (test4_2)
				check4VehicleJourney2(context,report, vj, parameters);

		}
		return null;
	}

	private long diffTime(Time first, Time last) {
		if (first == null || last == null)
			return Long.MIN_VALUE; // TODO
		long diff = last.getTime() / 1000L - first.getTime() / 1000L;
		if (diff < 0)
			diff += 86400L; // step upon midnight : add one day in seconds
		return diff;
	}

	private void check3VehicleJourney1(Context context, ValidationReport report, VehicleJourney vj, ValidationParameters parameters) {
		// 3-VehicleJourney-1 : check if time progress correctly on each stop
		if (isEmpty(vj.getVehicleJourneyAtStops())) {
			log.error("vehicleJourney " + vj.getObjectId() + " has no vehicleJourneyAtStop");
			return;
		}
		long maxDiffTime = parameters.getInterStopDurationMax();
		List<VehicleJourneyAtStop> vjasList = vj.getVehicleJourneyAtStops();
		for (VehicleJourneyAtStop vjas : vjasList) {
			long diffTime = Math.abs(diffTime(vjas.getArrivalTime(), vjas.getDepartureTime()));
			if (diffTime > maxDiffTime) {
				Location location = buildLocation(context,vj);
				Location target = buildLocation(context,vjas.getStopPoint().getContainedInStopArea());
				Detail detail = new Detail(VEHICLE_JOURNEY_1, location, Long.toString(diffTime),
						Long.toString(maxDiffTime), target);
				addValidationError(report, VEHICLE_JOURNEY_1, detail);
			}
		}

	}

	private void check3VehicleJourney2(Context context, ValidationReport report, VehicleJourney vj, ValidationParameters parameters) {
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

			long diffTime = diffTime(vjas0.getDepartureTime(), vjas1.getArrivalTime());
			if (diffTime < 0) {
				// chronologie inverse ou non définie
				Location source = buildLocation(context,vj);
				Location target1 = buildLocation(context,vjas0.getStopPoint().getContainedInStopArea());
				Location target2 = buildLocation(context,vjas1.getStopPoint().getContainedInStopArea());

				Detail detail = new Detail(VEHICLE_JOURNEY_2 + "_1", source, target1, target2);
				addValidationError(report, VEHICLE_JOURNEY_2, detail);
			} else {

				double distance = distance(vjas0.getStopPoint().getContainedInStopArea(), vjas1.getStopPoint()
						.getContainedInStopArea());
				if (distance < 1) {
					// arrêts superposés, vitesse non calculable
				} else {
					double speed = distance / (double) diffTime * 36 / 10; // (km/h)
					if (speed < minSpeed) {
						// trop lent
						Location source = buildLocation(context,vj);
						Location target1 = buildLocation(context,vjas0.getStopPoint().getContainedInStopArea());
						Location target2 = buildLocation(context,vjas1.getStopPoint().getContainedInStopArea());

						Detail detail = new Detail(VEHICLE_JOURNEY_2 + "_2", source, Integer.toString((int) speed),
								Integer.toString((int) minSpeed), target1, target2);
						addValidationError(report, VEHICLE_JOURNEY_2, detail);
					} else if (speed > maxSpeed) {
						// trop rapide
						Location source = buildLocation(context,vj);
						Location target1 = buildLocation(context,vjas0.getStopPoint().getContainedInStopArea());
						Location target2 = buildLocation(context,vjas1.getStopPoint().getContainedInStopArea());

						Detail detail = new Detail(VEHICLE_JOURNEY_2 + "_3", source, Integer.toString((int) speed),
								Integer.toString((int) minSpeed), target1, target2);
						addValidationError(report, VEHICLE_JOURNEY_2, detail);
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

	private void check3VehicleJourney3(Context context, ValidationReport report, List<VehicleJourney> beans, int rank,
			VehicleJourney vj0, ValidationParameters parameters) {
		if (isEmpty(vj0.getVehicleJourneyAtStops()))
			return;
		// 3-VehicleJourney-3 : check if two journeys progress similarly

		TransportModeNameEnum transportMode0 = getTransportMode(vj0);

		prepareCheckPoint(report, VEHICLE_JOURNEY_3);
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
						long duration0 = diffTime(vjas0.get(j - 1).getDepartureTime(), vjas0.get(j).getArrivalTime());
						long duration1 = diffTime(vjas1.get(j - 1).getDepartureTime(), vjas1.get(j).getArrivalTime());
						if (Math.abs(duration0 - duration1) > maxDuration) {
							Location source = buildLocation(context,vj0);
							Location target1 = buildLocation(context,vj1);
							Location target2 = buildLocation(context,vjas0.get(j - 1).getStopPoint().getContainedInStopArea());
							Location target3 = buildLocation(context,vjas0.get(j).getStopPoint().getContainedInStopArea());

							Detail detail = new Detail(VEHICLE_JOURNEY_3, source, Long.toString(Math.abs(duration0
									- duration1)), Long.toString(maxDuration), target1, target2, target3);
							addValidationError(report, VEHICLE_JOURNEY_3, detail);
						}
					}
				}
			}
		}

	}

	private void check3VehicleJourney4(Context context, ValidationReport report, VehicleJourney vj) {
		// 3-VehicleJourney-4 : check if each journey has minimum one timetable
		if (isEmpty(vj.getTimetables())) {
			Location location = buildLocation(context,vj);
			Detail detail = new Detail(VEHICLE_JOURNEY_4, location);
			addValidationError(report, VEHICLE_JOURNEY_4, detail);

		}

	}

	private void check4VehicleJourney2(Context context, ValidationReport report, VehicleJourney vj, ValidationParameters parameters) {
		// 4-VehicleJourney-2 : (optional) check transport modes
		if (vj.getTransportMode() == null)
			return;
		if (getModeParameters(parameters, vj.getTransportMode().name(), log).getAllowedTransport() != 1) {
			// failure encountered, add line 1
			Location location = buildLocation(context,vj);
			Detail detail = new Detail(L4_VEHICLE_JOURNEY_2, location, vj.getTransportMode().name());
			addValidationError(report, L4_VEHICLE_JOURNEY_2, detail);
		}
	}

}
