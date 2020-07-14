package mobi.chouette.exchange.netexprofile.exporter;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.exporter.DataCollector;
import mobi.chouette.model.Line;
import org.joda.time.LocalDate;

@Log4j
public class NetexDataCollector extends DataCollector {

	public NetexDataCollector(mobi.chouette.exchange.exporter.ExportableData collection, Line line, LocalDate startDate, LocalDate endDate) {
		super(collection, line, startDate, endDate, false, false);
	}

	@Override
	public boolean collect() {
		boolean res = super.collect();

		if (line.getNetwork().getCompany() != null) {
			collection.getCompanies().add(line.getNetwork().getCompany());
		}
		return res;
	}

}
