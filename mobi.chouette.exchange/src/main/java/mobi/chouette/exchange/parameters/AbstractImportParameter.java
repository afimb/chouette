package mobi.chouette.exchange.parameters;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import mobi.chouette.model.type.StopAreaImportModeEnum;

import org.apache.log4j.Logger;

@NoArgsConstructor
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "noSave", "cleanRepository", "stopAreaRemoteIdMapping", "stopAreaImportMode", "keepObsoleteLines" }, name = "actionImportParameter")
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
	 * Whether or not stop area ids from import files should be mapped against remote stop area registry (ie NSR).
	 *
	 */
	@XmlElement(name = "stop_area_remote_id_mapping", defaultValue = "true")
	@Getter
	@Setter
	private boolean stopAreaRemoteIdMapping = true;

	/**
	 * How stop areas in import file should be treated by chouette.
	 */
	@XmlElement(name = "stop_area_import_mode", defaultValue = "true")
	@Getter
	@Setter
	private StopAreaImportModeEnum stopAreaImportMode = StopAreaImportModeEnum.CREATE_OR_UPDATE;

	@XmlElement(name = "keep_obsolete_lines", defaultValue = "false")
	@Getter
	@Setter
	private boolean keepObsoleteLines = true;

	public boolean isValid(Logger log) {
		return super.isValid(log);
	}

}
