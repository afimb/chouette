package mobi.chouette.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import mobi.chouette.model.VehicleJourneyAtStop;

@Stateless
public class VehicleJourneyAtStopDAOImpl extends
		GenericDAOImpl<VehicleJourneyAtStop> implements VehicleJourneyAtStopDAO{

	public VehicleJourneyAtStopDAOImpl() {
		super(VehicleJourneyAtStop.class);
	}

	@PersistenceContext(unitName = "referential")
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

	@Override
	public VehicleJourneyAtStop findByChouetteId(String codeSpace, Object objectId) {
		throw new UnsupportedOperationException();
	}

}
