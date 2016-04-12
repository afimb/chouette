package mobi.chouette.exchange.report;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import mobi.chouette.model.Line;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder={"name","objectId","status","ioType","stats","errors"})
@Data
@EqualsAndHashCode(exclude={"name","status","stats","errors"})
@NoArgsConstructor
public class LineInfo {

	@XmlType(name="lineState")
	@XmlEnum
	public enum LINE_STATE 
	{
		OK,
		WARNING,
		ERROR
	};
	@XmlElement(name = "name",required=true)
	private String name;

	@XmlElement(name = "status",required=true)
	private LINE_STATE status = LINE_STATE.OK;
	
	@XmlElement(name = "stats",required=true)
	private DataStats stats = new DataStats();

	@XmlElement(name = "io_type")
	private IO_TYPE ioType;

	@XmlElement(name="errors")
	private List<LineError> errors = new ArrayList<>();
	
	@XmlElement(name="objectid")
	private String objectId;
	
	public LineInfo(Line line)
	{
		this.objectId = line.getObjectId();
		this.name = line.getName() + " (" + line.getNumber() + ")";
	}
	
	/**
	 * add an error; status will be set to ERROR
	 * 
	 * @param error
	 */
	public void addError(LineError error)
	{
		status = LINE_STATE.ERROR;
		errors.add(error);
	}

	public JSONObject toJson() throws JSONException {
		JSONObject object = new JSONObject();
		object.put("name", name);
		object.put("objectid", objectId);
		object.put("status", status);
		if (ioType != null)
		{
			object.put("io_type",ioType);
		}
		if (stats != null)
		   object.put("stats", stats.toJson());
		if (!errors.isEmpty()) {
			JSONArray array = new JSONArray();
			object.put("errors", array);
			for (LineError error : errors) {
				array.put(error.toJson());
			}
		}
		return object;
	}


}
