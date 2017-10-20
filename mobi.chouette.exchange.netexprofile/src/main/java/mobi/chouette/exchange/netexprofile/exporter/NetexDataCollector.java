package mobi.chouette.exchange.netexprofile.exporter;

import java.util.List;
import java.util.stream.Collectors;

import org.joda.time.LocalDate;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.exporter.DataCollector;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.Route;

@Log4j
public class NetexDataCollector extends DataCollector {

	public boolean collect(ExportableData collection, Line line, LocalDate startDate, LocalDate endDate) {
		boolean res = collect(collection, line, startDate, endDate, false, false);

		if (line.getNetwork().getCompany() != null) {
			collection.getCompanies().add(line.getNetwork().getCompany());
		}


		// Remove any routes or journey patterns without active vehicle journeys.
		List<Route> activeRoutes = collection.getVehicleJourneys().stream().map(vj -> vj.getRoute()).distinct().collect(Collectors.toList());
		collection.setRoutes(activeRoutes);
		List<JourneyPattern> activeJourneyPatterns = collection.getVehicleJourneys().stream().map(vj -> vj.getJourneyPattern()).filter(jp -> jp != null).distinct().collect(Collectors.toList());
		collection.setJourneyPatterns(activeJourneyPatterns);
		return res;
	}

}
