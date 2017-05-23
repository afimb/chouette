package mobi.chouette.dao;

import mobi.chouette.dao.GenericDAO;
import mobi.chouette.model.StopPoint;

import java.util.List;

public interface StopPointDAO extends GenericDAO<StopPoint> {
// TODO objectId
    List<StopPoint> getStopPointsContainedInStopArea(Long stopAreaId);

}
