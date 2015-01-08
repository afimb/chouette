package mobi.chouette.validation.report;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

@Data
public class Phase {

	@XmlAttribute(name = "name")
	private String name;
	
	@XmlElement(name = "check_point")
	private List<CheckPoint> checkPoint = new ArrayList<CheckPoint>();
	
}
