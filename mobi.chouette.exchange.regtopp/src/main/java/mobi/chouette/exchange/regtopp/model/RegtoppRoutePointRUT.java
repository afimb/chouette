package mobi.chouette.exchange.regtopp.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.net.URL;
import java.util.TimeZone;

import org.beanio.annotation.Field;
import org.beanio.annotation.Record;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import mobi.chouette.exchange.regtopp.model.enums.DirectionType;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Record(minOccurs = 1)
public class RegtoppRoutePointRUT extends RegtoppObject implements Serializable {

	public static final String FILE_EXTENSION = "RUT";

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
	@Field(at = 4, length = 4)
	private String lineId;

	@Getter
	@Setter
	@Field(at = 8, length = 10)
	private String periodId;

	@Getter
	@Setter
	@Field(at = 18, length = 3)
	private String sequenceNumberRoutePoint;

	@Getter
	@Setter
	@Field(at = 21, length = 1, regex = "[123]{1}", format = "toString")
	private DirectionType direction;

	@Getter
	@Setter
	@Field(at = 22, length = 8)
	private String stopId;

	@Override
	public String getIndexingKey() {
		return adminCode + counter + lineId + periodId + sequenceNumberRoutePoint;
	}

}
