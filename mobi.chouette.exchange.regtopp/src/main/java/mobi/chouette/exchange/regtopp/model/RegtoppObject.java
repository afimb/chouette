package mobi.chouette.exchange.regtopp.model;

import org.apache.commons.lang.StringUtils;

import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
public abstract class RegtoppObject {

	public abstract String getIndexingKey();
	
	protected String pad(String val, int size) {
		return StringUtils.leftPad(val, size,'0');
	}
	
	protected String pad(int val, int size) {
		return pad(String.valueOf(val),size);
	}
}
