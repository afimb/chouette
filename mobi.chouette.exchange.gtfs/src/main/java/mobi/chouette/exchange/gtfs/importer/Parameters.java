package mobi.chouette.exchange.gtfs.importer;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.Setter;
import mobi.chouette.parameters.AbstractParameter;
import mobi.chouette.parameters.validation.ValidationParameters;

@XmlRootElement(name = "")
@XmlType (name= "gtfsImport")
public class Parameters  extends AbstractParameter{
	
	@Getter
	@Setter
	@XmlAttribute(name = "object_id_prefix")
	private String objectIdPrefix;
	
	@Getter
	@Setter
	@XmlAttribute(name = "max_distance_for_connection_link")
	private Integer maxDistanceForConnectionLink;
	
	@Getter
	@Setter
	@XmlAttribute(name = "max_distance_for_commercial")
	private String maxDistanceForCommercial;
	
	@Getter
	@Setter
	@XmlAttribute(name = "ignore_end_chars")
	private Integer ignoreEndChars;
	
	@Getter
	@Setter
	@XmlAttribute(name = "ignore_last_word")
	private Boolean ignoreLastWord;

	@Getter
	@Setter
	@XmlElement(name = "references_type")
	private String referencesType;
	
	@Getter
	@Setter
	@XmlAttribute(name = "no_save")
	private Boolean noSave;
	
	@Getter
	@Setter
	@XmlElement(name = "validation")
	private ValidationParameters validation;
	
}
