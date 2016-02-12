package mobi.chouette.exchange.converter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Data;
import mobi.chouette.exchange.parameters.AbstractExportParameter;
import mobi.chouette.exchange.parameters.AbstractImportParameter;
import mobi.chouette.exchange.parameters.AbstractParameter;

@XmlRootElement(name = "convert")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "importConfiguration", "exportConfiguration" })
@Data
public class ConvertParameters extends AbstractParameter {

	@XmlElements(value = {
			@XmlElement(name = "neptune-import", type = mobi.chouette.exchange.neptune.importer.NeptuneImportParameters.class),
			@XmlElement(name = "gtfs-import", type = mobi.chouette.exchange.gtfs.importer.GtfsImportParameters.class),
			@XmlElement(name = "netex-import", type = mobi.chouette.exchange.netex.importer.NetexImportParameters.class) })
	private AbstractImportParameter importConfiguration;

	@XmlElements(value = {
			@XmlElement(name = "neptune-export", type = mobi.chouette.exchange.neptune.exporter.NeptuneExportParameters.class),
			@XmlElement(name = "gtfs-export", type = mobi.chouette.exchange.gtfs.exporter.GtfsExportParameters.class),
			@XmlElement(name = "netex-export", type = mobi.chouette.exchange.netex.exporter.NetexExportParameters.class) })
	private AbstractExportParameter exportConfiguration;

}
