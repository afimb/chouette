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

	public static DirectionType parseString(String val) {
		switch (val) {
		case "1":
			return Outbound;
		case "2":
			return Inbound;
		case "3":
			return BothDirections;
		default:
			return null;
		}
	}

	public DirectionType getOppositeDirection() {
		if (val == Outbound.val) {
			return Inbound;
		} else if (val == Inbound.val) {
			return Outbound;
		}
		return null; // For bothDirecitons
	}
}
