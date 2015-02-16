package mobi.chouette.exchange.validation.report;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class CheckPoint {

	private static final int maxDetails = 20;

	public enum SEVERITY {
		WARNING, ERROR, IMPROVMENT
	};

	public enum RESULT {
		UNCHECK, OK, NOK
	};

	@XmlAttribute(name = "test_id",required=true)
	private String name;

	@XmlAttribute(name="level",required=true)
	private String phase;

	@XmlAttribute(name="object_type",required=true)
	private String target;

	@XmlAttribute(name = "rank",required=true)
	private String rank;

	@XmlElement(name = "severity",required=true)
	private SEVERITY severity;

	@XmlElement(name = "result",required=true)
	private RESULT state;

	@XmlAttribute(name = "error_count")
	private int detailCount = 0;

	@XmlElement(name = "error")
	private List<Detail> details = new ArrayList<Detail>();

	public CheckPoint(String name,int order, RESULT state, SEVERITY severity)
	{
		this.name = name;
		// this.order = order;
		this.severity = severity;
		this.state = state;

		String[] token = name.split("\\-");
		if (token.length == 4)
		{
			this.phase = token[0];
			this.target = token[2];
			this.rank = token[3];
		}
		else if (token.length == 3)
		{
			this.phase = token[0];
			this.target = token[1];
			this.rank = token[2];
		}
		else 
		{
			throw new IllegalArgumentException("invalid name "+name);
		}
	}

	public void addDetail(Detail item) 
	{
		if (detailCount < maxDetails) 
		{
			details.add(item);
		}
		detailCount++;

		state = RESULT.NOK;


	}
}
