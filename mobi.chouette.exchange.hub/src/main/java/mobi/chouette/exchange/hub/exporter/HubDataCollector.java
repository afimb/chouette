package mobi.chouette.exchange.hub.exporter;

import java.sql.Date;
import java.util.List;

import mobi.chouette.exchange.exporter.DataCollector;
import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.Line;
import mobi.chouette.model.StopArea;

public class HubDataCollector extends DataCollector {
	public boolean collect(ExportableData collection, Line line, Date startDate, Date endDate) {
	       return collect(collection,line,startDate,endDate,false,false);
	}

	@Override
	protected void collectStopAreas(mobi.chouette.exchange.exporter.ExportableData collection, StopArea stopArea,boolean skipNoCoordinate,  boolean followLinks) {
		if (collection.getStopAreas().contains(stopArea))
			return;
		collection.getStopAreas().add(stopArea);
		switch (stopArea.getAreaType()) {
		case BoardingPosition:
		case Quay:
			collection.getPhysicalStops().add(stopArea);
			if (followLinks)
			{
				addConnectionLinks(collection,stopArea.getConnectionStartLinks(),skipNoCoordinate,followLinks);
				addConnectionLinks(collection,stopArea.getConnectionEndLinks(),skipNoCoordinate,followLinks);
			}
			if (stopArea.getParent() != null)
				collectStopAreas(collection, stopArea.getParent(), skipNoCoordinate,followLinks);
			break;
		case CommercialStopPoint:
			collection.getCommercialStops().add(stopArea);
			break;
		default:

		}

	}

@Override
	protected void addConnectionLinks(mobi.chouette.exchange.exporter.ExportableData collection, List<ConnectionLink> links,boolean skipNoCoordinate,  boolean followLinks)
	{
		for (ConnectionLink link : links) 
		{
			if (collection.getConnectionLinks().contains(link)) continue;
			if (link.getStartOfLink() == null || link.getEndOfLink() == null) continue;
			if (!link.getStartOfLink().hasCoordinates() || !link.getEndOfLink().hasCoordinates() ) continue;
			if (link.getLinkDistance() == null) continue;
			collection.getConnectionLinks().add(link);
			collectStopAreas(collection, link.getStartOfLink(), skipNoCoordinate,followLinks);
			collectStopAreas(collection, link.getEndOfLink(), skipNoCoordinate,followLinks);
		}
	}



}
