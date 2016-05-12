package mobi.chouette.exchange.regtopp.model.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum PathwayType {
	Walk(1),
	Car(2),
	Bike(3);
	private int val;

	public String toString() {
		return String.valueOf(val);
	}
}
