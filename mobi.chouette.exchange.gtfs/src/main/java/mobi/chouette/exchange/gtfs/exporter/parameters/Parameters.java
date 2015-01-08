package mobi.chouette.exchange.gtfs.exporter.parameters;

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
	
	@XmlAttribute(name = "time_zone")
	private Date timeZone;
	
	@XmlAttribute(name = "object_id_prefix")
	private String objectIdPrefix;

}
