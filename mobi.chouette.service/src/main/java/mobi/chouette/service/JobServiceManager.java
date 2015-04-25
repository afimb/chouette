package mobi.chouette.service;

import static mobi.chouette.common.Constant.ACTION_PARAMETERS_FILE;
import static mobi.chouette.common.Constant.PARAMETERS_FILE;
import static mobi.chouette.common.Constant.VALIDATION_PARAMETERS_FILE;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.JSONUtil;
import mobi.chouette.dao.JobDAO;
import mobi.chouette.dao.SchemaDAO;
import mobi.chouette.exchange.InputValidator;
import mobi.chouette.exchange.InputValidatorFactory;
import mobi.chouette.model.api.Job;
import mobi.chouette.model.api.Job.STATUS;
import mobi.chouette.model.api.Link;
import mobi.chouette.scheduler.Parameters;
import mobi.chouette.scheduler.Scheduler;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

@Singleton(name = JobServiceManager.BEAN_NAME)
@Startup
@Log4j
public class JobServiceManager {

	public static final String BEAN_NAME = "JobServiceManager";

	@EJB
	JobDAO jobDAO;

	@EJB
	SchemaDAO schemaDAO;

	@EJB 
	Scheduler scheduler;

	public JobService upload(String referential, String action, String type, Map<String, InputStream> inputStreamsByName) throws ServiceException {
		JobService jobService = create(referential,action,type,inputStreamsByName);
		// Lancer la tache
		scheduler.schedule(jobService.getReferential());
		return jobService;
	}

	// @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public JobService create(String referential, String action, String type, Map<String, InputStream> inputStreamsByName) throws ServiceException {

		// Valider les parametres
		validateParams(referential, action, type, inputStreamsByName);

		// Instancier le modèle du service 'upload'
		JobService jobService = new JobService(referential, action, type, inputStreamsByName);

		validateInputs(jobService);

