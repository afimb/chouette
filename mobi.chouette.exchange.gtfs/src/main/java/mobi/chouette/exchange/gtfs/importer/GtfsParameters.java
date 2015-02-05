package mobi.chouette.exchange.gtfs.importer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import mobi.chouette.exchange.parameters.AbstractParameter;

@NoArgsConstructor
@ToString(callSuper=true)
@XmlAccessorType(XmlAccessType.FIELD)
public class GtfsParameters extends AbstractParameter {

	@Getter@Setter
	@XmlElement(name = "object_id_prefix")
	private String objectIdPrefix;

	@Getter@Setter
	@XmlElement(name = "max_distance_for_connection_link")
	private Integer maxDistanceForConnectionLink;

	@Getter@Setter
	@XmlElement(name = "max_distance_for_commercial")
	private String maxDistanceForCommercial;

	@Getter@Setter
	@XmlElement(name = "ignore_end_chars")
	private Integer ignoreEndChars;

	@Getter@Setter
	@XmlElement(name = "ignore_last_word")
	private Boolean ignoreLastWord;

	@Getter@Setter
	@XmlElement(name = "references_type")
	private String referencesType;

	@Getter@Setter
	@XmlElement(name = "no_save")
	private Boolean noSave;

}
