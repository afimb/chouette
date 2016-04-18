package mobi.chouette.exchange.regtopp.model.v11;

import java.io.Serializable;

import org.beanio.annotation.Field;
import org.beanio.annotation.Record;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import mobi.chouette.exchange.regtopp.model.RegtoppObject;
import mobi.chouette.exchange.regtopp.model.enums.TransportType;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Record(minOccurs = 1)
public class RegtoppInterchangeSAM extends RegtoppObject implements Serializable {

	public static final String FILE_EXTENSION = "SAM";

	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Field(at = 0, length = 3)
	private String adminCode1;

	@Getter
	@Setter
	@Field(at = 3, length = 1)
	private String counter1;

	@Getter
	@Setter
	@Field(at = 4, length = 4)
	private String lineId1;

	@Getter
	@Setter
	@Field(at = 8, length = 4)
	private String tripId1;

	@Getter
	@Setter
	@Field(at = 12, length = 3, regex = "[0-9]{3}", format = "toString")
	private TransportType typeOfService1;

	@Getter
	@Setter
	@Field(at = 15, length = 3)
	private String adminCode2;

	@Getter
	@Setter
	@Field(at = 18, length = 1)
	private String counter2;

	@Getter
	@Setter
	@Field(at = 19, length = 4)
	private String lineId2;

	@Getter
	@Setter
	@Field(at = 23, length = 4)
	private String tripId2;

	@Getter
	@Setter
	@Field(at = 27, length = 3, regex = "[0-9]{3}", format = "toString")
	private TransportType typeOfService2;

	@Getter
	@Setter
	@Field(at = 30, length = 4)
	private Integer arrivalTime1;

	@Getter
	@Setter
	@Field(at = 34, length = 4)
	private Integer departureTime2;

	@Getter
	@Setter
	@Field(at = 38, length = 4)
	private String notInUse;

	@Getter
	@Setter
	@Field(at = 42, length = 8)
	private Integer stopId;

	@Getter
	@Setter
	@Field(at = 50, length = 1)
	private Integer interchangeCode;

	@Override
	public String getIndexingKey() {
		return (adminCode1 + counter1 + lineId1 + tripId1) + (adminCode2 + counter2 + lineId2 + tripId2);
	}

}
