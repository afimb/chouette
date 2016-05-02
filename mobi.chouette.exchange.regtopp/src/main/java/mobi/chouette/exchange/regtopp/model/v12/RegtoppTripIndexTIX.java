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
import mobi.chouette.exchange.regtopp.model.AbstractRegtoppTripIndexTIX;
import mobi.chouette.exchange.regtopp.model.enums.AnnouncementType;
import mobi.chouette.exchange.regtopp.model.enums.ParcelServiceType;
import mobi.chouette.exchange.regtopp.model.enums.TrafficType;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Record(minOccurs = 1)
public class RegtoppTripIndexTIX extends AbstractRegtoppTripIndexTIX implements Serializable {

	private static final long serialVersionUID = 1L;


	@Getter
	@Setter
	@Field(at = 47, length = 1, regex = "[01]{1}", format = "toString")
	private ParcelServiceType parcelService;

	@Getter
	@Setter
	@Field(at = 48, length = 1)
	private Integer priceCode;

	@Getter
	@Setter
	@Field(at = 49, length = 1, regex = "[01]{1}", format = "toString")
	private AnnouncementType notificationType = AnnouncementType.Announced;

	@Getter
	@Setter
	@Field(at = 50, length = 1, regex = "[01]{1}", format = "toString")
	private TrafficType trafficType = TrafficType.Normal;

	@Getter
	@Setter
	@Field(at = 51, length = 4)
	private String destinationIdArrivalRef;

	@Getter
	@Setter
	@Field(at = 55, length = 7)
	private String weekdaysOfService;


	public String getRouteKey() {
		return lineId + direction + routeIdRef;
	}
}
