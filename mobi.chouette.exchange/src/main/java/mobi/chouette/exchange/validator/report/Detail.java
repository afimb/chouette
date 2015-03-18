package mobi.chouette.exchange.validator.report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder={"key","source","targets","value","referenceValue"})
public class Detail {

	@XmlElement(name = "source")
	private Location source;

	@XmlElement(name = "target")
	private List<Location> targets = new ArrayList<>();

	@XmlElement(name = "error_id",required=true)
	private String key;

	@XmlElement(name = "error_value")
	private String value;

	@XmlElement(name = "reference_value")
	private String referenceValue;
	
	public Detail(String key, 
			Location source)
	{
		setKey(key.replaceAll("-", "_").toLowerCase());
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
