package mobi.chouette.dao;

import java.util.List;
import java.util.Set;

import mobi.chouette.model.ScheduledStopPoint;

public interface ScheduledStopPointDAO extends GenericDAO<ScheduledStopPoint> {

	List<ScheduledStopPoint> getScheduledStopPointsContainedInStopArea(String stopAreaObjectId);

	/**
	 * Replace all existing references to a set of stop area objectIds with references to another stop area.
	 * <p>
	 * Used when merging stop areas.
	 * @return no of updated scheduled stop points
	 */
	int replaceContainedInStopAreaReferences(Set<String> oldStopAreaIds, String newStopAreaId);

	/**
	 * Return a list with objectid for all stop areas referred to from all scheduled stop points.
	 *
	 * @return
	 */
	List<String> getAllStopAreaObjectIds();
}
