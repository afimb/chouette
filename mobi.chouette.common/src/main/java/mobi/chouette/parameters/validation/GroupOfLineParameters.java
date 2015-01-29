package mobi.chouette.parameters.validation;

import javax.xml.bind.annotation.XmlElement;

public class GroupOfLineParameters {

	private FieldParameters objectid;
	private FieldParameters name;
	private FieldParameters registrationNumber;

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
	 * @return the registrationNumber
	 */
	@XmlElement(name = "registration_number")
	public FieldParameters getRegistrationNumber() {
		return registrationNumber;
	}

	/**
	 * @param registrationNumber the registrationNumber to set
	 */
	public void setRegistrationNumber(FieldParameters registrationNumber) {
		this.registrationNumber = registrationNumber;
	}
	
}
