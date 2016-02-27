package mobi.chouette.model.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lombok.extern.log4j.Log4j;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.NeptuneIdentifiedObject;
import mobi.chouette.model.NeptuneLocalizedObject;
import mobi.chouette.model.NeptuneObject;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;

@Log4j
public abstract class NeptuneUtil {

	/**
	 * Build a list of Neptune Ids (ObjectId) from a list of Neptune Objects
	 * 
	 * @param neptuneObjects
	 *            the list to parse
	 * @return the object ids list
	 */
	public static List<String> extractObjectIds(Collection<? extends NeptuneIdentifiedObject> neptuneObjects) {
		List<String> objectIds = new ArrayList<String>();
		if (neptuneObjects != null) {
			for (NeptuneIdentifiedObject neptuneObject : neptuneObjects) {
				if (neptuneObject != null) {
					String objectId = neptuneObject.getObjectId();
					if (objectId != null) {
						objectIds.add(objectId);
					}
				}
			}
		}

		return objectIds;
	}

	/**
	 * Build a map of objectIds (Id) from a list of Neptune Identified Objects
	 * 
	 * @param neptuneObjects
	 *            the list to parse
	 * @return the ids map
	 */
	public static <T extends NeptuneIdentifiedObject> Map<String, T> mapOnObjectIds(Collection<T> neptuneObjects) {
		Map<String, T> map = new HashMap<String, T>();
		if (neptuneObjects != null) {
			for (T neptuneObject : neptuneObjects) {
				if (neptuneObject != null) {
					String id = neptuneObject.getObjectId();
					if (id != null) {
						map.put(id, neptuneObject);
					}
				}
			}
		}
		return map;
	}

	/**
	 * Build a list of internal Ids (Id) from a list of Neptune Objects
	 * 
	 * @param neptuneObjects
	 *            the list to parse
	 * @return the ids list
	 */
	public static List<Long> extractIds(Collection<? extends NeptuneObject> neptuneObjects) {
		List<Long> ids = new ArrayList<Long>();
		if (neptuneObjects != null) {
			for (NeptuneObject neptuneObject : neptuneObjects) {
				if (neptuneObject != null) {
					Long id = neptuneObject.getId();
					if (id != null) {
						ids.add(id);
					}
				}
			}
		}

		return ids;
	}

	/**
	 * Build a map of internal Ids (Id) from a list of Neptune Objects
	 * 
	 * @param neptuneObjects
	 *            the list to parse
	 * @return the ids map
	 */
	public static <T extends NeptuneObject> Map<Long, T> mapOnIds(Collection<T> neptuneObjects) {
		Map<Long, T> map = new HashMap<Long, T>();
		if (neptuneObjects != null) {
			for (T neptuneObject : neptuneObjects) {
				if (neptuneObject != null) {
					Long id = neptuneObject.getId();
					if (id != null) {
						map.put(id, neptuneObject);
					}
				}
			}
		}
		return map;
	}

	/**
	 * project latitude and longitude on x and y if not already set<br/>
	 * clears projection if no projection is given
	 * 
	 * @param projectionType
	 *            type of projection (EPSG:xxx)
	 */
	public static void toProjection(NeptuneLocalizedObject object, String projectionType) {
		if (!object.hasCoordinates())
			return;

		String projection = null;
		if (projectionType == null || projectionType.isEmpty()) {
			object.setX(null);
			object.setY(null);
			object.setProjectionType(null);
			return;
		}
		if (object.hasProjection())
			return;
		projection = projectionType.toUpperCase();

		Coordinate p = new Coordinate(object.getLongitude(), object.getLatitude());
		Coordinate coordinate = CoordinateUtil.transform(Coordinate.WGS84, projection, p);
		if (coordinate != null) {
			object.setX(coordinate.x);
			object.setY(coordinate.y);
			object.setProjectionType(projection);
		}
	}

	public static List<StopArea> getStopAreaOfRoute(Route route) {
		ArrayList<StopArea> areas = new ArrayList<>();
		ArrayList<StopPoint> points = new ArrayList<>(route.getStopPoints());
		for (Iterator<StopPoint> iterator = points.iterator(); iterator.hasNext();) {
			StopPoint stopPoint =  iterator.next();
			if (stopPoint == null) iterator.remove();
			
		}
		Collections.sort(points, new Comparator<StopPoint>() {

			@Override
			public int compare(StopPoint arg0, StopPoint arg1) {
				return arg0.getPosition().intValue() - arg1.getPosition().intValue();
			}
		});
		for (StopPoint point : points) {
			areas.add(point.getContainedInStopArea());
		}
		return areas;
	}

	public static String changePrefix(String objectId, String prefix) {
		String[] tokens = objectId.split(":");
		return prefix + ":" + tokens[1] + ":" + tokens[2];
	}

	/**
	 * update departure and arrival of JourneyPattern <br/>
	 * to be used after stopPoints update
	 */
	public static void refreshDepartureArrivals(JourneyPattern jp) {
		List<StopPoint> stopPoints = jp.getStopPoints();
		if (stopPoints == null || stopPoints.isEmpty()) {
			jp.setDepartureStopPoint(null);
			jp.setArrivalStopPoint(null);
		} else {
			for (StopPoint stopPoint : stopPoints) {
				if (stopPoint.getPosition() == null) {
					log.warn("stopPoint without position " + stopPoint.getObjectId());
					return;
				}
			}
			Collections.sort(jp.getStopPoints(), new Comparator<StopPoint>() {

				@Override
				public int compare(StopPoint arg0, StopPoint arg1) {
					return arg0.getPosition().intValue() - arg1.getPosition().intValue();
				}
			});
			jp.setDepartureStopPoint(stopPoints.get(0));
			jp.setArrivalStopPoint(stopPoints.get(stopPoints.size() - 1));
		}

	}

}
