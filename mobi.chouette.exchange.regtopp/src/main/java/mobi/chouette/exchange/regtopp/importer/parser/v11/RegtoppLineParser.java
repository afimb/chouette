package mobi.chouette.exchange.regtopp.importer.parser.v11;

import static mobi.chouette.common.Constant.CONFIGURATION;
import static mobi.chouette.common.Constant.PARSER;
import static mobi.chouette.common.Constant.REFERENTIAL;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.common.Pair;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.regtopp.RegtoppConstant;
import mobi.chouette.exchange.regtopp.importer.RegtoppImportParameters;
import mobi.chouette.exchange.regtopp.importer.RegtoppImporter;
import mobi.chouette.exchange.regtopp.importer.index.Index;
import mobi.chouette.exchange.regtopp.importer.index.v11.DaycodeById;
import mobi.chouette.exchange.regtopp.importer.parser.ObjectIdCreator;
import mobi.chouette.exchange.regtopp.importer.parser.LineSpecificParser;
import mobi.chouette.exchange.regtopp.importer.version.VersionHandler;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppDayCodeHeaderDKO;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppLineLIN;
import mobi.chouette.model.Footnote;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.Network;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.VehicleJourneyAtStop;
import mobi.chouette.model.type.AlightingPossibilityEnum;
import mobi.chouette.model.type.BoardingAlightingPossibilityEnum;
import mobi.chouette.model.type.BoardingPossibilityEnum;
import mobi.chouette.model.type.TransportModeNameEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

@Log4j
public class RegtoppLineParser extends LineSpecificParser {


	/*
	 * Validation rules of type III are checked at this step.
	 */
	// TODO. Rename this function "translate(Context context)" or "produce(Context context)", ...
	@Override
	public void parse(Context context) throws Exception {

		// Her tar vi allerede konsistenssjekkede data (ref validate-metode over) og bygger opp tilsvarende struktur i chouette.
		// Merk at import er linje-sentrisk, så man skal i denne klassen returnerer 1 line med x antall routes og stoppesteder, journeypatterns osv

		Referential referential = (Referential) context.get(REFERENTIAL);

		RegtoppImporter importer = (RegtoppImporter) context.get(PARSER);
		RegtoppImportParameters configuration = (RegtoppImportParameters) context.get(CONFIGURATION);
		String calendarStartDate = (String) context.get(RegtoppConstant.CALENDAR_START_DATE);

		String chouetteLineId = ObjectIdCreator.createLineId(configuration, lineId, calendarStartDate);

		// Create the actual Chouette Line and put it in the "referential" space (which is later used by the LineImporterCommand)
		Line line = ObjectFactory.getLine(referential, chouetteLineId);

		// Find line number (TODO check if index exists)
		if (importer.hasLINImporter()) {
			Index<RegtoppLineLIN> lineById = importer.getLineById();
			RegtoppLineLIN regtoppLine = lineById.getValue(lineId);
			if (regtoppLine != null) {
				line.setName(regtoppLine.getName());
				line.setPublishedName(regtoppLine.getName());
			}
		}

		List<Footnote> footnotes = line.getFootnotes();

		VersionHandler versionHandler = (VersionHandler) context.get(RegtoppConstant.VERSION_HANDLER);

		
		
		// Parse Route and JourneyPattern
		LineSpecificParser routeParser = versionHandler.createRouteParser();
		routeParser.setLineId(lineId);
		routeParser.parse(context);

		// Parse VehicleJourney
		List<TransportModePair> transportModes = new ArrayList<TransportModePair>();

		LineSpecificParser tripParser = versionHandler.createTripParser();
		tripParser.setLineId(lineId);
		tripParser.setTransportModes(transportModes);
		tripParser.parse(context);

		// Update transport mode for line
		updateLineTransportMode(referential, line,transportModes);

		// Link line to footnotes
		for (Footnote f : footnotes) {
			f.setLine(line);
		}

		// NOT DETERMINISTIC: deduplicateIdenticalRoutes(referential, configuration);
		// NOT WORKING: deduplicateSimilarRoutes(referential, configuration);
		// NOT DETERMINISTIC: deduplicateIdenticalJourneyPatterns(referential, configuration);
		// Update boarding/alighting at StopPoint
		updateBoardingAlighting(referential, configuration);
		updateLineName(referential, line, configuration);
		removeLineNumberFromRouteAndJourneyPatternsAndVehicleJourneys(referential, line, configuration);
		updateNetworkDate(importer, referential, line, configuration);

	}

