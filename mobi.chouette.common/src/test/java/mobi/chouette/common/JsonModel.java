package mobi.chouette.common;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name = "json_model")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "name", "userName", "value","list","child" })
public class JsonModel {

	@Getter
	@Setter
	@XmlElement(name = "name", required = true)
	private String name;

	@Getter
	@Setter
	@XmlElement(name = "user_name")
	private String userName;

	@Getter
	@Setter
	@XmlElement(name = "value")
	private Integer value;

	@Getter
	@Setter
	@XmlElement(name = "list")
	private List<String> list = new ArrayList<>();
	
	@Getter
	@Setter
	@XmlElement(name = "child")
	private JsonChildModel child;
	


}
