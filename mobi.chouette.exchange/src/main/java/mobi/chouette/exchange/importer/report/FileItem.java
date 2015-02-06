package mobi.chouette.exchange.importer.report;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class FileItem {
	
	@XmlAttribute(name="name")
	private String name;
	
	@XmlElement(name="errors")
	private List<String> errors = new ArrayList<>();

}
