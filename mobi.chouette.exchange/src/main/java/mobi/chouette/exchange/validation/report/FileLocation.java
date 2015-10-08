package mobi.chouette.exchange.validation.report;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Data;
import lombok.ToString;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

@Data
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "filename", "lineNumber", "columnNumber" })
public class FileLocation {

	@XmlElement(name = "filename", required = true)
	private String filename;

	@XmlElement(name = "line_number")
	private Integer lineNumber;

	@XmlElement(name = "column_number")
	private Integer columnNumber;

	public FileLocation(String fileName) {
		this.filename = fileName;
	}

	public FileLocation(String fileName, int lineNumber, int columnNumber) {
		this.filename = fileName;
		this.lineNumber = Integer.valueOf(lineNumber);
		if (Integer.valueOf(columnNumber) >= 0)
			this.columnNumber = Integer.valueOf(columnNumber);
	}

	public FileLocation(String fileName, int lineNumber, int columnNumber, String objectId) {
		this.filename = fileName;
		this.lineNumber = Integer.valueOf(lineNumber);
		if (Integer.valueOf(columnNumber) >= 0)
			this.columnNumber = Integer.valueOf(columnNumber);
	}

	public JSONObject toJson() throws JSONException {
		JSONObject object = new JSONObject();
		object.put("filename", filename);
		if (lineNumber != null) {
			object.put("line_number", lineNumber);
		}
		if (columnNumber != null) {
			object.put("column_number", columnNumber);
		}
		return object;
	}

}
