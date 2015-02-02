package mobi.chouette.exchange.gtfs.importer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;
import mobi.chouette.exchange.parameters.AbstractParameter;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class GtfsParameters extends AbstractParameter {

	@XmlElement(name = "object_id_prefix")
	private String objectIdPrefix;

	@XmlElement(name = "max_distance_for_connection_link")
	private Integer maxDistanceForConnectionLink;

	@XmlElement(name = "max_distance_for_commercial")
	private String maxDistanceForCommercial;

	@XmlElement(name = "ignore_end_chars")
	private Integer ignoreEndChars;

	@XmlElement(name = "ignore_last_word")
	private Boolean ignoreLastWord;

	@XmlElement(name = "references_type")
	private String referencesType;

	@XmlElement(name = "no_save")
	private Boolean noSave;

}
