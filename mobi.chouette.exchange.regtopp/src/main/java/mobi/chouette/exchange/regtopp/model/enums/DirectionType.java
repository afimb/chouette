package mobi.chouette.exchange.regtopp.model.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum DirectionType {
	Outbound(1),
	Inbound(2),
	BothDirections(3);
	private int val;

	public String toString() {
		return String.valueOf(val);
	}
}
