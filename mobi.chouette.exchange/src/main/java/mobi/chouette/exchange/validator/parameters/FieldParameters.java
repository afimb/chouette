package mobi.chouette.exchange.validator.parameters;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class FieldParameters {

	@XmlElement(name = "unique", defaultValue="0")
	private int unique = 0;

	@XmlElement(name = "pattern", defaultValue="0")
	private int pattern = 0;

	@XmlElement(name = "min_size")
	private Integer minSize;

	@XmlElement(name = "max_size")
	private Integer maxSize;

}
