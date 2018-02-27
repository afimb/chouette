package mobi.chouette.exchange.validation.checkpoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import lombok.Setter;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.Validator;
import mobi.chouette.exchange.validation.parameters.TransportModeParameters;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;
import mobi.chouette.exchange.validation.report.DataLocation;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.RouteSection;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.type.BoardingPossibilityEnum;
import mobi.chouette.model.type.TransportModeNameEnum;
import mobi.chouette.model.type.TransportSubModeNameEnum;
import mobi.chouette.model.util.NeptuneUtil;

import static mobi.chouette.model.VehicleJourney_.journeyPattern;

@Log4j
public class JourneyPatternCheckPoints extends AbstractValidation<JourneyPattern> implements Validator<JourneyPattern> {
	@Setter
	private VehicleJourneyCheckPoints vehicleJourneyCheckPoints;

	@Override
	public void validate(Context context, JourneyPattern target) {
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
		List<JourneyPattern> beans = new ArrayList<>(data.getJourneyPatterns());
		ValidationParameters parameters = (ValidationParameters) context.get(VALIDATION);
		if (isEmpty(beans))
			return;
		boolean sourceFile = context.get(SOURCE).equals(SOURCE_FILE);

		// init checkPoints : add here all defined check points for this kind of
		// object

		initCheckPoint(context, JOURNEY_PATTERN_1, SEVERITY.W);
		if (!sourceFile)
			initCheckPoint(context, JOURNEY_PATTERN_2, SEVERITY.E);
		initCheckPoint(context, ROUTE_SECTION_1, SEVERITY.W);
		prepareCheckPoint(context, JOURNEY_PATTERN_3);
		initCheckPoint(context, JOURNEY_PATTERN_3, SEVERITY.W);
		prepareCheckPoint(context, JOURNEY_PATTERN_4);
		initCheckPoint(context, JOURNEY_PATTERN_4, SEVERITY.W);

		initCheckPoint(context, JOURNEY_PATTERN_RB_1, SEVERITY.W);
		initCheckPoint(context, JOURNEY_PATTERN_RB_2, SEVERITY.W);
		initCheckPoint(context, JOURNEY_PATTERN_RB_3, SEVERITY.W);
		initCheckPoint(context, JOURNEY_PATTERN_RB_4, SEVERITY.E);

		// 3-JourneyPattern-1 : check if two journey patterns use same stops
		// 3-JourneyPattern-2 : Check if journey section routes count equals to
		// journey stops count minus 1 (only from database)
		// 3-RouteSection-1 : Check if route section distance doesn't exceed gap
		// as parameter

		boolean test4_1 = (parameters.getCheckJourneyPattern() != 0);
		if (test4_1) {
			initCheckPoint(context, L4_JOURNEY_PATTERN_1, SEVERITY.E);
			prepareCheckPoint(context, L4_JOURNEY_PATTERN_1);
		}

		// checkPoint is applicable
		for (int i = 0; i < beans.size(); i++) {
			JourneyPattern jp = beans.get(i);

			// 3-JourneyPattern-1 : check if two journey patterns use same stops
			check3JourneyPattern1(context, beans, i, jp);

			// 3-JourneyPattern-2 : Check if journey section route count equals
			// to journey stops count minus 1
			if (!sourceFile) {
				check3JourneyPattern2(context, jp);
				check3JourneyPatternRb4(context, jp);
			}
			// 3-RouteSection-1 : Check if route section distance doesn't exceed
			// gap as parameter
			check3RouteSection1(context, jp, parameters);

			// 3-JourneyPattern-3: Check that Line.TransportMode matches StopArea.TransportMode
			check3JourneyPattern3(context, jp);

			// 4-JourneyPattern-1 : check columns constraints
			if (test4_1)
				check4Generic1(context, jp, L4_JOURNEY_PATTERN_1, parameters, log);

			check3JourneyPattern4(context, jp);

			// 3-JourneyPattern-RB-1 : check distance between stops
			check3JourneyPatternRb1(context, jp, parameters);

			// 3-JourneyPattern-RB-2 : check if two successive stops are in same area
			check3JourneyPatternRb2(context, jp);

			for (int j = i + 1; j < beans.size(); j++) {
				// 3-JourneyPatter-rutebanken-3 : check identical journey patterns
				check3JourneyPatternRb3(context, jp, beans.get(j));
			}

		}
		return;

	}

