package mobi.chouette.exchange.validation.parameters;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import lombok.Data;
import mobi.chouette.model.Interchange;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
@XmlType(propOrder={"objectId", "name"})
public class InterchangeParameters {

	@XmlTransient
	public static String[] fields = { "ObjectId", "Name"} ;
	
	static {
		ValidationParametersUtil.addFieldList(Interchange.class.getSimpleName(), Arrays.asList(fields));
	}

	@XmlElement(name = "objectid")
	private FieldParameters objectId;

	@XmlElement(name = "name")
	private FieldParameters name;

}
