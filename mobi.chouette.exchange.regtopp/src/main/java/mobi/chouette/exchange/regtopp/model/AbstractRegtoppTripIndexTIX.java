package mobi.chouette.exchange.regtopp.model;

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
import mobi.chouette.exchange.regtopp.model.enums.DirectionType;
import mobi.chouette.exchange.regtopp.model.enums.TransportType;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Record(minOccurs = 1)
public abstract class AbstractRegtoppTripIndexTIX extends RegtoppObject implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String FILE_EXTENSION = "TIX";

	@Getter
	@Setter
	@Field(at = 0, length = 3)
	protected String adminCode;

	@Getter
	@Setter
	@Field(at = 3, length = 1)
	protected String counter;

	@Getter
	@Setter
	@Field(at = 4, length = 4)
	protected String lineId;

	@Getter
	@Setter
	@Field(at = 8, length = 4)
	protected String tripId;

	@Getter
	@Setter
	@Field(at = 12, length = 3, regex = "[0-9]{3}", format = "toString")
	protected TransportType typeOfService;

	@Getter
	@Setter
	@Field(at = 15, length = 4)
	protected String dayCodeRef;

	@Getter
	@Setter
	@Field(at = 19, length = 3)
	protected String operatorCode;

	@Getter
	@Setter
	@Field(at = 22, length = 3)
	protected String footnoteId1Ref;

	@Getter
	@Setter
	@Field(at = 25, length = 3)
	protected String footnoteId2Ref;

	@Getter
	@Setter
	@Field(at = 28, length = 4)
	protected String destinationIdDepartureRef;

	@Getter
	@Setter
	@Field(at = 32, length = 8, trim = true)
	protected String lineNumberVisible;

	@Getter
	@Setter
	@Field(at = 40, length = 1, regex = "[12]{1}", format = "toString")
	protected DirectionType direction;

	@Getter
	@Setter
	@Field(at = 41, length = 2)
	protected String routeIdRef;

	@Getter
	@Setter
	@Field(at = 43, length = 4, regex = "[0-2][0-9]{3}", handlerName = "departureTime")
	// This is period since we can have start times like 2415 which means 00:15 the next day. This cannot be represented with LocalTime.
	// Therefore the logic later must be able to
	protected Duration departureTime;
	
	public String getIndexingKey() {
		return adminCode + counter + lineId + tripId;
	}
	public String getRouteKey() {
		return lineId + direction + routeIdRef;
	}

}
