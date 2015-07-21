
package mobi.chouette.exchange.report;


import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Data;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder={"currentStep","stepsCount","steps"})
@Data
public class Progression {
	
	
    @XmlElement( name = "current_step")
    private int currentStep = 0;

    @XmlElement( name = "steps_count")
    private int stepsCount = 3;

    @XmlElement(name = "steps",required=true)
	private List<StepProgression> steps = new ArrayList<>();

    public Progression()
    {
    	steps.add(new StepProgression(StepProgression.STEP.INITIALISATION,1,0));
    	steps.add(new StepProgression(StepProgression.STEP.PROCESSING,1,0));
    	steps.add(new StepProgression(StepProgression.STEP.FINALISATION,1,0));    	
    }

	public JSONObject toJson() throws JSONException {
		JSONObject object = new JSONObject();
		object.put("current_step", currentStep);
		object.put("steps_count", stepsCount);
		JSONArray array = new JSONArray();
		object.put("steps", array);
		for (StepProgression step : steps) {
			array.put(step.toJson());
		}
		return object;
	}

}
