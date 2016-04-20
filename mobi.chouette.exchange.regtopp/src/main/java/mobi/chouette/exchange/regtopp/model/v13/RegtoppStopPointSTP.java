package mobi.chouette.exchange.regtopp.model.v13;

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
import mobi.chouette.exchange.regtopp.model.RegtoppObject;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Record(minOccurs = 1)
public  class RegtoppStopPointSTP extends RegtoppObject implements Serializable {

	public static final String FILE_EXTENSION = "STP";

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
	@Field(at = 12, length = 2)
	protected String stopPointId;

	@Getter
	@Setter
	@Field(at = 14, length = 2)
	protected String stopPointName;

	@Getter
	@Setter
	@Field(at = 16, length = 10)
	protected BigDecimal x;

	@Getter
	@Setter
	@Field(at = 26, length = 10)
	protected BigDecimal y;

	@Getter
	@Setter
	@Field(at = 36, length = 1)
	protected boolean informationSign;

	@Getter
	@Setter
	@Field(at = 37, length = 1)
	protected boolean realtimeSign;

	@Getter
	@Setter
	@Field(at = 38, length = 1)
	protected boolean bench;

	@Getter
	@Setter
	@Field(at = 39, length = 1)
	protected boolean shelter;

	@Getter
	@Setter
	@Field(at = 40, length = 6)
	protected String otherInfrastructure;

	@Getter
	@Setter
	@Field(at = 46, length = 40)
	protected String description;

	@Getter
	@Setter
	@Field(at = 86, length = 8)
	protected String sourceStopPointId;

	

	// TODO
	@Override
	public String getIndexingKey() {
		return adminCode + counter + stopId + stopPointId;
	}
	
	// TODO
	public String getFullStopId() {
		return stopId + stopPointId;
	}

}
