package mobi.chouette.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import mobi.chouette.model.RouteSection;

@Stateless
public class RouteSectionDAO extends GenericDAOImpl<RouteSection> {

	public RouteSectionDAO() {
		super(RouteSection.class);
	}

	@PersistenceContext(unitName = "referential")
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

}
