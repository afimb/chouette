package mobi.chouette.exchange.netex.exporter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.Setter;
import mobi.chouette.exchange.parameters.AbstractExportParameter;


@XmlRootElement(name = "netex-export")
@XmlType(propOrder={"projectionType"})
@XmlAccessorType(XmlAccessType.FIELD)
public class NetexExportParameters  extends AbstractExportParameter{
	
	@Getter @Setter
	@XmlElement(name = "projection_type")
	private String projectionType;


}
