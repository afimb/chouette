package mobi.chouette.dao;

import java.util.List;
import java.util.Map;

import mobi.chouette.common.ChouetteId;
import mobi.chouette.dao.GenericDAO;
import mobi.chouette.model.VehicleJourney;

public interface VehicleJourneyDAO extends GenericDAO<VehicleJourney> {

	void copy(String data);

	void deleteChildren(Map<String,List<ChouetteId>> vehicleJourneyChouetteIds);
	void deleteChildren(List<ChouetteId> vehicleJourneyChouetteIds);

}
