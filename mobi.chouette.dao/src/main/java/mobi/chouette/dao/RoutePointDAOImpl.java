package mobi.chouette.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import mobi.chouette.model.RoutePoint;

@Stateless
public class RoutePointDAOImpl extends GenericDAOImpl<RoutePoint> implements RoutePointDAO {

	public RoutePointDAOImpl() {
		super(RoutePoint.class);
	}

	@PersistenceContext(unitName = "referential")
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}
}
