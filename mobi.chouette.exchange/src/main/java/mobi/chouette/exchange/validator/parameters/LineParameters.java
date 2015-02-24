package mobi.chouette.exchange.validator.parameters;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class LineParameters {
	
	@XmlTransient
	public enum fields { Objectid, Name, Number, PublishedName, RegistrationNumber} ;

	@XmlElement(name = "objectid")
	private FieldParameters objectid;

	@XmlElement(name = "name")
	private FieldParameters name;

	@XmlElement(name = "number")
	private FieldParameters number;

	@XmlElement(name = "published_name")
	private FieldParameters publishedName;

	@XmlElement(name = "registration_number")
	private FieldParameters registrationNumber;

}
