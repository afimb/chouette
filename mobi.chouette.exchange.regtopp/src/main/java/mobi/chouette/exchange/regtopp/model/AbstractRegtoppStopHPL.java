package mobi.chouette.exchange.regtopp.model;

import java.io.Serializable;
import java.math.BigDecimal;

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
public abstract class AbstractRegtoppStopHPL extends RegtoppObject implements Serializable {

	public static final String FILE_EXTENSION = "HPL";

	private static final long serialVersionUID = 1L;

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
	@Field(at = 4, length = 8)
	protected String stopId;

	@Getter
	@Setter
	@Field(at = 12, length = 30)
	protected String fullName;

	@Getter
	@Setter
	@Field(at = 42, length = 5)
	protected String shortName;

	@Getter
	@Setter
	@Field(at = 47, length = 6)
	protected String zoneShortName;

	@Getter
	@Setter
	@Field(at = 53, length = 10)
	protected BigDecimal x;

	@Getter
	@Setter
	@Field(at = 63, length = 10)
	protected BigDecimal y;

	@Getter
	@Setter
	@Field(at = 73, length = 5)
	protected Integer zoneId1;

	@Getter
	@Setter
	@Field(at = 78, length = 5)
	protected Integer zoneId2;

	public abstract Integer getInterchangeType();

	public abstract Integer getInterchangeMinutes();

	public abstract Integer getCoachClass();

	
	@Override
	public String getIndexingKey() {
		return adminCode + counter + stopId;
	}
	
	public String getFullStopId() {
		return stopId;
	}

}
