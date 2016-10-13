package mobi.chouette.exchange.validation.report;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import mobi.chouette.exchange.report.AbstractReport;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "filename", "lineNumber", "columnNumber" })
public class FileLocation extends AbstractReport {

	@XmlElement(name = "filename", required = true)
	private String filename;

	@XmlElement(name = "line_number")
	private Integer lineNumber;

	@XmlElement(name = "column_number")
	private Integer columnNumber;

	protected FileLocation(String fileName) {
		this.filename = fileName;
	}

	protected FileLocation(String fileName, int lineNumber, int columnNumber) {
		this.filename = fileName;
		if (Integer.valueOf(lineNumber) >= 0)
			this.lineNumber = Integer.valueOf(lineNumber);
		if (Integer.valueOf(columnNumber) >= 0)
			this.columnNumber = Integer.valueOf(columnNumber);
	}

	public FileLocation(DataLocation dl) {
		this(dl.getFilename(),dl.getLineNumber(),dl.getColumnNumber());

	}

}
