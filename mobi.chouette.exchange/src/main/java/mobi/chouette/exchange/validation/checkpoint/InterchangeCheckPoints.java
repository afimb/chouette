package mobi.chouette.exchange.validation.checkpoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.Validator;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;
import mobi.chouette.exchange.validation.report.DataLocation;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import mobi.chouette.model.Interchange;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.VehicleJourneyAtStop;
import mobi.chouette.model.type.AlightingPossibilityEnum;
import mobi.chouette.model.type.BoardingPossibilityEnum;

import com.google.common.base.Joiner;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.tuple.Pair;

@Log4j
public class InterchangeCheckPoints extends AbstractValidation<Interchange> implements Validator<Interchange> {

	@Override
	public void validate(Context context, Interchange target) {
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
		List<Interchange> beans = new ArrayList<>(data.getInterchanges());
		Map<String, VehicleJourney> vehicleJourneyMap = data.getVehicleJourneys().stream().collect(Collectors.toMap(VehicleJourney::getObjectId, Function.identity()));
		ValidationParameters parameters = (ValidationParameters) context.get(VALIDATION);
		if (isEmpty(beans))
			return;

		initCheckPoints(context);


		boolean sourceFile = context.get(SOURCE).equals(SOURCE_FILE);

		if (!sourceFile) {
			checkDuplicateInterchanges(context, beans);
		}

		boolean test4_1 = (parameters.getCheckInterchange() != 0) && !sourceFile;
		if (test4_1) {
			initCheckPoint(context, L4_INTERCHANGE_1, SEVERITY.E);
			prepareCheckPoint(context, L4_INTERCHANGE_1);
		}

		for (int i = 0; i < beans.size(); i++) {
			Interchange bean = beans.get(i);

			if (!sourceFile) {
				checkInterchangePossible(context, parameters, vehicleJourneyMap, bean);
				checkInterchangeMandatoryFields(context, bean, true);
			}
			// 4-Interchange-1 : check columns constraints
			if (test4_1) {
				check4Generic1(context, bean, L4_INTERCHANGE_1, parameters, log);
			}


		}
		return;
	}

	void initCheckPoints(Context context) {
		initCheckPoint(context, INTERCHANGE_1, SEVERITY.E);
		prepareCheckPoint(context, INTERCHANGE_1);
		initCheckPoint(context, INTERCHANGE_2, SEVERITY.E);
		prepareCheckPoint(context, INTERCHANGE_2);
		initCheckPoint(context, INTERCHANGE_3, SEVERITY.E);
		prepareCheckPoint(context, INTERCHANGE_3);
		initCheckPoint(context, INTERCHANGE_4, SEVERITY.E);
		prepareCheckPoint(context, INTERCHANGE_4);
		initCheckPoint(context, INTERCHANGE_5, SEVERITY.W);
		prepareCheckPoint(context, INTERCHANGE_5);
		initCheckPoint(context, INTERCHANGE_6_1, SEVERITY.W);
		prepareCheckPoint(context, INTERCHANGE_6_1);
		initCheckPoint(context, INTERCHANGE_6_2, SEVERITY.W);
		prepareCheckPoint(context, INTERCHANGE_6_2);
		initCheckPoint(context, INTERCHANGE_7_1, SEVERITY.W);
		prepareCheckPoint(context, INTERCHANGE_7_1);
		initCheckPoint(context, INTERCHANGE_7_2, SEVERITY.W);
		prepareCheckPoint(context, INTERCHANGE_7_2);
		initCheckPoint(context, INTERCHANGE_8_1, SEVERITY.W);
		prepareCheckPoint(context, INTERCHANGE_8_1);
		initCheckPoint(context, INTERCHANGE_8_2, SEVERITY.W);
		prepareCheckPoint(context, INTERCHANGE_8_2);
		initCheckPoint(context, INTERCHANGE_9_1, SEVERITY.W);
		prepareCheckPoint(context, INTERCHANGE_9_1);
		initCheckPoint(context, INTERCHANGE_9_2, SEVERITY.W);
		prepareCheckPoint(context, INTERCHANGE_9_2);
	}

	void checkInterchangePossible(Context context, ValidationParameters parameters, Map<String, VehicleJourney> vehicleJourneyMap, Interchange interchange) {
		VehicleJourney consumerVJ = vehicleJourneyMap.get(interchange.getConsumerVehicleJourneyObjectid());
		VehicleJourneyAtStop consumerVJAtStop = getVehicleJourneyAtStop(consumerVJ, interchange.getConsumerStopPointObjectid());


		VehicleJourney feederVJ = vehicleJourneyMap.get(interchange.getFeederVehicleJourneyObjectid());
		VehicleJourneyAtStop feederVJAtStop = getVehicleJourneyAtStop(consumerVJ, interchange.getFeederStopPointObjectid());


		checkFeederStopInVehicleJourney(context, interchange, feederVJ, feederVJAtStop);
		checkConsumerStopInVehicleJourney(context, interchange, consumerVJ, consumerVJAtStop);


		checkDistance(context, parameters, interchange);

		checkWaitTime(context, parameters, interchange, consumerVJAtStop, feederVJAtStop);

		checkFeederAlighting(context, interchange, feederVJAtStop);

		checkConsumerBoarding(context, interchange, consumerVJAtStop);
	}

