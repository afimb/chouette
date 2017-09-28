package mobi.chouette.service;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Named;
import javax.ws.rs.core.MediaType;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.ContenerChecker;
import mobi.chouette.common.Pair;
import mobi.chouette.common.file.FileStore;
import mobi.chouette.common.file.LocalFileStore;
import mobi.chouette.model.iev.Job;

import org.joda.time.LocalDateTime;

import static java.util.concurrent.TimeUnit.SECONDS;
import static mobi.chouette.common.PropertyNames.FILE_STORE_IMPLEMENTATION;
import static mobi.chouette.service.CachingGoogleCloudFileStore.BEAN_NAME;

/**
 * Store permanent files in Google Cloud Storage. Use local file system for caching.
 */
@Singleton(name = BEAN_NAME)
@Named
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@Startup
@Log4j
public class CachingGoogleCloudFileStore implements FileStore {

	public static final String BEAN_NAME = "cachingGoogleCloudFileStore";

	@EJB(beanName = GoogleCloudFileStore.BEAN_NAME)
	FileStore cloudFileStore;


	FileStore localFileStore = new LocalFileStore();

	@EJB
	JobServiceManager jobServiceManager;

	@EJB
	ContenerChecker contenerChecker;

	private Date syncedUntil;

	private final ScheduledExecutorService scheduler =
			Executors.newScheduledThreadPool(1);

	private long updateFrequencySeconds = 300;

	@PostConstruct
	public void init() {
		if (BEAN_NAME.equals(System.getProperty(contenerChecker.getContext() + FILE_STORE_IMPLEMENTATION))) {
			log.info("Starting CachingGoogleCloudFileStore pre-fetch process");
			syncedUntil = new java.sql.Date(0);

			String updateFrequencyKey = "iev.file.store.cache.update.seconds";
			if (System.getProperty(updateFrequencyKey) != null) {
				try {
					updateFrequencySeconds = Long.valueOf(System.getProperty(updateFrequencyKey));
				} catch (NumberFormatException nfe) {
					log.warn("Illegal value for property named " + updateFrequencyKey + " in iev.properties. Should be no of seconds between cache updates (long)");
				}
			}

			scheduler.scheduleAtFixedRate(new PrefetchToLocalCacheTask(), 0, updateFrequencySeconds, SECONDS);

		}
	}


	@Override
	public InputStream getFileContent(Path filePath) {

		if (localFileStore.exists(filePath)) {
			log.debug("Returning file content from local cache: " + filePath);
			return localFileStore.getFileContent(filePath);
		}

		return cloudFileStore.getFileContent(filePath);
	}

	@Override
	public void writeFile(Path filePath, InputStream content) {
		localFileStore.writeFile(filePath, content);
		cloudFileStore.writeFile(filePath, content);
	}

	@Override
	public boolean delete(Path filePath) {
		localFileStore.delete(filePath);
		return cloudFileStore.delete(filePath);
	}

	@Override
	public void deleteFolder(Path folder) {
		localFileStore.delete(folder);
		cloudFileStore.delete(folder);
	}

	@Override
	public void createFolder(Path folder) {
		localFileStore.createFolder(folder);
		cloudFileStore.createFolder(folder);
	}

	@Override
	public boolean exists(Path filePath) {
		if (localFileStore.exists(filePath)) {
			return true;
		}
		return cloudFileStore.exists(filePath);
	}


	private class PrefetchToLocalCacheTask implements Runnable {

		@Override
		public void run() {
			log.info("Start pre-fetching job files from cloud storage. Caching all completed jobs since: " + syncedUntil);

			List<Job> completedJobsSinceLastSync = jobServiceManager.completedJobsSince(syncedUntil);
			completedJobsSinceLastSync.stream().forEach(job -> prefetchFilesForJob(job));
			syncedUntil = completedJobsSinceLastSync.stream().map(job -> job.getUpdated()).max(Date::compareTo).orElse(syncedUntil);

			log.info("Finished pre-fetching job files from cloud storage");

		}

		private void prefetchFilesForJob(Job job) {
			try {
				JobService jobService = jobServiceManager.getJobService(job.getId());

				job.getLinks().stream().filter(link -> MediaType.APPLICATION_JSON.equals(link.getType())).map(link -> Paths.get(jobService.getPathName(), link.getRel() + ".json")).map(path -> Pair.of(path, cloudFileStore.getFileContent(path))).filter(file -> file.getRight() != null)
						.forEach(file -> localFileStore.writeFile(file.getLeft(), file.getRight()));

			} catch (Exception exception) {
				log.warn("Unable to pre fetch files for job: " + job + " :" + exception.getMessage());
			}
		}
	}
}
