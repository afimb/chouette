package mobi.chouette.service;

import java.io.FileWriter;
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
import lombok.ToString;
import lombok.experimental.Delegate;
import mobi.chouette.common.JSONUtil;
import mobi.chouette.common.JobData;
import mobi.chouette.exchange.InputValidator;
import mobi.chouette.exchange.InputValidatorFactory;
import mobi.chouette.exchange.parameters.AbstractParameter;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;
import mobi.chouette.model.iev.Job;
import mobi.chouette.model.iev.Link;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

@Data
@ToString(exclude = { "inputValidator" })
public class JobService implements JobData, ServiceConstants {

	@Delegate(types = { Job.class }, excludes = { ExcludedJobMethods.class })
	private Job job;

	private String rootDirectory;

	private InputValidator inputValidator;

	/**
	 * create a jobService on existing job
	 * 
	 * @param job
	 */
	public JobService(String rootDirectory, Job job) {
		this.job = job;
		this.rootDirectory = rootDirectory;
		// TODO Exception si le job n'est pas persistent
	}

	/**
	 * create a new jobService
	 * 
	 * @param referential
	 *            : referential
	 * @param action
	 *            : action
	 * @param type
	 *            : type (may be null)
	 * @throws mobi.chouette.service.ServiceException
	 */
	public JobService(String rootDirectory, String referential, String action, String type) throws ServiceException {
		job = new Job(referential, action, type);
		this.rootDirectory = rootDirectory;

		if (!commandExists()) {
			throw new RequestServiceException(RequestExceptionCode.UNKNOWN_ACTION, "");
		}
	}

