package mobi.chouette.exchange.regtopp.model.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ParcelServiceType {
	Available(0),
	NotAvailable(1);
	private int val;

	public String toString() {
		return String.valueOf(val);
	}
}
