package fr.certu.chouette.exchange.xml.neptune.importer.producer;

import fr.certu.chouette.filter.DetailLevelEnum;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.type.TransportModeNameEnum;

public class LineProducer extends AbstractModelProducer<Line,chouette.schema.Line>
{
	@Override
	public Line produce(chouette.schema.Line xmlLine)
	{
		Line line = new Line();
		// objectId, objectVersion, creatorId, creationTime
		populateTridentObject(line, xmlLine);

		// Name optional
		line.setName(getNonEmptyTrimedString(xmlLine.getName()));

		// Number optional
		line.setNumber(getNonEmptyTrimedString(xmlLine.getNumber()));

		// PublishedName optional
		line.setPublishedName(getNonEmptyTrimedString(xmlLine.getPublishedName()));

		// TransportModeName optional
		if (xmlLine.getTransportModeName() != null)
		{
			try
			{
				line.setTransportModeName(TransportModeNameEnum.fromValue(xmlLine.getTransportModeName().value()));
			}
			catch (IllegalArgumentException e) 
			{
				// TODO: traiter le cas de non correspondance
			}
		}
		// LineEnd [0..w] : TODO 
		String[] castorLineEnds = xmlLine.getLineEnd();
		for (String lineEnd : castorLineEnds) 
		{
			String realLineEnd = getNonEmptyTrimedString(lineEnd);
			if (realLineEnd == null)
			{
				// TODO tracer 
			}
			else
			{
				line.addLineEnd(realLineEnd);
			}
		}

		// RouteId [1..w]  
		String[] castorRouteIds = xmlLine.getRouteId();
		for (String routeId : castorRouteIds) 
		{
			String realRouteId = getNonEmptyTrimedString(routeId);
			if (realRouteId == null)
			{
				// TODO tracer 
			}
			else
			{
				line.addRouteId(realRouteId);
			}
		}

		// Registration optional
		line.setRegistrationNumber(getRegistrationNumber(xmlLine.getRegistration()));

		// PtNetworkShortcut optional
		line.setPtNetworkIdShortcut(getNonEmptyTrimedString(xmlLine.getPtNetworkIdShortcut()));

		// Comment optional
		line.setComment(getNonEmptyTrimedString(xmlLine.getComment()));

		line.expand(DetailLevelEnum.ALL_DEPENDENCIES);
		return line;
	}

}
