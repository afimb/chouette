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
	@Field(length = 4)
	private String tripId;

	@Getter
	@Setter
	@Field(length = 3, regex = "[0-9]{3}", format="toString")
	private TransportType typeOfService;

	@Getter
	@Setter
	@Field(length = 4)
	private Integer dayCodeRef;

	@Getter
	@Setter
	@Field(length = 3)
	private Integer operatorCode;

	@Getter
	@Setter
	@Field(length = 3)
	private Integer remarkId1;

	@Getter
	@Setter
	@Field(length = 3)
	private Integer remarkId2;

	@Getter
	@Setter
	@Field(length = 4)
	private Integer destinationIdDeparture;

	@Getter
	@Setter
	@Field(length = 8,keepPadding = false)
	private String lineNumberVisible;

	@Getter
	@Setter
	@Field(length = 1, regex = "[12]{1}", format="toString")
	private DirectionType direction;

	@Getter
	@Setter
	@Field(length = 2)
	private String routeId;

	@Getter
	@Setter
	@Field(length = 4,regex = "[0-2][0-9]{3}")
	private String departureTime;

	@Getter
	@Setter
	@Field(length = 1, regex = "[01]{1}", format="toString")
	private ParcelServiceType parcelService;

	@Getter
	@Setter
	@Field(length = 1)
	private Integer priceCode;

	@Getter
	@Setter
	@Field(length = 1, regex = "[01]{1}", format="toString")
	private AnnouncementType notificationType;

	@Getter
	@Setter
	@Field(length = 1, regex = "[01]{1}", format="toString")
	private TrafficType trafficType;

	@Getter
	@Setter
	@Field(length = 4)
	private Integer destinationIdArrival;

	@Getter
	@Setter
	@Field(length = 7)
	private String weekdaysOfService;
	
	@Override
	public String getIndexingKey() {
		return adminCode+counter+lineId+tripId;
	}
}
