package mobi.chouette.exchange.report;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

import lombok.AllArgsConstructor;
import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder={"code","description"})
@AllArgsConstructor
@Data
public class FileError {
	
	@XmlType(name="FileCode")
	@XmlEnum
	public enum CODE 
	{
		FILE_NOT_FOUND,
		READ_ERROR,
		WRITE_ERROR,
		INVALID_FORMAT,
		INTERNAL_ERROR
	};
	
	@XmlElement(name="code",required=true)
	private CODE code;
	
	@XmlElement(name="description",required=true)
	private String description;
	
}
