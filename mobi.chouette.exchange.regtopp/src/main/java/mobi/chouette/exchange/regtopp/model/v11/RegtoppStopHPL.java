package mobi.chouette.exchange.regtopp.model.v11;

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
import mobi.chouette.exchange.regtopp.model.RegtoppObject;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Record(minOccurs = 1)
public class RegtoppStopHPL extends RegtoppObject implements Serializable {

	public static final String FILE_EXTENSION = "HPL";

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
	@Field(at = 4, length = 8)
	private String stopId;

	@Getter
	@Setter
	@Field(at = 12, length = 30)
	private String fullName;

	@Getter
	@Setter
	@Field(at = 42, length = 5)
	private String shortName;

	@Getter
	@Setter
	@Field(at = 47, length = 6)
	private String zoneShortName;

	@Getter
	@Setter
	@Field(at = 53, length = 10)
	private BigDecimal x;

	@Getter
	@Setter
	@Field(at = 63, length = 10)
	private BigDecimal y;

	@Getter
	@Setter
	@Field(at = 73, length = 5)
	private Integer zoneId1;

	@Getter
	@Setter
	@Field(at = 78, length = 5)
	private Integer zoneId2;

	@Getter
	@Setter
	@Field(at = 83, length = 1)
	private Integer interchangeType;

	@Getter
	@Setter
	@Field(at = 84, length = 2)
	private Integer interchangeMinutes;

	@Getter
	@Setter
	@Field(at = 86, length = 1)
	private Integer coachClass;

	@Override
	public String getIndexingKey() {
		return adminCode + counter + stopId;
	}
	
	public String getFullStopId() {
		return stopId;
	}

}
