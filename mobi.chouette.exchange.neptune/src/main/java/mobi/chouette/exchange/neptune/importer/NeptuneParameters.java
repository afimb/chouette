package mobi.chouette.exchange.neptune.importer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import mobi.chouette.exchange.parameters.AbstractParameter;


@NoArgsConstructor
@ToString(callSuper = true)
@XmlAccessorType(XmlAccessType.FIELD)
public class NeptuneParameters extends AbstractParameter {

	
	@Getter @Setter
	@XmlElement(name = "no_save")
	private Boolean noSave;

}
