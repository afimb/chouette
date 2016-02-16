package mobi.chouette.service;

import java.io.InputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.MediaType;

import lombok.Data;
import lombok.experimental.Delegate;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.JSONUtil;
import mobi.chouette.common.JobData;
import mobi.chouette.common.PropertyNames;
import mobi.chouette.exchange.InputValidator;
import mobi.chouette.exchange.InputValidatorFactory;
import mobi.chouette.exchange.parameters.AbstractParameter;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;
import mobi.chouette.model.iev.Job;
import mobi.chouette.model.iev.Link;
import mobi.chouette.scheduler.Parameters;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

@Data
@Log4j
public class JobService implements JobData,ServiceConstants {

    @Delegate(types = {Job.class}, excludes = {ExcludedJobMethods.class})
    private Job job;
    
    /**
     * create a jobService on existing job
     *
     * @param job
     */
    public JobService(Job job) {
        this.job = job;
        
        // TODO Exception si le job n'est pas persistent
    }

    /**
     * create a new jobService
     *
     * @param referential : referential
     * @param action : action
     * @param type : type (may be null)
     * @throws mobi.chouette.service.ServiceException
     */
    public JobService(String referential, String action, String type) throws ServiceException {
        job = new Job(referential, action, type);

        if (!commandExists()) {
            throw new RequestServiceException(RequestExceptionCode.UNKNOWN_ACTION, "");
        }
    }

