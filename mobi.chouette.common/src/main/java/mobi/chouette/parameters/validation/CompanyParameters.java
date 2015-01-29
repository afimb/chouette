package mobi.chouette.parameters.validation;

import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

@Data
public class CompanyParameters {

	@XmlElement(name = "objectid")
	private FieldParameters objectid;
	
	@XmlElement(name = "name")
	private FieldParameters name;
	
	@XmlElement(name = "registration_number")
	private FieldParameters registrationNumber;
	
}
