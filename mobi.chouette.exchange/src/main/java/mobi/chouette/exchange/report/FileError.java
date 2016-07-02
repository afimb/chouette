package mobi.chouette.exchange.report;

import java.io.PrintStream;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import mobi.chouette.exchange.report.ActionReporter.FILE_ERROR_CODE;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder={"code","description"})
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class FileError extends AbstractReport{
	
	@XmlElement(name="code",required=true)
	private FILE_ERROR_CODE code;
	
	@XmlElement(name="description",required=true)
	private String description;

	public JSONObject toJson() throws JSONException {
		JSONObject object = new JSONObject();
		object.put("code", code);
		object.put("description", description);
		return object;
	}

	@Override
	public void print(PrintStream out, int level, boolean first) {
		StringBuilder ret = new StringBuilder();
		out.print(addLevel(ret, level).append('{'));
		out.print(toJsonString(ret, level+1, "code", code, true));
		out.print(toJsonString(ret, level+1, "description", description, false));
		ret.setLength(0);
		out.print(addLevel(ret.append('\n'), level).append('}'));		
	}
	
}