	private void check3JourneyPattern1(Context context, List<JourneyPattern> beans, int jpRank, JourneyPattern jp) {
		// 3-JourneyPattern-1 : check if two journey patterns use same stops
		if (beans.size() <= 1)
			return;
		prepareCheckPoint(context, JOURNEY_PATTERN_1);
		int pointCount = jp.getStopPoints().size();
		List<StopPoint> sp1 = new ArrayList<>(jp.getStopPoints());
		List<StopPoint> sp2 = new ArrayList<>();
		for (int j = jpRank + 1; j < beans.size(); j++) {
			JourneyPattern jp2 = beans.get(j);
			sp2.clear();
			sp2.addAll(jp2.getStopPoints());
			if (sp1.equals(sp2)) {
				DataLocation location = buildLocation(context, jp);
				DataLocation targetLocation = buildLocation(context, jp2);

				ValidationReporter reporter = ValidationReporter.Factory.getInstance();
				reporter.addCheckPointReportError(context, JOURNEY_PATTERN_1, location, Integer.toString(pointCount),
						null, targetLocation);
			}
		}

	}

	// 3-JourneyPattern-2 : Check if journey section route count equals to
	// journey stops count minus 1
	private void check3JourneyPattern2(Context context, JourneyPattern jp) {
		int routeSectionCount = jp.getRouteSections().size();

		if (routeSectionCount > 0) {
			prepareCheckPoint(context, JOURNEY_PATTERN_2);
			// If journey section route count not equals to journey stops count
			// minus 1
			if (routeSectionCount < jp.getStopPoints().size() - 1) {
				DataLocation location = buildLocation(context, jp);
				DataLocation targetLocation = buildLocation(context, jp);

				ValidationReporter reporter = ValidationReporter.Factory.getInstance();
				reporter.addCheckPointReportError(context, JOURNEY_PATTERN_2, location,
						Integer.toString(routeSectionCount), Integer.toString(jp.getStopPoints().size() - 1),
						targetLocation);
			}

		}
	}


	// 3-JourneyPattern-4 : Check that last stop on journey pattern does not allow boarding
	private void check3JourneyPattern4(Context context, JourneyPattern jp) {
		if (jp.getArrivalStopPoint() == null) {
			return;
		}
		if (!BoardingPossibilityEnum.forbidden.equals(jp.getArrivalStopPoint().getForBoarding())) {
			DataLocation location = buildLocation(context, jp);
			ValidationReporter reporter = ValidationReporter.Factory.getInstance();
			reporter.addCheckPointReportError(context, JOURNEY_PATTERN_4, location);
		}
	}

	// 3-RouteSection-1 : Check if route section distance doesn't exceed gap as
	// parameter
	private void check3RouteSection1(Context context, JourneyPattern jp, ValidationParameters parameters) {

		prepareCheckPoint(context, ROUTE_SECTION_1);

		String modeKey = jp.getRoute().getLine().getTransportModeName().toString();
		TransportModeParameters mode = getModeParameters(parameters, modeKey, log);

		if (mode == null) {
			modeKey = "Other";
			mode = getModeParameters(parameters, MODE_OTHER, log);
			if (mode == null) {
				modeKey = "Default";
				mode = modeDefault;
			}
		}
		double distanceMax = mode.getRouteSectionStopAreaDistanceMax();
		if (distanceMax <=0 ){
			// No use unless max distance has been specified
			return;
		}

		List<RouteSection> lstRouteSection = jp.getRouteSections();
		double distance = 0;
		for (RouteSection rs : lstRouteSection) {
			double plotFirstLat = 0;
			double plotLastLat = 0;
			double plotFirstLong = 0;
			double plotLastLong = 0;

			StopArea fromStopArea = rs.getFromScheduledStopPoint().getContainedInStopAreaRef().getObject();
			StopArea toStopArea = rs.getToScheduledStopPoint().getContainedInStopAreaRef().getObject();
			if (fromStopArea == null) {
				continue;
			}

			if (rs.getNoProcessing()) {
				plotFirstLong = rs.getInputGeometry().getStartPoint().getX();
				plotFirstLat = rs.getInputGeometry().getStartPoint().getY();
				plotLastLong = rs.getInputGeometry().getEndPoint().getX();
				plotLastLat = rs.getInputGeometry().getEndPoint().getY();
			} else {
				plotFirstLong = rs.getProcessedGeometry().getStartPoint().getX();
				plotFirstLat = rs.getProcessedGeometry().getStartPoint().getY();
				plotLastLong = rs.getProcessedGeometry().getEndPoint().getX();
				plotLastLat = rs.getProcessedGeometry().getEndPoint().getY();
			}

			// Departuredepart
			distance = quickDistanceFromCoordinates(fromStopArea.getLatitude().doubleValue(), plotFirstLat,
					fromStopArea.getLongitude().doubleValue(), plotFirstLong);
			// If route section distance doesn't exceed gap as parameter
			if (distance > distanceMax) {
				DataLocation location = buildLocation(context, rs);
				DataLocation targetLocation = buildLocation(context, fromStopArea);

				ValidationReporter reporter = ValidationReporter.Factory.getInstance();
				reporter.addCheckPointReportError(context, ROUTE_SECTION_1, location, String.valueOf(distance),
						String.valueOf(distanceMax), targetLocation);
			}


			if (toStopArea == null) {
				continue;
			}
			// Arrival
			distance = quickDistanceFromCoordinates(toStopArea.getLatitude().doubleValue(), plotLastLat,
					toStopArea.getLongitude().doubleValue(), plotLastLong);
			// If route section distance doesn't exceed gap as parameter
			if (distance > distanceMax) {
				DataLocation location = buildLocation(context, rs);
				DataLocation targetLocation = buildLocation(context, fromStopArea);

				ValidationReporter reporter = ValidationReporter.Factory.getInstance();
				reporter.addCheckPointReportError(context, ROUTE_SECTION_1, location, String.valueOf(distance),
						String.valueOf(distanceMax), targetLocation);
			}
		}

	}

