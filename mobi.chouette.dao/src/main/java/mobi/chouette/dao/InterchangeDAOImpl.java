package mobi.chouette.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import mobi.chouette.model.Interchange;

@Stateless
public class InterchangeDAOImpl extends GenericDAOImpl<Interchange> implements InterchangeDAO{

	public InterchangeDAOImpl() {
		super(Interchange.class);
	}

	@PersistenceContext(unitName = "referential")
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

}
