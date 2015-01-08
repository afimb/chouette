package mobi.chouette.validation.report;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@Data
@XmlRootElement(name = "validation")
public class Report {

	@XmlElement(name = "phase")
	private List<Phase> phase = new ArrayList<Phase>();

}
