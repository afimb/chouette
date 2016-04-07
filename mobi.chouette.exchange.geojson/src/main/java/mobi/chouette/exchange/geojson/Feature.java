package mobi.chouette.exchange.geojson;

import java.util.Map;

import javax.xml.bind.annotation.XmlElement;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Feature extends Element {

	@XmlElement
	private String id;
	@XmlElement
	private Geometry geometry;

	@XmlElement
	private Map<String, Object> properties;

	public Feature(String id, Geometry geometry) {
		this(id, geometry, null);
	}

	public Feature(String id, Geometry geometry, Map<String, Object> properties) {
		super("Feature");
		this.id = id;
		this.geometry = geometry;
		this.properties = properties;
	}
}