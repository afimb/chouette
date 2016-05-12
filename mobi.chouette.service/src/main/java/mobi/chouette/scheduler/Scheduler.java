package mobi.chouette.scheduler;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.dao.iev.JobDAO;
import mobi.chouette.model.iev.Job.STATUS;
import mobi.chouette.persistence.hibernate.ContextHolder;
import mobi.chouette.service.JobService;
import mobi.chouette.service.JobServiceManager;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

/**
 * @author michel
 *
 */
@Singleton(name = Scheduler.BEAN_NAME)
@Startup
@Log4j
public class Scheduler {

	public static final String BEAN_NAME = "Scheduler";

	@EJB
	JobDAO jobDAO;

	@EJB
	JobServiceManager jobManager;

  	@Resource(lookup = "java:comp/DefaultManagedExecutorService")
	ManagedExecutorService executor;
	
	Map<Long,Future<STATUS>> startedFutures = new ConcurrentHashMap<>();
	// Map<Long,Task> startedTasks = new ConcurrentHashMap<>();

	public int getActivejobsCount()
	{
		return startedFutures.size();
	}

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void schedule(String referential) {
		
		log.info("schedule referential "+referential);
		JobService jobService =  jobManager.getNextJob(referential);
		if (jobService != null) {
			log.info("start a new job "+jobService.getId());
			jobManager.start(jobService);

			Map<String, String> properties = new HashMap<String, String>();
			Task task = new Task(jobService, properties, new TaskListener());
			// startedTasks.put(jobService.getId(),  task);
			Future<STATUS> future = executor.submit(task);
			startedFutures.put(jobService.getId(), future);
		}
		else
		{
			log.info("nothing to schedule ");
		}
	}
	

	@PostConstruct
	private void initialize() {

		List<JobService> list = jobManager.findAll();

		// abort started job
		Collection<JobService> scheduled = Collections2.filter(list, new Predicate<JobService>() {
			@Override
			public boolean apply(JobService jobService) {
				return jobService.getStatus() == STATUS.STARTED ;
			}
		});
		for (JobService jobService : scheduled) {
			jobManager.abort(jobService);
			
		}

		// schedule created job
		Collection<JobService> created = Collections2.filter(list, new Predicate<JobService>() {
			@Override
			public boolean apply(JobService job) {
				return job.getStatus() == STATUS.SCHEDULED;
			}
		});
		for (JobService jobService : created) {
			schedule(jobService.getReferential());
		}
	}

	
	/**
	 * cancel task 
	 * 
	 * @param job
	 * @return
	 */
	public boolean cancel(JobService jobService) {
	
		// remove prevents for multiple calls
		log.info("try to cancel "+jobService.getId());
		Future<STATUS> future = startedFutures.remove(jobService.getId());
	    if (future != null) 
	    {
	    	log.info("cancel future");
	    	future.cancel(false);
	    }
		
		return true;
	}



	class TaskListener implements ManagedTaskListener {

		@Override
		public void taskAborted(Future<?> future, ManagedExecutorService executor, Object task, Throwable exception) {
			log.info(Color.FAILURE + "task aborted : " + ContextHolder.getContext() + " -> " + task
					+ Color.NORMAL);
			if (task != null && task instanceof Task)
			{
		    	log.info("cancel task");
				((Task)task).cancel();
			}
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

		/**
		 * launch next task if exists
		 * 
		 * @param task
		 */
		private void schedule(final Task task) {
			// remove task from stated map
			// startedTasks.remove(task.getJob().getId());
			startedFutures.remove(task.getJob().getId());
			// launch next task
			executor.execute(new Runnable() {

				@Override
				public void run() {
					ContextHolder.setContext(null);
					try {
						String referential = task.getJob().getReferential();
						InitialContext initialContext = new InitialContext();
						Scheduler scheduler = (Scheduler) initialContext.lookup("java:app/mobi.chouette.service/"
								+ BEAN_NAME);

						scheduler.schedule(referential);
					} catch (Exception e) {
						log.error(e.getMessage(),e);
					}
				}
			});
		}

	}

}
