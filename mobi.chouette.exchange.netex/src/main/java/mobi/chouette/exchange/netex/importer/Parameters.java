package mobi.chouette.exchange.netex.importer;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;
import mobi.chouette.parameters.AbstractParameter;
import mobi.chouette.parameters.validation.ValidationParameters;


@XmlRootElement(name = "")
public class Parameters  extends AbstractParameter{

	@Getter
	@Setter
	@XmlAttribute(name = "no_save")
	private Boolean noSave;
	
	@Getter
	@Setter
	@XmlAttribute(name = "validation")
	private ValidationParameters validation;
}
