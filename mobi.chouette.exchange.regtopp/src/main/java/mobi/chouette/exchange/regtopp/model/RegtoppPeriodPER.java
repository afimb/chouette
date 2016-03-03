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
	@Field(length = 3)
	private Integer adminCode;

	@Getter
	@Setter
	@Field(length = 1)
	private Integer counter;

	@Getter
	@Setter
	@Field(length = 10)
	private String periodId;

	@Getter
	@Setter
	@Field(length = 1)
	private Integer sequenceNumberDateInterval;

	@Getter
	@Setter
	@Field(length = 6)
	private String startDate;

	@Getter
	@Setter
	@Field(length = 6)
	private String endDate;

	@Getter
	@Setter
	@Field(length = 73)
	private String periodText;

}
