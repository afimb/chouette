package mobi.chouette.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import mobi.chouette.model.DestinationDisplay;

@Stateless
public class DestinationDisplayDAOImpl extends GenericDAOImpl<DestinationDisplay> implements DestinationDisplayDAO{

	public DestinationDisplayDAOImpl() {
		super(DestinationDisplay.class);
	}

	@PersistenceContext(unitName = "referential")
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

}
