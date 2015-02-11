package mobi.chouette.exchange.validation.report;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@Data
@XmlRootElement(name = "validation")
public class ValidationReport {

	@XmlElement(name = "phase")
	private List<Phase> phases = new ArrayList<Phase>();
	
	public Phase findPhaseByName(String name)
	{
		for (Phase phase : phases) {
			if (phase.getName().equals(name))
				return phase;
		}
		return null;
	}

}
