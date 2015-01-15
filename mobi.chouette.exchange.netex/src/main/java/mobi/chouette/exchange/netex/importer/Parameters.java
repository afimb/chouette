package mobi.chouette.exchange.netex.importer;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@Data
@XmlRootElement(name = "")
public class Parameters {

	@XmlAttribute(name = "no_save")
	private Boolean noSave;
}
