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
public class RegtoppVehicleJourneyVLP extends RegtoppObject implements Serializable {

	public static final String FILE_EXTENSION = "VLP";

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
	@Field(at = 4, length = 6)
	private String vehicleJourneyId;

	@Getter
	@Setter
	@Field(at = 10, length = 4)
	private String dayCodeId;

	@Getter
	@Setter
	@Field(at = 14, length = 2)
	private String sequenceNumberTrip;

	@Getter
	@Setter
	@Field(at = 16, length = 4)
	private Integer lineId;

	@Getter
	@Setter
	@Field(at = 20, length = 4)
	private Integer tripId;

	@Getter
	@Setter
	@Field(at = 24, length = 5)
	private Integer id;

	@Override
	public String getIndexingKey() {
		return adminCode + counter + vehicleJourneyId + dayCodeId + sequenceNumberTrip;
	}
}
