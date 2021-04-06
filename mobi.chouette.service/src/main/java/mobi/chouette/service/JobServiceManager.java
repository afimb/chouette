package mobi.chouette.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ws.rs.core.MediaType;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Constant;
import mobi.chouette.common.ContenerChecker;
import mobi.chouette.common.PropertyNames;
import mobi.chouette.common.file.FileServiceException;
import mobi.chouette.common.file.FileStore;
import mobi.chouette.common.file.FileStoreFactory;
import mobi.chouette.dao.iev.JobDAO;
import mobi.chouette.dao.iev.StatDAO;
import mobi.chouette.exchange.InputValidator;
import mobi.chouette.exchange.TestDescription;
import mobi.chouette.model.iev.Job;
import mobi.chouette.model.iev.Job.STATUS;
import mobi.chouette.model.iev.Link;
import mobi.chouette.model.iev.Stat;
import mobi.chouette.persistence.hibernate.ChouetteIdentifierGenerator;
import mobi.chouette.scheduler.Scheduler;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ObjectUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

@Singleton(name = JobServiceManager.BEAN_NAME)
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@Startup
@Log4j
public class JobServiceManager {

	public static final String BEAN_NAME = "JobServiceManager";

	public static final String CONFIG_LOCATION_PROPERTY = "config.location";

	@EJB
	JobDAO jobDAO;

	@EJB
	StatDAO statDAO;

	@EJB(beanName = ContenerChecker.NAME)
	ContenerChecker checker;

	@EJB
	Scheduler scheduler;


	private Set<Object> referentials = Collections.synchronizedSet(new HashSet<>());
	private String rootDirectory;

	@PostConstruct
	public synchronized void init() {
		String context = checker.getContext();
		System.setProperty(context + PropertyNames.MAX_STARTED_JOBS, "5");
		System.setProperty(context + PropertyNames.MAX_COPY_BY_JOB, "5");
		try {
			// set default properties
			System.setProperty(checker.getContext() + PropertyNames.ROOT_DIRECTORY, System.getProperty("user.home"));

			String configLocation = System.getProperty(CONFIG_LOCATION_PROPERTY, "/etc/chouette/" + context + "/" + context + ".properties");
			Properties properties = new Properties();
			if (configLocation.startsWith("http")) {
				loadPropertiesFromHttpEndpoint(configLocation, properties);
			} else {
				loadPropertiesFromFile(configLocation.replace("file:/", ""), properties);
			}
			for (String key : properties.stringPropertyNames()) {
				if (key.startsWith(context))
					System.setProperty(key, properties.getProperty(key));
				else
					System.setProperty(context + "." + key, properties.getProperty(key));
			}
		} catch (Exception e) {
			log.error("cannot process properties", e);
		}

		rootDirectory = System.getProperty(checker.getContext() + PropertyNames.ROOT_DIRECTORY);

		// migrate jobs
		jobDAO.migrate();
	}

	private void loadPropertiesFromFile(String fileName, Properties properties) {
		// try to read properties
		File propertyFile = new File(fileName);
		if (propertyFile.exists() && propertyFile.isFile()) {
			try {
				FileInputStream fileInput = new FileInputStream(propertyFile);
				properties.load(fileInput);
				fileInput.close();
				log.info("reading properties from " + propertyFile.getAbsolutePath());
			} catch (IOException e) {
				log.error("cannot read properties " + propertyFile.getAbsolutePath()
						          + " , using default properties", e);
			}
		} else {
			log.info("no property file found " + propertyFile.getAbsolutePath() + " , using default properties");
		}
	}

