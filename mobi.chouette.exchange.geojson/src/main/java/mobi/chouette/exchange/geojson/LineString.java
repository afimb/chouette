package mobi.chouette.exchange.geojson;

import javax.xml.bind.annotation.XmlElement;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class LineString extends Geometry {

	@XmlElement
	private double[][] coordinates;

	public LineString(double[][] coordinates) {
		this(coordinates, null);
	}

	public LineString(double[][] coordinates, double[] bbox) {
		super("LineString", bbox);
		this.coordinates = coordinates;
	}
}
