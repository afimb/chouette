package mobi.chouette.parameters.validation;

import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

@Data
public class StopAreaParameters {

	@XmlElement(name = "objectid")
	private FieldParameters objectid;
	
	@XmlElement(name = "name")
	private FieldParameters name;
	
	@XmlElement(name = "registration_number")
	private FieldParameters registrationNumber;
	
	@XmlElement(name = "city_name")
	private FieldParameters cityName;
	
	@XmlElement(name = "country_code")
	private FieldParameters countryCode;
	
	@XmlElement(name = "zip_code")
	private FieldParameters zipCode;
		
}
