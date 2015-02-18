package mobi.chouette.exchange.report;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@XmlRootElement(name = "report")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class Report {

	/**
	 * indicate progression informations, will disapear when terminated
	 */
	@XmlElement(name = "progression")
	private Progression progression;

	@XmlElement(name = "result")
	private String result;
	
	@XmlElement(name = "zip")
	private ZipItem zip;
	
	@XmlElement(name = "files")
	private Files files = new Files();
	
	@XmlElement(name = "lines")
	private Lines lines = new Lines();
	
	@XmlElement(name = "failure")
	private String failure;
	
	
}
