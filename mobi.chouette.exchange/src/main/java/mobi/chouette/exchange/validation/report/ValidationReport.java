package mobi.chouette.exchange.validation.report;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@XmlRootElement(name = "validation_report")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder={"result", "checkPoints"})
public class ValidationReport {
	
	@XmlElement(name = "result")
	@Getter @Setter
	private String result = "NO_VALIDATION";

	@XmlElement(name = "tests")
	@Getter @Setter
	private List<CheckPoint> checkPoints = new ArrayList<CheckPoint>();
	
	public CheckPoint findCheckPointByName(String name)
	{
		for (CheckPoint checkPoint : checkPoints) {
			if (checkPoint.getName().equals(name))
				return checkPoint;
		}
		return null;
	}
	
	public void checkResult()
	{
		result = checkPoints.isEmpty() ? "NO_VALIDATION": "VALIDATION_PROCEDEED";
	}

}
