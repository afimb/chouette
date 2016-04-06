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

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Record(minOccurs = 1)
public class RegtoppPeriodPER extends RegtoppObject implements Serializable {

	public static final String FILE_EXTENSION = "PER";

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
	@Field(at = 4, length = 10)
	private String periodId;

	@Getter
	@Setter
	@Field(at = 14, length = 1)
	private String sequenceNumberDateInterval;

	@Getter
	@Setter
	@Field(at = 15, length = 6)
	private String startDate;

	@Getter
	@Setter
	@Field(at = 21, length = 6)
	private String endDate;

	@Getter
	@Setter
	@Field(at = 27, length = 73)
	private String periodText;

	@Override
	public String getIndexingKey() {
		return adminCode + counter + periodId + sequenceNumberDateInterval;
	}

}
