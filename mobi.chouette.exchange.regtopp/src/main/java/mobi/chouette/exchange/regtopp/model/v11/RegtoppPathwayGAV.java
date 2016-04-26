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
import mobi.chouette.exchange.regtopp.model.AbstractRegtoppPathwayGAV;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Record(minOccurs = 0)
public class RegtoppPathwayGAV extends AbstractRegtoppPathwayGAV implements Serializable {

	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Field(at = 20, length = 2)
	private Integer duration;

	@Setter
	@Field(at = 22, length = 0, minLength = 0, minOccurs=0, maxOccurs = 500)
	private char[] descriptionHack;

	public String getDescription() {
		return new String(descriptionHack).trim();
	}


}
