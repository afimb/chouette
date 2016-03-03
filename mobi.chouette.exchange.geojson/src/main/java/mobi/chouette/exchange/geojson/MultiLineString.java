package mobi.chouette.exchange.geojson;

import javax.xml.bind.annotation.XmlElement;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MultiLineString extends Geometry {

	@XmlElement
	private double[][][] coordinates;

	public MultiLineString(double[][][] coordinates) {
		this(coordinates, null);
	}

	public MultiLineString(double[][][] coordinates, double[] bbox) {
		super("MultiLineString", bbox);
		this.coordinates = coordinates;
	}
}
