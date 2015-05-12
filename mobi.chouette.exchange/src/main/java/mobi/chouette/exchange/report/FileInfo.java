package mobi.chouette.exchange.report;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

import lombok.Data;
import lombok.NonNull;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "name", "status", "errors" })
@Data
public class FileInfo {

	@XmlType(name = "fileState")
	@XmlEnum
	public enum FILE_STATE {
		IGNORED, OK, ERROR
	};

	@XmlElement(name = "name", required = true)
	@NonNull
	private String name;

	@XmlElement(name = "status", required = true)
	@NonNull
	private FILE_STATE status;

	@XmlElement(name = "errors")
	private List<FileError> errors = new ArrayList<>();

	public void addError(FileError error) {
		status = FILE_STATE.ERROR;
		errors.add(error);
	}

}
