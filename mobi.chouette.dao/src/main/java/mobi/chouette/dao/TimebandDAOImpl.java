package mobi.chouette.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import mobi.chouette.model.Timeband;

@Stateless
public class TimebandDAOImpl extends GenericDAOImpl<Timeband> implements TimebandDAO{

	public TimebandDAOImpl() {
		super(Timeband.class);
	}

	@PersistenceContext(unitName = "referential")
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}
}
