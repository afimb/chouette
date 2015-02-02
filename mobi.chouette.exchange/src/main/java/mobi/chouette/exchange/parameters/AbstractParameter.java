package mobi.chouette.exchange.parameters;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class AbstractParameter {

	@XmlElement(name = "name")
	private String name;

	@XmlElement(name = "user_name")
	private String userName;

}
