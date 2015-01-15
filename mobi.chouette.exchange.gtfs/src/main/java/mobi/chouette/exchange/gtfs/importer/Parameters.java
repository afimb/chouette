package mobi.chouette.exchange.gtfs.importer;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@Data
@XmlRootElement(name = "")
public class Parameters {
	
	@XmlAttribute(name = "object_id_prefix")
	private String objectIdPrefix;
	
	@XmlAttribute(name = "max_distance_for_connection_link")
	private Integer maxDistanceForConnectionLink;
	
	@XmlAttribute(name = "max_distance_for_commercial")
	private String maxDistanceForCommercial;
	
	@XmlAttribute(name = "ignore_end_chars")
	private Integer ignoreEndChars;
	
	@XmlAttribute(name = "ignore_last_word")
	private Boolean ignoreLastWord;

	@XmlAttribute(name = "no_save")
	private Boolean noSave;
}
