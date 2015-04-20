package mobi.chouette.exchange.validation.parameters;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import lombok.Data;
import mobi.chouette.model.JourneyPattern;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
@XmlType(propOrder={"objectId", "name", "registrationNumber","publishedName"})
public class JourneyPatternParameters {

	@XmlTransient
	public static String[] fields = { "ObjectId", "Name", "RegistrationNumber","PublishedName"} ;
	
	static {
		ValidationParametersUtil.addFieldList(JourneyPattern.class.getSimpleName(), Arrays.asList(fields));
	}

	@XmlElement(name = "objectid")
	private FieldParameters objectId;

	@XmlElement(name = "name")
	private FieldParameters name;

	@XmlElement(name = "registration_number")
	private FieldParameters registrationNumber;

	@XmlElement(name = "published_name")
	private FieldParameters publishedName;

}
