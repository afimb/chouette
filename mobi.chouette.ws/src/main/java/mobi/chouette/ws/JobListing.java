package mobi.chouette.ws;

import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Data;

@Data
@XmlRootElement (name="job_listings")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder={"list"})
public class JobListing {

	@XmlElement(name = "jobs")
	private Collection<JobInfo> list;

}
