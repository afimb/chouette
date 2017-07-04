package mobi.chouette.dao;

import mobi.chouette.model.StopPoint;

import java.util.List;
import java.util.Set;

public interface StopPointDAO extends GenericDAO<StopPoint> {

    List<StopPoint> getStopPointsContainedInStopArea(String stopAreaObjectId);

    /**
     * Replace all existing references to a set of stop area objectIds with references to another stop area.
     * <p>
     * Used when merging stop areas.
     * @return no of updated stop points
     */
    int replaceContainedInStopAreaReferences(Set<String> oldStopAreaIds, String newStopAreaId);

    /**
     * Return a list with objectid for all stop areas referred to from all stop points.
     *
     * @return
     */
    List<String> getAllStopAreaObjectIds();

}
