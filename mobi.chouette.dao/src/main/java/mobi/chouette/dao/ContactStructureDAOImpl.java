package mobi.chouette.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class ContactStructureDAOImpl implements ContactStructureDAO {

	private EntityManager em;

	@PersistenceContext(unitName = "referential")
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}


	@Override
	public int truncate() {
		return em.createNativeQuery("TRUNCATE TABLE CONTACT_STRUCTURES CASCADE").executeUpdate();
	}
}
