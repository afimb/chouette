package mobi.chouette.exchange.importer.report;

import javax.xml.bind.annotation.XmlAttribute;


public class FileStats {
	
	private Integer ignoredCount;
	
	private Integer errorCount;

	private Integer okCount;

	/**
	 * @return the ignoredCount
	 */
	@XmlAttribute(name="ignored_count")
	public Integer getIgnoredCount() {
		return ignoredCount;
	}

	/**
	 * @param ignoredCount the ignoredCount to set
	 */
	public void setIgnoredCount(Integer ignoredCount) {
		this.ignoredCount = ignoredCount;
	}

	/**
	 * @return the errorCount
	 */
	@XmlAttribute(name="error_count")
	public Integer getErrorCount() {
		return errorCount;
	}

	/**
	 * @param errorCount the errorCount to set
	 */
	public void setErrorCount(Integer errorCount) {
		this.errorCount = errorCount;
	}

	/**
	 * @return the okCount
	 */
	@XmlAttribute(name="ok_count")
	public Integer getOkCount() {
		return okCount;
	}

	/**
	 * @param okCount the okCount to set
	 */
	public void setOkCount(Integer okCount) {
		this.okCount = okCount;
	}

}
