package mobi.chouette.exchange.validator.parameters;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class TransportModeParameters {
	@XmlElement(name = "allowed_transport",defaultValue="0")
	private int allowedTransport=0;

	@XmlElement(name = "inter_stop_area_distance_min")
	private Integer interStopAreaDistanceMin;

	@XmlElement(name = "inter_stop_area_distance_max")
	private Integer interStopAreaDistanceMax;

	@XmlElement(name = "speed_max")
	private Integer speedMax;

	@XmlElement(name = "speed_min")
	private Integer speedMin;

	@XmlElement(name = "inter_stop_duration_variation_max")
	private Integer interStopDurationVariationMax;

}
