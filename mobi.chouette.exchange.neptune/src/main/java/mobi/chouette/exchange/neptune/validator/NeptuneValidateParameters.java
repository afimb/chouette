package mobi.chouette.exchange.neptune.validator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import mobi.chouette.exchange.neptune.importer.NeptuneImportParameters;

@XmlRootElement(name = "neptune-validate")
@XmlAccessorType(XmlAccessType.FIELD)

public class NeptuneValidateParameters extends NeptuneImportParameters {



}