	/**
	 * Reduce number of duplicate routes
	 * 
	 * @param referential
	 * @param configuration
	 */
	public void deduplicateSimilarRoutes(Referential referential, RegtoppImportParameters configuration) {
		Pair<Route, Route> similarPair = null;

		do {
			similarPair = findSimilarRoutes(referential, configuration);
			if (similarPair != null) {
				mergeRoutes(referential, configuration, similarPair);
			}
		} while (similarPair != null);
	}

	/**
	 * Reduce number of duplicate routes
	 * 
	 * @param referential
	 * @param configuration
	 */
	public void deduplicateIdenticalRoutes(Referential referential, RegtoppImportParameters configuration) {
		Pair<Route, Route> duplicatePair = null;

		do {
			duplicatePair = findDuplicateRoutes(referential, configuration);
			if (duplicatePair != null) {
				mergeRoutes(referential, configuration, duplicatePair);
			}
		} while (duplicatePair != null);
	}

	public void mergeRoutes(Referential referential, RegtoppImportParameters configuration, Pair<Route, Route> duplicatePair) {
		// Merge routes, that means drop the "right" Route along with StopPoints and JourneyPattern

		Route left = duplicatePair.getLeft();
		Route right = duplicatePair.getRight();

		log.info("Merging route " + left.getObjectId() + " and " + right.getObjectId());

		// Build a map of corresponding StopPoints
		Map<String, String> rightToLeftStopPointMap = buildStopPointConversionMap( left,  right);

		// There is a 1-1 for Route -> JourneyPattern in Regtopp. Therefore routes that are equal also have equal journey patterns.
		List<JourneyPattern> rightJourneyPatterns = right.getJourneyPatterns();
		for (JourneyPattern jp : rightJourneyPatterns) {
			List<VehicleJourney> rightVehicleJourneys = jp.getVehicleJourneys();

			for (VehicleJourney vj : rightVehicleJourneys) {

				// Update VehichleJourneyAtStopPoint
				List<VehicleJourneyAtStop> vehicleJourneyAtStops = vj.getVehicleJourneyAtStops();
				for (VehicleJourneyAtStop vjS : vehicleJourneyAtStops) {
					String newStopPointId = rightToLeftStopPointMap.get(vjS.getStopPoint().getObjectId());
					StopPoint newStopPoint = referential.getStopPoints().get(newStopPointId);

					vjS.setStopPoint(newStopPoint);
				}

				// Update Route
				vj.setRoute(left);
			}

			List<StopPoint> newStopPointsForJourneyPattern = new ArrayList<StopPoint>();
			for (StopPoint sp : jp.getStopPoints()) {
				String newStopPointId = rightToLeftStopPointMap.get(sp.getObjectId());
				StopPoint newStopPoint = referential.getStopPoints().get(newStopPointId);
				newStopPointsForJourneyPattern.add(newStopPoint);
			}

			jp.getStopPoints().clear();
			jp.getStopPoints().addAll(newStopPointsForJourneyPattern);

			if (jp.getDepartureStopPoint() != null) {
				jp.setDepartureStopPoint(referential.getStopPoints().get(rightToLeftStopPointMap.get(jp.getDepartureStopPoint().getObjectId())));
			}

			if (jp.getArrivalStopPoint() != null) {
				jp.setArrivalStopPoint(referential.getStopPoints().get(rightToLeftStopPointMap.get(jp.getArrivalStopPoint().getObjectId())));
			}

		}

		for (int i = 0; i < rightJourneyPatterns.size(); i++) {
			rightJourneyPatterns.get(i).setRoute(left);
		}

		// Keep opposite route if exists in any of the two
		if (left.getOppositeRoute() == null) {
			left.setOppositeRoute(right.getOppositeRoute());
		}

		// Clear Line reference
		right.setLine(null);
		right.getStopPoints().clear();
		right.setOppositeRoute(null);

		referential.getRoutes().remove(right.getObjectId());
		// for(String obsoleteStopPointObjectId : rightToLeftStopPointMap.values()) {
		// referential.getStopPoints().remove(obsoleteStopPointObjectId);
		//
		// }

	}
	
