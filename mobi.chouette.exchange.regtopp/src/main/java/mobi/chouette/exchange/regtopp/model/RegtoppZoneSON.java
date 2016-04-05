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
public class RegtoppZoneSON extends RegtoppObject implements Serializable {

	public static final String FILE_EXTENSION = "SON";

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
	@Field(length = 5)
	private String zoneId;

	@Getter
	@Setter
	@Field(length = 30)
	private String name;

	@Getter
	@Setter
	@Field(length = 6)
	private String zoneRef;

	@Override
	public String getIndexingKey() {
		return adminCode + counter + zoneId;
	}

}
