package mobi.chouette.exchange.report;

import java.io.PrintStream;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import mobi.chouette.exchange.report.ActionReporter.ERROR_CODE;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class ActionError extends AbstractReport {
	
	private ERROR_CODE code;
	
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
