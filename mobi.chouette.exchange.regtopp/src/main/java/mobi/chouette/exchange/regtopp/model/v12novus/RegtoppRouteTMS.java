package mobi.chouette.exchange.regtopp.model.v12novus;

import org.beanio.annotation.Record;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Record(minOccurs = 1)
public class RegtoppRouteTMS extends mobi.chouette.exchange.regtopp.model.v12.RegtoppRouteTMS {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getFullStopId() {
		return getStopId()+getStopPointIdArrival();
	}

}
