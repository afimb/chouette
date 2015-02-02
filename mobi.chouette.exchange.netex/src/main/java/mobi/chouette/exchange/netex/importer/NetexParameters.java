package mobi.chouette.exchange.netex.importer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;
import mobi.chouette.exchange.parameters.AbstractParameter;


@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class NetexParameters  extends AbstractParameter{

	@XmlElement(name = "no_save")
	private Boolean noSave;

}
