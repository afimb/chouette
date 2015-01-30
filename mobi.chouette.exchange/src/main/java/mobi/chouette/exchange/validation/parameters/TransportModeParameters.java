package mobi.chouette.exchange.validation.parameters;

import javax.xml.bind.annotation.XmlAttribute;

public class TransportModeParameters 
{
	private Integer allowedTransport;
	
	private Integer interStopAreaDistanceMin;
	
	private Integer interStopAreaDistanceMax;

	private Integer speedMax;
	
	private Integer speedMin;
	
	private Integer interStopDurationVariationMax;

	/**
	 * @return the allowedTransport
	 */
	@XmlAttribute(name = "allowed_transport")
	public Integer getAllowedTransport() {
		return allowedTransport;
	}

	/**
	 * @param allowedTransport the allowedTransport to set
	 */
	public void setAllowedTransport(Integer allowedTransport) {
		this.allowedTransport = allowedTransport;
	}

	/**
	 * @return the interStopAreaDistanceMin
	 */
	@XmlAttribute(name = "inter_stop_area_distance_min")
	public Integer getInterStopAreaDistanceMin() {
		return interStopAreaDistanceMin;
	}

	/**
	 * @param interStopAreaDistanceMin the interStopAreaDistanceMin to set
	 */
	public void setInterStopAreaDistanceMin(Integer interStopAreaDistanceMin) {
		this.interStopAreaDistanceMin = interStopAreaDistanceMin;
	}

	/**
	 * @return the interStopAreaDistanceMax
	 */
	@XmlAttribute(name = "inter_stop_area_distance_max")
	public Integer getInterStopAreaDistanceMax() {
		return interStopAreaDistanceMax;
	}

	/**
	 * @param interStopAreaDistanceMax the interStopAreaDistanceMax to set
	 */
	public void setInterStopAreaDistanceMax(Integer interStopAreaDistanceMax) {
		this.interStopAreaDistanceMax = interStopAreaDistanceMax;
	}

	/**
	 * @return the speedMax
	 */
	@XmlAttribute(name = "speed_max")
	public Integer getSpeedMax() {
		return speedMax;
	}

	/**
	 * @param speedMax the speedMax to set
	 */
	public void setSpeedMax(Integer speedMax) {
		this.speedMax = speedMax;
	}

	/**
	 * @return the speedMin
	 */
	@XmlAttribute(name = "speed_min")
	public Integer getSpeedMin() {
		return speedMin;
	}

	/**
	 * @param speedMin the speedMin to set
	 */
	public void setSpeedMin(Integer speedMin) {
		this.speedMin = speedMin;
	}

	/**
	 * @return the interStopDurationVariationMax
	 */
	@XmlAttribute(name = "inter_stop_duration_variation_max")
	public Integer getInterStopDurationVariationMax() {
		return interStopDurationVariationMax;
	}

	/**
	 * @param interStopDurationVariationMax the interStopDurationVariationMax to set
	 */
	public void setInterStopDurationVariationMax(
			Integer interStopDurationVariationMax) {
		this.interStopDurationVariationMax = interStopDurationVariationMax;
	}

}
