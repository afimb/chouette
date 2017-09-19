package mobi.chouette.exchange.netexprofile.exporter;

import org.joda.time.LocalDate;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.exporter.DataCollector;
import mobi.chouette.model.Line;

@Log4j
public class NetexDataCollector extends DataCollector {

    public boolean collect(ExportableData collection, Line line, LocalDate startDate, LocalDate endDate) {
        boolean res =  collect(collection, line, startDate, endDate, false, false);

        if(line.getNetwork().getCompany() != null) {
        	collection.getCompanies().add(line.getNetwork().getCompany());
        }
        
        return res;
    }

}
