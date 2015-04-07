package mobi.chouette.exchange.hub.exporter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.NoArgsConstructor;
import lombok.ToString;
import mobi.chouette.exchange.parameters.AbstractExportParameter;

@XmlRootElement(name = "hub-export")
@NoArgsConstructor
@ToString(callSuper = true)
@XmlAccessorType(XmlAccessType.FIELD)
// @XmlType(propOrder={})
public class HubExportParameters extends AbstractExportParameter {

}
