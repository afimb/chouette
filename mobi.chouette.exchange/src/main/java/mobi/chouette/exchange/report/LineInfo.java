package mobi.chouette.exchange.report;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Data;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder={})
@Data
public class LineInfo {

	public enum LINE_STATE 
	{
		OK,
		WARNING,
		ERROR
	};
	@XmlElement(name = "name")
	private String name;

	@XmlElement(name = "status")
	private LINE_STATE status;
	
	@XmlElement(name = "stats")
	private LineStats stats;
	

}
