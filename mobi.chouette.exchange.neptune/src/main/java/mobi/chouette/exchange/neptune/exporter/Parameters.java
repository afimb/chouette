package mobi.chouette.exchange.neptune.exporter;

import java.sql.Date;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@Data
@XmlRootElement(name = "")
public class Parameters {
	
	@XmlAttribute(name = "start_date")
	private Date startDate;
	
	@XmlAttribute(name = "end_date")
	private Date endDate;
	
	@XmlAttribute(name = "projection_type")
	private String projectionType;

}
