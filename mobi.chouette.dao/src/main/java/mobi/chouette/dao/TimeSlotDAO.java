package mobi.chouette.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import mobi.chouette.model.TimeSlot;

@Stateless
public class TimeSlotDAO extends GenericDAOImpl<TimeSlot> {

	public TimeSlotDAO() {
		super(TimeSlot.class);
	}

	@PersistenceContext(unitName = "referential")
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

}
