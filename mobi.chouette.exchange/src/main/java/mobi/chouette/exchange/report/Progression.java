
package mobi.chouette.exchange.report;


import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

@Data
@EqualsAndHashCode(callSuper=false)

public class Progression extends AbstractReport {
	
	
    private int currentStep = 0;

    private int stepsCount = 3;

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

	@Override
	public void print(PrintStream out, StringBuilder ret , int level, boolean first) {
		ret.setLength(0);
		out.print(addLevel(ret, level).append('{'));
		out.print(toJsonString(ret, level+1, "current_step", currentStep, true));
		out.print(toJsonString(ret, level+1, "steps_count", stepsCount, false));
		if (!steps.isEmpty()) {
			printArray(out, ret, level + 1, "steps", steps, false);
		}
		ret.setLength(0);
		out.print(addLevel(ret.append('\n'), level).append('}'));
		
	}

}
