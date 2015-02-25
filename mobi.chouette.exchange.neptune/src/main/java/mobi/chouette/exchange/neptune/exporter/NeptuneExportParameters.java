package mobi.chouette.exchange.neptune.exporter;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import mobi.chouette.exchange.parameters.AbstractParameter;

@NoArgsConstructor
@ToString(callSuper = true)
@XmlAccessorType(XmlAccessType.FIELD)
public class NeptuneExportParameters  extends AbstractParameter {
	
	@Getter @Setter
	@XmlElement(name = "references_type")
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
	
	@Getter @Setter
	@XmlElement(name = "projection_type")
	private String projectionType;
	
	@Getter @Setter
	@XmlElement(name = "add_extension")
	private boolean addExtension = false;
	
	@Getter @Setter
	@XmlElement(name = "add_metadata")
	private boolean addMetadata = false;


}
