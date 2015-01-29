package mobi.chouette.parameters.validation;

import javax.xml.bind.annotation.XmlAttribute;

import lombok.Data;

@Data
public class FieldParameters {

	@XmlAttribute(name = "unique")
	private Integer unique;
	
	@XmlAttribute(name = "pattern")
	private Integer pattern;
	
	@XmlAttribute(name = "min_size")
	private Integer minSize;
	
	@XmlAttribute(name = "max_size")
	private Integer maxSize;
	
}
