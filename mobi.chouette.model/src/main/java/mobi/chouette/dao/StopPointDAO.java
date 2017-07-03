package mobi.chouette.dao;

import mobi.chouette.model.StopPoint;

import java.util.List;
import java.util.Set;

public interface StopPointDAO extends GenericDAO<StopPoint> {

    List<StopPoint> getStopPointsContainedInStopArea(String stopAreaObjectId);

    /**
     * Replace all existing references to a set of stop area objectIds with references to another stop area.
     *
     * Used when merging stop areas.
     */
    void replaceContainedInStopAreaReferences(Set<String> oldStopAreaIds, String newStopAreaId);

}
