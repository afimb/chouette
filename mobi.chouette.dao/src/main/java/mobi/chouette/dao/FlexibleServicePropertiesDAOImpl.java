package mobi.chouette.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import mobi.chouette.model.FlexibleServiceProperties;
@Stateless
public class FlexibleServicePropertiesDAOImpl extends GenericDAOImpl<FlexibleServiceProperties> implements FlexibleServicePropertiesDAO {

	public FlexibleServicePropertiesDAOImpl() {
		super(FlexibleServiceProperties.class);
	}

	@PersistenceContext(unitName = "referential")
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}
}