	private Map<String,String> buildStopPointConversionMap(Route left, Route right) {
		Map<String, String> rightToLeftStopPointMap = new HashMap<String, String>();

		for(StopPoint lSp : left.getStopPoints()) {
			for(StopPoint rSp : right.getStopPoints()) {
				String lStopAreaId = lSp.getContainedInStopArea().getObjectId();
				String rStopAreaId = rSp.getContainedInStopArea().getObjectId();
				
				if(lStopAreaId.equals(rStopAreaId)) {
					rightToLeftStopPointMap.put(rSp.getObjectId(), lSp.getObjectId());
				}
			}
		}
		
		return rightToLeftStopPointMap;
		
	}

	

	public void mergeJourneyPatterns(Referential referential, RegtoppImportParameters configuration, Pair<JourneyPattern, JourneyPattern> duplicatePair) {
		// Merge routes, that means drop the "right" Route along with StopPoints and JourneyPattern

		JourneyPattern left = duplicatePair.getLeft();
		JourneyPattern right = duplicatePair.getRight();

		log.info("Merging journey pattern " + left.getObjectId() + " and " + right.getObjectId());

		List<VehicleJourney> vjCopy = new ArrayList<VehicleJourney>();
		vjCopy.addAll(right.getVehicleJourneys());
		for (int i = 0; i < vjCopy.size(); i++) {
			vjCopy.get(i).setJourneyPattern(left);
		}

		left.getRouteSections().addAll(right.getRouteSections());

		right.getVehicleJourneys().clear();
		right.getRouteSections().clear();
		right.setRoute(null);
		right.getStopPoints().clear();
		right.setDepartureStopPoint(null);
		right.setArrivalStopPoint(null);

	}

	public Pair<Route, Route> findDuplicateRoutes(Referential referential, RegtoppImportParameters parameters) {
		for (Route left : referential.getRoutes().values()) {
			for (Route right : referential.getRoutes().values()) {
				if (left != right) {
					// log.info("Checking route " + left.getObjectId() + " vs " + right.getObjectId());
					int numStopPointsLeft = left.getStopPoints().size();
					int numStopPointsRight = right.getStopPoints().size();

					if (numStopPointsLeft == numStopPointsRight) {
						// Same number of stop points
						boolean sameStopPointsInOrder = true;
						for (int i = 0; i < numStopPointsLeft; i++) {
							StopPoint l = left.getStopPoints().get(i);
							StopPoint r = right.getStopPoints().get(i);

							if (!isStopPointIdentical(l, r)) {
								sameStopPointsInOrder = false;
								break;

							}
						}

						if (sameStopPointsInOrder) {
							// log.info("Route " + left.getObjectId() + " and " + right.getObjectId() + " are identical");

							return new Pair<Route, Route>(left, right);
						}
					}
				}
			}
		}
		// No more duplicates
		return null;
	}

	public Pair<Route, Route> findSimilarRoutes(Referential referential, RegtoppImportParameters parameters) {
		for (Route left : referential.getRoutes().values()) {
			for (Route right : referential.getRoutes().values()) {
				if (left != right) {

					Set<String> departureStopAreas = new HashSet<String>();
					Set<String> arrivalStopAreas = new HashSet<String>();
					Set<String> leftStopAreas = new HashSet<String>();
					Set<String> rightStopAreas = new HashSet<String>();

					for (JourneyPattern jp : left.getJourneyPatterns()) {
						departureStopAreas.add(jp.getDepartureStopPoint().getContainedInStopArea().getObjectId());
						arrivalStopAreas.add(jp.getArrivalStopPoint().getContainedInStopArea().getObjectId());
						for (StopPoint sp : jp.getStopPoints()) {
							leftStopAreas.add(sp.getContainedInStopArea().getObjectId());
						}
					}
					for (JourneyPattern jp : right.getJourneyPatterns()) {
						departureStopAreas.add(jp.getDepartureStopPoint().getContainedInStopArea().getObjectId());
						arrivalStopAreas.add(jp.getArrivalStopPoint().getContainedInStopArea().getObjectId());
						for (StopPoint sp : jp.getStopPoints()) {
							rightStopAreas.add(sp.getContainedInStopArea().getObjectId());
						}
					}

					if (departureStopAreas.size() == 1 && arrivalStopAreas.size() == 1) {

						if (leftStopAreas.containsAll(rightStopAreas) || rightStopAreas.containsAll(leftStopAreas)) {

							if (rightStopAreas.size() > leftStopAreas.size()) {
								// Merge opposite way, swap left and right
								Route tmp = left;
								left = right;
								right = tmp;
							}

							log.info("Route " + left.getObjectId() + " and " + right.getObjectId() + " are similar");
							return new Pair<Route, Route>(left, right);
						}
					}
				}
			}
		}
		// No more similarieties
		return null;
	}

