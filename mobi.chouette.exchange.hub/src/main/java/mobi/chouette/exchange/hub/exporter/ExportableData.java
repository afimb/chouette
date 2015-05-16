package mobi.chouette.exchange.hub.exporter;

import lombok.Getter;
import lombok.Setter;

public class ExportableData extends mobi.chouette.exchange.exporter.ExportableData{
	@Getter
	@Setter
	private int vehicleJourneyRank = 0;
	// refilled line by line
	@Getter
	@Setter
	private int pmrFootenoteId = -1;
}
