package mobi.chouette.exchange.regtopp.model;

import java.io.Serializable;

import org.beanio.annotation.Field;
import org.beanio.annotation.Record;
import org.joda.time.Duration;
import org.joda.time.Period;

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
public class RegtoppRouteTMS extends RegtoppObject implements Serializable {

	public static final String FILE_EXTENSION = "TMS";

	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Field(length = 3)
	private String adminCode;

	@Getter
	@Setter
	@Field(length = 1)
	private String counter;

	@Getter
	@Setter
	@Field(length = 4)
	private String lineId;

	@Getter
	@Setter
	@Field(length = 1, regex = "[12]{1}", format = "toString")
	private DirectionType direction;

	@Getter
	@Setter
	@Field(length = 2)
	private String routeId;

	@Getter
	@Setter
	@Field(length = 3)
	private Integer sequenceNumberStop;

	@Getter
	@Setter
	@Field(length = 8)
	private String stopId;

	@Getter
	@Setter
	@Field(length = 3, handlerName = "drivingDuration")
	private Duration driverTimeArrival;

	@Getter
	@Setter
	@Field(length = 2)
	private String stopIdArrival;

	@Getter
	@Setter
	@Field(length = 3, handlerName = "drivingDuration")
	private Duration driverTimeDeparture;

	@Getter
	@Setter
	@Field(length = 2)
	private String stopIdDeparture;

	@Getter
	@Setter
	@Field(length = 4)
	private String destinationId;

	@Getter
	@Setter
	@Field(length = 3)
	private String remarkId;

	@Getter
	@Setter
	@Field(length = 2)
	private String monitor;

	@Getter
	@Setter
	@Field(length = 6)
	private Integer distance;

	@Override
	public String getIndexingKey() {
		return pad(adminCode, 3) + counter + pad(lineId, 4) + direction + pad(routeId, 2) + pad(sequenceNumberStop, 3);
	}

}