	void checkDistance(Context context, ValidationParameters parameters, Interchange interchange) {
		if (interchange.getFeederStopPoint() != null && interchange.getConsumerStopPoint() != null) {

			StopArea fromArea = interchange.getFeederStopPoint().getContainedInStopAreaRef().getObject();
			StopArea toArea = interchange.getConsumerStopPoint().getContainedInStopAreaRef().getObject();

			if (fromArea != null && toArea != null) {
				double distance = quickDistance(fromArea, toArea);
				int warnDistance = parameters.getInterchangeMaxDistance();


				if (warnDistance > 0 && distance > warnDistance) {
					ValidationReporter reporter = ValidationReporter.Factory.getInstance();
					DataLocation source = buildLocation(context, interchange);
					DataLocation target0 = buildLocation(context, fromArea);
					DataLocation target1 = buildLocation(context, toArea);
					String checkPointName = INTERCHANGE_7_2;
					int refValue = warnDistance;

					int errorDistance = 3 * warnDistance;
					if (distance > errorDistance) {
						checkPointName = INTERCHANGE_7_1;
						refValue = errorDistance;
					}
					reporter.addCheckPointReportError(context, checkPointName, source, "" + distance, "" + refValue, target0, target1);
				}
			}
		}
	}

	void checkConsumerStopInVehicleJourney(Context context, Interchange interchange, VehicleJourney consumerVJ, VehicleJourneyAtStop consumerVJAtStop) {
		if (consumerVJ != null && consumerVJAtStop == null) {
			ValidationReporter reporter = ValidationReporter.Factory.getInstance();
			DataLocation source = buildLocation(context, interchange);
			DataLocation target0 = buildLocation(context, interchange.getConsumerStopPoint());
			DataLocation target1 = buildLocation(context, consumerVJ);
			reporter.addCheckPointReportError(context, INTERCHANGE_6_1, source, "", "", target0, target1);
		}
	}

	void checkFeederStopInVehicleJourney(Context context, Interchange interchange, VehicleJourney feederVJ, VehicleJourneyAtStop feederVJAtStop) {
		if (feederVJ != null && feederVJAtStop == null) {
			ValidationReporter reporter = ValidationReporter.Factory.getInstance();
			DataLocation source = buildLocation(context, interchange);
			DataLocation target0 = buildLocation(context, interchange.getFeederStopPoint());
			DataLocation target1 = buildLocation(context, feederVJ);
			reporter.addCheckPointReportError(context, INTERCHANGE_6_2, source, "", "", target0, target1);
		}
	}

	private void checkWaitTime(Context context, ValidationParameters parameters, Interchange interchange, VehicleJourneyAtStop consumerVJAtStop, VehicleJourneyAtStop feederVJAtStop) {
		if (feederVJAtStop != null && consumerVJAtStop != null) {
			long warnWaitMs = parameters.getInterchangeMaxWaitSeconds() * 1000;
			long errorWaitMs = 3 * warnWaitMs;
			long refValueMs = warnWaitMs;
			String checkPointName = INTERCHANGE_8_2;

			int dayOffset = consumerVJAtStop.getDepartureDayOffset() - feederVJAtStop.getArrivalDayOffset();
			long msWait = consumerVJAtStop.getDepartureTime().getMillisOfSecond() - feederVJAtStop.getArrivalTime().getMillisOfDay();
			if (msWait < 0) {
				msWait = DateUtils.MILLIS_PER_DAY - msWait;
				dayOffset++;
			}


			boolean accepted = false;
			if (msWait <= errorWaitMs) {
				//  TODO check dates
				if (msWait <= warnWaitMs) {
					accepted = true;
				}

			} else {
				refValueMs = errorWaitMs;
				checkPointName = INTERCHANGE_8_1;
			}

			if (!accepted) {
				ValidationReporter reporter = ValidationReporter.Factory.getInstance();
				DataLocation source = buildLocation(context, interchange);
				String waitSeconds = "" + (msWait / 1000);
				String refValueSeconds = "" + (refValueMs / 1000);
				reporter.addCheckPointReportError(context, checkPointName, source, waitSeconds, refValueSeconds);

			}

		}
	}

