
package mobi.chouette.exchange.report;


import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class Progression {
	
	
    @XmlElement( name = "current_step")
    private int currentStep = 0;
	
    @XmlElement(name = "steps")
	private List<StepProgression> steps = new ArrayList<>();

    public Progression()
    {
    	steps.add(new StepProgression(StepProgression.STEP.INITIALISATION,0,0));
    	steps.add(new StepProgression(StepProgression.STEP.PROCESSING,0,0));
    	steps.add(new StepProgression(StepProgression.STEP.FINALISATION,0,0));    	
    }

}
