package mobi.chouette.exchange.validator.parameters;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import lombok.Data;
import mobi.chouette.model.PTNetwork;


@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class NetworkParameters {

	@XmlTransient
	public static String[] fields = { "ObjectId", "Name", "RegistrationNumber"} ;
	
	static {
		ValidationParametersUtil.addFieldList(PTNetwork.class.getSimpleName(), Arrays.asList(fields));
	}

	@XmlElement(name = "objectid")
	private FieldParameters objectId;

	@XmlElement(name = "name")
	private FieldParameters name;

	@XmlElement(name = "registration_number")
	private FieldParameters registrationNumber;

}