	/**
	 * Reduce number of duplicate journeypatterns
	 * 
	 * @param referential
	 * @param configuration
	 */
	public void deduplicateIdenticalJourneyPatterns(Referential referential, RegtoppImportParameters configuration) {

		for (Route route : referential.getRoutes().values()) {
			Pair<JourneyPattern, JourneyPattern> duplicatePair = null;

			do {
				duplicatePair = findDuplicateJourneyPatterns(referential, configuration, route);
				if (duplicatePair != null) {
					mergeJourneyPatterns(referential, configuration, duplicatePair);
				}
			} while (duplicatePair != null);

		}

	}

	public Pair<JourneyPattern, JourneyPattern> findDuplicateJourneyPatterns(Referential referential, RegtoppImportParameters parameters, Route route) {

		for (JourneyPattern left : route.getJourneyPatterns()) {
			for (JourneyPattern right : route.getJourneyPatterns()) {
				if (left != right) {
					int numStopPointsLeft = left.getStopPoints().size();
					int numStopPointsRight = right.getStopPoints().size();

					if (numStopPointsLeft == numStopPointsRight) {
						// Same number of stop points
						boolean sameStopPointsInOrder = true;
						for (int i = 0; i < numStopPointsLeft; i++) {
							StopPoint l = left.getStopPoints().get(i);
							StopPoint r = right.getStopPoints().get(i);

							if (!isStopPointIdentical(l, r)) {
								sameStopPointsInOrder = false;
								break;

							}
						}

						if (sameStopPointsInOrder) {
							return new Pair<JourneyPattern, JourneyPattern>(left, right);
						}
					}

				}
			}

		}

		// No more duplicates
		return null;
	}

	private boolean isStopPointIdentical(StopPoint left, StopPoint right) {
		boolean identical = false;

		if (left.getContainedInStopArea().getObjectId().equals(right.getContainedInStopArea().getObjectId())
				&& left.getForAlighting() == right.getForAlighting() && left.getForBoarding() == right.getForBoarding()
				&& left.getPosition() == right.getPosition()) {
			identical = true;
		}

		return identical;

	}

	private boolean isJourneyPatternIdentical(JourneyPattern left, JourneyPattern right) {
		if (left.getStopPoints().size() != right.getStopPoints().size()) {
			return false;
		} else {
			for (int i = 0; i < left.getStopPoints().size(); i++) {
				if (!isStopPointIdentical(left.getStopPoints().get(i), right.getStopPoints().get(i))) {
					return false;
				}
			}
		}
		return true;
	}

	private void updateNetworkDate(RegtoppImporter importer, Referential referential, Line line, RegtoppImportParameters configuration) throws Exception {
		DaycodeById dayCodeIndex = (DaycodeById) importer.getDayCodeById();

		RegtoppDayCodeHeaderDKO header = dayCodeIndex.getHeader();
		LocalDate calStartDate = header.getDate();
		for (Network network : referential.getPtNetworks().values()) {
			network.setVersionDate(calStartDate.toDateMidnight().toDate());
		}
	}

	private void updateLineName(Referential referential, Line line, RegtoppImportParameters configuration) {
		if (line.getName() == null) {
			Set<String> routeNames = new HashSet<String>();
			for (Route r : line.getRoutes()) {
				routeNames.add(r.getName());
			}

			String lineName = StringUtils.join(routeNames, " - ");
			line.setName(lineName);
		} else if (line.getNumber() != null && line.getName().startsWith(line.getNumber()+" ")) {
			line.setName(StringUtils.trim(line.getName().substring(line.getNumber().length())));
		}
	}

