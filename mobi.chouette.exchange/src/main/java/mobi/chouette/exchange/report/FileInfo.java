package mobi.chouette.exchange.report;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "name", "status","ioType", "errors" })
@Data
@EqualsAndHashCode(exclude={"status", "errors"})
@NoArgsConstructor
public class FileInfo {

	@XmlType(name = "fileState")
	@XmlEnum
	public enum FILE_STATE {
		IGNORED, OK, ERROR
	};

	@XmlElement(name = "name", required = true)
	private String name;

	@XmlElement(name = "status", required = true)
	private FILE_STATE status;
	
	@XmlElement(name = "io_type")
	private IO_TYPE ioType;

	@XmlElement(name = "errors")
	private List<FileError> errors = new ArrayList<>();

	public void addError(FileError error) {
		status = FILE_STATE.ERROR;
		errors.add(error);
	}

	public FileInfo(String name, FILE_STATE state) {
		this.name = name;
		this.status = state;
	}

	public FileInfo(String name, FILE_STATE state, FileError fileError) {
		this.name = name;
		this.status = state;
		errors.add(fileError);
	}

	public FileInfo(String name, FILE_STATE state, List<FileError> fileErrors) {
		this.name = name;
		this.status = state;
		errors.addAll(fileErrors);
	}

	public JSONObject toJson() throws JSONException {
		JSONObject object = new JSONObject();
		object.put("name", name);
		object.put("status", status);
		if (ioType != null)
		{
			object.put("io_type",ioType);
		}
		if (!errors.isEmpty()) {
			JSONArray array = new JSONArray();
			object.put("errors", array);
			for (FileError error : errors) {
				array.put(error.toJson());
			}
		}
		return object;
	}
}
