package mobi.chouette.exchange.importer.report;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class FilesDetail {

	@XmlElement(name = "ignored")
	private List<FileItem> ignored = new ArrayList<FileItem>();

	@XmlElement(name = "error")
	private List<FileItem> error = new ArrayList<FileItem>();

	@XmlElement(name = "ok")
	private List<FileItem> ok = new ArrayList<FileItem>();


}
