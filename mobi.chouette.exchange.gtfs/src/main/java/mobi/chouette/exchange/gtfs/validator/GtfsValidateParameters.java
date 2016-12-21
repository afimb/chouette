package mobi.chouette.exchange.gtfs.validator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import mobi.chouette.exchange.gtfs.importer.GtfsImportParameters;

@XmlRootElement(name = "gtfs-validate")
@XmlAccessorType(XmlAccessType.FIELD)

public class GtfsValidateParameters extends GtfsImportParameters {



}
