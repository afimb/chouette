package mobi.chouette.exchange.generic.importer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@XmlRootElement(name = "parameters")
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = "parameters")
public class JobParametersWrapper {
	
	@XmlElement(name = "generic-import")
	GenericImportParameters parameters;
}
