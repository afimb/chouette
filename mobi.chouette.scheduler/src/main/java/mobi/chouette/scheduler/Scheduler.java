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
import mobi.chouette.dao.JobDAO;
import mobi.chouette.dao.SchemaDAO;
import mobi.chouette.model.api.Job;
import mobi.chouette.model.api.Job.STATUS;
import mobi.chouette.model.api.Link;
import mobi.chouette.persistence.hibernate.ContextHolder;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;

@Singleton(name = Scheduler.BEAN_NAME)
@Startup
@Log4j
public class Scheduler {

	public static final String BEAN_NAME = "Scheduler";

	@EJB
	JobDAO jobDAO;

	@EJB
	SchemaDAO schemaDAO;

	@Resource(lookup = "java:comp/DefaultManagedExecutorService")
	ManagedExecutorService executor;

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void schedule(String referential) {
		System.out.println("Scheduler.schedule() tenant "
				+ ContextHolder.getContext()+ " >>>>>>>>>>>>>>> " + schemaDAO.getCurrentSchema());
		Job job = jobDAO.getNextJob(referential);
		if (job != null) {
			job.setStatus(STATUS.SCHEDULED);

			// remove cancel link
			Iterables.removeIf(job.getLinks(), new Predicate<Link>() {
				@Override
				public boolean apply(Link link) {
					return link.getRel().equals(Link.CANCEL_REL);
				}
			});

			job.setUpdated(new Date());
			jobDAO.update(job);

			Map<String, String> properties = new HashMap<String, String>();
			Task task = new Task(job, properties, new TaskListener());
			Future<?> future = executor.submit(task);
		}
	}

	@PostConstruct
	private void initialize() {

		List<Job> list = jobDAO.findAll();

		// abort scheduled job
		Collection<Job> scheduled = Collections2.filter(list,
				new Predicate<Job>() {
			@Override
			public boolean apply(Job job) {
				return job.getStatus() == STATUS.SCHEDULED;
			}
		});
		for (Job job : scheduled) {
			job.setStatus(STATUS.ABORTED);

			// remove location link
			Iterables.removeIf(job.getLinks(), new Predicate<Link>() {
				@Override
				public boolean apply(Link link) {
					return link.getRel().equals(Link.LOCATION_REL);
				}
			});

			// set delete link
			Link link = new Link();
			link.setType(MediaType.APPLICATION_JSON);
			link.setRel(Link.DELETE_REL);
			link.setMethod(Link.DELETE_METHOD);
			link.setHref(MessageFormat.format("/{0}/{1}/reports/{2,number,#}",
					Constant.ROOT_PATH, job.getReferential(), job.getId()));
			job.getLinks().clear();
			job.getLinks().add(link);

			job.setUpdated(new Date());
			jobDAO.update(job);
		}

		// schedule created job
		Collection<Job> created = Collections2.filter(list,
				new Predicate<Job>() {
			@Override
			public boolean apply(Job job) {
				return job.getStatus() == STATUS.CREATED;
			}
		});
		for (Job job : created) {
			schedule(job.getReferential());
		}
	}

	public boolean cancel(Long id) {

		Job job = jobDAO.find(id);
		if (job.getStatus().equals(STATUS.CREATED) || job.getStatus().equals(STATUS.SCHEDULED))
		{
			job.setStatus(STATUS.CANCELED);

			// set delete link
			Link link = new Link();
			link.setType(MediaType.APPLICATION_JSON);
			link.setRel(Link.DELETE_REL);
			link.setMethod(Link.DELETE_METHOD);
			link.setHref(MessageFormat.format("/{0}/{1}/reports/{2,number,#}",
					Constant.ROOT_PATH, job.getReferential(), job.getId()));
			job.getLinks().clear();
			job.getLinks().add(link);

			job.setUpdated(new Date());
			jobDAO.update(job);

		}
		return true;
	}

	public boolean delete(Job job) {
		jobDAO.delete(job);
		return true;
	}

	class TaskListener implements ManagedTaskListener {

		@Override
		public void taskAborted(Future<?> future,
				ManagedExecutorService executor, Object task,
				Throwable exception) {
			log.info(Color.FAILURE + "[DSU] task aborted : "
					+ ContextHolder.getContext() + " -> " + task + Color.NORMAL);
			Task target = (Task) task;
			final String referential = target.getJob().getReferential();
			doSchedule(referential);
		}

		@Override
		public void taskDone(Future<?> future, ManagedExecutorService executor,
				Object task, Throwable exception) {
			log.info(Color.SUCCESS + "[DSU] task done : "
					+ ContextHolder.getContext() + " -> " + task + Color.NORMAL);
			Task target = (Task) task;
			final String referential = target.getJob().getReferential();
			doSchedule(referential);
		}

		@Override
		public void taskStarting(Future<?> future,
				ManagedExecutorService executor, Object task) {
			log.info(Color.SUCCESS + "[DSU] task starting : " + task
					+ Color.NORMAL);

		}

		@Override
		public void taskSubmitted(Future<?> future,
				ManagedExecutorService executor, Object task) {
			log.info(Color.SUCCESS + "[DSU] task submitted : " + task
					+ Color.NORMAL);
		}

		private void doSchedule(final String referential) {
			executor.execute(new Runnable() {

				@Override
				public void run() {
					ContextHolder.setContext(null);
					try {						
						InitialContext initialContext = new InitialContext();
						Scheduler scheduler = (Scheduler) initialContext
								.lookup("java:app/mobi.chouette.scheduler/"
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