	public void check3JourneyPattern3(Context context, JourneyPattern vj) {

		TransportModeNameEnum lineMode = vj.getRoute().getLine().getTransportModeName();
		TransportSubModeNameEnum lineSubMode = vj.getRoute().getLine().getTransportSubModeName();

		for (StopPoint sp : vj.getStopPoints()) {

			if (sp.getScheduledStopPoint().getContainedInStopAreaRef().getObject() != null) {
				StopArea sa = sp.getScheduledStopPoint().getContainedInStopAreaRef().getObject();
				TransportModeNameEnum stopMode = sa.getTransportModeName();
				TransportSubModeNameEnum stopSubMode = sa.getTransportSubMode();

				// Recurse to parent(s) if necessary
				while (stopMode == null && sa.getParent() != null) {
					sa = sa.getParent();
					stopMode = sa.getTransportModeName();
					stopSubMode = sa.getTransportSubMode();
				}

				boolean valid = validCombination(lineMode, lineSubMode, stopMode, stopSubMode);

				if (!valid) {
					DataLocation location = buildLocation(context, vj);
					DataLocation targetLocation = buildLocation(context, sa);

					String referenceValue = stopMode + (stopSubMode != null ? "/" + stopSubMode : "");
					String errorValue = lineMode + (lineSubMode != null ? "/" + lineSubMode : "");

					ValidationReporter reporter = ValidationReporter.Factory.getInstance();
					reporter.addCheckPointReportError(context, JOURNEY_PATTERN_3, location, errorValue,
							referenceValue, targetLocation);
				}
			}
		}

	}

	private boolean validCombination(TransportModeNameEnum lineMode, TransportSubModeNameEnum lineSubMode, TransportModeNameEnum stopMode,
									 TransportSubModeNameEnum stopSubMode) {
		// TODO very simple checking
		if (lineMode == null) {
			return true;
		} else if (stopMode == null) {
			return true;
		} else if ((TransportModeNameEnum.Coach.equals(lineMode) && TransportModeNameEnum.Bus.equals(stopMode)) ||
				(TransportModeNameEnum.Bus.equals(lineMode) && TransportModeNameEnum.Coach.equals(stopMode))) {
			// Coach and bus interchangable
			return true;

		} else if (lineMode != stopMode) {
			return false;
		} else if (TransportSubModeNameEnum.RailReplacementBus.equals(stopSubMode) && (lineSubMode == null || TransportSubModeNameEnum.RailReplacementBus != stopSubMode)) {
			return false;
			// Only rail replacement bus service can visit rail replacement bus stops
		}


		return true;

	}

	private void check3JourneyPatternRb1(Context context, JourneyPattern journeyPattern, ValidationParameters parameters) {
		List<StopArea> areas = NeptuneUtil.getStopAreaOfJourneyPattern(journeyPattern);
		if (isEmpty(areas))
			return;
		// 3-JourneyPattern-rutebanken-1 : check distance between stops
		prepareCheckPoint(context, JOURNEY_PATTERN_RB_1);
		// find transportMode :
		String modeKey = journeyPattern.getRoute().getLine().getTransportModeName().toString();

		TransportModeParameters mode = getModeParameters(parameters, modeKey, log);
		if (mode == null) {
			log.error("no parameters for mode " + modeKey);
			mode = getModeParameters(parameters, MODE_OTHER, log);
			if (mode == null) {
				log.error("no parameters for mode " + MODE_OTHER);
				mode = modeDefault;
			}
		}
		double distanceMin = mode.getInterStopAreaDistanceMin();
		double distanceMax = mode.getInterStopAreaDistanceMax();

		for (int i = 1; i < areas.size(); i++) {
			StopArea firstArea = areas.get(i - 1);
			StopArea nextArea = areas.get(i);
			if (firstArea == null || nextArea == null || !firstArea.hasCoordinates() || !nextArea.hasCoordinates())
				continue;
			double distance = distance(firstArea, nextArea);
			if (distance < distanceMin) {
				DataLocation location = buildLocation(context, journeyPattern);
				DataLocation target1 = buildLocation(context, firstArea);
				DataLocation target2 = buildLocation(context, nextArea);

				ValidationReporter reporter = ValidationReporter.Factory.getInstance();
				reporter.addCheckPointReportError(context, JOURNEY_PATTERN_RB_1, "1", location, Integer.toString((int) distance),
						Integer.toString((int) distanceMin), target1, target2);
				break; // do not check for oder stops in this jp
			}
			if (distance > distanceMax) {
				DataLocation location = buildLocation(context, journeyPattern);
				DataLocation target1 = buildLocation(context, firstArea);
				DataLocation target2 = buildLocation(context, nextArea);

				ValidationReporter reporter = ValidationReporter.Factory.getInstance();
				reporter.addCheckPointReportError(context, JOURNEY_PATTERN_RB_1, "2", location, Integer.toString((int) distance),
						Integer.toString((int) distanceMax), target1, target2);
				break; // do not check for oder stops in this jp
			}
		}

	}


