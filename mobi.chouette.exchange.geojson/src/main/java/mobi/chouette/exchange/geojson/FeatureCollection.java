package mobi.chouette.exchange.geojson;

import java.util.Collection;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@XmlRootElement
public class FeatureCollection extends Element {

	@XmlElement
	Collection<Feature> features;

	public FeatureCollection(Collection<Feature> features) {
		super("FeatureCollection");
		this.features = features;
	}
}
