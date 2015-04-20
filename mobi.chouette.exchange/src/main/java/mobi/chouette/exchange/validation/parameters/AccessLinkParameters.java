package mobi.chouette.exchange.validation.parameters;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import lombok.Data;
import mobi.chouette.model.AccessLink;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
@XmlType(propOrder={"objectId","name","linkDistance","defaultDuration"})
public class AccessLinkParameters {

	@XmlTransient
	public static String[] fields = { "ObjectId", "Name", "LinkDistance", "DefaultDuration"} ;
	
	static {
		ValidationParametersUtil.addFieldList(AccessLink.class.getSimpleName(), Arrays.asList(fields));
	}

	@XmlElement(name = "objectid")
	private FieldParameters objectId;

	@XmlElement(name = "name")
	private FieldParameters name;

	@XmlElement(name = "link_distance")
	private FieldParameters linkDistance;

	@XmlElement(name = "default_duration")
	private FieldParameters defaultDuration;

}
