package mobi.chouette.exchange.gtfs.importer;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import mobi.chouette.exchange.parameters.AbstractParameter;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;

@XmlType (name= "gtfsImport")
public class Parameters  extends AbstractParameter{
	
	private String objectIdPrefix;
	
	private Integer maxDistanceForConnectionLink;
	
	private String maxDistanceForCommercial;
	
	private Integer ignoreEndChars;
	
	private Boolean ignoreLastWord;

	private String referencesType;
	
	private Boolean noSave;
	
	private ValidationParameters validation;

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

	/**
	 * @return the maxDistanceForConnectionLink
	 */
	@XmlAttribute(name = "max_distance_for_connection_link")
	public Integer getMaxDistanceForConnectionLink() {
		return maxDistanceForConnectionLink;
	}

	/**
	 * @param maxDistanceForConnectionLink the maxDistanceForConnectionLink to set
	 */
	public void setMaxDistanceForConnectionLink(Integer maxDistanceForConnectionLink) {
		this.maxDistanceForConnectionLink = maxDistanceForConnectionLink;
	}

	/**
	 * @return the maxDistanceForCommercial
	 */
	@XmlAttribute(name = "max_distance_for_commercial")
	public String getMaxDistanceForCommercial() {
		return maxDistanceForCommercial;
	}

	/**
	 * @param maxDistanceForCommercial the maxDistanceForCommercial to set
	 */
	public void setMaxDistanceForCommercial(String maxDistanceForCommercial) {
		this.maxDistanceForCommercial = maxDistanceForCommercial;
	}

	/**
	 * @return the ignoreEndChars
	 */
	@XmlAttribute(name = "ignore_end_chars")
	public Integer getIgnoreEndChars() {
		return ignoreEndChars;
	}

	/**
	 * @param ignoreEndChars the ignoreEndChars to set
	 */
	public void setIgnoreEndChars(Integer ignoreEndChars) {
		this.ignoreEndChars = ignoreEndChars;
	}

	/**
	 * @return the ignoreLastWord
	 */
	@XmlAttribute(name = "ignore_last_word")
	public Boolean getIgnoreLastWord() {
		return ignoreLastWord;
	}

	/**
	 * @param ignoreLastWord the ignoreLastWord to set
	 */
	public void setIgnoreLastWord(Boolean ignoreLastWord) {
		this.ignoreLastWord = ignoreLastWord;
	}

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
	 * @return the noSave
	 */
	@XmlAttribute(name = "no_save")
	public Boolean getNoSave() {
		return noSave;
	}

	/**
	 * @param noSave the noSave to set
	 */
	public void setNoSave(Boolean noSave) {
		this.noSave = noSave;
	}

	/**
	 * @return the validation
	 */
	@XmlElement(name = "validation")
	public ValidationParameters getValidation() {
		return validation;
	}

	/**
	 * @param validation the validation to set
	 */
	public void setValidation(ValidationParameters validation) {
		this.validation = validation;
	}
	
}