	private void removeLineNumberFromRouteAndJourneyPatternsAndVehicleJourneys(Referential referential, Line line, RegtoppImportParameters configuration) {
		String lineNumber = line.getNumber();
		if(StringUtils.trimToNull(lineNumber) != null) {
			for(Route route : line.getRoutes()) {
				String updatedName = null;
				String originalRouteName = route.getName();
				if (originalRouteName != null && originalRouteName.startsWith(line.getNumber()+" ")) {
					updatedName = StringUtils.trim(route.getName().substring(line.getNumber().length()));
					
					route.setName(updatedName);
					route.setPublishedName(updatedName);
				}
				
				if(updatedName != null) {
					for(JourneyPattern jp : route.getJourneyPatterns()) {
						jp.setName(updatedName);
						jp.setPublishedName(updatedName);
					}
				}
				
				for(JourneyPattern jp : route.getJourneyPatterns()) {
					for(VehicleJourney vj : jp.getVehicleJourneys()) {
						String vjName = vj.getPublishedJourneyName();
						if(vjName != null &&vjName.startsWith(line.getNumber()+" ")) {
							// Remove from vehicle journey if same as journey pattern
							vj.setPublishedJourneyName(StringUtils.trim(vjName.substring(line.getNumber().length())));
						}
					}
				}
				
			}
		}
	}

	private void updateLineTransportMode(Referential referential, Line line, List<TransportModePair> transportModes) {

		if(transportModes.size() == 0) {
			// In case no trips 
			line.setTransportModeName(TransportModeNameEnum.Other);
		} else if (transportModes.size() == 1) {
			
			TransportModePair pair = transportModes.iterator().next();
			if(pair.transportMode != null) {
			// Only one transport mode used for all routes/journeys
			line.setTransportModeName(pair.transportMode);
			line.setTransportSubModeName(pair.subMode);
			} else {
				log.warn("No TransportMode on TransportModePair, using 'Other'");
				line.setTransportModeName(TransportModeNameEnum.Other);
			}
			
			
			// Remove overrides from servicejourneys
			for(VehicleJourney vj : referential.getVehicleJourneys().values()) {
				vj.setTransportMode(null);
				vj.setTransportSubMode(null);
			}
			
		} else {
			// Find the one used the most used
			Map<TransportModePair,Integer> usageMap = new HashMap<>();
			for(TransportModePair pair : transportModes) {
				Integer count = usageMap.get(pair);
				if(count == null) {
					count = 0;
				}
				count++;
				usageMap.put(pair, count);
			}
			TransportModePair mostUsed = null;
			int usageCounter = 0;
			for(Entry<TransportModePair, Integer> usage : usageMap.entrySet()) {
				if(usage.getValue() > usageCounter) {
					usageCounter = usage.getValue();
					mostUsed = usage.getKey();
				}
			}
			
			line.setTransportModeName(mostUsed.transportMode);
			line.setTransportSubModeName(mostUsed.subMode);

			// Remove overrides on journeys
			for(VehicleJourney vj : referential.getVehicleJourneys().values()) {
				if(line.getTransportModeName() != null && line.getTransportModeName() == vj.getTransportMode()) {
					vj.setTransportMode(null);
				}
				if(line.getTransportSubModeName() != null && line.getTransportSubModeName() == vj.getTransportSubMode()) {
					vj.setTransportSubMode(null);
				}
			}
			
		}
	}

	public static Time calculateTripVisitTime(Duration tripDepartureTime, Duration timeSinceTripDepatureTime) {
		// TODO Ugly ugly ugly

		LocalTime localTime = new LocalTime(0, 0, 0, 0)
				.plusSeconds((int) (tripDepartureTime.getStandardSeconds() + timeSinceTripDepatureTime.getStandardSeconds()));

		@SuppressWarnings("deprecation")
		java.sql.Time sqlTime = new java.sql.Time(localTime.getHourOfDay(), localTime.getMinuteOfHour(), localTime.getSecondOfMinute());

		return sqlTime;

	}

