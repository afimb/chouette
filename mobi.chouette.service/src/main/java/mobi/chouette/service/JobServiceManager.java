package mobi.chouette.service;

import static mobi.chouette.common.Constant.PARAMETERS_FILE;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ws.rs.core.MediaType;

import mobi.chouette.dao.JobDAO;
import mobi.chouette.dao.SchemaDAO;
import mobi.chouette.model.api.Job;
import mobi.chouette.model.api.Job.STATUS;
import mobi.chouette.model.api.Link;
import mobi.chouette.model.util.JobUtil;
import mobi.chouette.scheduler.Scheduler;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

@Singleton(name = JobServiceManager.BEAN_NAME)
@Startup

public class JobServiceManager {

	public static final String BEAN_NAME = "JobServiceManager";
	
    @EJB
    JobDAO jobDAO;

    @EJB
    SchemaDAO schemaDAO;
    
    @EJB 
    Scheduler scheduler;

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public JobService upload(String referential, String action, String type, Map<String, InputStream> inputStreamsByName) throws ServiceException {

        // Valider les parametres
        validateParams( referential, action, type, inputStreamsByName);

        JobService jobService = new JobService(referential, action, type, inputStreamsByName);

        try {
            jobDAO.create(jobService.getJob());
            
            fileResourceSave( jobService);

            return jobService;
            
        } catch (Exception ex) {
            if (jobService != null && jobService.getJob().getId()!= null) {
                jobDAO.delete(jobService.getJob());
            }
            // remove path if exists

            throw new ServiceException(ServiceExceptionCode.INTERNAL_ERROR, ex) ;
        }
    }
    
    private void fileResourceSave(JobService jobService) throws IOException {
        // mkdir
        java.nio.file.Path dir = getJobDataDirectory(jobService);
        if (Files.exists(dir)) {
            jobDAO.delete(jobService.getJob());
        }
        Files.createDirectories(dir);
    }

    private java.nio.file.Path getJobDataDirectory(JobService jobService) {
        return Paths.get(jobService.getPath());
    }
    
    private void validateParams( String referential, String action, String type, Map<String, InputStream> inputStreamsByName) throws ServiceException {
        if (!schemaDAO.getSchemaListing().contains(referential)) {
            throw new RequestServiceException( RequestExceptionCode.UNKNOWN_REFERENTIAL, "");
        }
        if (!commandExists(action, type)) {
            throw new RequestServiceException( RequestExceptionCode.UNKNOWN_ACTION, "");
        }
        if ( !inputStreamsByName.containsKey( PARAMETERS_FILE)){
            throw new RequestServiceException( RequestExceptionCode.MISSING_PARAMETERS, "");
        }
    }

    public void download() {

    }

    public void jobs() {

    }

    public void scheduledJob() {

    }

    public void cancel() {

    }

    public void terminatedJob() {

    }

    public void remove() {

    }

    public void drop() {

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
		jobDAO.update(jobService.getJob());
    }

    public void terminate(JobService jobService) {

    }

    public void abort(JobService jobService) {
    	Job job = jobService.getJob();
		job.setStatus(STATUS.ABORTED);

		// remove location link
		Iterables.removeIf(job.getLinks(), new Predicate<Link>() {
			@Override
			public boolean apply(Link link) {
				return link.getRel().equals(Link.LOCATION_REL) || link.getRel().equals(Link.CANCEL_REL);
			}
		});

		// set delete link
		job.getLinks().clear();
		Link link = new Link();
		link.setType(MediaType.APPLICATION_JSON);
		link.setRel(Link.DELETE_REL);
		
		JobUtil.updateLink(job, link); //job.getLinks().add(link);
		link = new Link();
		link.setType(MediaType.APPLICATION_JSON);
		link.setRel(Link.LOCATION_REL);
		
		JobUtil.updateLink(job, link); //job.getLinks().add(link);

		job.setUpdated(new Date());
		jobDAO.update(job);

    }

	public List<JobService> findAll() {
		List<Job> jobs = jobDAO.findAll();
		List<JobService> jobServices = new ArrayList<>(jobs.size());
		for (Job job : jobs) {
			jobServices.add(new JobService(job));
		}
		return jobServices;
	}
	
	public JobService getJobService(Long id) {
		Job job = jobDAO.find(id);
		if (job != null) return new JobService(job);
		return null;
	}

    public static String getCommandName(String action, String type) {
        type = type == null ? "" : type;

        return "mobi.chouette.exchange."
                + (type.isEmpty() ? "" : type + ".") + action + "."
                + StringUtils.capitalize(type)
                + StringUtils.capitalize(action) + "Command";
    }

	


}
