package mobi.chouette.exchange.validation.parameters;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class FieldParameters {

	@XmlElement(name = "unique")
	private Integer unique;

	@XmlElement(name = "pattern")
	private Integer pattern;

	@XmlElement(name = "min_size")
	private Integer minSize;

	@XmlElement(name = "max_size")
	private Integer maxSize;

}
