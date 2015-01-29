package mobi.chouette.exchange.netex.exporter;

import java.sql.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;
import mobi.chouette.parameters.AbstractParameter;

@XmlRootElement(name = "")
public class Parameters  extends AbstractParameter{
	
	@Getter
	@Setter
	@XmlElement(name = "references_type")
	private String referencesType;
	
	@Getter
	@Setter
	@XmlElement(name = "ids")
	private List<Integer> ids;
	
	@Getter
	@Setter
	@XmlElement(name = "start_date")
	private Date startDate;
	
	@Getter
	@Setter
	@XmlElement(name = "end_date")
	private Date endDate;
	
	@Getter
	@Setter
	@XmlElement(name = "projection_type")
	private String projectionType;


}
