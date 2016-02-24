package mobi.chouette.dao;

import java.util.List;

import mobi.chouette.dao.GenericDAO;
import mobi.chouette.model.VehicleJourney;

public interface VehicleJourneyDAO extends GenericDAO<VehicleJourney> {

	void copy(String data);

	void deleteChildren(List<String> list);

}
