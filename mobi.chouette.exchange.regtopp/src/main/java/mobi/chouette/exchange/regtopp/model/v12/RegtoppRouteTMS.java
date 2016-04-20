package mobi.chouette.exchange.regtopp.model.v12;

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
	@Field(at = 14, length = 8)
	protected String stopId;

	@Getter
	@Setter
	@Field(at = 22, length = 3, handlerName = "drivingDuration")
	private Duration driverTimeArrival;

	@Getter
	@Setter
	@Field(at = 25, length = 2)
	private String stopIdArrival;

	@Getter
	@Setter
	@Field(at = 27, length = 3, handlerName = "drivingDuration")
	private Duration driverTimeDeparture;

	@Getter
	@Setter
	@Field(at = 30, length = 2)
	private String stopIdDeparture;

	@Getter
	@Setter
	@Field(at = 32, length = 4)
	private String destinationId;

	@Getter
	@Setter
	@Field(at = 36, length = 3)
	private String remarkId;

	@Getter
	@Setter
	@Field(at = 39, length = 2)
	private String monitor;

	@Getter
	@Setter
	@Field(at = 41, length = 6)
	private Integer distance;
}
