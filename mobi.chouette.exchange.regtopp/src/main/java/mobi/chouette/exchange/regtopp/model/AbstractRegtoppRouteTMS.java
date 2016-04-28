package mobi.chouette.exchange.regtopp.model;

import java.io.Serializable;

import org.beanio.annotation.Field;
import org.beanio.annotation.Record;
import org.joda.time.Duration;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import mobi.chouette.exchange.regtopp.model.enums.DirectionType;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Record(minOccurs = 1)
public abstract class AbstractRegtoppRouteTMS extends RegtoppObject implements Serializable {

	public static final String FILE_EXTENSION = "TMS";

	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Field(at = 0, length = 3)
	protected String adminCode;

	@Getter
	@Setter
	@Field(at = 3, length = 1)
	protected String counter;

	@Getter
	@Setter
	@Field(at = 4, length = 4)
	protected String lineId;

	@Getter
	@Setter
	@Field(at = 8, length = 1, regex = "[12]{1}", format = "toString")
	protected DirectionType direction;

	@Getter
	@Setter
	@Field(at = 9, length = 2)
	protected String routeId;

	@Getter
	@Setter
	@Field(at = 11, length = 3)
	protected String sequenceNumberStop;

	public abstract String getStopId();

	public abstract String getStopIdDeparture();

	public abstract String getStopIdArrival();

	public abstract Duration getDriverTimeArrival();

	public abstract Duration getDriverTimeDeparture();

	public abstract String getDestinationId();

	public abstract String getRemarkId();

	public abstract String getMonitor();

	public abstract Integer getDistance();
	
	public String getFullStopId() {
		return getStopId();
	}

	public String getRouteKey() {
		if (lineId == null || direction == null || routeId == null) {
			throw new IllegalArgumentException("Key");
		}
		return lineId + direction + routeId;
	}

	@Override
	public String getIndexingKey() {
		return pad(adminCode, 3) + counter + pad(lineId, 4) + direction + pad(routeId, 2) + pad(sequenceNumberStop, 3);
	}

}
