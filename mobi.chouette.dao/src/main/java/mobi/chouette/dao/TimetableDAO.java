package mobi.chouette.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import mobi.chouette.model.Timetable;

@Stateless
public class TimetableDAO extends GenericDAOImpl<Timetable> {

	public TimetableDAO() {
		super(Timetable.class);
	}

	@PersistenceContext(unitName = "referential")
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

}
