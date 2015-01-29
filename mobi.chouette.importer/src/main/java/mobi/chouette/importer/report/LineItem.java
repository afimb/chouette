package mobi.chouette.importer.report;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;


public class LineItem {

	private String status;
	
	private LineStats stats;
	
	private String name;

	/**
	 * @return the status
	 */
	@XmlAttribute(name = "status")
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the stats
	 */
	@XmlElement(name = "stats")
	public LineStats getStats() {
		return stats;
	}

	/**
	 * @param stats the stats to set
	 */
	public void setStats(LineStats stats) {
		this.stats = stats;
	}

	/**
	 * @return the name
	 */
	@XmlAttribute(name = "name")
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

}
