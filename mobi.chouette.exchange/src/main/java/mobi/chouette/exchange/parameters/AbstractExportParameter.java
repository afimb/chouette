package mobi.chouette.exchange.parameters;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@NoArgsConstructor
@ToString(callSuper=true)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder={},name="actionExportParameter")
public class AbstractExportParameter extends AbstractParameter{

	@Getter @Setter
	@XmlElement(name = "references_type", required=true)
	private String referencesType;
	
	@Getter @Setter
	@XmlElement(name = "id")
	private List<Long> ids;
	
	@Getter @Setter
	@XmlElement(name = "start_date")
	private Date startDate;
	
	@Getter @Setter
	@XmlElement(name = "end_date")
	private Date endDate;

	@Getter @Setter
	@XmlElement(name = "add_metadata", defaultValue="false")
	private boolean addMetadata = false;

	
}
