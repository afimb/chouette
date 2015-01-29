package mobi.chouette.exchange.gtfs.exporter;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import mobi.chouette.parameters.AbstractParameter;

@XmlRootElement(name = "")
@XmlType (name= "gtfsExport")
public class Parameters  extends AbstractParameter {
	
	private String referencesType;
	
	private List<Integer> ids;
	
	private Date startDate;
	
	private Date endDate;
	
	private String timeZone;
	
	private String objectIdPrefix;

	/**
	 * @return the referencesType
	 */
	@XmlElement(name = "references_type")
	public String getReferencesType() {
		return referencesType;
	}

	/**
	 * @param referencesType the referencesType to set
	 */
	public void setReferencesType(String referencesType) {
		this.referencesType = referencesType;
	}

	/**
	 * @return the ids
	 */
	@XmlElement(name = "ids")
	public List<Integer> getIds() {
		return ids;
	}

	/**
	 * @param ids the ids to set
	 */
	public void setIds(List<Integer> ids) {
		this.ids = ids;
	}

	/**
	 * @return the startDate
	 */
	@XmlAttribute(name = "start_date")
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the endDate
	 */
	@XmlAttribute(name = "end_date")
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return the timeZone
	 */
	@XmlAttribute(name = "time_zone")
	public String getTimeZone() {
		return timeZone;
	}

	/**
	 * @param timeZone the timeZone to set
	 */
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	/**
	 * @return the objectIdPrefix
	 */
	@XmlAttribute(name = "object_id_prefix")
	public String getObjectIdPrefix() {
		return objectIdPrefix;
	}

	/**
	 * @param objectIdPrefix the objectIdPrefix to set
	 */
	public void setObjectIdPrefix(String objectIdPrefix) {
		this.objectIdPrefix = objectIdPrefix;
	}

}
