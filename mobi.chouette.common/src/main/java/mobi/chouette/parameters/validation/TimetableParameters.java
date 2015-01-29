package mobi.chouette.parameters.validation;

import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

@Data
public class TimetableParameters {

	@XmlElement(name = "objectid")
	private FieldParameters objectid;
	
	@XmlElement(name = "comment")
	private FieldParameters comment;
	
	@XmlElement(name = "version")
	private FieldParameters version;
	
}