	private void check3JourneyPatternRb2(Context context, JourneyPattern journeyPattern) {
		// 3-JourneyPattern-rutebanken-2 : check if two successive stops are in same area
		prepareCheckPoint(context, JOURNEY_PATTERN_RB_2);

		List<StopArea> areas = NeptuneUtil.getStopAreaOfJourneyPattern(journeyPattern);
		for (int j = 1; j < areas.size(); j++) {
			if (isSame(areas.get(j - 1), areas.get(j))) {
				// failure encountered, add journey pattern 1
				DataLocation location = buildLocation(context, journeyPattern);
				DataLocation targetLocation = buildLocation(context, areas.get(j));

				ValidationReporter reporter = ValidationReporter.Factory.getInstance();
				reporter.addCheckPointReportError(context, JOURNEY_PATTERN_RB_2, location, null, null, targetLocation);
				break;
			}
		}

	}


	void check3JourneyPatternRb3(Context context, JourneyPattern journeyPattern, JourneyPattern journeyPattern2) {
		// 3-JourneyPattern-rutebanken-3 : check identical journey pattern
		if (isEmpty(journeyPattern.getStopPoints()))
			return;
		prepareCheckPoint(context, JOURNEY_PATTERN_RB_3);

		if (areIdentical(journeyPattern, journeyPattern2)) {
			// Improvement encountered, add journey pattern 1
			DataLocation location = buildLocation(context, journeyPattern);
			DataLocation target = buildLocation(context, journeyPattern2);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("journeyPatternId", journeyPattern2.getObjectId());
			ValidationReporter reporter = ValidationReporter.Factory.getInstance();
			reporter.addCheckPointReportError(context, JOURNEY_PATTERN_RB_3, location, null, null, target);
		}

	}

	private boolean areIdentical(JourneyPattern jp1, JourneyPattern jp2) {

		if (jp1.getStopPoints().size() != jp2.getStopPoints().size()) {
			return false;
		}

		Iterator<StopPoint> sp2Iterator = jp2.getStopPoints().iterator();
		for (StopPoint sp1 : jp1.getStopPoints()) {
			StopPoint sp2 = sp2Iterator.next();
			if (!Objects.equals(sp1.getScheduledStopPoint().getContainedInStopAreaRef().getObjectId(), sp2.getScheduledStopPoint().getContainedInStopAreaRef().getObjectId())) {
				return false;
			}

			if (!Objects.equals(sp1.getDestinationDisplay(), sp2.getDestinationDisplay())) {
				return false;
			}
			if (!Objects.equals(sp1.getForBoarding(), sp2.getForBoarding())) {
				return false;
			}
			if (!Objects.equals(sp1.getForAlighting(), sp2.getForAlighting())) {
				return false;
			}
		}
		return true;
	}


	private void check3JourneyPatternRb4(Context context, JourneyPattern journeyPattern) {
		// 3-JourneyPattern-rutebanken-4 : check if journeyPattern has minimum 2 StopPoints
		if (isEmpty(journeyPattern.getStopPoints()) || journeyPattern.getStopPoints().size() < 2) {
			// failure encountered, add journey pattern 1
			DataLocation location = buildLocation(context, journeyPattern);
			ValidationReporter reporter = ValidationReporter.Factory.getInstance();
			reporter.addCheckPointReportError(context, JOURNEY_PATTERN_RB_4, location);
		}
	}


	private boolean isSame(StopArea area1, StopArea area2) {
		if (area1 == null || area2 == null) {
			return false;
		}
		return area1.equals(area2);
	}

}
