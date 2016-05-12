package mobi.chouette.exchange.regtopp.model.v13;

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
import mobi.chouette.exchange.regtopp.model.AbstractRegtoppRouteTMS;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Record(minOccurs = 1)
public class RegtoppRouteTMS extends AbstractRegtoppRouteTMS implements Serializable {

	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Field(at = 14, length = 1)
	protected String stopType;

	@Getter
	@Setter
	@Field(at = 15, length = 8)
	protected String stopId;

	@Getter
	@Setter
	@Field(at = 23, length = 2)
	private String stopPointIdDeparture;

	@Getter
	@Setter
	@Field(at = 25, length = 2)
	private String stopPointIdArrival;

	@Getter
	@Setter
	@Field(at = 27, length = 4, handlerName = "drivingDuration")
	private Duration driverTimeArrival;

	@Getter
	@Setter
	@Field(at = 31, length = 4, handlerName = "drivingDuration")
	private Duration driverTimeDeparture;

	@Getter
	@Setter
	@Field(at = 35, length = 4)
	private String destinationId;

	@Getter
	@Setter
	@Field(at = 39, length = 3)
	private String remarkId;

	@Getter
	@Setter
	@Field(at = 42, length = 2)
	private String monitor;

	@Getter
	@Setter
	@Field(at = 44, length = 6)
	private Integer distance;

	@Override
	public String getFullStopId() {
		return stopId+getStopPointIdArrival();
	}

}
