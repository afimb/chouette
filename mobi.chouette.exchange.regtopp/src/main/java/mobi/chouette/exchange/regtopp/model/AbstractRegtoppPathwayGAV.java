package mobi.chouette.exchange.regtopp.model;

import java.io.Serializable;

import org.beanio.annotation.Field;

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
public abstract class AbstractRegtoppPathwayGAV extends RegtoppObject implements Serializable {

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

	public abstract Integer getDuration();

	public abstract String getDescription();

	@Override
	public String getIndexingKey() {
		return adminCode + counter + stopIdFrom + stopIdTo;
	}

}
