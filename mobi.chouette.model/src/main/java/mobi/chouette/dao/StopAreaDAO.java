package mobi.chouette.dao;

import java.util.List;

import mobi.chouette.model.StopArea;

public interface StopAreaDAO extends GenericDAO<StopArea> {

    List<String> getBoardingPositionObjectIds();
}
