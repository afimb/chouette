package mobi.chouette.exchange.report;

import lombok.*;
import mobi.chouette.exchange.report.ActionReporter.ERROR_CODE;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.PrintStream;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder={"code","description"})
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@Setter
public class ObjectError extends AbstractReport {
	
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
	@Override
	public void print(PrintStream out, StringBuilder ret , int level, boolean first) {
		ret.setLength(0);
		out.print(addLevel(ret, level).append('{'));
		out.print(toJsonString(ret, level+1, "code", code, true));
		out.print(toJsonString(ret, level+1, "description", description, false));
		ret.setLength(0);
		out.print(addLevel(ret.append('\n'), level).append('}'));		
	}
}
