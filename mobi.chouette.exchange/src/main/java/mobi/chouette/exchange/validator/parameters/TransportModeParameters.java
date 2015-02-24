package mobi.chouette.exchange.validator.parameters;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class TransportModeParameters {
	@XmlAttribute(name = "allowed_transport")
	private Integer allowedTransport;

	@XmlAttribute(name = "inter_stop_area_distance_min")
	private Integer interStopAreaDistanceMin;

	@XmlAttribute(name = "inter_stop_area_distance_max")
	private Integer interStopAreaDistanceMax;

	@XmlAttribute(name = "speed_max")
	private Integer speedMax;

	@XmlAttribute(name = "speed_min")
	private Integer speedMin;

	@XmlAttribute(name = "inter_stop_duration_variation_max")
	private Integer interStopDurationVariationMax;

}
