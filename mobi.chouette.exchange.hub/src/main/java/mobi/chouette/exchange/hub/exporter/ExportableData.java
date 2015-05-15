package mobi.chouette.exchange.hub.exporter;

import lombok.Data;
import mobi.chouette.model.Line;

@Data
public class ExportableData extends mobi.chouette.exchange.exporter.ExportableData{
	private int vehicleJourneyRank = 0;
	// refilled line by line
	private Line line;
	private int pmrFootenoteId = -1;
}
