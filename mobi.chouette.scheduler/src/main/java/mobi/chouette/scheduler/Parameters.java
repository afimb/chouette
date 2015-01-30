package mobi.chouette.scheduler;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Setter;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;

@XmlRootElement(name = "parameters")
public class Parameters {

	
	private Object configuration;

	@XmlElements(value = {
			@XmlElement(name = "neptuneImport", type = mobi.chouette.exchange.neptune.importer.Parameters.class),
			@XmlElement(name = "gtfsImport", type = mobi.chouette.exchange.gtfs.importer.Parameters.class),
			@XmlElement(name = "netexImport", type = mobi.chouette.exchange.netex.importer.Parameters.class) })
	public Object getConfiguration() {
		return configuration;
	}

	@Setter
	private ValidationParameters validation;

	@XmlElement(name = "validation")
	public ValidationParameters getValidation() {
		return validation;
	}

}
