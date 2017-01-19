package mobi.chouette.exchange.generic.importer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lombok.NoArgsConstructor;
import lombok.ToString;
import mobi.chouette.exchange.parameters.AbstractImportParameter;

@XmlRootElement(name = "generic-import")
@NoArgsConstructor
@ToString(callSuper = true)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class GenericImportParameters extends AbstractImportParameter {

}
