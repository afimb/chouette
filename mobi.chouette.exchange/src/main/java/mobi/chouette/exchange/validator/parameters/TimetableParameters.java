package mobi.chouette.exchange.validator.parameters;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class TimetableParameters {

	@XmlElement(name = "objectid")
	private FieldParameters objectid;

	@XmlElement(name = "comment")
	private FieldParameters comment;

	@XmlElement(name = "version")
	private FieldParameters version;

}
