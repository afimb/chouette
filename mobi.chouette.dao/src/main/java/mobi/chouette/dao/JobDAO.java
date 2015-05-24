package mobi.chouette.dao;

import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import mobi.chouette.model.api.Job;
import mobi.chouette.model.api.Job.STATUS;
import mobi.chouette.model.api.Job_;

@Stateless
public class JobDAO extends GenericDAOImpl<Job> {

	public JobDAO() {
		super(Job.class);
	}

	@PersistenceContext(unitName = "iev")
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

	public List<Job> findByReferential(String referential) {
		List<Job> result;
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Job> criteria = builder.createQuery(type);
		Root<Job> root = criteria.from(type);
		Predicate statusPredicate = builder.notEqual(root.get(Job_.status),
				Job.STATUS.CREATED); // Created jobs are only in initialization phase, should not be sent
		Predicate referentialPredicate = builder.equal(root.get(Job_.referential),
				referential);
		criteria.where(builder.and( referentialPredicate, statusPredicate));
		criteria.orderBy(builder.asc(root.get(Job_.created)));
		TypedQuery<Job> query = em.createQuery(criteria);
		result = query.getResultList();
		return result;
	}

	public List<Job> findByReferentialAndAction(String referential, String action) {
		List<Job> result;
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Job> criteria = builder.createQuery(type);
		Root<Job> root = criteria.from(type);
		Predicate statusPredicate = builder.notEqual(root.get(Job_.status),
				Job.STATUS.CREATED); // Created jobs are only in initialization phase, should not be sent
		Predicate referentialPredicate = builder.equal(root.get(Job_.referential),
				referential);
		Predicate actionPredicate = builder.equal(root.get(Job_.action),
				action);
		criteria.where( builder.and(referentialPredicate, actionPredicate,statusPredicate ));
		criteria.orderBy(builder.asc(root.get(Job_.created)));
		TypedQuery<Job> query = em.createQuery(criteria);
		result = query.getResultList();
		return result;
	}

	public List<Job> findByStatus(STATUS status) {
		List<Job> result;
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Job> criteria = builder.createQuery(type);
		Root<Job> root = criteria.from(type);
		Predicate statusPredicate = builder.equal(root.get(Job_.status),
				status); // Created jobs are only in initialization phase, should not be sent
		criteria.where( statusPredicate);
		criteria.orderBy(builder.asc(root.get(Job_.created)));
		TypedQuery<Job> query = em.createQuery(criteria);
		result = query.getResultList();
		return result;
	}


	@SuppressWarnings("unchecked")
	public Job getNextJob(String referential) {

		Job result = null;
		Query query = em
				.createQuery("from Job j where j.referential = ?1 and j.status in ( ?2 ) order by id");
		query.setParameter(1, referential);
		query.setParameter(2, Arrays.asList(STATUS.STARTED, STATUS.SCHEDULED));
		List<Job> list = query.getResultList();
		if (list != null && !list.isEmpty()) {
			if (list.get(0).getStatus().equals(STATUS.SCHEDULED)) {
				result = list.get(0);
			}
		}
		return result;
	}

	public int deleteAll(String referential) {
		List<Job> list = findByReferential(referential);
		for (Job entity : list) {
			delete(entity);
		}
		return list.size();
	}
}
