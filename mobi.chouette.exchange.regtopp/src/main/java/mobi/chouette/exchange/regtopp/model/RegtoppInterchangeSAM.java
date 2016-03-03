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
public class RegtoppInterchangeSAM extends RegtoppObject implements Serializable {

	public static final String FILE_EXTENSION = "SAM";

	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Field(length = 3)
	private String adminCode1;

	@Getter
	@Setter
	@Field(length = 1)
	private String counter1;

	@Getter
	@Setter
	@Field(length = 4)
	private String lineId1;

	@Getter
	@Setter
	@Field(length = 4)
	private String tripId1;

	@Getter
	@Setter
	@Field(length = 3)
	private String typeOfTraffic1;

	@Getter
	@Setter
	@Field(length = 3)
	private String adminCode2;

	@Getter
	@Setter
	@Field(length = 1)
	private String counter2;

	@Getter
	@Setter
	@Field(length = 4)
	private String lineId2;

	@Getter
	@Setter
	@Field(length = 4)
	private String tripId2;

	@Getter
	@Setter
	@Field(length = 3)
	private String typeOfTraffic2;

	@Getter
	@Setter
	@Field(length = 4)
	private Integer arrivalTime1;

	@Getter
	@Setter
	@Field(length = 4)
	private Integer departureTime2;

	@Getter
	@Setter
	@Field(length = 3)
	private String notInUse;

	@Getter
	@Setter
	@Field(length = 8)
	private Integer stopId;

	@Getter
	@Setter
	@Field(length = 1)
	private Integer interchangeCode;

	
	@Override
	public String getIndexingKey() {
		return (adminCode1+counter1+lineId1+tripId1)+(adminCode2+counter2+lineId2+tripId2);
	}

}
