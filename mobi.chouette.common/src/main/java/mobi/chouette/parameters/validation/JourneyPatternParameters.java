package mobi.chouette.parameters.validation;

import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

@Data
public class JourneyPatternParameters {

	@XmlElement(name = "objectid")
	private FieldParameters objectid;
	
	@XmlElement(name = "name")
	private FieldParameters name;
	
	@XmlElement(name = "number")
	private FieldParameters number;
	
	@XmlElement(name = "published_name")
	private FieldParameters publishedName;
	
}