	protected void loadPropertiesFromHttpEndpoint(String endpoint, Properties properties) {
		log.info("Loading remote properties from: " + endpoint);
		try {
			HttpURLConnection con = (HttpURLConnection) new URL(endpoint).openConnection();
			properties.load(con.getInputStream());
		} catch (Exception e) {
			log.error("Failed to load properties from remote source: " + e.getMessage(), e);
		}

	}

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public JobService create(String referential, String action, String type, Map<String, InputStream> inputStreamsByName)
			throws ServiceException {
		// Valider les parametres
		validateReferential(referential);

		JobService jobService = createJob(referential, action, type, inputStreamsByName);
		try {
			scheduler.schedule();
		} catch (Exception e) {
			log.warn("Failed to schedule job, leaving it to timed scheduler");
		}
		return jobService;
	}

	public List<Stat> getMontlyStats() throws ServiceException {
		try {
			return statDAO.getCurrentYearStats();
		} catch (Exception ex) {
			throw new ServiceException(ServiceExceptionCode.INTERNAL_ERROR, "Failed to read stats", ex);
		}
	}

	private JobService createJob(String referential, String action, String type,
			Map<String, InputStream> inputStreamsByName) throws ServiceException {
		JobService jobService = null;
		try {
			log.info("Creating job referential="+referential+" ...");
			// Instancier le modèle du service 'upload'
			jobService = new JobService(rootDirectory, referential, action, type);

			// Enregistrer le jobService pour obtenir un id
			jobDAO.create(jobService.getJob());
			// mkdir

			FileStore fileStore = FileStoreFactory.getFileStore();
			fileStore.deleteFolder(jobService.getPath());
			fileStore.createFolder(jobService.getPath());

			// Enregistrer des paramètres à conserver sur fichier
			jobService.saveInputStreams(inputStreamsByName);

			// set cancel link
			jobService.addLink(MediaType.APPLICATION_JSON, Link.CANCEL_REL);

			jobDAO.update(jobService.getJob());
			// jobDAO.detach(jobService.getJob());

			log.info("Job id=" + jobService.getJob().getId() + " referential="+referential+" created");
			return jobService;

		} catch (RequestServiceException ex) {
			deleteBadCreatedJob(jobService);
			throw new RequestServiceException(ex.getRequestExceptionCode(), "Failed to create job ",ex);
		} catch (Exception ex) {
			deleteBadCreatedJob(jobService);
			throw new ServiceException(ServiceExceptionCode.INTERNAL_ERROR, "Failed to create job", ex);
		}
	}

	private void deleteBadCreatedJob(JobService jobService) {
		if (jobService == null || jobService.getJob() == null || jobService.getJob().getId() == null)
			return;
		try {
			// remove path if exists
			if (jobService.getPath() != null) {
				FileStoreFactory.getFileStore().deleteFolder(jobService.getPath());
			}
		} catch (Exception e) {
			log.error("Failed to delete directory " + jobService.getPath(), e);
		}
		try {
			Job job = jobService.getJob();
			if (job != null && job.getId() != null) {
				log.info("deleting bad job " + job.getId());
				jobDAO.deleteById(job.getId());
			}
		} catch (Exception e) {
			log.error("Failed to delete job " + jobService.getJob().getId(), e);
		}

	}

	public List<TestDescription> getTestList(String action, String type) throws ServiceException {
		try {
			InputValidator inputValidator = JobService.getCommandInputValidator(action, type);
			return inputValidator.getTestList();

		} catch (Exception ex) {
			log.info("fail to read tests ",ex);
			throw new ServiceException(ServiceExceptionCode.INTERNAL_ERROR, ex);
		}
	}
	
	public void validateReferential(final String referential) throws ServiceException {

		if (referentials.contains(referential))
			return;

		boolean result = checker.validateContener(referential);
		if (!result) {
			throw new RequestServiceException(RequestExceptionCode.UNKNOWN_REFERENTIAL, "Unknown referential " + referential);
		}

		referentials.add(referential);
	}

	public JobService download(String referential, Long id) throws ServiceException {
		return getJobService(referential, id);
	}

	/**
	 * find ordered list of next waiting jobs.
	 *
	 *
	 * @return
	 */
	public List<JobService> getNextJobs() {
		return jobDAO.getNextJobs().stream().map(job -> new JobService(rootDirectory, job)).collect(Collectors.toList());
	}


