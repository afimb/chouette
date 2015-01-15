package mobi.chouette.importer.report;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@Data
@XmlRootElement(name = "import")
public class Report {

	@XmlElement(name = "files")
	private Files files;
	
	@XmlElement(name = "lines")
	private Lines lines;

}