	/**
	 * Read and save inputStreams as File
	 * 
	 * @param inputStreamsByName
	 * @throws ServiceException
	 *             : if inputStream not valid with job
	 */
	public void saveInputStreams(final Map<String, InputStream> inputStreamsByName) throws ServiceException {
		try {
			if (!inputStreamsByName.containsKey(PARAMETERS_FILE)) {
				throw new RequestServiceException(RequestExceptionCode.MISSING_PARAMETERS, "");
			}

			StringWriter writer = new StringWriter();
			IOUtils.copy(inputStreamsByName.get(PARAMETERS_FILE), writer, "UTF-8");

			InputValidator validator = getCommandInputValidator();
			setParametersAsString(writer.toString());
			writer.close();
			Parameters parameters = new Parameters(getParametersAsString(), validator);

			FileWriter fwriter = new FileWriter(filePath(PARAMETERS_FILE).toFile());
			fwriter.write(getParametersAsString());
			fwriter.write("\n");
			fwriter.close();
			addLink(MediaType.APPLICATION_JSON, Link.PARAMETERS_REL);


			String inputStreamName = selectDataInputStreamName(inputStreamsByName);
			if (inputStreamName != null) {
				Files.copy(inputStreamsByName.get(inputStreamName), filePath(inputStreamName));
				addLink(MediaType.APPLICATION_OCTET_STREAM, Link.DATA_REL);
				addLink(MediaType.APPLICATION_OCTET_STREAM, Link.INPUT_REL);
				job.setInputFilename(inputStreamName);
			}

			// Class.forName(getCommandInputValidatorName());
			if (!validator.checkParameters(parameters.getConfiguration(), parameters.getValidation()))
				throw new RequestServiceException(RequestExceptionCode.INVALID_PARAMETERS, "");
			if (!validator.checkFilename(job.getInputFilename()))
				throw new RequestServiceException(RequestExceptionCode.INVALID_FILE_FORMAT, "");

			JSONUtil.toJSON(filePath(ACTION_PARAMETERS_FILE), parameters.getConfiguration());
			addLink(MediaType.APPLICATION_JSON, Link.ACTION_PARAMETERS_REL);

			if (parameters.getValidation() != null) {
				JSONUtil.toJSON(filePath(VALIDATION_PARAMETERS_FILE), parameters.getValidation());
				addLink(MediaType.APPLICATION_JSON, Link.VALIDATION_PARAMETERS_REL);
			}
			
			validator.initReport(this);
			setStatus(Job.STATUS.SCHEDULED); // job is ready

		} catch (ServiceException ex) {
			throw ex;
		} catch (Exception ex) {
			Logger.getLogger(JobService.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
			throw new RequestServiceException(RequestExceptionCode.INVALID_PARAMETERS, ex);
		}

	}

	private String selectDataInputStreamName(final Map<String, InputStream> inputStreamsByName) {
		for (String name : inputStreamsByName.keySet()) {
			if (!name.equals(PARAMETERS_FILE)) {
				return name;
			}
		}
		return null;
	}

	private boolean jobPersisted() {
		return job.getId() != null;
	}

	private java.nio.file.Path filePath(String fileName) {
		return Paths.get(getPathName(), fileName);
	}

	private Parameters getParameters() throws ServiceException {
		if (jobPersisted()) {
			try {
				return new Parameters(getParametersAsString(), getCommandInputValidator());
				// return JSONUtil.fromJSON( getParametersAsString(),
				// Parameters.class);
			} catch (Exception ex) {
				try {
					return new Parameters(getParametersAsString(), inputValidator);
				} catch (Exception e) {
					return null;
				}
				// throw new
				// RequestServiceException(RequestExceptionCode.INVALID_PARAMETERS,
				// ex);
			}
		}
		return null;
	}

	public AbstractParameter getActionParameter() throws ServiceException {
		if (jobPersisted()) {
			return getParameters().getConfiguration();
		}
		return null;
	}

	public ValidationParameters getValidationParameter() throws ServiceException {
		if (jobPersisted()) {
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
		if (jobPersisted()) {
			return Paths.get(rootDirectory, ROOT_PATH, job.getReferential(), "data", job.getId().toString()).toString();
		}
		// TODO Non, lever une exception
		return null;
	}

	public static String getRootPathName(String rootDirectory, String referential) {
		return Paths.get(rootDirectory, ROOT_PATH, referential).toString();
	}

	public java.nio.file.Path getPath() {
		if (jobPersisted()) {
			return java.nio.file.Paths.get(getPathName());
		}
		// TODO Non, lever une exception
		return null;
	}

	/**
	 * add a link or replace
	 * 
	 * @param mediaType
	 *            : mime type
	 * @param rel
	 *            : link key
	 */
	public void addLink(String mediaType, String rel) {
		linkRemove(rel);
		Link link = new Link(mediaType, rel);
		job.getLinks().add(link);

	}

	/**
	 * check link existence
	 * 
	 * @param rel
	 *            link key
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
	 * check link existence
	 * 
	 * @param rel
	 *            link key
	 * @return
	 */
	public Link linkRemove(String rel) {
		for (Iterator<Link> iterator = job.getLinks().iterator(); iterator.hasNext();) {
			Link link = iterator.next();
			if (link.getRel().equals(rel)) {
				iterator.remove();
				return link;
			}
		}
		return null;
	}

	/**
	 * remove a link if exists <br/>
	 * does nothing if not
	 * 
	 * @param rel
	 *            link key
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
		return "mobi.chouette.exchange." + (type.isEmpty() ? "" : type + ".") + getAction() + "."
				+ StringUtils.capitalize(type) + StringUtils.capitalize(getAction()) + "Command";
	}

	private InputValidator getCommandInputValidator() throws Exception {
		if (inputValidator == null) {
			String type = getType() == null ? "" : getType();
			inputValidator = InputValidatorFactory.create("mobi.chouette.exchange."
					+ (type.isEmpty() ? "" : type + ".") + getAction() + "." + StringUtils.capitalize(type)
					+ StringUtils.capitalize(getAction()) + "InputValidator");
		}
		return inputValidator;
	}
}
