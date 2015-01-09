package mobi.chouette.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import mobi.chouette.model.VehicleJourneyAtStop;

@Stateless
public class VehicleJourneyAtStopDAO extends
		GenericDAOImpl<VehicleJourneyAtStop> {

	public VehicleJourneyAtStopDAO() {
		super(VehicleJourneyAtStop.class);
	}

	@PersistenceContext(unitName = "referential")
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

	@Override
	public VehicleJourneyAtStop findByObjectId(String objectId) {
		throw new UnsupportedOperationException();
	}

}
