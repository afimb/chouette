package mobi.chouette.dao;

import mobi.chouette.model.StopPoint;

import java.util.List;

public interface StopPointDAO extends GenericDAO<StopPoint> {

    List<StopPoint> getStopPointsContainedInStopArea(String stopAreaObjectId);

    /**
     * Replace all existing references to a stop area with references to another stop area.
     *
     * Used when merging stop areas.
     */
    void replaceContainedInStopAreaReference(String oldStopAreaId, String newStopAreaId);

}
