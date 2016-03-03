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
	@Field(length = 1)
	private String direction;

	@Getter
	@Setter
	@Field(length = 2)
	private String routeId;

	@Getter
	@Setter
	@Field(length = 3)
	private String sequenceNumberStop;

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
	
	@Override
	public String getIndexingKey() {
		return adminCode+counter+lineId+direction+routeId+sequenceNumberStop;
	}


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
