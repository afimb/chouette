package mobi.chouette.importer.report;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class FilesDetail {

	private List<FileItem> ignored = new ArrayList<FileItem>();

	private List<FileItem> error = new ArrayList<FileItem>();

	private List<FileItem> ok = new ArrayList<FileItem>();

	/**
	 * @return the ignored
	 */
	@XmlElement(name = "ignored")
	public List<FileItem> getIgnored() {
		return ignored;
	}

	/**
	 * @param ignored the ignored to set
	 */
	public void setIgnored(List<FileItem> ignored) {
		this.ignored = ignored;
	}

	/**
	 * @return the error
	 */
	@XmlElement(name = "error")
	public List<FileItem> getError() {
		return error;
	}

	/**
	 * @param error the error to set
	 */
	public void setError(List<FileItem> error) {
		this.error = error;
	}

	/**
	 * @return the ok
	 */
	@XmlElement(name = "ok")
	public List<FileItem> getOk() {
		return ok;
	}

	/**
	 * @param ok the ok to set
	 */
	public void setOk(List<FileItem> ok) {
		this.ok = ok;
	}

}
