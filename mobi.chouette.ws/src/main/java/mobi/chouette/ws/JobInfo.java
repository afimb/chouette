package mobi.chouette.ws;


import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Data;
import lombok.NoArgsConstructor;
import mobi.chouette.exchange.parameters.AbstractParameter;
import mobi.chouette.model.iev.Job;
import mobi.chouette.model.iev.Link;
import mobi.chouette.service.JobService;
import mobi.chouette.service.ServiceConstants;
import mobi.chouette.service.ServiceException;

@Data
@NoArgsConstructor
@XmlRootElement(name = "job")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "id", "referential", "action", "type", "created", "started", "updated", "status", "linkInfos",
		"actionParameters" })
// @XmlSeeAlso({NeptuneImportParameters.class,
// NeptuneExportParameters.class,
// GtfsImportParameters.class,
// GtfsExportParameters.class,
// NetexImportParameters.class,
// NetexExportParameters.class,
// HubExportParameters.class,
// KmlExportParameters.class,
// GeojsonExportParameters.class,
// SigExportParameters.class,
// ConvertParameters.class,
// ValidateParameters.class,
// NeptuneValidateParameters.class,
// GtfsValidateParameters.class,
// NetexValidateParameters.class})
public class JobInfo implements ServiceConstants {

	@XmlElement(name = "id", required = true)
	private Long id;

	@XmlElement(name = "referential", required = true)
	private String referential;

	@XmlElement(name = "action", required = true)
	private String action;

	@XmlElement(name = "type")
	private String type;

	@XmlElement(name = "created", required = true)
	private Date created;

	@XmlElement(name = "started")
	private Date started;

	@XmlElement(name = "updated")
	private Date updated;

	@XmlElement(name = "status", required = true)
	private STATUS status;

	@XmlElement(name = "links")
	private List<LinkInfo> linkInfos;

	@XmlElementRef(name = "action_parameters")
	private AbstractParameter actionParameters;

	public JobInfo(JobService job, boolean addLink, UriInfo uriInfo) throws ServiceException {
		id = job.getId();
		referential = job.getReferential();
		action = job.getAction();
		type = job.getType();
		created = job.getCreated();
		started = job.getStarted();
		updated = job.getUpdated();
		status = STATUS.valueOf(job.getStatus().name());

		actionParameters = job.getActionParameter();

		if (addLink) {
			linkInfos = new ArrayList<>();
			for (Link link : job.getJob().getLinks()) {
				link.setHref(getRelHref(link.getRel(), job));
				link.setMethod(getMethod(link.getRel(), job));
				linkInfos.add(new LinkInfo(link, uriInfo));
			}
		}
	}

	private String getFileBaseHref() {
		return MessageFormat.format("{0}/{1}/data/{2,number,#}", ROOT_PATH, referential, id);
	}

	private String getScheduledJobHref() {
		return MessageFormat.format("{0}/{1}/scheduled_jobs/{2,number,#}", ROOT_PATH, referential, id);
	}

	private String getTerminatedJobHref() {
		return MessageFormat.format("{0}/{1}/terminated_jobs/{2,number,#}", ROOT_PATH, referential, id);
	}

	private String getRelHref(String rel, JobService jobService) {
		if (rel.equals(Link.PARAMETERS_REL)) {
			return getFileBaseHref() + "/" + PARAMETERS_FILE;
		} else if (rel.equals(Link.ACTION_PARAMETERS_REL)) {
			return getFileBaseHref() + "/" + ACTION_PARAMETERS_FILE;
		} else if (rel.equals(Link.VALIDATION_PARAMETERS_REL)) {
			return getFileBaseHref() + "/" + VALIDATION_PARAMETERS_FILE;
		} else if (rel.equals(Link.DATA_REL) && action.equals("exporter")) {
			return getFileBaseHref() + "/" + jobService.getOutputFilename();
		} else if (rel.equals(Link.DATA_REL) && !action.equals("exporter")) {
			return getFileBaseHref() + "/" + jobService.getInputFilename();
		} else if (rel.equals(Link.INPUT_REL)) {
			return getFileBaseHref() + "/" + jobService.getInputFilename();
		} else if (rel.equals(Link.OUTPUT_REL)) {
			return getFileBaseHref() + "/" + jobService.getOutputFilename();
		} else if (rel.equals(Link.VALIDATION_REL)) {
			return getFileBaseHref() + "/" + VALIDATION_FILE;
		} else if (rel.equals(Link.REPORT_REL)) {
			return getFileBaseHref() + "/" + REPORT_FILE;
		} else if (rel.equals(Link.CANCEL_REL)) {
			return getScheduledJobHref();
		} else if (rel.equals(Link.DELETE_REL)) {
			return getTerminatedJobHref();
		} else if (rel.equals(Link.LOCATION_REL) && hasTerminatedState(jobService)) {
			return getTerminatedJobHref();
		} else if (rel.equals(Link.LOCATION_REL) && !hasTerminatedState(jobService)) {
			return getScheduledJobHref();
		}
		return null;
	}

	private boolean hasTerminatedState(JobService jobService) {
		return terminatedStates().contains(jobService.getStatus());
	}

	private Set<Job.STATUS> terminatedStates() {
		Set<Job.STATUS> set = new HashSet<Job.STATUS>();
		set.add(Job.STATUS.TERMINATED);
		set.add(Job.STATUS.DELETED);
		return set;
	}

	private String getMethod(String rel, JobService jobService) {
		if (rel.equals(Link.PARAMETERS_REL)) {
			return Link.GET_METHOD;
		} else if (rel.equals(Link.ACTION_PARAMETERS_REL)) {
			return Link.GET_METHOD;
		} else if (rel.equals(Link.VALIDATION_PARAMETERS_REL)) {
			return Link.GET_METHOD;
		} else if (rel.equals(Link.DATA_REL)) {
			return Link.GET_METHOD;
		} else if (rel.equals(Link.INPUT_REL)) {
			return Link.GET_METHOD;
		} else if (rel.equals(Link.OUTPUT_REL)) {
			return Link.GET_METHOD;
		} else if (rel.equals(Link.VALIDATION_REL)) {
			return Link.GET_METHOD;
		} else if (rel.equals(Link.REPORT_REL)) {
			return Link.GET_METHOD;
		} else if (rel.equals(Link.CANCEL_REL)) {
			return Link.DELETE_METHOD;
		} else if (rel.equals(Link.DELETE_REL)) {
			return Link.DELETE_METHOD;
		} else if (rel.equals(Link.LOCATION_REL)) {
			return Link.GET_METHOD;
		}
		return null;
	}

	@XmlType(name = "jobStatus")
	@XmlEnum(String.class)
	public enum STATUS implements java.io.Serializable {
		SCHEDULED, STARTED, TERMINATED, CANCELED, ABORTED
	}

}
