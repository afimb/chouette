package mobi.chouette.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Data;
import lombok.NoArgsConstructor;
import mobi.chouette.exchange.parameters.AbstractParameter;
import mobi.chouette.model.api.Job;
import mobi.chouette.model.api.Link;

@Data
@NoArgsConstructor
@XmlRootElement(name="job")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder={})

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
	private String status;
	
	@XmlElement(name = "links")
	private List<LinkInfo> linkInfos;
	
	@XmlAnyElement
	private AbstractParameter action_parameters;
	
	public JobInfo(Job job,boolean addLink)
	{
		id = job.getId();
		referential = job.getReferential();
		action = job.getAction();
		type = job.getType();
		created = job.getCreated();
		updated = job.getUpdated();
		status = job.getStatus().name();
		if (addLink)
		{
			linkInfos = new ArrayList<>();
			for (Link link : job.getLinks()) 
			{
				linkInfos.add(new LinkInfo(link));
			}
		}
	}

}
