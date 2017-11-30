package mobi.chouette.scheduler;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.concurrent.ManagedTaskListener;
import javax.naming.InitialContext;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Constant;
import mobi.chouette.common.ContenerChecker;
import mobi.chouette.common.PropertyNames;
import mobi.chouette.model.iev.Job.STATUS;
import mobi.chouette.persistence.hibernate.ContextHolder;
import mobi.chouette.service.JobService;
import mobi.chouette.service.JobServiceManager;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import org.apache.commons.collections.CollectionUtils;

/**
 * @author michel
 *
 */
@Singleton(name = Scheduler.BEAN_NAME)
@Startup
@Log4j
public class Scheduler {

	public static final String BEAN_NAME = "Scheduler";

	private static final int MAX_JOBS_DEFAULT = 5;

	private static final long JOB_SCHEDULE_INTERVAL_MS_DEFAULT = 120000;

	@EJB(beanName = ContenerChecker.NAME)
	ContenerChecker checker;

	@EJB
	JobServiceManager jobManager;

  	@Resource(lookup = "java:comp/DefaultManagedExecutorService")
	ManagedExecutorService executor;

	Map<Long,Future<STATUS>> startedFutures = new ConcurrentHashMap<>();
	// Map<Long,Task> startedTasks = new ConcurrentHashMap<>();

	private Integer maxJobs;

	private Integer maxTransfers;

	private String lock = "lock";

	@Lock(LockType.READ)
	public int getActiveJobsCount() {
		return startedFutures.size();
	}

	private Set<Long> activeTransferJobIds = new HashSet<>();

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void schedule() {
		int numActiveJobs = getActiveJobsCount();
		if (numActiveJobs >= getMaxJobs()) {
			log.info("Too many active jobs (" + numActiveJobs + "). Ignoring scheduling request");
			return;
		}

		List<JobService> waitingJobs = jobManager.getNextJobs();
		if (!CollectionUtils.isEmpty(waitingJobs)) {

			for (JobService jobService : waitingJobs) {
				if (!schedule(jobService)) {
					break;
				}
			}
		} else {
			log.info("nothing to schedule");
		}
	}

	private boolean schedule(JobService jobService) {
		synchronized (lock) {
			int numActiveJobs = getActiveJobsCount();
			int activeTransfersCount = activeTransferJobIds.size();
			log.info("Inside lock, numActiveJobs=" + numActiveJobs + ", numActiveTransfers: " + activeTransfersCount);
			if (numActiveJobs >= getMaxJobs()) {
				log.info("Too many active jobs, delay start up of job: " + jobService.getId());
				return false;
			}
			if (isTransferJob(jobService) && activeTransfersCount >= getMaxTransferJobs()) {
				log.info("Too many active transfer jobs, delay start up of job: " + jobService.getId());
				return true;
			}
			ReferentialLockManager referentialLockManager = ReferentialLockManagerFactory.getLockManager();
			if (referentialLockManager.attemptAcquireLocks(jobService.getRequiredReferentialsLocks())) {
				if (referentialLockManager.attemptAcquireJobLock(jobService.getId())) {
					startJob(jobService);
				} else {
					log.warn("Failed to acquire job lock after acquiring referential lock. JobId: " + jobService.getId());
					referentialLockManager.releaseLocks(jobService.getRequiredReferentialsLocks());
				}
			} else {
				log.info("Could not acquire necessary locks (" + jobService.getRequiredReferentialsLocks() + "), delay start up of job: " + jobService.getJob());
			}

		}
		return true;
	}

	private boolean isTransferJob(JobService jobService) {
		return Constant.EXPORTER.equals(jobService.getAction()) && "transfer".equals(jobService.getType());
	}


	private void startJob(JobService jobService) {
		log.info("start a new job " + jobService.getId() + " for referential: " + jobService.getReferential());
		jobManager.start(jobService);

		Map<String, String> properties = new HashMap<String, String>();
		Task task = new Task(jobService, properties, new TaskListener());
		// startedTasks.put(jobService.getId(),  task);
		Future<STATUS> future = executor.submit(task);
		startedFutures.put(jobService.getId(), future);

		if (isTransferJob(jobService)) {
			activeTransferJobIds.add(jobService.getId());
		}
	}

