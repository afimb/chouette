package mobi.chouette.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import mobi.chouette.exchange.gtfs.exporter.GtfsExportParameters;
import mobi.chouette.exchange.gtfs.importer.GtfsImportParameters;
import mobi.chouette.exchange.neptune.exporter.NeptuneExportParameters;
import mobi.chouette.exchange.neptune.importer.NeptuneImportParameters;
import mobi.chouette.exchange.netex.exporter.NetexExportParameters;
import mobi.chouette.exchange.netex.importer.NetexImportParameters;
import mobi.chouette.exchange.parameters.AbstractParameter;
import mobi.chouette.exchange.validator.ValidateParameters;
import mobi.chouette.model.api.Job;
import mobi.chouette.model.api.Link;

@Data
@NoArgsConstructor
@XmlRootElement(name="job")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder={"id","referential","action","type","created","updated","status","links","action_parameters"})
@XmlSeeAlso({NeptuneExportParameters.class,
	         NeptuneImportParameters.class,
	         GtfsImportParameters.class,
	         GtfsExportParameters.class,
	         NetexImportParameters.class,
	         NetexExportParameters.class,
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
	
	public JobInfo(Job job,boolean addLink)
	{
		id = job.getId();
		referential = job.getReferential();
		action = job.getAction();
		type = job.getType();
		created = job.getCreated();
		updated = job.getUpdated();
		status = STATUS.valueOf(job.getStatus().name());
		if (addLink)
		{
			linkInfos = new ArrayList<>();
			for (Link link : job.getLinks()) 
			{
				linkInfos.add(new LinkInfo(link));
			}
		}
	}

	@XmlType(name="jobStatus")
	@XmlEnum(String.class)
	public enum STATUS implements java.io.Serializable {
		SCHEDULED, STARTED, TERMINATED, CANCELED, ABORTED
	}

}
