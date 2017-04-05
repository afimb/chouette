package mobi.chouette.command;

import lombok.Data;
import mobi.chouette.exchange.parameters.AbstractParameter;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;

import javax.xml.bind.annotation.*;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder={"configuration","validation"})
@Data
public class Parameters {

	
	@XmlElements(value = {
			@XmlElement(name = "neptune-import", type = mobi.chouette.exchange.neptune.importer.NeptuneImportParameters.class),
			@XmlElement(name = "neptune-export", type = mobi.chouette.exchange.neptune.exporter.NeptuneExportParameters.class),
			@XmlElement(name = "gtfs-import", type = mobi.chouette.exchange.gtfs.importer.GtfsImportParameters.class),
			@XmlElement(name = "gtfs-export", type = mobi.chouette.exchange.gtfs.exporter.GtfsExportParameters.class),
			@XmlElement(name = "netex-import", type = mobi.chouette.exchange.netex.importer.NetexImportParameters.class),
			@XmlElement(name = "netex-export", type = mobi.chouette.exchange.netex.exporter.NetexExportParameters.class),
			@XmlElement(name = "netexprofile-import", type = mobi.chouette.exchange.netexprofile.importer.NetexprofileImportParameters.class),
			@XmlElement(name = "netexprofile-export", type = mobi.chouette.exchange.netexprofile.exporter.NetexprofileExportParameters.class),
			@XmlElement(name = "kml-export", type = mobi.chouette.exchange.kml.exporter.KmlExportParameters.class),
			@XmlElement(name = "hub-export", type = mobi.chouette.exchange.hub.exporter.HubExportParameters.class),
        	@XmlElement(name = "validate", type = mobi.chouette.exchange.validator.ValidateParameters.class) })
	private AbstractParameter configuration;

	@XmlElement(name = "validation")
	private ValidationParameters validation;

}