	@PostConstruct
	private void initialize() {
		log.info("Initializing job scheduler");
		List<JobService> list = jobManager.findAll();

		ReferentialLockManager lockManager = ReferentialLockManagerFactory.getLockManager();


		// abort started job
		Collection<JobService> scheduled = Collections2.filter(list, new Predicate<JobService>() {
			@Override
			public boolean apply(JobService jobService) {
				return jobService.getStatus() == STATUS.STARTED;
			}
		});
		for (JobService jobService : scheduled) {
			// Lock job to make sure no other nodes are executing it.
			if (lockManager.attemptAcquireJobLock(jobService.getId())) {
				log.info("Processing interrupted job " + jobService.getId());
				jobManager.processInterrupted(jobService);
				lockManager.releaseJobLock(jobService.getId());
			} else {
				log.info("Failed to acquire lock for started job, assuming job is executing on other node. JobId: " + jobService.getId());
			}
		}

		Timer timer = new Timer(true);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					schedule();
				} catch (Exception e) {
					log.warn("Scheduled request for starting waiting jobs failed with exception: " + e.getMessage(), e);
				}
			}
		}, 0, getScheduleIntervalMs());

	}

	private long getScheduleIntervalMs() {
		long scheduleFrequencyMs = JOB_SCHEDULE_INTERVAL_MS_DEFAULT;
		String key = checker.getContext() + PropertyNames.JOB_SHCEDULE_INTERVAL_MS;
		if (System.getProperty(key) != null) {
			scheduleFrequencyMs = Long.parseLong(System.getProperty(key));
		} else {
			log.warn("No value set for property: " + key + ", using default value: " + scheduleFrequencyMs);
		}

		return scheduleFrequencyMs;
	}

	private int getMaxJobs() {
		if (maxJobs == null) {
			String maxJobsKey = checker.getContext() + PropertyNames.MAX_STARTED_JOBS;
			if (System.getProperty(maxJobsKey) != null) {
				maxJobs = Integer.parseInt(System.getProperty(maxJobsKey));
			} else {
				log.warn("No value set for property: " + maxJobsKey + ", using default value: " + MAX_JOBS_DEFAULT);
				return MAX_JOBS_DEFAULT;
			}
		}
		return maxJobs;
	}

	private int getMaxTransferJobs() {
		if (maxTransfers == null) {
			String maxTransfersKey = checker.getContext() + PropertyNames.MAX_STARTED_TRANSFER_JOBS;
			if (System.getProperty(maxTransfersKey) != null) {
				maxTransfers = Integer.parseInt(System.getProperty(maxTransfersKey));
			} else {
				log.warn("No value set for property: " + maxTransfersKey + ", using default value: " + MAX_JOBS_DEFAULT);
				return MAX_JOBS_DEFAULT;
			}
		}
		return maxTransfers;
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

		if (isTransferJob(jobService)) {
			activeTransferJobIds.remove(jobService.getId());
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
			if (isTransferJob(task.getJob())) {
				activeTransferJobIds.remove(task.getJob().getId());
			}

			ReferentialLockManager lockManager = ReferentialLockManagerFactory.getLockManager();
			lockManager.releaseLocks(task.getJob().getRequiredReferentialsLocks());
			lockManager.releaseJobLock(task.getJob().getId());
			// launch next task
			executor.execute(new Runnable() {

				@Override
				public void run() {
					ContextHolder.setContext(null);
					try {
						InitialContext initialContext = new InitialContext();
						Scheduler scheduler = (Scheduler) initialContext.lookup("java:app/mobi.chouette.service/"
								+ BEAN_NAME);

						scheduler.schedule();
					} catch (Exception e) {
						log.error(e.getMessage(),e);
					}
				}
			});
		}

	}

}
