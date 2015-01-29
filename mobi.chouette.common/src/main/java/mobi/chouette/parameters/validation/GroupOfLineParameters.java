package mobi.chouette.parameters.validation;

import javax.xml.bind.annotation.XmlAttribute;

import lombok.Data;

@Data
public class GroupOfLineParameters {

	@XmlAttribute(name = "objectid")
	private FieldParameters objectid;
	
	@XmlAttribute(name = "name")
	private FieldParameters name;
	
	@XmlAttribute(name = "registration_number")
	private FieldParameters registrationNumber;
	
}
