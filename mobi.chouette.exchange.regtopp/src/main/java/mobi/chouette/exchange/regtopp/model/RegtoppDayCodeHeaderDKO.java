package mobi.chouette.exchange.regtopp.model;

import java.io.Serializable;
import java.util.Date;

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
@Record(minOccurs = 1, maxOccurs = 1, order = 1)
public class RegtoppDayCodeHeaderDKO extends RegtoppObject implements Serializable {

	public static final String FILE_EXTENSION = "DKO";

	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Field(length = 6, type = java.util.Date.class, format = "yyMMdd")
	private Date date;

	@Getter
	@Setter
	@Field(length = 1)
	private Integer weekDay;

	@Override
	public String getIndexingKey() {
		throw new RuntimeException("No key registerted");
	}
}
