package mobi.chouette.exchange.converter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mobi.chouette.exchange.parameters.AbstractExportParameter;
import mobi.chouette.exchange.parameters.AbstractImportParameter;
import mobi.chouette.exchange.parameters.AbstractParameter;

@XmlRootElement(name = "convert")
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "importConfiguration", "exportConfiguration" })
public class ConvertParameters extends AbstractParameter {

	@XmlElements(value = {
			@XmlElement(name = "neptune-input", type = mobi.chouette.exchange.neptune.importer.NeptuneImportParameters.class),
			@XmlElement(name = "gtfs-input", type = mobi.chouette.exchange.gtfs.importer.GtfsImportParameters.class),
			@XmlElement(name = "netex-input", type = mobi.chouette.exchange.netex.importer.NetexImportParameters.class) })
	@Getter
	@Setter
	private AbstractImportParameter importConfiguration;

	@XmlElements(value = {
			@XmlElement(name = "neptune-output", type = mobi.chouette.exchange.neptune.exporter.NeptuneExportParameters.class),
			@XmlElement(name = "gtfs-output", type = mobi.chouette.exchange.gtfs.exporter.GtfsExportParameters.class),
			@XmlElement(name = "netex-output", type = mobi.chouette.exchange.netex.exporter.NetexExportParameters.class) })
	@Getter
	@Setter
	private AbstractExportParameter exportConfiguration;

}
