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
public class ActionError {
	
	@XmlType(name="ActionCode")
	@XmlEnum
	public enum CODE 
	{
		INVALID_PARAMETERS,
		NO_DATA_FOUND,
		NO_DATA_PROCEEDED,
		INTERNAL_ERROR
	};
	
	@XmlElement(name="code",required=true)
	private CODE code;
	
	@XmlElement(name="description",required=true)
	private String description;
	
}
