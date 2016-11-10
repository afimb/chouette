package mobi.chouette.exchange.regtopp.model.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum AnnouncementType {
	Announced(0),
	Unannouced(1),
    //AtB uses this
	Unknown(2);
	private int val;

	public String toString() {
		return String.valueOf(val);
	}
}
