package mobi.chouette.exporter.report;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@Data
@XmlRootElement(name = "export")
public class Report {

	@XmlElement(name = "files")
	private Files files;
	
	@XmlElement(name = "lines")
	private Lines lines;

}
