package mobi.chouette.exchange.validation.parameters;

import javax.xml.bind.annotation.XmlElement;

public class JourneyPatternParameters {

	private FieldParameters objectid;
	
	private FieldParameters name;
	
	private FieldParameters number;
	
	private FieldParameters publishedName;

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
	 * @return the number
	 */
	@XmlElement(name = "number")
	public FieldParameters getNumber() {
		return number;
	}

	/**
	 * @param number the number to set
	 */
	public void setNumber(FieldParameters number) {
		this.number = number;
	}

	/**
	 * @return the publishedName
	 */
	@XmlElement(name = "published_name")
	public FieldParameters getPublishedName() {
		return publishedName;
	}

	/**
	 * @param publishedName the publishedName to set
	 */
	public void setPublishedName(FieldParameters publishedName) {
		this.publishedName = publishedName;
	}
	
}
