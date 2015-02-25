package mobi.chouette.exchange.parameters;

import java.sql.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@NoArgsConstructor
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
public class AbstractExportParameter extends AbstractParameter{

	@Getter @Setter
	@XmlElement(name = "references_type", required=true)
	private String referencesType;
	
	@Getter @Setter
	@XmlElement(name = "ids")
	private List<Integer> ids;
	
	@Getter @Setter
	@XmlElement(name = "start_date")
	private Date startDate;
	
	@Getter @Setter
	@XmlElement(name = "end_date")
	private Date endDate;

}
