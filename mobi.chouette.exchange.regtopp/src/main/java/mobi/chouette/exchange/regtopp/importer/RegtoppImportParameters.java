package mobi.chouette.exchange.regtopp.importer;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.log4j.Logger;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import mobi.chouette.exchange.parameters.AbstractImportParameter;
import mobi.chouette.exchange.regtopp.importer.version.RegtoppVersion;

@XmlRootElement(name = "regtopp-import")
@NoArgsConstructor
@ToString(callSuper = true)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "objectIdPrefix", "maxDistanceForConnectionLink", "maxDistanceForCommercial", "ignoreEndChars", "ignoreLastWord", "referencesType","version","coordinateProjection" })
public class RegtoppImportParameters extends AbstractImportParameter {

	@Getter
	@Setter
	@XmlElement(name = "object_id_prefix", required = true)
	private String objectIdPrefix;

	// TODO do we need this?
	@Getter
	@Setter
	@XmlElement(name = "max_distance_for_connection_link", defaultValue = "0")
	private int maxDistanceForConnectionLink = 0;

	// TODO do we need this?
	@Getter
	@Setter
	@XmlElement(name = "max_distance_for_commercial", defaultValue = "0")
	private int maxDistanceForCommercial = 0;

	// TODO do we need this?
	@Getter
	@Setter
	@XmlElement(name = "ignore_end_chars", defaultValue = "0")
	private int ignoreEndChars = 0;

	// TODO do we need this?
	@Getter
	@Setter
	@XmlElement(name = "ignore_last_word", defaultValue = "false")
	private boolean ignoreLastWord = false;

	// Type of data to import (line/stop_area)
	@Getter
	@Setter
	@XmlElement(name = "references_type")
	private String referencesType;

	// Regtopp version
	@Getter
	@Setter
	@XmlElement(name = "version", required = true)
	private RegtoppVersion version;

	// Coordinate projection ie EPSG:32632 (UTM32_N)
	@Getter
	@Setter
	@XmlElement(name = "coordinate_projection", required = true)
	private String coordinateProjection;

	public boolean isValid(Logger log, String[] allowedTypes) {
		if (!super.isValid(log))
			return false;

		if (objectIdPrefix == null || objectIdPrefix.isEmpty()) {
			log.error("missing object_id_prefix");
			return false;
		}

		if (referencesType != null && !referencesType.isEmpty()) {
			if (!Arrays.asList(allowedTypes).contains(referencesType.toLowerCase())) {
				log.error("invalid type " + referencesType);
				return false;
			}
		}
		
		if(version == null) {
			log.error("missing regtopp version");
			return false;
		}
		
		return true;

	}
}
