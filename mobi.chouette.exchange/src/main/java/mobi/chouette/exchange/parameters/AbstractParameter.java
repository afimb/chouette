package mobi.chouette.exchange.parameters;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@NoArgsConstructor
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
public class AbstractParameter {

	@Getter@Setter
	@XmlElement(name = "name")
	private String name;

	@Getter@Setter
	@XmlElement(name = "user_name")
	private String userName;

}
