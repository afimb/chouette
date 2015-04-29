package mobi.chouette.api;

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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import lombok.Data;
import lombok.NoArgsConstructor;
import static mobi.chouette.common.Constant.ACTION_PARAMETERS_FILE;
import static mobi.chouette.common.Constant.PARAMETERS_FILE;
import static mobi.chouette.common.Constant.REPORT_FILE;
import static mobi.chouette.common.Constant.ROOT_PATH;
import static mobi.chouette.common.Constant.VALIDATION_FILE;
import static mobi.chouette.common.Constant.VALIDATION_PARAMETERS_FILE;
import mobi.chouette.exchange.gtfs.exporter.GtfsExportParameters;
import mobi.chouette.exchange.gtfs.importer.GtfsImportParameters;
import mobi.chouette.exchange.hub.exporter.HubExportParameters;
import mobi.chouette.exchange.kml.exporter.KmlExportParameters;
import mobi.chouette.exchange.neptune.exporter.NeptuneExportParameters;
import mobi.chouette.exchange.neptune.importer.NeptuneImportParameters;
import mobi.chouette.exchange.netex.exporter.NetexExportParameters;
import mobi.chouette.exchange.netex.importer.NetexImportParameters;
import mobi.chouette.exchange.parameters.AbstractParameter;
import mobi.chouette.exchange.validator.ValidateParameters;
import mobi.chouette.model.api.Job;
import mobi.chouette.model.api.Link;
import static mobi.chouette.model.api.Link.CANCEL_REL;
import static mobi.chouette.model.api.Link.DELETE_REL;
import static mobi.chouette.model.api.Link.LOCATION_REL;
import mobi.chouette.service.JobService;
import static mobi.chouette.service.ServiceConstants.ACTION_PARAMETERS_REL;
import static mobi.chouette.service.ServiceConstants.DATA_REL;
import static mobi.chouette.service.ServiceConstants.PARAMETERS_REL;
import static mobi.chouette.service.ServiceConstants.REPORT_REL;
import static mobi.chouette.service.ServiceConstants.VALIDATION_PARAMETERS_REL;
import static mobi.chouette.service.ServiceConstants.VALIDATION_REL;
import mobi.chouette.service.ServiceException;

@Data
@NoArgsConstructor
@XmlRootElement(name="job")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder={"id","referential","action","type","created","updated","status","linkInfos","actionParameters"})
@XmlSeeAlso({NeptuneExportParameters.class,
	         NeptuneImportParameters.class,
	         GtfsImportParameters.class,
	         GtfsExportParameters.class,
	         NetexImportParameters.class,
	         NetexExportParameters.class,
	         HubExportParameters.class,
	         KmlExportParameters.class,
	         ValidateParameters.class})
public class JobInfo {

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

	@XmlElement(name = "updated")
	private Date updated;

	@XmlElement(name = "status", required = true)
	private STATUS status;
	
	@XmlElement(name = "links")
	private List<LinkInfo> linkInfos;
	
	@XmlElementRef(name="action_parameters")
	private AbstractParameter actionParameters;
	
	public JobInfo( JobService job, boolean addLink, UriInfo uriInfo) throws ServiceException
	{
		id = job.getId();
		referential = job.getReferential();
		action = job.getAction();
		type = job.getType();
		created = job.getCreated();
		updated = job.getUpdated();
		status = STATUS.valueOf(job.getStatus().name());
                actionParameters = job.getActionParameter();
		if (addLink)
		{
			linkInfos = new ArrayList<>();
			for (Link link : job.getJob().getLinks()) 
			{
                            link.setHref( getRelHref( link.getRel(), job));
                            link.setMethod( getMethod( link.getRel(), job));
                            linkInfos.add( new LinkInfo( link, uriInfo));
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

        private String getRelHref( String rel, JobService jobService) {
            if ( rel.equals( PARAMETERS_REL)) {
                return getFileBaseHref()+ "/"+ PARAMETERS_FILE;
            } else if ( rel.equals( ACTION_PARAMETERS_REL)) {
                return getFileBaseHref()+ "/" + ACTION_PARAMETERS_FILE;
            } else if ( rel.equals( VALIDATION_PARAMETERS_REL)) {
                return getFileBaseHref()+ "/" + VALIDATION_PARAMETERS_FILE;
            } else if ( rel.equals( DATA_REL)) {
                return getFileBaseHref()+ "/" + jobService.getFilename();
            } else if ( rel.equals( VALIDATION_REL)) {
                return getFileBaseHref()+ "/" + VALIDATION_FILE;
            } else if ( rel.equals( REPORT_REL)) {
                return getFileBaseHref()+ "/" + REPORT_FILE;
            } else if ( rel.equals( CANCEL_REL)) {
                return getScheduledJobHref();
            } else if ( rel.equals( DELETE_REL)) {
                return getTerminatedJobHref();
            } else if ( rel.equals( LOCATION_REL) && hasTerminatedState( jobService)) {
                return getTerminatedJobHref();
            } else if ( rel.equals( LOCATION_REL) && !hasTerminatedState( jobService)) {
                return getScheduledJobHref();
            }
            return null;
        }
        
        private boolean hasTerminatedState( JobService jobService) {
            return terminatedStates().contains( jobService.getStatus());
        }
        
        private Set<Job.STATUS> terminatedStates() {
            Set<Job.STATUS> set = new HashSet<Job.STATUS>();
            set.add(Job.STATUS.TERMINATED);
            set.add(Job.STATUS.DELETED);
            return set;
        } 
        private String getMethod( String rel, JobService jobService) {
            if ( rel.equals( PARAMETERS_REL)) {
                return Link.GET_METHOD;
            } else if ( rel.equals( ACTION_PARAMETERS_REL)) {
                return Link.GET_METHOD;
            } else if ( rel.equals( VALIDATION_PARAMETERS_REL)) {
                return Link.GET_METHOD;
            } else if ( rel.equals( DATA_REL)) {
                return Link.GET_METHOD;
            } else if ( rel.equals( VALIDATION_REL)) {
                return Link.GET_METHOD;
            } else if ( rel.equals( REPORT_REL)) {
                return Link.GET_METHOD;
            } else if ( rel.equals( CANCEL_REL)) {
                return Link.DELETE_METHOD;
            } else if ( rel.equals( DELETE_REL)) {
                return Link.DELETE_METHOD;
            } else if ( rel.equals( LOCATION_REL)) {
                return Link.GET_METHOD;
            } 
            return null;
        }


	@XmlType(name="jobStatus")
	@XmlEnum(String.class)
	public enum STATUS implements java.io.Serializable {
		SCHEDULED, STARTED, TERMINATED, CANCELED, ABORTED
	}

}
