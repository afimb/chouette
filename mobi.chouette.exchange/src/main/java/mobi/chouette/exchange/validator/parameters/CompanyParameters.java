package mobi.chouette.exchange.validator.parameters;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import lombok.Data;
import mobi.chouette.model.Company;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class CompanyParameters {

	@XmlTransient
	public static String[] fields = { "ObjectId", "Name", "RegistrationNumber"} ;
	
	static {
		ValidationParametersUtil.addFieldList(Company.class.getSimpleName(), Arrays.asList(fields));
	}

	@XmlElement(name = "objectid")
	private FieldParameters objectId;

	@XmlElement(name = "name")
	private FieldParameters name;

	@XmlElement(name = "registration_number")
	private FieldParameters registrationNumber;

}
