package mobi.chouette.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import lombok.Data;
import lombok.experimental.Delegate;
import static mobi.chouette.common.Constant.PARAMETERS_FILE;
import mobi.chouette.common.JSONUtil;
import mobi.chouette.exchange.parameters.AbstractParameter;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;
import mobi.chouette.model.api.Job;
import mobi.chouette.model.api.Link;
import mobi.chouette.scheduler.Parameters;
import static mobi.chouette.service.ServiceConstants.DATA_REL;
import static mobi.chouette.service.ServiceConstants.DUPPLICATE_DATA;
import static mobi.chouette.service.ServiceConstants.DUPPLICATE_PARAMETERS;
import static mobi.chouette.service.ServiceConstants.PARAMETERS_REL;
import org.apache.commons.io.IOUtils;
import org.codehaus.jettison.json.JSONException;

@Data
public class JobService implements ServiceConstants {

	@Delegate(types = { Job.class }, excludes = { ExcludedJobMethods.class })
	private Job job;

        private AbstractParameter actionParameters;
        private ValidationParameters validationParameters;
	
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
	 * @param inputStreamsByName : inputStream hash for action including parameters and data
	 */
        public JobService(String referential, String action, String type, Map<String, InputStream> inputStreamsByName) throws ServiceException {
            job = new Job(referential, action, type);

            if (!checkCommand(action, type)) {
                throw new WebApplicationException("unknown action or type", Response.Status.BAD_REQUEST);
            }
            
            for (Map.Entry<String, InputStream> entry : inputStreamsByName.entrySet()) {
                String name = entry.getKey();
                InputStream stream = entry.getValue();
                addPart( name, stream);
            }

            // valider et conserver sous forme de String le part "paraemeters.json"
            // à découper en paramètres action et validation
            Parameters parameters = readParameters( inputStreamsByName.get( PARAMETERS_FILE));
            actionParameters = parameters.getConfiguration();
            validationParameters = parameters.getValidation();
        }
        
        private Parameters readParameters( InputStream parameterInputStream) throws RequestServiceException {
            try {
                StringWriter writer = new StringWriter();
                IOUtils.copy(parameterInputStream, writer, "UTF-8");
                return JSONUtil.fromJSON(writer.toString(), Parameters.class);
            }  catch ( Exception ex) {
                throw new RequestServiceException( RequestExceptionCode.INVALID_PARAMETERS, ex);
            }
        }
        
    private void addPart( String name, InputStream stream) throws ServiceException {
        if (name.equals(PARAMETERS_FILE)) {
            addParameterPart( stream);
        } else {
            addDataPart( stream);
        }
    }

    private void addDataPart( InputStream stream) throws ServiceException {
        if (linkExists(DATA_REL)) {
            throw new RequestServiceException( RequestExceptionCode.DUPPLICATE_DATA, "");

        }
        // add link
        addLink(MediaType.APPLICATION_OCTET_STREAM, DATA_REL);
    }

    private void addParameterPart( InputStream stream) throws ServiceException {
        if (linkExists(PARAMETERS_REL)) {
            throw new RequestServiceException( RequestExceptionCode.DUPPLICATE_PARAMETERS, "");
        }
        // add link
        addLink(MediaType.APPLICATION_JSON, PARAMETERS_REL);

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
			if (link.getRel().equals(rel))
				return true;
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
        
	private boolean checkCommand(String action, String type) {
		try {
			Class.forName( JobServiceManager.getCommandName(action, type));
		} catch (ClassNotFoundException e) {
			return false;
		}
		return true;
	}        

}
