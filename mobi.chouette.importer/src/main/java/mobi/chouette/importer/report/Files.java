package mobi.chouette.importer.report;

import javax.xml.bind.annotation.XmlElement;


public class Files {

	private FileStats fileStats;
	
	private FilesDetail filesDetail;

	/**
	 * @return the fileStats
	 */
	@XmlElement(name = "stats")
	public FileStats getFileStats() {
		return fileStats;
	}

	/**
	 * @param fileStats the fileStats to set
	 */
	public void setFileStats(FileStats fileStats) {
		this.fileStats = fileStats;
	}

	/**
	 * @return the filesDetail
	 */
	@XmlElement(name = "list")
	public FilesDetail getFilesDetail() {
		return filesDetail;
	}

	/**
	 * @param filesDetail the filesDetail to set
	 */
	public void setFilesDetail(FilesDetail filesDetail) {
		this.filesDetail = filesDetail;
	}

}
