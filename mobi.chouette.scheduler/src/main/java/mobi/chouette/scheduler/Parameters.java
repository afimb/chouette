package mobi.chouette.scheduler;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class Parameters {

	
	@XmlElements(value = {
			@XmlElement(name = "neptune-import", type = mobi.chouette.exchange.neptune.importer.NeptuneParameters.class),
			@XmlElement(name = "gtfs-import", type = mobi.chouette.exchange.gtfs.importer.GtfsParameters.class),
			@XmlElement(name = "netex-import", type = mobi.chouette.exchange.netex.importer.NetexParameters.class) })
	private Object configuration;

	@XmlElement(name = "validation")
	private ValidationParameters validation;

}
