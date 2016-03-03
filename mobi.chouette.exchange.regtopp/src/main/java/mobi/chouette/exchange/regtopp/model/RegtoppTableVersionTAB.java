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
public class RegtoppTableVersionTAB extends RegtoppObject implements Serializable {

	public static final String FILE_EXTENSION = "TAB";

	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Field(length = 3)
	private String adminCode;

	@Getter
	@Setter
	@Field(length = 1)
	private String counter;

	@Getter
	@Setter
	@Field(length = 4)
	private String lineId;

	@Getter
	@Setter
	@Field(length = 10)
	private String periodId;

	@Getter
	@Setter
	@Field(length = 8)
	private String name;

	@Getter
	@Setter
	@Field(length = 82)
	private String text;

	@Override
	public String getIndexingKey() {
		return adminCode+counter+lineId+periodId;
	}

}
