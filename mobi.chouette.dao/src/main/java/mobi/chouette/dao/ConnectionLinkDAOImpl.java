package mobi.chouette.dao;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.ConnectionLink_;

@Stateless
public class ConnectionLinkDAOImpl extends GenericDAOImpl<ConnectionLink> implements ConnectionLinkDAO{

	public ConnectionLinkDAOImpl() {
		super(ConnectionLink.class);
	}

	@PersistenceContext(unitName = "public")
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

	public int deleteOrphan() {
		int result = 0;
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaDelete<ConnectionLink> delete = builder
				.createCriteriaDelete(ConnectionLink.class);
		Root<ConnectionLink> root = delete.from(ConnectionLink.class);
		List<Predicate> predicates = new ArrayList<Predicate>();
		predicates.add(builder.isNull(root.get(ConnectionLink_.startOfLink)));
		predicates.add(builder.isNull(root.get(ConnectionLink_.endOfLink)));
		delete.where(builder.or(predicates.toArray(new Predicate[] {})));
		Query query = em.createQuery(delete);
		result = query.executeUpdate();
		return result;
	}
}
