package mobi.chouette.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import mobi.chouette.model.JourneyFrequency;

@Stateless
public class JourneyFrequencyDAOImpl extends GenericDAOImpl<JourneyFrequency> implements JourneyFrequencyDAO{

	public JourneyFrequencyDAOImpl() {
		super(JourneyFrequency.class);
	}

	@PersistenceContext(unitName = "referential")
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

	@Override
	public JourneyFrequency findByObjectId(String objectId) {
		throw new UnsupportedOperationException();
	}
}
