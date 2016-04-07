package mobi.chouette.exchange.validation.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import lombok.Data;
import lombok.ToString;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

@Data
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "name", "phase", "target", "rank", "severity", "state", "detailCount", "details" })
public class CheckPoint {

	public static final int maxDetails = 30;

	public enum SEVERITY {
		WARNING, ERROR, IMPROVMENT
	};

	public enum RESULT {
		UNCHECK, OK, NOK
	};

	@XmlElement(name = "test_id", required = true)
	private String name;

	@XmlElement(name = "level", required = true)
	private String phase;

	@XmlElement(name = "object_type", required = true)
	private String target;

	@XmlElement(name = "rank", required = true)
	private String rank;

	@XmlElement(name = "severity", required = true)
	private SEVERITY severity;

	@XmlElement(name = "result", required = true)
	private RESULT state;

	@XmlElement(name = "error_count")
	private int detailCount = 0;

	@XmlElement(name = "errors")
	private List<Detail> details = new ArrayList<Detail>();

	@XmlTransient
	private Map<String, Integer> fileMap = new HashMap<>();

	@XmlTransient
	private boolean maxByFile = true;

	public CheckPoint(String name, RESULT state, SEVERITY severity) {
		this.name = name;
		this.severity = severity;
		this.state = state;

		String[] token = name.split("\\-");
		if (token.length >= 4) {
			this.phase = token[0];
			this.target = token[2];
			this.rank = token[3];
		} else if (token.length == 3) {
			this.phase = token[0];
			this.target = token[1];
			this.rank = token[2];
		} else {
			throw new IllegalArgumentException("invalid name " + name);
		}
	}

	public void addDetail(Detail item) {
		if (maxByFile) {
			String fileName = "no_file";
			try
			{
	            fileName = item.getSource().getFile().getFilename();
			}
			catch (NullPointerException e)
			{
				// ignore
			}
			Integer count = fileMap.get(fileName);
			if (count == null)
			{
				count = new Integer(0);
				fileMap.put(fileName, count);
			}
			if (count < maxDetails)
			{
				details.add(item);
			}
			count ++;
		} else if (detailCount < maxDetails) {
			details.add(item);
		}
		detailCount++;

		state = RESULT.NOK;

	}

	public JSONObject toJson() throws JSONException {
		JSONObject object = new JSONObject();
		object.put("test_id", name);
		object.put("level", phase);
		object.put("object_type", target);
		object.put("rank", rank);
		object.put("severity", severity);
		object.put("result", state);
		object.put("error_count", detailCount);
		if (detailCount > 0) {
			JSONArray errors = new JSONArray();

			for (Detail detail : details) {
				errors.put(detail.toJson());
			}
			object.put("errors", errors);
		}

		return object;
	}
}
