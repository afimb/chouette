package mobi.chouette.scheduler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import javax.ejb.Timeout;
import javax.ejb.TimerService;
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
import mobi.chouette.service.ServiceException;

import org.apache.commons.collections.CollectionUtils;

/**
 * @author michel
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

	Map<Long, Future<STATUS>> startedFutures = new ConcurrentHashMap<>();
	// Map<Long,Task> startedTasks = new ConcurrentHashMap<>();

	private Integer maxJobs;

	private Integer maxTransfers;

	private String lock = "lock";

	@Resource
	private TimerService timerService;

	@Lock(LockType.READ)
	public int getActiveJobsCount() {
		return startedFutures.size();
	}

	private Set<Long> activeTransferJobIds = new HashSet<>();

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	@Timeout
	public synchronized void schedule() {
		interruptStartedJobsWithoutOwner();
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
			log.debug("nothing to schedule");
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
		interruptStartedJobsWithoutOwner();

		timerService.createTimer(10000, getScheduleIntervalMs(), "Timed scheduler");

	}

	// Find jobs with status 'STARTED' that no nodes have active lock ownership claims for. These are probably not executing and need to be aborted or rescheduled
	private void interruptStartedJobsWithoutOwner() {
		// Make sure jobManager is initialized before doing anything else as this service loads all system properties!!
		List<JobService> started = jobManager.findByStatus(STATUS.STARTED);

		ReferentialLockManager lockManager = ReferentialLockManagerFactory.getLockManager();
		// abort started job
		for (JobService jobService : started) {
			// Lock job to make sure no other nodes are executing it.
			boolean locked = false;
			try {
				locked = lockManager.attemptAcquireJobLock(jobService.getId());
				if (locked) {
					interruptJobIfStarted(jobService.getId());
				} else {
					log.info("Failed to acquire lock for started job, assuming job is executing on other node. JobId: " + jobService.getId());
				}
			} finally {
				if (locked) {
					lockManager.releaseJobLock(jobService.getId());
				}
			}
		}
	}

	private void interruptJobIfStarted(Long id) {

		try {
			// Update job to be sure we have the latest status
			JobService job = jobManager.getJobService(id);
			if (STATUS.STARTED.equals(job.getStatus())) {
				log.info("Processing interrupted job " + job.getId());
				jobManager.processInterrupted(job);
			} else {
				log.info("Unable to interrupt started job (" + job.getId() + ") as status has changed from STARTED to: " + job.getStatus());
			}
		} catch (ServiceException serviceException) {
			log.warn("Exception while updating job information before interrupting job: " + serviceException.getMessage(), serviceException);
		}

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
		log.info("try to cancel " + jobService.getId());
		Future<STATUS> future = startedFutures.remove(jobService.getId());
		if (future != null) {
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
			if (task != null && task instanceof Task) {
				log.info("cancel task");
				((Task) task).cancel();
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
						log.error(e.getMessage(), e);
					}
				}
			});
		}

	}

}
