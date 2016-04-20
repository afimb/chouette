package mobi.chouette.exchange.regtopp.model.v13;

import java.io.Serializable;

import org.beanio.annotation.Field;
import org.beanio.annotation.Record;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import mobi.chouette.exchange.regtopp.model.AbstractRegtoppPathwayGAV;
import mobi.chouette.exchange.regtopp.model.enums.PathwayType;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Record(minOccurs = 1)
public class RegtoppPathwayGAV extends AbstractRegtoppPathwayGAV implements Serializable {

	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Field(at = 20, length = 1,regex = "[123]{1}", format = "toString")
	private PathwayType type;

	@Getter
	@Setter
	@Field(at = 21, length = 2)
	private Integer duration;

	@Getter
	@Setter
	@Field(at = 23, length = 20)
	private String description;
}
