package mobi.chouette.exchange.gtfs.exporter;

import java.sql.Date;
import java.util.Collection;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.exporter.DataCollector;
import mobi.chouette.model.Line;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;

@Log4j
public class GtfsDataCollector extends DataCollector{
	public boolean collect(ExportableData collection, Line line, Date startDate, Date endDate) {
       boolean res =  collect(collection,line,startDate,endDate,false,false);
		if (line.getCompany() == null) {
			log.error("line " + line.getObjectId() + " : missing company");
			return false;
		}
		return res;
	}

	public boolean collect(ExportableData collection, Collection<StopArea> stopAreas) {
		return collect(collection, stopAreas,false,false);

	}

    @Override
	protected void collectStopAreas(mobi.chouette.exchange.exporter.ExportableData collection, StopArea stopArea,boolean skipNoCoordinates,boolean followLinks) {
		if (stopArea.getAreaType().equals(ChouetteAreaEnum.BoardingPosition)
				|| stopArea.getAreaType().equals(ChouetteAreaEnum.Quay)) {
			if (collection.getPhysicalStops().contains(stopArea))
				return;
			collection.getPhysicalStops().add(stopArea);
			collection.getConnectionLinks().addAll(stopArea.getConnectionStartLinks());
			collection.getConnectionLinks().addAll(stopArea.getConnectionEndLinks());
			if (stopArea.getParent() != null)
				collectStopAreas(collection, stopArea.getParent(),skipNoCoordinates,followLinks);
		} else if (stopArea.getAreaType().equals(ChouetteAreaEnum.CommercialStopPoint)) {
			if (collection.getCommercialStops().contains(stopArea))
				return;
			collection.getCommercialStops().add(stopArea);
			collection.getConnectionLinks().addAll(stopArea.getConnectionStartLinks());
			collection.getConnectionLinks().addAll(stopArea.getConnectionEndLinks());
		}
	}


}
