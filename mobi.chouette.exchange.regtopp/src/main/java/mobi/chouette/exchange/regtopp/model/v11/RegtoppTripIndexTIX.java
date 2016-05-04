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
import mobi.chouette.exchange.regtopp.model.AbstractRegtoppTripIndexTIX;
import mobi.chouette.exchange.regtopp.model.enums.ParcelServiceType;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Record(minOccurs = 1)
public class RegtoppTripIndexTIX extends AbstractRegtoppTripIndexTIX implements Serializable {

	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Field(at = 47, length = 3)
	private Integer numStops;

	@Getter
	@Setter
	@Field(at = 50, length = 7)
	private String firstStop;

	@Getter
	@Setter
	@Field(at = 57, length = 1, regex = "[01]{1}", format = "toString")
	private ParcelServiceType parcelService;

	@Getter
	@Setter
	@Field(at = 58, length = 1)
	private Integer priceCode;


}
