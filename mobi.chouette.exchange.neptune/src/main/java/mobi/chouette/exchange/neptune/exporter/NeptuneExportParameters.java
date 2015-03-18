package mobi.chouette.exchange.neptune.exporter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import mobi.chouette.exchange.parameters.AbstractExportParameter;

@XmlRootElement(name = "neptune-export")
@NoArgsConstructor
@ToString(callSuper = true)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder={"projectionType","addExtension"})
public class NeptuneExportParameters  extends AbstractExportParameter {
	
	@Getter @Setter
	@XmlElement(name = "projection_type")
	private String projectionType;
	
	@Getter @Setter
	@XmlElement(name = "add_extension")
	private boolean addExtension = false;
	

}
