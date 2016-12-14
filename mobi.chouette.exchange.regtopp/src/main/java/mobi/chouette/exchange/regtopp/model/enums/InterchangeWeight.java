package mobi.chouette.exchange.regtopp.model.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum InterchangeWeight {
	Forbidden(0),
	Normal(1),
	Priority(2),
	PriorityPlus(3);
	private int val;

	public String toString() {
		return String.valueOf(val);
	}
}
