package mobi.chouette.exchange.exporter.report;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class FileItem {
	
	@XmlAttribute(name="name")
	private String name;
	
	@XmlElement(name="errors")
	private List<String> errors;

}
