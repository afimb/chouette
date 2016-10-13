package mobi.chouette.dao.iev;

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

import lombok.extern.log4j.Log4j;
import mobi.chouette.model.iev.Job;
import mobi.chouette.model.iev.Job_;
import mobi.chouette.model.iev.Link;

@Stateless
@Log4j
public class JobDAO extends GenericDAOImpl<Job> {

	public JobDAO() {
		super(Job.class);
	}
	
	private static boolean migrated = false;
	private static final String LOCK="";

	@PersistenceContext(unitName = "iev")
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

	public List<Job> findByReferential(String referential) {
		return findByReferential(referential,new Job.STATUS[0]);
	}
	public List<Job> findByReferential(String referential, Job.STATUS[] status) {
		List<Job> result;
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Job> criteria = builder.createQuery(type);
		Root<Job> root = criteria.from(type);
		Predicate statusPredicate = builder.notEqual(root.get(Job_.status),
				Job.STATUS.CREATED); // Created jobs are only in initialization phase, should not be sent
		if(status.length != 0) {
			 statusPredicate = root.get(Job_.status).in(Arrays.asList(status));
		}
		
		Predicate referentialPredicate = builder.equal(root.get(Job_.referential),
				referential);
		criteria.where(builder.and( referentialPredicate, statusPredicate));
		criteria.orderBy(builder.asc(root.get(Job_.created)));
		TypedQuery<Job> query = em.createQuery(criteria);
		result = query.getResultList();
		return result;
	}

	public List<Job> findByReferentialAndAction(String referential, String action[]) {
		return findByReferentialAndAction(referential, action, new Job.STATUS[0]);
	}
	
	public List<Job> findByReferentialAndAction(String referential, String action[], Job.STATUS[] status) {
		List<Job> result;
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Job> criteria = builder.createQuery(type);
		Root<Job> root = criteria.from(type);
		Predicate statusPredicate = builder.notEqual(root.get(Job_.status),
				Job.STATUS.CREATED); // Created jobs are only in initialization phase, should not be sent
		if(status.length != 0) {
			 statusPredicate = root.get(Job_.status).in(Arrays.asList(status));
		}
		Predicate referentialPredicate = builder.equal(root.get(Job_.referential),
				referential);

		if(action.length != 0) {
			Predicate actionPredicate = root.get(Job_.action).in(Arrays.asList(action)); 
			criteria.where( builder.and(referentialPredicate, actionPredicate,statusPredicate ));
		} else {
			criteria.where( builder.and(referentialPredicate, statusPredicate ));
		}
		criteria.orderBy(builder.asc(root.get(Job_.created)));
		TypedQuery<Job> query = em.createQuery(criteria);
		result = query.getResultList();
		return result;
	}

	public List<Job> findByStatus(Job.STATUS status) {
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
		query.setParameter(2, Arrays.asList(Job.STATUS.STARTED, Job.STATUS.SCHEDULED));
		List<Job> list = query.getResultList();
		if (list != null && !list.isEmpty()) {
			if (list.get(0).getStatus().equals(Job.STATUS.SCHEDULED)) {
				result = list.get(0);
			}
		}
		return result;
	}

	public int deleteAll(String referential) {
		List<Job> list = findByReferential(referential,new Job.STATUS[0]);
		for (Job entity : list) {
			delete(entity);
		}
		return list.size();
	}

	@SuppressWarnings("deprecation")
	public void migrate() {
		// migrate data from previous versions
		if (migrated) return;
		synchronized (LOCK) {
			
		migrated = true;
		log.info("migrating data");
		List<Job> jobs = findAll();
		for (Job job : jobs) {
			if (job.getDataFilename() != null)
			{
				log.info("migrating job "+job.getId()+" "+job.getAction());
				if (job.getAction().equals("exporter"))
				{
					job.setOutputFilename(job.getDataFilename());
					job.getLinks().add(new Link("application/octet-stream",Link.OUTPUT_REL));
				}
				else
				{
					job.setInputFilename(job.getDataFilename());
					job.getLinks().add(new Link("application/octet-stream",Link.INPUT_REL));
				}
				job.setDataFilename(null);
			}
		}
		}

		
	}

	@Override
	public void clear() {
		em.clear();
		
	}
}
