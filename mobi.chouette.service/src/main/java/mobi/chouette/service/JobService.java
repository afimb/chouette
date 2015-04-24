package mobi.chouette.service;

import java.io.InputStream;
import java.io.StringWriter;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.MediaType;

import lombok.Data;
import lombok.experimental.Delegate;
import mobi.chouette.common.JSONUtil;
import mobi.chouette.model.api.Job;
import mobi.chouette.model.api.Link;
import mobi.chouette.scheduler.Parameters;

import org.apache.commons.io.IOUtils;

@Data
public class JobService implements ServiceConstants {

    @Delegate(types = {Job.class}, excludes = {ExcludedJobMethods.class})
    private Job job;

    @Delegate(types = {FileResourceProperties.class})
    private FileResourceProperties fileResourceProperties;

    /**
     * create a jobService on existing job
     *
     * @param job
     */
    public JobService(Job job) {
        this.job = job;
    }

    /**
     * create a new jobService
     *
     * @param referential : referential
     * @param action : action
     * @param type : type (may be null)
     * @param inputStreamsByName : inputStream hash for action including
     * parameters and data
     */
    public JobService(String referential, String action, String type, Map<String, InputStream> inputStreamsByName) throws ServiceException {
        job = new Job(referential, action, type);

        addLink(MediaType.APPLICATION_JSON, PARAMETERS_REL);
        
        if ( dataInputStream( inputStreamsByName)!=null) {
            addLink(MediaType.APPLICATION_OCTET_STREAM, DATA_REL);
            job.setFilename( dataFileName( inputStreamsByName.keySet()));
        }

        // valider et conserver sous forme de String le part "paraemeters.json"
        // à découper en paramètres action et validation
        Parameters parameters = readParameters(inputStreamsByName.get(PARAMETERS_FILE));

        fileResourceProperties = new FileResourceProperties(
                parameters.getConfiguration(),
                parameters.getValidation(),
                dataInputStream(inputStreamsByName));
    }

    public boolean hasFileResourceProperties() {
        return fileResourceProperties != null;
    }

    private Parameters readParameters(InputStream parameterInputStream) throws RequestServiceException {
        try {
            StringWriter writer = new StringWriter();
            IOUtils.copy(parameterInputStream, writer, "UTF-8");
            return JSONUtil.fromJSON(writer.toString(), Parameters.class);
        } catch (Exception ex) {
            throw new RequestServiceException(RequestExceptionCode.INVALID_PARAMETERS, ex);
        }
    }

    private InputStream dataInputStream(Map<String, InputStream> inputStreamsByName) {
        for (String name : inputStreamsByName.keySet()) {
            if (!name.equals(PARAMETERS_FILE)) {
                return inputStreamsByName.get(name);
            }
        }
        return null;
    }

    private String dataFileName(Set<String> inputNames) {
        for (String name : inputNames) {
            if (!name.equals(PARAMETERS_FILE)) {
                return name;
            }
        }
        return null;
    }

    /**
     * return job file path <br/>
     * build it if not set and job saved
     *
     * @return path or null if job not saved
     */
    public String getPath() {
        if (job.getPath() == null && job.getId() != null) {
            String path = Paths.get(System.getProperty("user.home"), ROOT_PATH, getReferential(), "data",
                    getId().toString()).toString();
            job.setPath(path);
        }
        return job.getPath(); // TODO choisir si on retourne null ou une
        // exception
    }

    /**
     * add a link if not already present
     *
     * @param mediaType : mime type
     * @param rel : link key
     */
    public void addLink(String mediaType, String rel) {
        if (!linkExists(rel)) {
            Link link = new Link(mediaType, rel, "", ""); // TODO réduire la
            // signature à 2
            // args
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

}
