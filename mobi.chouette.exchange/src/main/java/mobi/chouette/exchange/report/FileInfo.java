package mobi.chouette.exchange.report;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class FileInfo {
	
	public enum STATE 
	{
		UNCHECKED,
		OK,
		NOK
	};
	
	@XmlAttribute(name="name",required=true)
	private String name;
	
	@XmlAttribute(name="status",required=true)
	private STATE status;
	
	@XmlElement(name="errors")
	private List<String> errors = new ArrayList<>();

}
