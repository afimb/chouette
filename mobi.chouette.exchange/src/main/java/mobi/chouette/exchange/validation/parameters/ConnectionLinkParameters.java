package mobi.chouette.exchange.validation.parameters;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import lombok.Data;
import mobi.chouette.model.ConnectionLink;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
@XmlType(propOrder={"technicalId", "name", "linkDistance", "defaultDuration"})
public class ConnectionLinkParameters {

	@XmlTransient
	public static String[] fields = { "TechnicalId", "Name", "LinkDistance", "DefaultDuration"} ;
	
	static {
		ValidationParametersUtil.addFieldList(ConnectionLink.class.getSimpleName(), Arrays.asList(fields));
	}

//	@XmlElement(name = "objectid")
//	private FieldParameters objectId;
	
	@XmlElement(name = "technical_id")
	private FieldParameters technicalId;

	@XmlElement(name = "name")
	private FieldParameters name;

	@XmlElement(name = "link_distance")
	private FieldParameters linkDistance;

	@XmlElement(name = "default_duration")
	private FieldParameters defaultDuration;

}
