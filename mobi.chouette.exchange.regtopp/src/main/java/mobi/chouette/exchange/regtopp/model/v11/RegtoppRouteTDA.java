package mobi.chouette.exchange.regtopp.model.v11;

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

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Record(minOccurs = 1)
public class RegtoppRouteTDA extends RegtoppObject implements Serializable {

	public static final String FILE_EXTENSION = "TDA";

	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Field(at = 0, length = 8)
	private String stopId;

	@Getter
	@Setter
	@Field(at = 8, length = 3, handlerName = "drivingDuration")
	private Duration driverTimeArrival;

	@Getter
	@Setter
	@Field(at = 11, length = 3, handlerName = "drivingDuration")
	private Duration driverTimeDeparture;

	@Getter
	@Setter
	@Field(at = 14, length = 6)
	private Integer distance;

	@Override
	public String getIndexingKey() {
		// TODO This must be revisited. Not sure what to put in here yet
		return pad(getRecordLineNumber(), 7) +stopId;
	}
}
