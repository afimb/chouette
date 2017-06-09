package mobi.chouette.exchange.parameters;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.apache.log4j.Logger;

@NoArgsConstructor
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "noSave", "cleanRepository", "updateStopPlaces", "importStopPlaces", "keepObsoleteLines" }, name = "actionImportParameter")
public class AbstractImportParameter extends AbstractParameter {

	@XmlElement(name = "no_save", defaultValue = "false")
	@Getter
	@Setter
	private boolean noSave = false;

	@XmlElement(name = "clean_repository", defaultValue = "false")
	@Getter
	@Setter
	private boolean cleanRepository = false;

	/**
	 * Whether or not stop places from import files should be used to update remote stop area repository (NSR).
	 *
	 */
	@XmlElement(name = "update_stop_places", defaultValue = "true")
	@Getter
	@Setter
	private boolean updateStopPlaces = true;

	/**
	 * Whether or not stop places from import files should be imported to chouette stop area repository.
	 */
	@XmlElement(name = "import_stop_places", defaultValue = "true")
	@Getter
	@Setter
	private boolean importStopPlaces = true;
	@XmlElement(name = "keep_obsolete_lines", defaultValue = "false")
	@Getter
	@Setter
	private boolean keepObsoleteLines = true;

	public boolean isValid(Logger log) {
		return super.isValid(log);
	}

}
