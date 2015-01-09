package mobi.chouette.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import mobi.chouette.model.JourneyPattern;

@Stateless
public class JourneyPatternDAO extends GenericDAOImpl<JourneyPattern> {

	public JourneyPatternDAO() {
		super(JourneyPattern.class);
	}

	@PersistenceContext(unitName = "referential")
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

}
