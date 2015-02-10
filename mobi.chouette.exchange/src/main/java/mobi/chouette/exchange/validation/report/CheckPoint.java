package mobi.chouette.exchange.validation.report;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

@Data
public class CheckPoint {
	
	private static final int maxDetails = 20;

	public enum SEVERITY {
		WARNING, ERROR, IMPROVMENT
	};

	public enum STATE {
		UNCHECK, OK, NOK
	};

	@XmlAttribute(name = "name")
	private String name;

	@XmlAttribute(name = "order")
	private int order;

	@XmlAttribute(name = "severity")
	private SEVERITY severity;

	@XmlAttribute(name = "state")
	private STATE state;

	@XmlAttribute(name = "detail_count")
	private Integer detailCount;

	@XmlElement(name = "details")
	private List<Detail> details = new ArrayList<Detail>();

	public CheckPoint(String name,int order, STATE state, SEVERITY severity)
	{
		this.name = name;
		this.order = order;
		this.severity = severity;
		this.state = state;
	}

	public void addDetail(Detail item) 
	{
		if (detailCount < maxDetails) 
		{
			details.add(item);
		}
		detailCount++;
		
	    	state = STATE.NOK;
	    
		
	}
}
