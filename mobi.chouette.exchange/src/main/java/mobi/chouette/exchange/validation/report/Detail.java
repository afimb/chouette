package mobi.chouette.exchange.validation.report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Detail {

	@XmlElement(name = "source")
	private Location source;

	@XmlElement(name = "targets")
	private List<Location> targets = new ArrayList<>();

	@XmlAttribute(name = "key")
	private String key;

	@XmlAttribute(name = "value")
	private String value;

	@XmlAttribute(name = "reference_value")
	private String referenceValue;

	public Detail(String key, 
			Location source)
	{

		setKey("detail_" + key.replaceAll("-", "_").toLowerCase());
		this.source = source;

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
