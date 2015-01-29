package mobi.chouette.parameters;

import javax.xml.bind.annotation.XmlAttribute;

import lombok.Getter;
import lombok.Setter;

public class AbstractParameter {
	@Getter
	@Setter
	@XmlAttribute(name = "name")
	private String name;

	@Getter
	@Setter
	@XmlAttribute(name = "user_name")
	private String userName;

}