	void checkConsumerBoarding(Context context, Interchange interchange, VehicleJourneyAtStop consumerVJAtStop) {
		if (consumerVJAtStop != null && BoardingPossibilityEnum.forbidden.equals(consumerVJAtStop.getStopPoint().getForBoarding())) {
			ValidationReporter reporter = ValidationReporter.Factory.getInstance();
			DataLocation source = buildLocation(context, interchange);
			DataLocation target = buildLocation(context, consumerVJAtStop.getStopPoint());
			reporter.addCheckPointReportError(context, INTERCHANGE_9_2, source, "", "", target);
		}
	}

	void checkFeederAlighting(Context context, Interchange interchange, VehicleJourneyAtStop feederVJAtStop) {
		if (feederVJAtStop != null && AlightingPossibilityEnum.forbidden.equals(feederVJAtStop.getStopPoint().getForAlighting())) {
			ValidationReporter reporter = ValidationReporter.Factory.getInstance();
			DataLocation source = buildLocation(context, interchange);
			DataLocation target = buildLocation(context, feederVJAtStop.getStopPoint());
			reporter.addCheckPointReportError(context, INTERCHANGE_9_1, source, "", "", target);
		}
	}


	private VehicleJourneyAtStop getVehicleJourneyAtStop(VehicleJourney vehicleJourney, String scheduledStopPointId) {
		if (vehicleJourney == null) {
			return null;
		}

		return vehicleJourney.getVehicleJourneyAtStops().stream()
				.filter(vjas -> Objects.equals(vjas.getStopPoint().getScheduledStopPoint().getObjectId(), scheduledStopPointId)).findFirst().orElse(null);

	}

	private void checkInterchangeMandatoryFields(Context context, Interchange interchange,
												 boolean onlyWithinReferential) {

		ValidationReporter reporter = ValidationReporter.Factory.getInstance();
		if (interchange.getFeederStopPoint() == null) {
			DataLocation source = buildLocation(context, interchange);
			reporter.addCheckPointReportError(context, INTERCHANGE_1, source, interchange.getFeederStopPointObjectid());
		}
		if (interchange.getFeederVehicleJourney() == null) {
			DataLocation source = buildLocation(context, interchange);
			reporter.addCheckPointReportError(context, INTERCHANGE_2, source, interchange.getFeederVehicleJourneyObjectid());
		}

		// TODO code only support local interchanges (within dataspace)

		if (interchange.getConsumerStopPoint() == null) {
			String consumerScheduledStopPointId = interchange.getConsumerStopPointObjectid();
			if (!onlyWithinReferential || consumerScheduledStopPointId == null || consumerScheduledStopPointId.startsWith(interchange.objectIdPrefix())) {
				DataLocation source = buildLocation(context, interchange);
				reporter.addCheckPointReportError(context, INTERCHANGE_3, source, consumerScheduledStopPointId);
			}
		}
		if (interchange.getConsumerVehicleJourney() == null) {
			String consumerVehicleJourneyId = interchange.getConsumerVehicleJourneyObjectid();
			if (!onlyWithinReferential || consumerVehicleJourneyId == null || consumerVehicleJourneyId.startsWith(interchange.objectIdPrefix())) {
				DataLocation source = buildLocation(context, interchange);
				reporter.addCheckPointReportError(context, INTERCHANGE_4, source, consumerVehicleJourneyId);
			}
		}

	}

	private void checkDuplicateInterchanges(Context context, List<Interchange> interchangeList) {
		ValidationReporter reporter = ValidationReporter.Factory.getInstance();

		for (Pair<Interchange, Interchange> duplicate : findDuplicates(interchangeList)) {
			DataLocation source = buildLocation(context, duplicate.getLeft());
			DataLocation target = buildLocation(context, duplicate.getRight());
			reporter.addCheckPointReportError(context, INTERCHANGE_5, source, "", "", target);
		}

	}

	protected List<Pair<Interchange, Interchange>> findDuplicates(List<Interchange> interchangeList) {
		List<Pair<Interchange, Interchange>> duplicates = new ArrayList<>();
		Map<String, Interchange> interchangesByUniqueKeys = new HashMap<>();
		for (Interchange interchange : interchangeList) {
			String key = toUniqueKey(interchange);
			Interchange existing = interchangesByUniqueKeys.get(key);
			if (existing != null) {
				duplicates.add(Pair.of(existing, interchange));
			} else {
				interchangesByUniqueKeys.put(key, interchange);
			}
		}
		return duplicates;
	}


	private String toUniqueKey(Interchange i) {
		return Joiner.on(".").join(i.getFeederStopPointObjectid(), i.getConsumerStopPointObjectid(), i.getFeederVehicleJourneyObjectid(), i.getConsumerVehicleJourneyObjectid());
	}


}
