package mobi.chouette.importer.report;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;


public class Lines {

	private LineStats stats;

	private List<LineItem> list;

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
	 * @return the list
	 */
	@XmlElement(name = "list")
	public List<LineItem> getList() {
		return list;
	}

	/**
	 * @param list the list to set
	 */
	public void setList(List<LineItem> list) {
		this.list = list;
	}

}
