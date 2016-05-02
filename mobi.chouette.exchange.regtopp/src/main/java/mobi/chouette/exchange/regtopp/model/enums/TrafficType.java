package mobi.chouette.exchange.regtopp.model.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum TrafficType {
	Normal(0),
	Express(1),
	// AkT using this
	SchoolBus(8);
	private int val;

	public String toString() {
		return String.valueOf(val);
	}
}
