package mobi.chouette.exchange.validation.report;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
public class FileLocation {

	@XmlElement(name = "filename",required=true)
	private String filename;

	@XmlElement(name = "line_number")
	private Integer lineNumber;

	@XmlElement(name = "column_number")
	private Integer columnNumber;


	public FileLocation(String fileName)
	{
		this.filename = fileName;
	}

	public FileLocation(String fileName, int lineNumber, int columnNumber)
	{
		this.filename = fileName;
		this.lineNumber = Integer.valueOf(lineNumber);
		this.columnNumber = Integer.valueOf(columnNumber);
	}

	public FileLocation(String fileName, int lineNumber, int columnNumber, String objectId)
	{
		this.filename = fileName;
		this.lineNumber = Integer.valueOf(lineNumber);
		this.columnNumber = Integer.valueOf(columnNumber);
	}

}
