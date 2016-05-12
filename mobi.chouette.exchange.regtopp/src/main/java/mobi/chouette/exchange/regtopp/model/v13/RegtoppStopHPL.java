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
import mobi.chouette.exchange.regtopp.model.AbstractRegtoppStopHPL;
import mobi.chouette.exchange.regtopp.model.enums.StopType;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Record(minOccurs = 1)
public class RegtoppStopHPL extends AbstractRegtoppStopHPL implements Serializable {

	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Field(at = 83, length = 1, regex = "[01]{1}", format = "toString")
	private StopType type; 

	@Getter
	@Setter
	@Field(at = 84, length = 1)
	private Integer interchangeType;

	@Getter
	@Setter
	@Field(at = 85, length = 2)
	private Integer interchangeMinutes;

	@Getter
	@Setter
	@Field(at = 87, length = 1)
	private Integer coachClass;

}