	private void updateBoardingAlighting(Referential referential, RegtoppImportParameters configuration) {

		for (Route route : referential.getRoutes().values()) {
			boolean invalidData = false;
			boolean usefullData = false;

			b1: for (JourneyPattern jp : route.getJourneyPatterns()) {
				for (VehicleJourney vj : jp.getVehicleJourneys()) {
					for (VehicleJourneyAtStop vjas : vj.getVehicleJourneyAtStops()) {
						if (!updateStopPoint(vjas)) {
							invalidData = true;
							break b1;
						}
					}
				}
			}
			if (!invalidData) {
				// check if every stoppoints were updated, complete missing ones to
				// normal; if all normal clean all
				for (StopPoint sp : route.getStopPoints()) {
					if (sp.getForAlighting() == null)
						sp.setForAlighting(AlightingPossibilityEnum.normal);
					if (sp.getForBoarding() == null)
						sp.setForBoarding(BoardingPossibilityEnum.normal);
				}
				for (StopPoint sp : route.getStopPoints()) {
					if (!sp.getForAlighting().equals(AlightingPossibilityEnum.normal)) {
						usefullData = true;
						break;
					}
					if (!sp.getForBoarding().equals(BoardingPossibilityEnum.normal)) {
						usefullData = true;
						break;
					}
				}

			}
			if (invalidData || !usefullData) {
				// remove useless informations
				for (StopPoint sp : route.getStopPoints()) {
					sp.setForAlighting(null);
					sp.setForBoarding(null);
				}
			}

		}
	}

	private boolean updateStopPoint(VehicleJourneyAtStop vjas) {
		StopPoint sp = vjas.getStopPoint();
		BoardingPossibilityEnum forBoarding = getForBoarding(vjas.getBoardingAlightingPossibility());
		AlightingPossibilityEnum forAlighting = getForAlighting(vjas.getBoardingAlightingPossibility());
		if (sp.getForBoarding() != null && !sp.getForBoarding().equals(forBoarding))
			return false;
		if (sp.getForAlighting() != null && !sp.getForAlighting().equals(forAlighting))
			return false;
		sp.setForBoarding(forBoarding);
		sp.setForAlighting(forAlighting);
		return true;
	}

	private AlightingPossibilityEnum getForAlighting(BoardingAlightingPossibilityEnum boardingAlightingPossibility) {
		if (boardingAlightingPossibility == null)
			return AlightingPossibilityEnum.normal;
		switch (boardingAlightingPossibility) {
		case BoardAndAlight:
			return AlightingPossibilityEnum.normal;
		case AlightOnly:
			return AlightingPossibilityEnum.normal;
		case BoardOnly:
			return AlightingPossibilityEnum.forbidden;
		case NeitherBoardOrAlight:
			return AlightingPossibilityEnum.forbidden;
		case BoardAndAlightOnRequest:
			return AlightingPossibilityEnum.request_stop;
		case AlightOnRequest:
			return AlightingPossibilityEnum.request_stop;
		case BoardOnRequest:
			return AlightingPossibilityEnum.normal;
		}
		return null;
	}

	private BoardingPossibilityEnum getForBoarding(BoardingAlightingPossibilityEnum boardingAlightingPossibility) {
		if (boardingAlightingPossibility == null)
			return BoardingPossibilityEnum.normal;
		switch (boardingAlightingPossibility) {
		case BoardAndAlight:
			return BoardingPossibilityEnum.normal;
		case AlightOnly:
			return BoardingPossibilityEnum.forbidden;
		case BoardOnly:
			return BoardingPossibilityEnum.normal;
		case NeitherBoardOrAlight:
			return BoardingPossibilityEnum.forbidden;
		case BoardAndAlightOnRequest:
			return BoardingPossibilityEnum.request_stop;
		case AlightOnRequest:
			return BoardingPossibilityEnum.normal;
		case BoardOnRequest:
			return BoardingPossibilityEnum.request_stop;
		}
		return null;
	}

	static {
		ParserFactory.register(RegtoppLineParser.class.getName(), new ParserFactory() {
			@Override
			protected Parser create() {
				return new RegtoppLineParser();
			}
		});
	}

}
