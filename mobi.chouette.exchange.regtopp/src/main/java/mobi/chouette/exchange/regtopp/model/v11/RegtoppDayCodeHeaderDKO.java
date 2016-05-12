package mobi.chouette.exchange.regtopp.model.v11;

import java.io.Serializable;

import org.beanio.annotation.Field;
import org.beanio.annotation.Record;
import org.joda.time.LocalDate;

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
@Record(name = "header", minOccurs = 1, maxOccurs = 1, order = 2)
public class RegtoppDayCodeHeaderDKO extends RegtoppObject implements Serializable {

	public static final String FILE_EXTENSION = "DKO";

	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Field(at = 0, length = 6, type = LocalDate.class, handlerName = "localDate")
	private LocalDate date;

	@Getter
	@Setter
	@Field(at = 6, length = 1)
	private Integer weekDay;

	@Override
	public String getIndexingKey() {
		throw new RuntimeException("No key registerted");
	}
}
