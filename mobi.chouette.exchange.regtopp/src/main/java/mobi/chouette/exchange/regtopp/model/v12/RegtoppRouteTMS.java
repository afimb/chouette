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
import mobi.chouette.exchange.regtopp.model.RegtoppObject;
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
	@Field(at = 0, length = 3)
	private String adminCode;

	@Getter
	@Setter
	@Field(at = 3, length = 1)
	private String counter;

	@Getter
	@Setter
	@Field(at = 4, length = 4)
	private String lineId;

	@Getter
	@Setter
	@Field(at = 8, length = 1, regex = "[12]{1}", format = "toString")
	private DirectionType direction;

	@Getter
	@Setter
	@Field(at = 9, length = 2)
	private String routeId;

	@Getter
	@Setter
	@Field(at = 11, length = 3)
	private String sequenceNumberStop;

	@Getter
	@Setter
	@Field(at = 14, length = 8)
	private String stopId;

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

	public String getRouteKey() {
		if (lineId == null || direction == null || routeId == null){
			throw new IllegalArgumentException("Key");
		}
		return lineId + direction + routeId;
	}

	@Override
	public String getIndexingKey() {
		return pad(adminCode, 3) + counter + pad(lineId, 4) + direction + pad(routeId, 2) + pad(sequenceNumberStop, 3);
	}

}
