package mobi.chouette.importer.report;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class FilesDetail {

	@XmlElement(name = "ignored")
	private List<FileItem> ignored = new ArrayList<FileItem>();

	@XmlElement(name = "error")
	private List<FileItem> error = new ArrayList<FileItem>();

	@XmlElement(name = "ok")
	private List<FileItem> ok = new ArrayList<FileItem>();

}
