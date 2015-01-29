package mobi.chouette.exchange.neptune.exporter;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import mobi.chouette.parameters.AbstractParameter;

@XmlRootElement(name = "")
@XmlType (name= "neptuneExport")
public class Parameters  extends AbstractParameter {
	
	private String referencesType;
	
	private List<Integer> ids;
	
	private Date startDate;
	
	private Date endDate;
	
	private String projectionType;

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
	@XmlElement(name = "start_date")
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
	@XmlElement(name = "end_date")
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
	 * @return the projectionType
	 */
	@XmlElement(name = "projection_type")
	public String getProjectionType() {
		return projectionType;
	}

	/**
	 * @param projectionType the projectionType to set
	 */
	public void setProjectionType(String projectionType) {
		this.projectionType = projectionType;
	}

}
