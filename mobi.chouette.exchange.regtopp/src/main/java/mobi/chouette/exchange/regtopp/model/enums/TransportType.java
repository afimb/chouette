package mobi.chouette.exchange.regtopp.model.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum TransportType {
	AirplaneOrAirportExpress(1),
	LocalBus(2),
	ExpressCoach(3),
	Various(4),
	FerryBoat(5),
	Train(6),
	Tram(7),
	Subway(8);
	private int val;

	public String toString() {
		return "00" + String.valueOf(val);
	}
}
