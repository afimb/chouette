package mobi.chouette.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import mobi.chouette.model.RouteSection;

@Stateless
public class RouteSectionDAOImpl extends GenericDAOImpl<RouteSection> implements RouteSectionDAO{

	public RouteSectionDAOImpl() {
		super(RouteSection.class);
	}

	@PersistenceContext(unitName = "referential")
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

}
