package mobi.chouette.common;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.Setter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "data", "names" })
public class JsonChildModel {


	@Getter
	@Setter
	@XmlElement(name = "data")
	private String data;


	@Getter
	@Setter
	@XmlElement(name = "names")
	private List<String> names = new ArrayList<>();


}
