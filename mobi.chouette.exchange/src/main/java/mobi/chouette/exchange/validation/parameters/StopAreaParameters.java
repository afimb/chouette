package mobi.chouette.exchange.validation.parameters;

import javax.xml.bind.annotation.XmlElement;

public class StopAreaParameters {

	private FieldParameters objectid;
	
	private FieldParameters name;
	
	private FieldParameters registrationNumber;
	
	private FieldParameters cityName;
	
	private FieldParameters countryCode;
	
	private FieldParameters zipCode;

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

	/**
	 * @return the cityName
	 */
	@XmlElement(name = "city_name")
	public FieldParameters getCityName() {
		return cityName;
	}

	/**
	 * @param cityName the cityName to set
	 */
	public void setCityName(FieldParameters cityName) {
		this.cityName = cityName;
	}

	/**
	 * @return the countryCode
	 */
	@XmlElement(name = "country_code")
	public FieldParameters getCountryCode() {
		return countryCode;
	}

	/**
	 * @param countryCode the countryCode to set
	 */
	public void setCountryCode(FieldParameters countryCode) {
		this.countryCode = countryCode;
	}

	/**
	 * @return the zipCode
	 */
	@XmlElement(name = "zip_code")
	public FieldParameters getZipCode() {
		return zipCode;
	}

	/**
	 * @param zipCode the zipCode to set
	 */
	public void setZipCode(FieldParameters zipCode) {
		this.zipCode = zipCode;
	}
		
}
