package mobi.chouette.exchange.importer.report;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "import")
public class Report {

	private Files files;
	
	private Lines lines;
	
	/**
	 * @return the files
	 */
	@XmlElement(name = "files")
	public Files getFiles() {
		return files;
	}

	/**
	 * @param files the files to set
	 */
	public void setFiles(Files files) {
		this.files = files;
	}



	/**
	 * @return the lines
	 */
	@XmlElement(name = "lines")
	public Lines getLines() {
		return lines;
	}

	/**
	 * @param lines the lines to set
	 */
	public void setLines(Lines lines) {
		this.lines = lines;
	}

}
