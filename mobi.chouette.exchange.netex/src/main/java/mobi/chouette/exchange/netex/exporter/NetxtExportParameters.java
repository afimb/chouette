package mobi.chouette.exchange.netex.exporter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;
import mobi.chouette.exchange.parameters.AbstractExportParameter;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class NetxtExportParameters  extends AbstractExportParameter{
	
	@XmlElement(name = "projection_type")
	private String projectionType;


}