	public void start(JobService jobService) {
		jobService.setStatus(STATUS.STARTED);
		jobService.setUpdated(LocalDateTime.now());
		jobService.setStarted(LocalDateTime.now());
		jobService.addLink(MediaType.APPLICATION_JSON, Link.REPORT_REL);
		jobDAO.update(jobService.getJob());
	}

	public void processInterrupted(JobService jobService) {
		if (rescheduleJobs()) {
			reschedule(jobService);
		} else {
			abort(jobService);
		}
	}

	public void reschedule(JobService jobService) {
		jobService.setStatus(STATUS.RESCHEDULED);
		jobService.setUpdated(LocalDateTime.now());
		jobService.setStarted(null);
		jobService.removeLink(Link.REPORT_REL);
		jobDAO.update(jobService.getJob());
	}

	private boolean rescheduleJobs() {
		String propertyName = checker.getContext() + PropertyNames.RESCHEDULE_INTERRUPTED_JOBS;
		String property = System.getProperty(propertyName);
		if (property == null || property.trim().equals("")) {
			log.warn("Property " + propertyName + " not set. Falling back to default behaviour, which is to abort jobs");
			return false;
		}
		return Boolean.parseBoolean(property);
	}

	public JobService cancel(String referential, Long id) throws ServiceException {
		validateReferential(referential);
		JobService jobService = getJobService(referential, id);
		if (jobService.getStatus().ordinal() <= STATUS.STARTED.ordinal()) {

			if (jobService.getStatus().equals(STATUS.STARTED)) {
				scheduler.cancel(jobService);
			}

			jobService.setStatus(STATUS.CANCELED);

			// remove cancel link only
			jobService.removeLink(Link.CANCEL_REL);
			// set delete link
			jobService.addLink(MediaType.APPLICATION_JSON, Link.DELETE_REL);

			jobService.setUpdated(LocalDateTime.now());
			jobDAO.update(jobService.getJob());

		}
		return jobService;
	}

