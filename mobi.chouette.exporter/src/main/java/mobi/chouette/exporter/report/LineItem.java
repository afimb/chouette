package mobi.chouette.exporter.report;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

@Data
public class LineItem {

	@XmlAttribute(name = "status")
	private STATUS status;
	
	@XmlElement(name = "status")
	private LineStats stats;
	
	@XmlAttribute(name = "name")
	private String name;
	
	enum STATUS {
		ERROR, SAVED
	}

}
