package mobi.chouette.exchange.gtfs.parser;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import mobi.chouette.model.VehicleJourneyAtStop;
import mobi.chouette.model.type.AlightingPossibilityEnum;
import mobi.chouette.model.type.BoardingPossibilityEnum;

/**
 * @author michel
 * 
 */
@AllArgsConstructor
class VehicleJourneyAtStopWrapper extends VehicleJourneyAtStop {

	private static final long serialVersionUID = 5052093726657799027L;
	@Getter
	@Setter
	String stopId;
	@Getter
	@Setter
	int stopSequence;
	@Getter
	@Setter
	Float shapeDistTraveled;
	@Getter
	@Setter
	BoardingPossibilityEnum pickUpType = BoardingPossibilityEnum.normal;
	@Getter
	@Setter
	AlightingPossibilityEnum dropOffType = AlightingPossibilityEnum.normal;

}