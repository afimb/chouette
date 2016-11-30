package mobi.chouette.dao;

import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import mobi.chouette.model.VehicleJourneyAtStop;

@Stateless
public class VehicleJourneyAtStopDAOImpl extends GenericDAOImpl<VehicleJourneyAtStop> implements
		VehicleJourneyAtStopDAO {

	public VehicleJourneyAtStopDAOImpl() {
		super(VehicleJourneyAtStop.class);
	}

	@PersistenceContext(unitName = "referential")
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

	@Override
	public VehicleJourneyAtStop findByChouetteId(String codeSpace, String objectId) {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mobi.chouette.dao.GenericDAOImpl#findByChouetteId(java.util.Map)
	 */
	@Override
	public List<VehicleJourneyAtStop> findByChouetteId(Map<String, List<String>> chouetteIdsByCodeSpace) {
		throw new UnsupportedOperationException();
	}

}
