package mobi.chouette.exchange.regtopp.model.v11;

import java.io.Serializable;

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
public class RegtoppPathwayGAV extends RegtoppObject implements Serializable {

	public static final String FILE_EXTENSION = "GAV";

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
	private String stopIdFrom;

	@Getter
	@Setter
	@Field(at = 12, length = 8)
	private String stopIdTo;

	@Getter
	@Setter
	@Field(at = 20, length = 2)
	private Integer duration;

	@Getter
	@Setter
	@Field(at = 22, length = 20)
	private String description;

	@Override
	public String getIndexingKey() {
		return adminCode + counter + stopIdFrom + stopIdTo;
	}

}
