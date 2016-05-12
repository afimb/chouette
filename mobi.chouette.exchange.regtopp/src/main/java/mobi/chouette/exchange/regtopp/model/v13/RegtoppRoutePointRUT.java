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

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Record(minOccurs = 1)
public class RegtoppRoutePointRUT extends mobi.chouette.exchange.regtopp.model.v12.RegtoppRoutePointRUT implements Serializable {

	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Field(at = 30, length = 2)
	private String stopPointId;

}
