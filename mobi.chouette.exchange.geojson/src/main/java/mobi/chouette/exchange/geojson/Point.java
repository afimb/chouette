package mobi.chouette.exchange.geojson;

import javax.xml.bind.annotation.XmlElement;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Point extends Geometry {

	@XmlElement
	private double[] coordinates;

	public Point(double[] coordinates) {
		this(coordinates, null);
	}

	public Point(double[] coordinates, double[] bbox) {
		super("Point", bbox);
		this.coordinates = coordinates;
	}
}
