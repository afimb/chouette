package mobi.chouette.parameters.validation;

import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

@Data
public class AccessLinkParameters {

	@XmlElement(name = "objectid")
	private FieldParameters objectid;
	
	@XmlElement(name = "name")
	private FieldParameters name;
	
	@XmlElement(name = "link_distance")
	private FieldParameters linkDistance;
	
	@XmlElement(name = "default_duration")
	private FieldParameters defaultDuration;
		
}
