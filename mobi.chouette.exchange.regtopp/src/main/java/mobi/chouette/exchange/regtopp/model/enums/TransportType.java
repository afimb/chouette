package mobi.chouette.exchange.regtopp.model.enums;

import org.apache.commons.lang.StringUtils;

public enum TransportType {
	// Since Regtopp 1.1
	AirplaneOrAirportExpress(1),
	Bus(2),
	ExpressCoach(3),
	Various(4),
	FerryBoat(5),
	Train(6),
	Tram(7),
	Subway(8),
	
	// Finnmark
	FlexibleBus(9),

	// Since Regtopp 1.3
	LocalBus(22),
	SchoolBus(23),
	AirportExpressBus(24),
	CarFerry(51),
	ExpressBoat(52),
	AirportExpressTrain(61),
	Unknown(999),
	Empty(000);

	private int val;

	TransportType(final int val) {
		if ("000".equals(val)) {
			//both 000 and 999 should be treated as Unknown
			this.val = 999;
		} else {
			this.val = val;
		}
	}

	public String toString() {
		return StringUtils.leftPad(String.valueOf(val), 3, "0");
	}
}
