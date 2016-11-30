package mobi.chouette.dao;

import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import mobi.chouette.model.JourneyFrequency;

@Stateless
public class JourneyFrequencyDAOImpl extends GenericDAOImpl<JourneyFrequency> implements JourneyFrequencyDAO {

	public JourneyFrequencyDAOImpl() {
		super(JourneyFrequency.class);
	}

	@PersistenceContext(unitName = "referential")
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

	@Override
	public JourneyFrequency findByChouetteId(String codeSpace, String objectId) {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mobi.chouette.dao.GenericDAOImpl#findByChouetteId(java.util.Map)
	 */
	@Override
	public List<JourneyFrequency> findByChouetteId(Map<String, List<String>> chouetteIdsByCodeSpace) {
		throw new UnsupportedOperationException();
	}

}
