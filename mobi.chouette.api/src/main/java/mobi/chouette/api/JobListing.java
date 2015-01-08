package mobi.chouette.api;

import java.util.Collection;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;
import mobi.chouette.model.Job;

@Data
@XmlRootElement
public class JobListing {

	@XmlElement(name = "jobs")
	private Collection<Job> list;

}
