package mobi.chouette.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import mobi.chouette.dao.JobDAO;
import mobi.chouette.dao.SchemaDAO;
import mobi.chouette.model.api.Job;

import org.apache.commons.lang.StringUtils;

@Singleton(name = JobServiceManager.BEAN_NAME)
public class JobServiceManager implements ServiceConstants {

	public static final String BEAN_NAME = "JobServiceManager";
	
    @Inject
    SchemaDAO schemas;

    @EJB
    JobDAO jobDAO;

    @EJB
    SchemaDAO schemaDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public JobService upload(String referential, String action, String type, Map<String, InputStream> parts) throws Exception {
        
        // Convertir les parametres fournies 
        type = parseType( type);

        // Valider les parametres
        validateParams( referential, action, type, parts);

        JobService jobService = new JobService(referential, action, type, parts);

        try {
            jobDAO.create(jobService.getJob());
            
            fileResourceSave( jobService);

            return jobService;
            
        } catch (Exception ex) {
            if (jobService != null && jobService.getJob().getId()!= null) {
                jobDAO.delete(jobService.getJob());
            }
            // remove path if exists

            throw ex;
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
    
    private String parseType( String type) {
        if (type != null && type.startsWith("/")) {
            return type.substring(1);
        }
        return type;
    }
    
    private void validateParams( String referential, String action, String type, Map<String, InputStream> parts) throws Exception {
        if (!schemaDAO.getSchemaListing().contains(referential)) {
            throw new Exception(UNKNOWN_REFERENTIAL);
        }
        if (!commandExists(action, type)) {
            throw new Exception(UNKNOWN_ACTION);
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

    }

    public void terminate(JobService jobService) {

    }

    public void abort(JobService jobService) {

    }

    public static String getCommandName(String action, String type) {
        type = type == null ? "" : type;

        return "mobi.chouette.exchange."
                + (type.isEmpty() ? "" : type + ".") + action + "."
                + StringUtils.capitalize(type)
                + StringUtils.capitalize(action) + "Command";
    }

}
