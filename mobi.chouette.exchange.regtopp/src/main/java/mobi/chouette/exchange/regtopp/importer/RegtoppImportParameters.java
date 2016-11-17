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
@XmlType(propOrder = { "objectIdPrefix", "referencesType", "version", "coordinateProjection", "charsetEncoding", "calendarStrategy", "batchParse" })
public class RegtoppImportParameters extends AbstractImportParameter {

	@Getter
	@Setter
	@XmlElement(name = "object_id_prefix", required = true)
	private String objectIdPrefix;

	// Type of data to import (line/stop_area)
	@Getter
	@Setter
	@XmlElement(name = "references_type")
	private String referencesType;

	// Regtopp version
	@Getter
	@Setter
	@XmlElement(name = "version", required = false)
	private RegtoppVersion version;

	// Coordinate projection ie EPSG:32632 (UTM32_N)
	@Getter
	@Setter
	@XmlElement(name = "coordinate_projection", required = true)
	private String coordinateProjection;

	// Charset encoding. Some use latin1, others use MSDOS Nordic ie.
	@Getter
	@Setter
	@XmlElement(name = "charset_encoding", required = false)
	private String charsetEncoding;

	// Regtopp version
	@Setter
	@XmlElement(name = "calendar_strategy", required = false)
	private CalendarStrategy calendarStrategy = CalendarStrategy.ADD;

	// Batch parse whole shipment at once instead of Line by Line
	@Getter
	@Setter
	@XmlElement(name = "batch_parse", required = false)
	private boolean batchParse = true;

	public CalendarStrategy getCalendarStrategy() {
		if (calendarStrategy == null) {
			calendarStrategy = CalendarStrategy.ADD;
		}

		return calendarStrategy;
	}

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

		return true;

	}
}
