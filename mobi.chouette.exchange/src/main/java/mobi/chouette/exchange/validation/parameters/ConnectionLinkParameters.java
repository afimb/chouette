package mobi.chouette.exchange.validation.parameters;

import javax.xml.bind.annotation.XmlElement;

public class ConnectionLinkParameters {

	private FieldParameters objectid;
	
	private FieldParameters name;
	
	private FieldParameters linkDistance;
	
	private FieldParameters defaultDuration;

	/**
	 * @return the objectid
	 */
	@XmlElement(name = "objectid")
	public FieldParameters getObjectid() {
		return objectid;
	}

	/**
	 * @param objectid the objectid to set
	 */
	public void setObjectid(FieldParameters objectid) {
		this.objectid = objectid;
	}

	/**
	 * @return the name
	 */
	@XmlElement(name = "name")
	public FieldParameters getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(FieldParameters name) {
		this.name = name;
	}

	/**
	 * @return the linkDistance
	 */
	@XmlElement(name = "link_distance")
	public FieldParameters getLinkDistance() {
		return linkDistance;
	}

	/**
	 * @param linkDistance the linkDistance to set
	 */
	public void setLinkDistance(FieldParameters linkDistance) {
		this.linkDistance = linkDistance;
	}

	/**
	 * @return the defaultDuration
	 */
	@XmlElement(name = "default_duration")
	public FieldParameters getDefaultDuration() {
		return defaultDuration;
	}

	/**
	 * @param defaultDuration the defaultDuration to set
	 */
	public void setDefaultDuration(FieldParameters defaultDuration) {
		this.defaultDuration = defaultDuration;
	}
		
}
