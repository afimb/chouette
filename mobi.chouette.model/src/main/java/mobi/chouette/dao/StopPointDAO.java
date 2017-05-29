package mobi.chouette.dao;

import mobi.chouette.model.StopPoint;

import java.util.List;

public interface StopPointDAO extends GenericDAO<StopPoint> {

    List<StopPoint> getStopPointsContainedInStopArea(String stopAreaObjectId);

}
