package mobi.chouette.exchange.netex.exporter;

import java.sql.Date;

import mobi.chouette.exchange.exporter.DataCollector;
import mobi.chouette.model.Line;

public class NetexDataCollector extends DataCollector {
	public boolean collect(ExportableData collection, Line line, Date startDate, Date endDate) {
       return collect(collection, line, startDate, endDate, false, false);
		
	}


}
