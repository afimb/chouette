package mobi.chouette.exchange.report;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder={"name","status","error"})
@Data
public class FileInfo {
	
	@XmlType(name="fileState")
	@XmlEnum
	public enum FILE_STATE 
	{
		UNCHECKED,
		OK,
		NOK
	};
	
	@XmlElement(name="name",required=true)
	private String name;
	
	@XmlElement(name="status",required=true)
	private FILE_STATE status;
	
	@XmlElement(name="error")
	private List<String> errors = new ArrayList<>();

}
