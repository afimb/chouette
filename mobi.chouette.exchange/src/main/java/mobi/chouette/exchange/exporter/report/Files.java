package mobi.chouette.exchange.exporter.report;

import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

@Data
public class Files {

	@XmlElement(name = "stats")
	private FileStats fileStats;
	
	@XmlElement(name = "list")
	private FilesDetail filesDetail;

}
