package mobi.chouette.parameters.validation;

import javax.xml.bind.annotation.XmlAttribute;

import lombok.Data;

@Data
public class LineParameters {

	@XmlAttribute(name = "objectid")
	private FieldParameters objectid;
	
	@XmlAttribute(name = "name")
	private FieldParameters name;
	
	@XmlAttribute(name = "number")
	private FieldParameters number;
	
	@XmlAttribute(name = "published_name")
	private FieldParameters publishedName;
	
}
