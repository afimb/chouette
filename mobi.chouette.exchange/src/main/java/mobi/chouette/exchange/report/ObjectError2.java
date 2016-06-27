package mobi.chouette.exchange.report;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import lombok.AllArgsConstructor;
import lombok.Data;
import mobi.chouette.exchange.report.ActionReporter.ERROR_CODE;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder={"code","description"})
@AllArgsConstructor
@Data
public class ObjectError2 {
	
	@XmlElement(name="code",required=true)
	private ERROR_CODE code;
	
	@XmlElement(name="description",required=true)
	private String description;

	public JSONObject toJson() throws JSONException {
		JSONObject object = new JSONObject();
		object.put("code", code);
		object.put("description", description);
		return object;
	}
}
