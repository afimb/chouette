package mobi.chouette.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import mobi.chouette.model.VehicleJourney;

@Stateless
public class VehicleJourneyDAO extends GenericDAOImpl<VehicleJourney> {

	public VehicleJourneyDAO() {
		super(VehicleJourney.class);
	}

	@PersistenceContext(unitName = "referential")
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

}
