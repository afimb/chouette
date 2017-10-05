package mobi.chouette.exchange.gtfs.exporter;

import java.util.Collection;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.exporter.DataCollector;
import mobi.chouette.model.Line;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;

import org.joda.time.LocalDate;

@Log4j
public class GtfsDataCollector extends DataCollector{
	public boolean collect(ExportableData collection, Line line, LocalDate startDate, LocalDate endDate) {
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
			if (stopArea.getParent() != null) {
				collectStopAreas(collection, stopArea.getParent(),skipNoCoordinates,followLinks);
			}
			if (collection.getPhysicalStops().contains(stopArea)) {
				return;
			}
			collection.getPhysicalStops().add(stopArea);
			addConnectionLinks(collection, stopArea.getConnectionStartLinks(), skipNoCoordinates, followLinks);
			addConnectionLinks(collection, stopArea.getConnectionEndLinks(), skipNoCoordinates, followLinks);
		} else if (stopArea.getAreaType().equals(ChouetteAreaEnum.CommercialStopPoint)) {
			if(stopArea.getParent() != null) {
				collectStopAreas(collection, stopArea.getParent(), skipNoCoordinates, followLinks);
			}
			if (collection.getCommercialStops().contains(stopArea)) {
				return;
			}
			collection.getCommercialStops().add(stopArea);
			addConnectionLinks(collection, stopArea.getConnectionStartLinks(), skipNoCoordinates, followLinks);
			addConnectionLinks(collection, stopArea.getConnectionEndLinks(), skipNoCoordinates, followLinks);
			for(StopArea sa : stopArea.getContainedStopAreas()) {
				collectStopAreas(collection, sa, skipNoCoordinates, followLinks);
			}
		}
	}

    
}
