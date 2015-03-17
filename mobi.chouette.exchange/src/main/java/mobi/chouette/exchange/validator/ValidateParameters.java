package mobi.chouette.exchange.validator;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;
import mobi.chouette.exchange.parameters.AbstractParameter;

@XmlRootElement(name = "validate")
@XmlAccessorType(XmlAccessType.FIELD)
public class ValidateParameters extends AbstractParameter {

	@XmlElement(name = "references_type", required=true)
	@Getter @Setter
	private String referencesType;
	
	@Getter @Setter
	@XmlElement(name = "ids")
	private List<Integer> ids;
	


}
