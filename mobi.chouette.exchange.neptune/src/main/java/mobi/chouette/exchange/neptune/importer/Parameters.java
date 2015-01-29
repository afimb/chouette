package mobi.chouette.exchange.neptune.importer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.Setter;
import mobi.chouette.parameters.AbstractParameter;
import mobi.chouette.parameters.validation.ValidationParameters;

@XmlRootElement(name = "")
@XmlType (name= "neptuneImport")
public class Parameters extends AbstractParameter{

	@Getter
	@Setter
	@XmlElement(name = "no_save")
	private Boolean noSave;
	
	@Getter
	@Setter
	@XmlElement(name = "validation")
	private ValidationParameters validation;
	
}