	public void remove(String referential, Long id) throws ServiceException {
		validateReferential(referential);
		JobService jobService = getJobService(referential, id);
		if (jobService.getStatus().ordinal() <= STATUS.STARTED.ordinal()) {
			throw new RequestServiceException(RequestExceptionCode.SCHEDULED_JOB, "referential = " + referential
					+ " ,id = " + id);
		}
		try {
			FileStoreFactory.getFileStore().deleteFolder(jobService.getPath());
		} catch (FileServiceException e) {
			log.error("fail to delete directory " + jobService.getPath(), e);
		}
		jobDAO.delete(jobService.getJob());
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void removeOldJobs(final int keepDays, final int keepJobsPerReferential) throws ServiceException {

		List<Job> completedJobs = new ArrayList<>();
		Job.STATUS.getCompletedStatuses().stream().forEach(status -> completedJobs.addAll(jobDAO.findByStatus(status)));
		Collections.sort(completedJobs, (job1, job2) -> ObjectUtils.compare(job1.getUpdated(), job2.getUpdated()));
		int jobsDeleted = 0;

		List<List<Job>> referentialsWithCompletedJobs = completedJobs.stream().collect(Collectors.groupingBy(Job::getReferential))
				.values().stream().filter(completedJobsForRef -> completedJobsForRef.size() > keepJobsPerReferential).collect(Collectors.toList());

		for (List<Job> referentialWithCompletedJobs : referentialsWithCompletedJobs) {
			LocalDateTime ageLimit = LocalDateTime.now().minusDays(keepDays);
			List<Job> deleteJobs = referentialWithCompletedJobs.subList(0, referentialWithCompletedJobs.size() - keepJobsPerReferential)
					.stream().filter(job -> job.getUpdated() != null && job.getUpdated().isBefore(ageLimit)).collect(Collectors.toList());

			for (Job deleteJob : deleteJobs) {
				log.debug("Removing old, completed job: " + deleteJob);
				remove(deleteJob.getReferential(), deleteJob.getId());
			}

			jobsDeleted += deleteJobs.size();

		}

		log.info("Removed old jobs. Cnt: " + jobsDeleted);
	}

	public void drop(String referential) throws ServiceException {

		List<JobService> jobServices = findAll(referential);
		// reject demand if non terminated jobs are present
		for (JobService jobService : jobServices) {
			if (jobService.getStatus().equals(STATUS.STARTED) || jobService.getStatus().equals(STATUS.SCHEDULED)) {
				throw new RequestServiceException(RequestExceptionCode.REFERENTIAL_BUSY, "referential");
			}
		}

		// remove all jobs
		jobDAO.deleteAll(referential);

		// clean directories
		try {
			FileStoreFactory.getFileStore().deleteFolder(Paths.get(JobService.getRootPathName(rootDirectory, referential)));
		} catch (FileServiceException e) {
			log.error("fail to delete directory for" + referential, e);
		}

		// remove referential from known ones
		referentials.remove(referential);

		// remove sequences data for this tenant
		ChouetteIdentifierGenerator.deleteTenant(referential);

	}

	public void terminate(JobService jobService) {
		jobService.setStatus(STATUS.TERMINATED);

		// remove cancel link only
		jobService.removeLink(Link.CANCEL_REL);
		// set delete link
		jobService.addLink(MediaType.APPLICATION_JSON, Link.DELETE_REL);

		FileStore fileStore = FileStoreFactory.getFileStore();

		// add data link if necessary
		if (!jobService.linkExists(Link.OUTPUT_REL)) {
			if (jobService.getOutputFilename() != null
					&& fileStore.exists(Paths.get(jobService.getPathName(), jobService.getOutputFilename()))) {
				jobService.addLink(MediaType.APPLICATION_OCTET_STREAM, Link.DATA_REL);
				jobService.addLink(MediaType.APPLICATION_OCTET_STREAM, Link.OUTPUT_REL);
			}
		}
		// add validation report link
		if (!jobService.linkExists(Link.VALIDATION_REL)) {
			if (fileStore.exists(Paths.get(jobService.getPathName(), Constant.VALIDATION_FILE)))
				jobService.addLink(MediaType.APPLICATION_JSON, Link.VALIDATION_REL);
		}
		jobService.setUpdated(LocalDateTime.now());
		jobDAO.update(jobService.getJob());

		// update statistics
		// Ajout des statistiques d'import, export ou validation en base de données
		{
			// log.info("BEGIN ADDING STAT referential : " + jobService.getReferential() + " action : " + jobService.getAction() + " type :" + jobService.getType());
			LocalDate now = LocalDate.now();
			
			// Suppression des lignes de statistiques pour n'avoir que 12 mois glissants
			statDAO.removeObsoleteStatFromDatabase(now);

			// log.info("END DELETING OBSOLETE STATS FROM DATABASE");

			//Ajout d'une nouvelle statistique en base
			statDAO.addStatToDatabase(now, jobService.getReferential(), jobService.getAction(), jobService.getType());

			// log.info("END ADDING STAT referential : " + jobService.getReferential() + " action : " + jobService.getAction() + " type :" + jobService.getType());
		}
	}

	public void abort(JobService jobService) {
		if (jobService.getStatus().ordinal() <= STATUS.STARTED.ordinal()) {
			jobService.setStatus(STATUS.ABORTED);

			// remove cancel link only
			jobService.removeLink(Link.CANCEL_REL);
			// set delete link
			jobService.addLink(MediaType.APPLICATION_JSON, Link.DELETE_REL);

			// add validation report link
			if (!jobService.linkExists(Link.VALIDATION_REL)) {
				if (FileStoreFactory.getFileStore().exists(Paths.get(jobService.getPathName(), Constant.VALIDATION_FILE)))
					jobService.addLink(MediaType.APPLICATION_JSON, Link.VALIDATION_REL);
			}

            jobService.setUpdated(LocalDateTime.now());
            jobDAO.update(jobService.getJob());
		}

	}

	public List<JobService> findAll() {
		List<Job> jobs = jobDAO.findAll();
		return wrapAsJobServices(jobs);
	}

	public List<JobService> findByStatus(Job.STATUS status) {
		List<Job> jobs = jobDAO.findByStatus(status);
		return wrapAsJobServices(jobs);
	}

	public List<JobService> findAll(String referential) {
		List<Job> jobs = jobDAO.findByReferential(referential);
		return wrapAsJobServices(jobs);
	}

	private List<JobService> wrapAsJobServices(List<Job> jobs) {
		List<JobService> jobServices = new ArrayList<>(jobs.size());
		for (Job job : jobs) {
			jobServices.add(new JobService(rootDirectory, job));
		}

		return jobServices;
	}

	public JobService scheduledJob(String referential, Long id) throws ServiceException {
		validateReferential(referential);
		return getJobService(referential, id);
	}

	public JobService terminatedJob(String referential, Long id) throws ServiceException {
		validateReferential(referential);
		JobService jobService = getJobService(referential, id);

		if (jobService.getStatus().ordinal() < STATUS.TERMINATED.ordinal()
				|| jobService.getStatus().ordinal() == STATUS.DELETED.ordinal()) {
			throw new RequestServiceException(RequestExceptionCode.UNKNOWN_JOB, "referential = " + referential
					+ " ,id = " + id);
		}

		return jobService;
	}

	private JobService getJobService(String referential, Long id) throws ServiceException {

		Job job = jobDAO.find(id);
		if (job != null && job.getReferential().equals(referential)) {
			return new JobService(rootDirectory, job);
		}
		throw new RequestServiceException(RequestExceptionCode.UNKNOWN_JOB, "referential = " + referential + " ,id = "
				+ id);
	}

	public JobService getJobService(Long id) throws ServiceException {
		Job job = jobDAO.find(id);
		if (job != null) {
			return new JobService(rootDirectory, job);
		}
		throw new RequestServiceException(RequestExceptionCode.UNKNOWN_JOB, " id = " + id);
	}

	public List<JobService> jobs(String referential, String action[], final Long version, Job.STATUS[] status) throws ServiceException {
		if (referential!=null) {
			validateReferential(referential);
		}
		List<Job> jobs = null;
		if (action == null) {
			jobs = jobDAO.findByReferential(referential,status);
		} else {
			jobs = jobDAO.findByReferentialAndAction(referential, action,status);
		}

		Collection<Job> filtered = Collections2.filter(jobs, new Predicate<Job>() {
			@Override
			public boolean apply(Job job) {
				// filter on update time if given, otherwise don't return
				// deleted jobs
				boolean versionZeroCondition = (version == 0) && job.getStatus().ordinal() < STATUS.DELETED.ordinal();
				boolean versionNonZeroCondition = (version > 0) && version < job.getUpdated().toDate().getTime();

				return versionZeroCondition || versionNonZeroCondition;
			}
		});

		List<JobService> jobServices = new ArrayList<>(filtered.size());
		for (Job job : filtered) {
			jobServices.add(new JobService(rootDirectory, job));
		}
		return jobServices;
	}

	// administration operation
	public List<JobService> activeJobs() {

		List<Job> jobs = jobDAO.findByStatus(Job.STATUS.STARTED);
		jobs.addAll(jobDAO.findByStatus(Job.STATUS.SCHEDULED));
		jobs.addAll(jobDAO.findByStatus(Job.STATUS.RESCHEDULED));

		return wrapAsJobServices(jobs);
	}

	/**
	 * Return all jobs in a completed state that has last updated time not before given datetime.
	 */
	public List<Job> completedJobsSince(LocalDateTime since) {
		return jobDAO.findByStatusesAndUpdatedSince(STATUS.getCompletedStatuses(),since);
	}

}
