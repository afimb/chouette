package mobi.chouette.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import mobi.chouette.model.RoutingConstraint;

@Stateless
public class RoutingConstraintDAOImpl extends GenericDAOImpl<RoutingConstraint> implements RoutingConstraintDAO{
	public RoutingConstraintDAOImpl() {
		super(RoutingConstraint.class);
	}

	@PersistenceContext(unitName = "referential")
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}
}