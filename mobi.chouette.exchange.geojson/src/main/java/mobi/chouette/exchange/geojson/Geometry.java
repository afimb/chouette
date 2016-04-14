package mobi.chouette.exchange.geojson;

import javax.xml.bind.annotation.XmlElement;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Geometry extends Element {

	@XmlElement
	private double[] bbox;

	public Geometry(String type) {
		this(type, null);
	}

	public Geometry(String type, double[] bbox) {
		super(type);
		this.bbox = bbox;
	}
}
