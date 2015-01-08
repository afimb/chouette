package mobi.chouette.validation.parameters;

import javax.xml.bind.annotation.XmlAttribute;

import lombok.Data;

@Data
public class TransportMode {

	@XmlAttribute(name = "inter_stop_area_distance_min")
	private Long interStopAreaDistanceMin;
	@XmlAttribute(name = "inter_stop_area_distance_max")
	private Long interStopAreaDistanceMax;
	@XmlAttribute(name = "speed_max")
	private Long speedMax;
	@XmlAttribute(name = "speed_min")
	private Long speedMin;
	@XmlAttribute(name = "inter_stop_duration_variation_max")
	private Long interStopDurationVariationMax;
	@XmlAttribute(name = "allowed_transport")
	private Long allowedTransport;

}
