package mobi.chouette.exchange.validation.report;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@XmlRootElement(name = "validation")
@XmlAccessorType(XmlAccessType.FIELD)
public class ValidationReport {

	@XmlElement(name = "tests")
	private List<CheckPoint> checkPoints = new ArrayList<CheckPoint>();
	
	public CheckPoint findCheckPointByName(String name)
	{
		for (CheckPoint checkPoint : checkPoints) {
			if (checkPoint.getName().equals(name))
				return checkPoint;
		}
		return null;
	}

}
