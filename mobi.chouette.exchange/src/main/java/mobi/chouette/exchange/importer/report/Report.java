package mobi.chouette.exchange.importer.report;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@XmlRootElement(name = "import")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class Report {

	@XmlAttribute(name = "status")
	private String status;
	
	@XmlElement(name = "zip")
	private ZipItem zip;
	
	@XmlElement(name = "files")
	private Files files = new Files();
	
	@XmlElement(name = "lines")
	private Lines lines = new Lines();
	
	@XmlAttribute(name = "error")
	private String error;
	
	
}
