package mobi.chouette.exchange.importer.report;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;


@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class Files {

	@XmlElement(name = "stats")
	private FileStats fileStats = new FileStats();
	
	@XmlElement(name = "list")
	private FilesDetail filesDetail = new FilesDetail();


}
