package mobi.chouette.exchange.gtfs.exporter;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.Setter;
import mobi.chouette.parameters.AbstractParameter;

@XmlRootElement(name = "")
@XmlType (name= "gtfsExport")
public class Parameters  extends AbstractParameter {
	
	@Getter
	@Setter
	@XmlElement(name = "references_type")
	private String referencesType;
	
	@Getter
	@Setter
	@XmlElement(name = "ids")
	private List<Integer> ids;
	
	@XmlAttribute(name = "start_date")
	private Date startDate;
	
	@XmlAttribute(name = "end_date")
	private Date endDate;
	
	@XmlAttribute(name = "time_zone")
	private String timeZone;
	
	@XmlAttribute(name = "object_id_prefix")
	private String objectIdPrefix;

}
