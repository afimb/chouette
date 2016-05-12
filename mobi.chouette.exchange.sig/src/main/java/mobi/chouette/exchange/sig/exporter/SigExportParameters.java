package mobi.chouette.exchange.sig.exporter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.NoArgsConstructor;
import mobi.chouette.exchange.parameters.AbstractExportParameter;

@XmlRootElement(name = "sig-export")
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
// @XmlType(propOrder = {  })
public class SigExportParameters extends AbstractExportParameter {


}
