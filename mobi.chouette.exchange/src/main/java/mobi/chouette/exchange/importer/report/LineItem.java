package mobi.chouette.exchange.importer.report;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;


@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class LineItem {

	@XmlAttribute(name = "status")
	private String status;
	
	@XmlElement(name = "stats")
	private LineStats stats;
	
	@XmlAttribute(name = "name")
	private String name;

}
