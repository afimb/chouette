package mobi.chouette.exchange.netexprofile.exporter;

import lombok.Getter;
import lombok.Setter;
import mobi.chouette.exchange.parameters.AbstractExportParameter;

import javax.xml.bind.annotation.*;


@XmlRootElement(name = "netex-export")
@XmlType(propOrder={"projectionType"})
@XmlAccessorType(XmlAccessType.FIELD)
public class NetexExportParameters  extends AbstractExportParameter{
	
	@Getter @Setter
	@XmlElement(name = "projection_type")
	private String projectionType;


}
