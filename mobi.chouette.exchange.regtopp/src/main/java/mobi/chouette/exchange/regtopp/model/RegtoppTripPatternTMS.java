package mobi.chouette.exchange.regtopp.model;

import java.io.Serializable;

import org.beanio.annotation.Field;
import org.beanio.annotation.Record;

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
public class RegtoppTripPatternTMS extends RegtoppObject implements Serializable {

	public static final String FILE_EXTENSION = "TMS";

	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Field(length = 3)
	private Integer adminCode;

	@Getter
	@Setter
	@Field(length = 1)
	private Integer counter;

	@Getter
	@Setter
	@Field(length = 4)
	private Integer lineId;

	@Getter
	@Setter
	@Field(length = 1, regex = "[12]{1}", format = "toString")
	private DirectionType direction;

	@Getter
	@Setter
	@Field(length = 2)
	private Integer routeId;

	@Getter
	@Setter
	@Field(length = 3)
	private Integer sequenceNumberStop;

	@Getter
	@Setter
	@Field(length = 8)
	private Integer stopId;

	@Getter
	@Setter
	@Field(length = 3)
	private Integer driverTimeArrival;

	@Getter
	@Setter
	@Field(length = 2)
	private Integer stopIdArrival;

	@Getter
	@Setter
	@Field(length = 3)
	private Integer driverTimeDeparture;

	@Getter
	@Setter
	@Field(length = 2)
	private Integer stopIdDeparture;

	@Getter
	@Setter
	@Field(length = 4)
	private Integer destinationId;

	@Getter
	@Setter
	@Field(length = 3)
	private Integer remarkId;

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
