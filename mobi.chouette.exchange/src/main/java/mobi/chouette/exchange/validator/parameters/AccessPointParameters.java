package mobi.chouette.exchange.validator.parameters;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class AccessPointParameters {

	@XmlTransient
	public enum fields { Objectid, Name, CityName, CountryCode, ZipCode} ;

	@XmlElement(name = "objectid")
	private FieldParameters objectid;

	@XmlElement(name = "name")
	private FieldParameters name;

	@XmlElement(name = "city_name")
	private FieldParameters cityName;

	@XmlElement(name = "country_code")
	private FieldParameters countryCode;

	@XmlElement(name = "zip_code")
	private FieldParameters zipCode;

}
