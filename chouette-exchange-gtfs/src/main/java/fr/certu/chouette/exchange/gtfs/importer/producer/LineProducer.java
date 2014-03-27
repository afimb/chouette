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
		/** GTFS definitions
			0 - Tram, Streetcar, Light rail. Any light rail or street level system within a metropolitan area.
			1 - Subway, Metro. Any underground rail system within a metropolitan area.
			2 - Rail. Used for intercity or long-distance travel.
			3 - Bus. Used for short- and long-distance bus routes.
			4 - Ferry. Used for short- and long-distance boat service.
			5 - Cable car. Used for street-level cable cars where the cable runs beneath the car.
			6 - Gondola, Suspended cable car. Typically used for aerial cable cars where the car is suspended from the cable.
			7 - Funicular. Any rail system designed for steep inclines.
		 **/
		switch (gtfsLine.getRouteType())
		{
		case 0 : line.setTransportModeName(TransportModeNameEnum.TRAMWAY); break;
		case 1 : line.setTransportModeName(TransportModeNameEnum.METRO); break;
		case 2 : line.setTransportModeName(TransportModeNameEnum.TRAIN); break;
		case 3 : line.setTransportModeName(TransportModeNameEnum.BUS); break;
		case 4 : line.setTransportModeName(TransportModeNameEnum.FERRY); break;
		case 5 : line.setTransportModeName(TransportModeNameEnum.TRAMWAY); break;
		case 6 : line.setTransportModeName(TransportModeNameEnum.WATERBORNE); break;
		// case 7 : line.setTransportModeName(TransportModeNameEnum.FUNICULAR); break;
		default : line.setTransportModeName(TransportModeNameEnum.OTHER); break;

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
