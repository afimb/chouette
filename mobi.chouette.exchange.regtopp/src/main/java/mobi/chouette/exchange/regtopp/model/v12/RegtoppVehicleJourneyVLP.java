package mobi.chouette.exchange.regtopp.model.v12;

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
public class RegtoppVehicleJourneyVLP extends mobi.chouette.exchange.regtopp.model.v11.RegtoppVehicleJourneyVLP implements Serializable {

	public static final String FILE_EXTENSION = "VLP";

	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Field(at = 24, length = 5)
	private Integer vehicleId;
}
