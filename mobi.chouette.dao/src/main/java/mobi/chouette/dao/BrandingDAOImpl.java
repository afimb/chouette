package mobi.chouette.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import mobi.chouette.model.Branding;

@Stateless
public class BrandingDAOImpl extends GenericDAOImpl<Branding> implements BrandingDAO {

	public BrandingDAOImpl() {
		super(Branding.class);
	}

	@PersistenceContext(unitName = "referential")
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}
}
