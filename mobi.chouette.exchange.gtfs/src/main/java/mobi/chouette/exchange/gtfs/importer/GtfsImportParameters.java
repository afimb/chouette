package mobi.chouette.exchange.gtfs.importer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import mobi.chouette.exchange.parameters.AbstractImportParameter;

@XmlRootElement(name = "gtfs-import")
@NoArgsConstructor
@ToString(callSuper=true)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder={})
public class GtfsImportParameters extends AbstractImportParameter {

	@Getter@Setter
	@XmlElement(name = "object_id_prefix", required=true)
	private String objectIdPrefix;

	@Getter@Setter
	@XmlElement(name = "max_distance_for_connection_link", defaultValue="0")
	private int maxDistanceForConnectionLink = 0;

	@Getter@Setter
	@XmlElement(name = "max_distance_for_commercial", defaultValue="0")
	private int maxDistanceForCommercial = 0;

	@Getter@Setter
	@XmlElement(name = "ignore_end_chars", defaultValue="0")
	private int ignoreEndChars = 0;

	@Getter@Setter
	@XmlElement(name = "ignore_last_word", defaultValue="false")
	private boolean ignoreLastWord = false;

	@Getter@Setter
	@XmlElement(name = "references_type")
	private String referencesType;


}
