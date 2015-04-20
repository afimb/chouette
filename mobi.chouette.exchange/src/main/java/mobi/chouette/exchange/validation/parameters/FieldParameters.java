package mobi.chouette.exchange.validation.parameters;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
@XmlType(propOrder={"unique","pattern","minSize","maxSize"})
public class FieldParameters {

	@XmlElement(name = "unique", defaultValue="0")
	private int unique = 0;

	@XmlElement(name = "pattern", defaultValue="0")
	private int pattern = 0;

	@XmlElement(name = "min_size")
	private String minSize;

	@XmlElement(name = "max_size")
	private String maxSize;

}
