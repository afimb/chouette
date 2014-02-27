package fr.certu.chouette.exchange.gtfs.importer.producer;

import org.apache.log4j.Logger;

import fr.certu.chouette.exchange.gtfs.model.GtfsRoute;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.type.TransportModeNameEnum;
import fr.certu.chouette.plugin.report.Report;

public class LineProducer extends AbstractModelProducer<Line,GtfsRoute>
{
	private static Logger logger = Logger.getLogger(LineProducer.class);
	
	public Line produce(GtfsRoute gtfsLine,Report report)
	{
		Line line = new Line();

		line.setObjectId(composeObjectId( Line.LINE_KEY, gtfsLine.getRouteId() ,logger));

		// Name optional
		line.setName(getNonEmptyTrimedString(gtfsLine.getRouteShortName()));
		if (line.getName() == null)
		   line.setName(getNonEmptyTrimedString(gtfsLine.getRouteLongName()));

		// Number optional
		line.setNumber(getNonEmptyTrimedString(gtfsLine.getRouteShortName()));

		// PublishedName optional
		line.setPublishedName(getNonEmptyTrimedString(gtfsLine.getRouteLongName()));

		// TransportModeName optional
		switch (gtfsLine.getRouteType())
		{
		case 0 : line.setTransportModeName(TransportModeNameEnum.Bus); break;
		case 1 : line.setTransportModeName(TransportModeNameEnum.Metro); break;
		case 2 : line.setTransportModeName(TransportModeNameEnum.LongDistanceTrain); break;
		case 3 : line.setTransportModeName(TransportModeNameEnum.Coach); break;
		case 4 : line.setTransportModeName(TransportModeNameEnum.Ferry); break;
		case 5 : line.setTransportModeName(TransportModeNameEnum.Tramway); break;
		default : line.setTransportModeName(TransportModeNameEnum.Other); break;

		}

		// Registration optional
		String[] token = line.getObjectId().split(":");
		line.setRegistrationNumber(token[2]);

		// Comment optional : refers to company
		line.setComment(getNonEmptyTrimedString(composeObjectId( Company.COMPANY_KEY,gtfsLine.getAgencyId(),logger)));

		if (line.getComment() != null && line.getComment().length() > 255) 
		   line.setComment(line.getComment().substring(0,255));
		return line;
	}

  

}
