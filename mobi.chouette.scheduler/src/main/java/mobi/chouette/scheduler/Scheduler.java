package mobi.chouette.scheduler;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.concurrent.ManagedTaskListener;
import javax.ws.rs.core.MediaType;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.dao.JobDAO;
import mobi.chouette.model.api.Job;
import mobi.chouette.model.api.Link;
import mobi.chouette.model.api.Job.STATUS;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;

@Singleton(name = "Scheduler")
@Startup
@Log4j
public class Scheduler {

	@EJB
	JobDAO dao;

	@Resource(lookup = "java:comp/DefaultManagedExecutorService")
	ManagedExecutorService executor;

	public void schedule(String referential) {
		Job job = dao.getNextJob(referential);
		if (job != null) {
			job.setStatus(STATUS.SCHEDULED);

			// remove cancel link
			Iterables.removeIf(job.getLinks(), new Predicate<Link>() {
				@Override
				public boolean apply(Link link) {
					return link.getRel().equals(Link.CANCEL_REL);
				}
			});
			dao.update(job);

			Map<String, String> properties = new HashMap<String, String>();
			Task task = new Task(job, properties, new TaskListener());
			Future<?> future = executor.submit(task);
		}
	}

	@PostConstruct
	private void initialize() {

		List<Job> list = dao.findAll();

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

			// add delete link
			Link link = new Link();
			link.setType(MediaType.APPLICATION_JSON);
			link.setRel(Link.DELETE_REL);
			link.setMethod(Link.DELETE_METHOD);
			link.setHref(MessageFormat.format("/{0}/{1}/reports/{2}",
					Constant.ROOT_PATH, job.getReferential(), job.getId()));
			job.getLinks().add(link);

			dao.update(job);
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

	public boolean cancel(Job job) {

		job.setStatus(STATUS.CANCELED);

		// remove location & cancel link
		Iterables.removeIf(job.getLinks(), new Predicate<Link>() {
			@Override
			public boolean apply(Link link) {
				return link.getRel().equals(Link.LOCATION_REL)
						|| link.getRel().equals(Link.CANCEL_REL);
			}
		});

		// add delete link
		Link link = new Link();
		link.setType(MediaType.APPLICATION_JSON);
		link.setRel(Link.DELETE_REL);
		link.setMethod(Link.DELETE_METHOD);
		link.setHref(MessageFormat.format("/{0}/{1}/reports/{2}", Constant.ROOT_PATH,
				job.getReferential(), job.getId()));
		job.getLinks().add(link);

		dao.update(job);

		return true;
	}

	public boolean delete(Job job) {
		dao.delete(job);
		return true;
	}

	class TaskListener implements ManagedTaskListener {

		@Override
		public void taskAborted(Future<?> future,
				ManagedExecutorService executor, Object task,
				Throwable exception) {
			log.info(Color.FAILURE + "[DSU] task aborted : " + task
					+ Color.NORMAL);
			Task target = (Task) task;
			Scheduler.this.schedule(target.getJob().getReferential());
		}

		@Override
		public void taskDone(Future<?> future, ManagedExecutorService executor,
				Object task, Throwable exception) {
			log.info(Color.SUCCESS + "[DSU] task done : " + task
					+ Color.NORMAL);
			Task target = (Task) task;
			Scheduler.this.schedule(target.getJob().getReferential());
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

	}
}
