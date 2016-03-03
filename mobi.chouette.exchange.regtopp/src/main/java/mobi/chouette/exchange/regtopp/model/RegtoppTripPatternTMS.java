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
	private Integer administrationCode;

	@Getter
	@Setter
	@Field(length = 1)
	private Integer counter;

	@Getter
	@Setter
	@Field(length = 4)
	private Integer lineNumber;

	@Getter
	@Setter
	@Field(length = 1)
	private Integer direction;

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
	private String remarkId;

	@Getter
	@Setter
	@Field(length = 2)
	private String monitor;

	@Getter
	@Setter
	@Field(length = 6)
	private Integer distance;

	@AllArgsConstructor
	public enum DirectionType implements Serializable {
		Outbound, Inbound;

	}

	@AllArgsConstructor
	public enum BikesAllowedType implements Serializable {
		NoInformation, Allowed, NoAllowed;

	}

	@AllArgsConstructor
	public enum WheelchairAccessibleType implements Serializable {
		NoInformation, Allowed, NoAllowed;

	}
}
