package mobi.chouette.importer.report;

import javax.xml.bind.annotation.XmlAttribute;

import lombok.Data;

@Data
public class FileStats {
	
	@XmlAttribute(name="ignored_count")
	private Integer ignoredCount;
	
	@XmlAttribute(name="error_count")
	private Integer errorCount;

	@XmlAttribute(name="ok_count")
	private Integer okCount;

}
