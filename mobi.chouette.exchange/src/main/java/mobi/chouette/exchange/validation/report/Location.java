package mobi.chouette.exchange.validation.report;

import javax.xml.bind.annotation.XmlAttribute;

import lombok.Data;

@Data
public class Location {

	@XmlAttribute(name = "filename")
	private String filename;

	@XmlAttribute(name = "line_number")
	private Long lineNumber;

	@XmlAttribute(name = "column_number")
	private Long columnNumber;

	@XmlAttribute(name = "object_id")
	private String objectId;

}
