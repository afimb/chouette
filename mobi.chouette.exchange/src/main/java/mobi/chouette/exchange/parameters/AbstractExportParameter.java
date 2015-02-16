package mobi.chouette.exchange.parameters;

import java.sql.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.NoArgsConstructor;
import lombok.ToString;


@NoArgsConstructor
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
public class AbstractExportParameter extends AbstractParameter{

	@XmlElement(name = "references_type", required=true)
	private String referencesType;
	
	@XmlElement(name = "ids")
	private List<Integer> ids;
	
	@XmlElement(name = "start_date")
	private Date startDate;
	
	@XmlElement(name = "end_date")
	private Date endDate;

}
