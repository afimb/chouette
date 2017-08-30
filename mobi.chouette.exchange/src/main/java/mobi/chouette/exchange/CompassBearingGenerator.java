package mobi.chouette.exchange;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import lombok.extern.log4j.Log4j;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Route;
import mobi.chouette.model.ScheduledStopPoint;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.util.Referential;

@Log4j
public class CompassBearingGenerator {
	private static final int MAX_DIFF_BEARING_DEGREES = 60;

	public void cacluateCompassBearings(Referential referential) {

		for (StopArea boardingPosition : referential.getSharedStopAreas().values()) {

			if (boardingPosition.getAreaType() == ChouetteAreaEnum.BoardingPosition
					&& boardingPosition.getCompassBearing() == null) {
				Set<Integer> compassBearings = findCompassBearingForBoardingPosition(boardingPosition);

				if (compassBearings.size() > 1) {
					// See if we can merge some nearly identical
					Integer min = Collections.min(compassBearings);
					Integer max = Collections.max(compassBearings);
					int angle = getAngle(min, max);
					if (Math.abs(angle) < MAX_DIFF_BEARING_DEGREES) {
						compassBearings.clear();
						compassBearings.add((max + (angle / 2)) % 360);
						log.info("Average compass bearing is " + compassBearings.iterator().next()
								+ " for BoardingPosition " + boardingPosition.getObjectId() + " and name "
								+ boardingPosition.getName());

					}
				}

				if (compassBearings.size() == 1) {
					boardingPosition.setCompassBearing(compassBearings.iterator().next());
				} else if (compassBearings.size() > 1) {
					log.warn(
							"Found at least 2 conflicting compass bearings "
									+ ToStringBuilder.reflectionToString(compassBearings.toArray(),
											ToStringStyle.SIMPLE_STYLE)
									+ " for BoardingPosition " + boardingPosition.getObjectId() + " and name "
									+ boardingPosition.getName());
				} else {
				}

			}

		}

	}

	protected Set<Integer> findCompassBearingForBoardingPosition(StopArea sa) {
		Set<Integer> compassBearings = new TreeSet<Integer>();
		List<StopPoint> stopPoints = sa.getContainedScheduledStopPoints().stream().map(ScheduledStopPoint::getStopPoints).flatMap(List::stream).collect(Collectors.toList());
		for (StopPoint stop : stopPoints) {
			Route route = stop.getRoute();
			List<JourneyPattern> journeyPatterns = route.getJourneyPatterns();
			for (JourneyPattern jp : journeyPatterns) {
				StopPoint previous = null;
				StopPoint next = null;

				List<StopPoint> stopPointsInJourneyPattern = jp.getStopPoints();

				// TODO NRP 1692 Need to null check?

				if (jp.getDepartureStopPoint().getScheduledStopPoint().getContainedInStopArea().getObjectId()
						.equals(stop.getScheduledStopPoint().getContainedInStopArea().getObjectId())) {
					next = stopPointsInJourneyPattern.get(1);
				} else if (jp.getArrivalStopPoint().getScheduledStopPoint().getContainedInStopArea().getObjectId()
						.equals(stop.getScheduledStopPoint().getContainedInStopArea().getObjectId())) {
					previous = stopPointsInJourneyPattern.get(stopPointsInJourneyPattern.size() - 2);
				} else {
					// In the middle somewhere
					for (int i = 0; i < stopPointsInJourneyPattern.size(); i++) {
						if (stop.getScheduledStopPoint().getContainedInStopArea().getObjectId()
								.equals(stopPointsInJourneyPattern.get(i).getScheduledStopPoint().getContainedInStopArea().getObjectId())) {
							previous = stopPointsInJourneyPattern.get(i - 1);
							next = stopPointsInJourneyPattern.get(i + 1);
							break;
						}
					}
				}

				Integer bearing = null;
				// Calculate general direction previous -> stop -> next
				if (previous != null && next != null) {
					// Use previous and next
					bearing = bearing(previous, next);
				} else if (previous != null) {
					bearing = bearing(previous, stop);
				} else {
					bearing = bearing(stop, next);
				}
				
				if(bearing != null) {
					compassBearings.add(bearing);
				}

			}

		}

		return compassBearings;

	}

	private Integer bearing(StopPoint from, StopPoint to) {

		StopArea fromArea = from.getScheduledStopPoint().getContainedInStopArea();
		StopArea toArea = to.getScheduledStopPoint().getContainedInStopArea();

		if(fromArea != null && toArea != null && hasCoordinates(fromArea) && hasCoordinates(toArea)) {
			
			double longitude1 = fromArea.getLongitude().doubleValue();
			double longitude2 = toArea.getLongitude().doubleValue();
			double latitude1 = Math.toRadians(fromArea.getLatitude().doubleValue());
			double latitude2 = Math.toRadians(toArea.getLatitude().doubleValue());
			double longDiff = Math.toRadians(longitude2 - longitude1);
			double y = Math.sin(longDiff) * Math.cos(latitude2);
			double x = Math.cos(latitude1) * Math.sin(latitude2)
					- Math.sin(latitude1) * Math.cos(latitude2) * Math.cos(longDiff);

			double bearing = (Math.toDegrees(Math.atan2(y, x)) + 360) % 360;

			// 1 to 360 degrees, not 0 to 359

			return new Integer((int) bearing + 1);
		} else {
			if(fromArea == null) {
				log.warn("StopPoint "+from.getObjectId()+" in route "+from.getRoute().getObjectId()+" and line "+from.getRoute().getLine().getObjectId()+"/" +from.getRoute().getLine().getName()+" has no StopArea");
			}
			if(toArea == null) {
				log.warn("StopPoint "+to.getObjectId()+" in route "+to.getRoute().getObjectId()+" and line "+to.getRoute().getLine().getObjectId()+"/" +to.getRoute().getLine().getName()+" has no StopArea");
			}
			
			return null;
		}
		
	}
	
	private boolean hasCoordinates(StopArea stopArea) {
		return stopArea.getLatitude() != null && stopArea.getLongitude() != null;
	}

	private int getAngle(Integer bearing, Integer heading) {
		return ((((bearing - heading) % 360) + 540) % 360) - 180;

	}

}
