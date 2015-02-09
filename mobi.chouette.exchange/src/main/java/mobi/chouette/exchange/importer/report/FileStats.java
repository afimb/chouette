package mobi.chouette.exchange.importer.report;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import lombok.Data;


@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class FileStats {
	
	@XmlAttribute(name="ignored_count")
	private Integer ignoredCount;
	
	@XmlAttribute(name="error_count")
	private Integer errorCount;

	@XmlAttribute(name="ok_count")
	private Integer okCount;

}
