package mobi.chouette.exchange.validation.report;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

@Data
public class Phase {

	public enum PHASE
	{
		ZERO, ONE, TWO, THREE
	};


	@XmlAttribute(name = "name")
	private String name;

	@XmlElement(name = "check_point")
	private List<CheckPoint> checkPoints = new ArrayList<CheckPoint>();
	
	public Phase(String name)
	{
		this.name = name;
	}

	public void addCheckPoint(CheckPoint checkPoint) 
	{
		this.checkPoints.add(checkPoint);
	}
	
	public CheckPoint findCheckPointByName(String name)
	{
		for (CheckPoint checkPoint : checkPoints) {
			if (checkPoint.getName().equals(name))
				return checkPoint;
		}
		return null;
	}


}
