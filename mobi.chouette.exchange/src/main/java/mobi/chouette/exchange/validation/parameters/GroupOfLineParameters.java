package mobi.chouette.exchange.validation.parameters;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import lombok.Data;
import mobi.chouette.model.GroupOfLine;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
@XmlType(propOrder={"technicalId", "name", "registrationNumber"})
public class GroupOfLineParameters {

	@XmlTransient
	public static String[] fields = { "TechnicalId", "Name", "RegistrationNumber"} ;
	
	static {
		ValidationParametersUtil.addFieldList(GroupOfLine.class.getSimpleName(), Arrays.asList(fields));
	}

//	@XmlElement(name = "objectid")
//	private FieldParameters objectId;
	
	@XmlElement(name = "technical_id")
	private FieldParameters technicalId;

	@XmlElement(name = "name")
	private FieldParameters name;

	@XmlElement(name = "registration_number")
	private FieldParameters registrationNumber;

}
