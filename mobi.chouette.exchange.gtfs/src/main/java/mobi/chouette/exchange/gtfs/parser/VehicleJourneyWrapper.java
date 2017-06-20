package mobi.chouette.exchange.gtfs.parser;

import lombok.Getter;
import lombok.Setter;
import mobi.chouette.model.VehicleJourney;

class VehicleJourneyWrapper extends VehicleJourney {
	private static final long serialVersionUID = -2001837138013440802L;
	@Getter
	@Setter
	String shapeId;

}