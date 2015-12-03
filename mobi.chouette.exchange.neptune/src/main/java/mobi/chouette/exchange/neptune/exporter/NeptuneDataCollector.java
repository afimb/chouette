package mobi.chouette.exchange.neptune.exporter;

import java.sql.Date;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.exporter.DataCollector;
import mobi.chouette.model.Line;

@Log4j
public class NeptuneDataCollector extends DataCollector {
	public boolean collect(ExportableData collection, Line line, Date startDate, Date endDate) {
		boolean res =  collect(collection, line, startDate, endDate, false, false);
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