    /**
     * Read and save inputStreams as File
     * 
     * @param inputStreamsByName
     * @throws ServiceException : if inputStream not valid with job
     */
    public void saveInputStreams( final Map<String, InputStream> inputStreamsByName) throws ServiceException {
        try {
            if (!inputStreamsByName.containsKey(PARAMETERS_FILE)) {
                throw new RequestServiceException(RequestExceptionCode.MISSING_PARAMETERS, "");
            }
            
            StringWriter writer = new StringWriter();
            IOUtils.copy(inputStreamsByName.get(PARAMETERS_FILE), writer, "UTF-8");
            log.info("parameters = "+writer.toString());
            Parameters parameters = JSONUtil.fromJSON( writer.toString(), Parameters.class);
            setParametersAsString(JSONUtil.toJSON(parameters));
            log.info("saved parameters = "+getParametersAsString());
            JSONUtil.toJSON( filePath(PARAMETERS_FILE), parameters);
            addLink(MediaType.APPLICATION_JSON, PARAMETERS_REL);
            
            JSONUtil.toJSON( filePath(ACTION_PARAMETERS_FILE), parameters.getConfiguration());
            addLink(MediaType.APPLICATION_JSON, ACTION_PARAMETERS_REL);
            
            if ( parameters.getValidation()!=null) {
                JSONUtil.toJSON( filePath(VALIDATION_PARAMETERS_FILE), parameters.getValidation());
                addLink(MediaType.APPLICATION_JSON, VALIDATION_PARAMETERS_REL);
            }
            
            String inputStreamName = selectDataInputStreamName( inputStreamsByName);
            if ( inputStreamName!=null) {
                Files.copy( inputStreamsByName.get( inputStreamName), filePath( inputStreamName));
                addLink(MediaType.APPLICATION_OCTET_STREAM, DATA_REL);
                job.setFilename( inputStreamName);
            }
            
            InputValidator validator = InputValidatorFactory.create( getCommandInputValidatorName());
            Class.forName(getCommandInputValidatorName());
            if (!validator.checkParameters( parameters.getConfiguration(), parameters.getValidation())) 
                throw new RequestServiceException(RequestExceptionCode.INVALID_PARAMETERS, "");
            if (!validator.checkFilename( job.getFilename())) 
                throw new RequestServiceException(RequestExceptionCode.INVALID_FILE_FORMAT, "");
            validator.initReport(this);
            setStatus(Job.STATUS.SCHEDULED); // job is ready
            
        } catch ( ServiceException ex) {
            throw ex;
        } catch ( Exception ex) {
            Logger.getLogger(JobService.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            throw new RequestServiceException( RequestExceptionCode.INVALID_PARAMETERS, ex);
        }

    }
    
    private String selectDataInputStreamName( final Map<String, InputStream> inputStreamsByName) {
        for ( String name : inputStreamsByName.keySet()) {
            if ( ! name.equals(PARAMETERS_FILE)) {
                return name;
            }
        }
        return null;
    }
    
    private boolean jobPersisted() {
        return job.getId()!=null;
    }
    
    private java.nio.file.Path filePath( final String fileName) {
        return Paths.get( getPathName(), fileName);
    }

    private Parameters getParameters() throws ServiceException {
        if ( jobPersisted()) {
            try {
                return JSONUtil.fromJSON( getParametersAsString(), Parameters.class);
            } catch (Exception ex) {
                throw new RequestServiceException(RequestExceptionCode.INVALID_PARAMETERS, ex);
            }
        }
        return null;
    }
    
    public AbstractParameter getActionParameter()  throws ServiceException {
        if ( jobPersisted()) {
            return getParameters().getConfiguration();
        }
        return null;
    }
    
    public ValidationParameters getValidationParameter()  throws ServiceException {
        if ( jobPersisted()) {
            return getParameters().getValidation();
        }
        return null;
    }

    /**
     * return job file path <br/>
     * build it if not set and job saved
     *
     * @return path or null if job not saved
     */
    public String getPathName() {
        if ( jobPersisted()) {
            return Paths.get(System.getProperty(PropertyNames.ROOT_DIRECTORY), ROOT_PATH, job.getReferential(), "data",
                    job.getId().toString()).toString();
        }
        // TODO Non, lever une exception
        return null;
    }
    
    public static String getRootPathName(String referential)
    {
    	 return Paths.get(System.getProperty(PropertyNames.ROOT_DIRECTORY), ROOT_PATH, referential).toString();
    }
    
    public java.nio.file.Path getPath() {
        if ( jobPersisted()) {
            return java.nio.file.Paths.get( getPathName());
        }
        // TODO Non, lever une exception
        return null;
    }

    /**
     * add a link if not already present
     *
     * @param mediaType : mime type
     * @param rel : link key
     */
    public void addLink(String mediaType, String rel) {
        if (!linkExists(rel)) {
            Link link = new Link(mediaType, rel); 
            job.getLinks().add(link);
        }
    }

    /**
     * check link existence
     *
     * @param rel link key
     * @return
     */
    public boolean linkExists(String rel) {
        for (Link link : job.getLinks()) {
            if (link.getRel().equals(rel)) {
                return true;
            }
        }
        return false;
    }

    /**
     * remove a link if exists <br/>
     * does nothing if not
     *
     * @param rel link key
     */
    public void removeLink(String rel) {
        for (Iterator<Link> iterator = job.getLinks().iterator(); iterator.hasNext();) {
            Link link = iterator.next();
            if (link.getRel().equals(rel)) {
                iterator.remove();
                break;
            }
        }
    }

    private boolean commandExists() {
            try {
                    Class.forName(getCommandName());
            } catch (ClassNotFoundException e) {
                    return false;
            }
            return true;
    }

    public String getCommandName() {
            String type = getType() == null ? "" : getType();
            return "mobi.chouette.exchange."
            + (type.isEmpty() ? "" : type + ".") + getAction() + "."
            + StringUtils.capitalize(type)
            + StringUtils.capitalize( getAction()) + "Command";
    }

    private String getCommandInputValidatorName() {
        String type = getType() == null ? "" : getType();
        return "mobi.chouette.exchange."
                + (type.isEmpty() ? "" : type + ".") + getAction() + "."
                + StringUtils.capitalize(type)
                + StringUtils.capitalize(getAction()) + "InputValidator";
    }
}
