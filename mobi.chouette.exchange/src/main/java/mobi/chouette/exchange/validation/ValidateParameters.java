package mobi.chouette.exchange.validation;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;
import mobi.chouette.exchange.parameters.AbstractParameter;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class ValidateParameters extends AbstractParameter {

	@XmlElement(name = "references_type", required=true)
	private String referencesType;
	
	@XmlElement(name = "ids")
	private List<Integer> ids;
	


}
