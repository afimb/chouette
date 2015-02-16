package mobi.chouette.exchange.validation.report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Detail {

	@XmlElement(name = "source")
	private Location source;

	@XmlTransient
	// @XmlElement(name = "targets")
	private List<Location> targets = new ArrayList<>();

	@XmlAttribute(name = "error_id",required=true)
	private String key;

	@XmlTransient
	// @XmlAttribute(name = "error_value")
	private String value;

	@XmlTransient
	// @XmlAttribute(name = "reference_value")
	private String referenceValue;
	
	@XmlElement(name = "details")
	private  Map<String, Object> map;

	public Detail(String key, 
			Location source)
	{
		setKey(key.replaceAll("-", "_").toLowerCase());
		this.source = source;

	}

	public Detail(String key, 
			Location source, Map<String, Object> map)
	{

		this(key,source);
		this.map = map;

	}

	public Detail(String key, 
			Location source, String value)
	{
		this(key, source);
		this.value = value;


	}


	public Detail(String key, Location source,
			String value, String refValue)
	{
		this(key,source,value);
		this.referenceValue = refValue;

	}
	public Detail(String key, 
			Location source, Location... targets)
	{

		this(key, source);
		this.getTargets().addAll(Arrays.asList(targets));

	}

	public Detail(String key, 
			Location source, String value, Location... targets)
	{
		this(key, source, value);
		this.getTargets().addAll(Arrays.asList(targets));


	}


	public Detail(String key, Location source,
			String value, String refValue, Location... targets)
	{
		this(key,source,value, refValue);
		this.getTargets().addAll(Arrays.asList(targets));

	}


}
