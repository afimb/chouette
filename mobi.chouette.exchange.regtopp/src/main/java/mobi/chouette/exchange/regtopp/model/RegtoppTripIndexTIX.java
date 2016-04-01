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
import mobi.chouette.exchange.regtopp.model.enums.AnnouncementType;
import mobi.chouette.exchange.regtopp.model.enums.DirectionType;
import mobi.chouette.exchange.regtopp.model.enums.ParcelServiceType;
import mobi.chouette.exchange.regtopp.model.enums.TrafficType;
import mobi.chouette.exchange.regtopp.model.enums.TransportType;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Record(minOccurs = 1)
public class RegtoppTripIndexTIX extends RegtoppObject implements Serializable {

	public static final String FILE_EXTENSION = "TIX";

	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Field(at = 0,length = 3)
	private String adminCode;

	@Getter
	@Setter
	@Field(at = 3,length = 1)
	private String counter;

	@Getter
	@Setter
	@Field(at = 4,length = 4)
	private String lineId;

	@Getter
	@Setter
	@Field(at = 8,length = 4)
	private String tripId;

	@Getter
	@Setter
	@Field(at = 12,length = 3, regex = "[0-9]{3}", format = "toString")
	private TransportType typeOfService;

	@Getter
	@Setter
	@Field(at = 15,length = 4)
	private Integer dayCodeRef;

	@Getter
	@Setter
	@Field(at = 19,length = 3)
	private Integer operatorCode;

	@Getter
	@Setter
	@Field(at = 22,length = 3)
	private String remarkId1;

	@Getter
	@Setter
	@Field(at = 25,length = 3)
	private String remarkId2;

	@Getter
	@Setter
	@Field(at = 28,length = 4)
	private String destinationIdDeparture;

	@Getter
	@Setter
	@Field(at = 32,length = 8, trim = true)
	private String lineNumberVisible;

	@Getter
	@Setter
	@Field(at = 40,length = 1, regex = "[12]{1}", format = "toString")
	private DirectionType direction;

	@Getter
	@Setter
	@Field(at = 41,length = 2)
	private String routeId;

	@Getter
	@Setter
	@Field(at = 43,length = 4, regex = "[0-2][0-9]{3}", handlerName = "departureTime")
	// This is period since we can have start times like 2415 which means 00:15 the next day. This cannot be represented with LocalTime.
	// Therefore the logic later must be able to 
	private Duration departureTime;

	@Getter
	@Setter
	@Field(at = 47,length = 1, regex = "[01]{1}", format = "toString")
	private ParcelServiceType parcelService;

	@Getter
	@Setter
	@Field(at = 48,length = 1)
	private Integer priceCode;

	@Getter
	@Setter
	@Field(at = 49,length = 1, regex = "[01]{1}", format = "toString")
	private AnnouncementType notificationType;

	@Getter
	@Setter
	@Field(at = 50,length = 1, regex = "[01]{1}", format = "toString")
	private TrafficType trafficType;

	@Getter
	@Setter
	@Field(at = 51,length = 4)
	private String destinationIdArrival;

	@Getter
	@Setter
	@Field(at = 55,length = 7)
	private String weekdaysOfService;

	@Override
	public String getIndexingKey() {
		return adminCode + counter + lineId + tripId;
	}
}
