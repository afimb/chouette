package mobi.chouette.scheduler;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.concurrent.ManagedTaskListener;
import javax.naming.InitialContext;
import javax.ws.rs.core.MediaType;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Constant;
import mobi.chouette.dao.SchemaDAO;
import mobi.chouette.model.api.Job;
import mobi.chouette.model.api.Job.STATUS;
import mobi.chouette.model.api.Link;
import mobi.chouette.model.util.JobUtil;
import mobi.chouette.persistence.hibernate.ContextHolder;
import mobi.chouette.service.JobService;
import mobi.chouette.service.JobServiceManager;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;

@Singleton(name = Scheduler.BEAN_NAME)
@Startup
@Log4j
public class Scheduler {

	public static final String BEAN_NAME = "Scheduler";

	@EJB
	JobServiceManager jobManager;

	@EJB
	SchemaDAO schemaDAO;

	@Resource(lookup = "java:comp/DefaultManagedExecutorService")
	ManagedExecutorService executor;


	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void schedule(String referential) {

		JobService job = jobManager.getNextJob(referential);
		if (job != null) {
			jobManager.start(job);

			Map<String, String> properties = new HashMap<String, String>();
			Task task = new Task(job, properties, new TaskListener());
			executor.submit(task);
		}
	}

	@PostConstruct
	private void initialize() {

		List<JobService> list = jobManager.findAll();

		// abort started job
		Collection<JobService> scheduled = Collections2.filter(list, new Predicate<JobService>() {
			@Override
			public boolean apply(JobService job) {
				return job.getStatus() == STATUS.STARTED ;
			}
		});
		for (JobService job : scheduled) {
			jobManager.abort(job);
			
		}

		// schedule created job
		Collection<JobService> created = Collections2.filter(list, new Predicate<JobService>() {
			@Override
			public boolean apply(JobService job) {
				return job.getStatus() == STATUS.SCHEDULED;
			}
		});
		for (JobService job : created) {
			schedule(job.getReferential());
		}
	}

	public boolean cancel(Long id) {
		JobService job = jobManager.getJobService(id);
		
		
//		if (job.getStatus().ordinal() <= STATUS.STARTED.ordinal()) {
//			job.setStatus(STATUS.CANCELED);
//
//			// TODO remove location and cancel link only
//			job.getLinks().clear();
//			// set delete link
//			Link link = new Link();
//			link.setType(MediaType.APPLICATION_JSON);
//			link.setRel(Link.DELETE_REL);
//			link.setMethod(Link.DELETE_METHOD);
//			String href = MessageFormat.format("/{0}/{1}/terminated_jobs/{2,number,#}", Constant.ROOT_PATH,
//					job.getReferential(), job.getId());
//			link.setHref(href);
//			JobUtil.updateLink(job, link); //job.getLinks().add(link);
//			link = new Link();
//			link.setType(MediaType.APPLICATION_JSON);
//			link.setRel(Link.LOCATION_REL);
//			link.setMethod(Link.GET_METHOD);
//			href = MessageFormat.format("/{0}/{1}/terminated_jobs/{2,number,#}",
//					Constant.ROOT_PATH, job.getReferential(), job.getId());
//			link.setHref(href);
//			JobUtil.updateLink(job, link); //job.getLinks().add(link);
//
//			job.setUpdated(new Date());
//			jobDAO.update(job);
//			return true;
//
//		}
		return false;
	}

	public boolean delete(Long id) {
		// TODO refactor to maintain deleted jobs without data
//		Job job = jobDAO.find(id);
//		if (job.getStatus().ordinal() > STATUS.STARTED.ordinal()) {
//			jobDAO.delete(job);
//			return true;
//		}
		return false;
	}

	public boolean deleteAll(String referential) {
//		jobDAO.deleteAll(referential);
		return true;
	}
	


	class TaskListener implements ManagedTaskListener {

		@Override
		public void taskAborted(Future<?> future, ManagedExecutorService executor, Object task, Throwable exception) {
			log.info(Color.FAILURE + "task aborted : " + ContextHolder.getContext() + " -> " + task
					+ Color.NORMAL);
			schedule((Task) task);
		}

		@Override
		public void taskDone(Future<?> future, ManagedExecutorService executor, Object task, Throwable exception) {
			log.info(Color.SUCCESS + "task done : " + ContextHolder.getContext() + " -> " + task + Color.NORMAL);
			schedule((Task) task);
		}

		@Override
		public void taskStarting(Future<?> future, ManagedExecutorService executor, Object task) {
			log.info(Color.SUCCESS + "task starting : " + task + Color.NORMAL);

		}

		@Override
		public void taskSubmitted(Future<?> future, ManagedExecutorService executor, Object task) {
			log.info(Color.SUCCESS + "task submitted : " + task + Color.NORMAL);
		}

		private void schedule(final Task task) {
			executor.execute(new Runnable() {

				@Override
				public void run() {
					ContextHolder.setContext(null);
					try {
						String referential = task.getJob().getReferential();
						InitialContext initialContext = new InitialContext();
						Scheduler scheduler = (Scheduler) initialContext.lookup("java:app/mobi.chouette.scheduler/"
								+ BEAN_NAME);

						scheduler.schedule(referential);
					} catch (Exception e) {
						log.error(e);
					}
				}
			});
		}

	}

}
