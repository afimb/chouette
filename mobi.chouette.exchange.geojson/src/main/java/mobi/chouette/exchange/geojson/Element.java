package mobi.chouette.exchange.geojson;

import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

@Data
public class Element {

	@XmlElement
	private String type;

	public Element(String type) {
		this.type = type;
	}
}