		try {
			// Enregistrer ce qui est persistent en base
			// Les liens sont créés par anticipation sur l'enregistrements des paramètres reçus
			jobDAO.create(jobService.getJob());
			jobDAO.flush();

			// Enregistrer des paramètres à conserver sur fichier
			fileResourceSave(jobService);

			return jobService;

		} catch (Exception ex) {
			if (jobService != null && jobService.getJob().getId() != null) {
				jobDAO.delete(jobService.getJob());
			}
			// remove path if exists

			throw new ServiceException(ServiceExceptionCode.INTERNAL_ERROR, ex);
		}
	}

	private void fileResourceSave(JobService jobService) throws IOException, JAXBException {
		if ( jobService.hasFileResourceProperties()) {
			// mkdir
			java.nio.file.Path dir = getJobDataDirectory(jobService);
			if (Files.exists(dir)) {
				jobDAO.delete(jobService.getJob());
			}
			Files.createDirectories(dir);
			
			Parameters parameters = new Parameters();
			parameters.setConfiguration(jobService.getActionParameters());
			parameters.setValidation(jobService.getValidationParameters());
			JSONUtil.toJSON( Paths.get(jobService.getPath(), PARAMETERS_FILE), parameters);

			JSONUtil.toJSON( Paths.get(jobService.getPath(), ACTION_PARAMETERS_FILE), jobService.getActionParameters());

			if ( jobService.getActionDataInputStream()!=null)
				Files.copy(jobService.getActionDataInputStream(), Paths.get( jobService.getPath(), jobService.getFilename()));

			if ( jobService.getValidationParameters()!=null)
				JSONUtil.toJSON( Paths.get(jobService.getPath(), VALIDATION_PARAMETERS_FILE), jobService.getValidationParameters());
		}
	}

	private java.nio.file.Path getJobDataDirectory(JobService jobService) {
		return Paths.get(jobService.getPath());
	}

	private void validateParams(String referential, String action, String type, Map<String, InputStream> inputStreamsByName) throws ServiceException {
		if (!schemaDAO.getSchemaListing().contains(referential)) {
			throw new RequestServiceException(RequestExceptionCode.UNKNOWN_REFERENTIAL, "");
		}
		if (!commandExists(action, type)) {
			throw new RequestServiceException(RequestExceptionCode.UNKNOWN_ACTION, "");
		}
		if (!inputStreamsByName.containsKey(PARAMETERS_FILE)) {
			throw new RequestServiceException(RequestExceptionCode.MISSING_PARAMETERS, "");
		}

		// TODO controler inputStreams par la Commande impossible ici car il faut extraire Parameters.json

		//		if (!checkCommandInputs(action, type,inputStreamsByName)) {
		//			throw new RequestServiceException(RequestExceptionCode.ACTION_TYPE_MISMATCH, "");
		//		}
	}

	public void download() {

	}

	public void jobs() {

	}

	public void scheduledJob() {

	}

	public void terminatedJob() {

	}


	public boolean commandExists(String action, String type) {
		try {
			Class.forName(getCommandName(action, type));
		} catch (ClassNotFoundException e) {
			return false;
		}
		return true;
	}

	/**
	 * find next waiting job on referential <br/>
	 * return null if a job is STARTED or if no job is SCHEDULED
	 *
	 * @param referential
	 * @return
	 */
	// @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public JobService getNextJob(String referential) {
		Job job = jobDAO.getNextJob(referential);
		if (job == null) {
			return null;
		}
		return new JobService(job);
	}

	public void start(JobService jobService) {
		jobService.setStatus(STATUS.STARTED);
		jobService.setUpdated(new Date());
		jobService.addLink(MediaType.APPLICATION_JSON, Link.REPORT_REL);
		jobService.addLink(MediaType.APPLICATION_JSON, Link.VALIDATION_REL);
		jobDAO.update(jobService.getJob());
	}

	// @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void cancel(String referential, Long id) throws ServiceException {
		JobService jobService = getJobService(referential,id);
		if (jobService.getStatus().ordinal() <= STATUS.STARTED.ordinal()) {

			if (jobService.getStatus().equals(STATUS.STARTED)) 
			{
				scheduler.cancel(jobService);
			}

			jobService.setStatus(STATUS.CANCELED);

			// remove cancel link only
			jobService.removeLink(Link.CANCEL_REL);
			// set delete link
			jobService.addLink(MediaType.APPLICATION_JSON, Link.DELETE_REL);

			jobService.setUpdated(new Date());
			jobDAO.update(jobService.getJob());

		}

	}

	// @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void remove(String referential, Long id) throws ServiceException {
		JobService jobService = getJobService(referential,id);
		if (jobService.getStatus().ordinal() <= STATUS.STARTED.ordinal()) {
			throw new RequestServiceException(RequestExceptionCode.SCHEDULED_JOB, "referential = "+referential+" ,id = "+id);
		}
		java.nio.file.Path dir = getJobDataDirectory(jobService);
		try {
			FileUtils.deleteDirectory(dir.toFile());
		} catch (IOException e) {
			log.error("fail to delete directory",e);
		}

		jobDAO.delete(jobService.getJob());
	}

	// @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void drop(String referential) throws ServiceException
	{
		List<JobService> jobServices = findAll(referential);
		// supprimer en premier les jobs en attente, puis les autres
		for (Iterator<JobService> iterator = jobServices.iterator(); iterator.hasNext();) {
			JobService jobService = iterator.next();
			if (jobService.getStatus().equals(STATUS.SCHEDULED))
			{
				jobDAO.delete(jobService.getJob());
				java.nio.file.Path dir = getJobDataDirectory(jobService);
				try {
					FileUtils.deleteDirectory(dir.toFile());
				} catch (IOException e) {
					log.error("fail to delete directory",e);
				}
				iterator.remove();
			}
		}
		for (JobService jobService : jobServices) {
			if (jobService.getStatus().equals(STATUS.STARTED))
			{
				scheduler.cancel(jobService);
			}
			java.nio.file.Path dir = getJobDataDirectory(jobService);
			try {
				FileUtils.deleteDirectory(dir.toFile());
			} catch (IOException e) {
				log.error("fail to delete directory",e);
			}
			jobDAO.delete(jobService.getJob());
		}

	}


	public void terminate(JobService jobService) {
		jobService.setStatus(STATUS.TERMINATED);

		// remove cancel link only
		jobService.removeLink(Link.CANCEL_REL);
		// set delete link
		jobService.addLink(MediaType.APPLICATION_JSON, Link.DELETE_REL);
		// add data link if necessary
		if (!jobService.linkExists(Link.DATA_REL))
		{
			if (jobService.getFilename() != null && Files.exists(Paths.get(jobService.getPath(),jobService.getFilename())))
			{
				jobService.addLink(MediaType.APPLICATION_OCTET_STREAM, Link.DATA_REL);
			}
		}

		jobService.setUpdated(new Date());
		jobDAO.update(jobService.getJob());


	}

	public void abort(JobService jobService) {

		jobService.setStatus(STATUS.ABORTED);

		// remove cancel link only
		jobService.removeLink(Link.CANCEL_REL);
		// set delete link
		jobService.addLink(MediaType.APPLICATION_JSON, Link.DELETE_REL);

		jobService.setUpdated(new Date());
		jobDAO.update(jobService.getJob());

	}


	public JobService getJobService(Long id) {
		Job job = jobDAO.find(id);
		if (job != null) {
			return new JobService(job);
		}
		return null;
	}

	public List<JobService> findAll() {
		List<Job> jobs = jobDAO.findAll();
		List<JobService> jobServices = new ArrayList<>(jobs.size());
		for (Job job : jobs) {
			jobServices.add(new JobService(job));
		}
		return jobServices;
	}

	public List<JobService> findAll(String referential) {
		List<Job> jobs  = jobDAO.findByReferential(referential);
		List<JobService> jobServices = new ArrayList<>(jobs.size());
		for (Job job : jobs) {
			jobServices.add(new JobService(job));
		}
		return jobServices;
	}


	public JobService getJobService(String referential, Long id) throws ServiceException
	{
		Job job = jobDAO.find(id);
		if (job != null && job.getReferential().equals(referential)) return new JobService(job);
		throw new RequestServiceException(RequestExceptionCode.UNKNOWN_JOB, "referential = "+referential+" ,id = "+id);
	}

	public static String getCommandName(String action, String type) {
		type = type == null ? "" : type;
		return "mobi.chouette.exchange."
		+ (type.isEmpty() ? "" : type + ".") + action + "."
		+ StringUtils.capitalize(type)
		+ StringUtils.capitalize(action) + "Command";
	}

	public static String getCommandInputValidatorName(String action, String type) {
		type = type == null ? "" : type;
		return "mobi.chouette.exchange."
		+ (type.isEmpty() ? "" : type + ".") + action + "."
		+ StringUtils.capitalize(type)
		+ StringUtils.capitalize(action) + "InputValidator";
	}

	private void validateInputs(JobService jobService) throws ServiceException 
	{
		try
		{
			InputValidator validator = InputValidatorFactory.create(getCommandInputValidatorName(jobService.getAction(), jobService.getType()));
			Class.forName( getCommandInputValidatorName(jobService.getAction(), jobService.getType()));
			if (!validator.check(jobService.getActionParameters(), jobService.getValidationParameters(), jobService.getFilename()))
			{
				throw new RequestServiceException(RequestExceptionCode.ACTION_TYPE_MISMATCH, "");
			}
		} catch (ServiceException e) 
		{
			throw e;
		}
		catch (Exception e) 
		{
			throw new ServiceException(ServiceExceptionCode.INTERNAL_ERROR, e.getMessage());
		}

		return;
	} 
}
