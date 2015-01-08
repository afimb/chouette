package mobi.chouette.validation.report;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

@Data
public class Detail {

	@XmlElement(name = "location")
	private Location location;

	@XmlAttribute(name = "object_id")
	private String objectId;

	@XmlElement(name = "message_args")
	private Map<String, String> messageArgs = new HashMap<String, String>();

	@XmlAttribute(name = "message_key")
	private String messageKey;

}
