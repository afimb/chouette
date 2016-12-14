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
import mobi.chouette.exchange.regtopp.model.AbstractRegtoppStopHPL;
import mobi.chouette.exchange.regtopp.model.enums.InterchangeWeight;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Record(minOccurs = 1)
public class RegtoppStopHPL extends AbstractRegtoppStopHPL implements Serializable {

	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Field(at = 83, length = 1,regex = "[0123]{1}", format = "toString")
	private InterchangeWeight interchangeType = InterchangeWeight.Normal;

	@Getter
	@Setter
	@Field(at = 84, length = 2)
	private Integer interchangeMinutes;

	@Getter
	@Setter
	@Field(at = 86, length = 1)
	private Integer coachClass;

}
