package mobi.chouette.exchange.parameters;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.apache.log4j.Logger;


@NoArgsConstructor
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder={"name","userName","organisationName","referentialName","test"},name="actionParameters")
public class AbstractParameter {

	@Getter@Setter
	@XmlElement(name = "name", required=true)
	private String name;

	@Getter@Setter
	@XmlElement(name = "user_name", required=true)
	private String userName;

	@Getter@Setter
	@XmlElement(name = "organisation_name")
	private String organisationName;

	@Getter@Setter
	@XmlElement(name = "referential_name")
	private String referentialName;
	
	@Getter@Setter
	@XmlElement(name = "test")
	private boolean test = false;
	public boolean isValid(Logger log)
	{
		return true;
	}

}
