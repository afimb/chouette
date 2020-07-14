package mobi.chouette.exchange.kml.exporter;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.exporter.DataCollector;
import mobi.chouette.model.Line;

import org.joda.time.LocalDate;

@Log4j
public class KmlDataCollector extends DataCollector{


	public KmlDataCollector(mobi.chouette.exchange.exporter.ExportableData collection, Line line, org.joda.time.LocalDate startDate, org.joda.time.LocalDate endDate) {
		super(collection, line, startDate, endDate, true, true);
	}

	@Override
	public boolean collect() {
		boolean res =  super.collect();
		if (line.getNetwork() == null) {
			log.error("line " + line.getObjectId() + " : missing network");
			return false;
		}
		if (line.getCompany() == null) {
			log.error("line " + line.getObjectId() + " : missing company");
			return false;
		}
		return res;
			
	}

	
}
