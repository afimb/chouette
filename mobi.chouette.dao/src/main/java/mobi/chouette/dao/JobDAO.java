package mobi.chouette.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import mobi.chouette.api.model.Job;
import mobi.chouette.api.model.Job.STATUS;
import mobi.chouette.model.Job_;

@Stateless
public class JobDAO extends GenericDAOImpl<Job> {

	public JobDAO() {
		super(Job.class);
	}

	@PersistenceContext(unitName = "public")
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

	public List<Job> findByReferential(String referential) {
		List<Job> result;
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Job> criteria = builder.createQuery(Job.class);
		Root<Job> root = criteria.from(type);
		Predicate predicate = builder.equal(root.get(Job_.referential),
				referential);
		criteria.where(predicate);
		TypedQuery<Job> query = em.createQuery(criteria);
		result = query.getResultList();
		return result;
	}

	public Job getNextJob(String referential) {
		Job result = null;
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Job> criteria = builder.createQuery(Job.class);
		Root<Job> root = criteria.from(type);
		Predicate p1 = builder.equal(root.get(Job_.referential), referential);
		Predicate p2 = builder.lessThan(root.get(Job_.status),
				STATUS.TERMINATED);
		criteria.where(builder.and(p1, p2));
		criteria.orderBy(builder.desc(root.get(Job_.status)));
		TypedQuery<Job> query = em.createQuery(criteria);
		List<Job> list = query.getResultList();
		if (list != null && !list.isEmpty()) {
			if (list.get(0).getStatus().equals(STATUS.CREATED)) {
				result = list.get(0);
			}
		}
		return result;
	}

}
