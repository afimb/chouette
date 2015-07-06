
package mobi.chouette.exchange.report;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder={"step","total","realized"})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StepProgression {
	
	@XmlType(name="step")
	@XmlEnum
	public enum STEP {
		INITIALISATION,
		PROCESSING,
		FINALISATION,
		TERMINATED
	};
    @XmlElement( name = "step",required=true)
    private STEP step;
	
    @XmlElement(name = "total",required=true)
	private int total = 0;

    @XmlElement(name = "realized",required=true)
	private int realized = 0;

	public JSONObject toJson() throws JSONException {
		JSONObject object = new JSONObject();
		object.put("step", step);
		object.put("total", total);
		object.put("realized", realized);
		return object;	}


}
