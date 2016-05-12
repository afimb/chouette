package mobi.chouette.exchange.regtopp.model.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum StopType {
	Stop(0),
	Other(1);
	private int val;

	public String toString() {
		return String.valueOf(val);
	}
}
