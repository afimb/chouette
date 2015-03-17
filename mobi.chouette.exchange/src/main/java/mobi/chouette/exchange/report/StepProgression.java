
package mobi.chouette.exchange.report;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StepProgression {
	
	public enum STEP {
		INITIALISATION,
		PROCESSING,
		FINALISATION
	};
    @XmlElement( name = "step")
    private STEP step;
	
    @XmlElement(name = "total")
	private int total = 0;

    @XmlElement(name = "realized")
	private int realized = 0;


}
