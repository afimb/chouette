package fr.certu.chouette.exchange.xml.neptune.importer.producer;

import chouette.schema.AccessibilitySuitabilityDetailsItem;
import chouette.schema.LineExtension;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.type.TransportModeNameEnum;
import fr.certu.chouette.model.neptune.type.UserNeedEnum;

public class LineProducer extends AbstractModelProducer<Line,chouette.schema.Line>
{
	@Override
	public Line produce(chouette.schema.Line xmlLine)
	{
		Line line = new Line();
		// objectId, objectVersion, creatorId, creationTime
		populateFromCastorNeptune(line, xmlLine);

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
		
		// LineExtension optional
		LineExtension xmlLineExtension = xmlLine.getLineExtension();
		if(xmlLineExtension != null){
			
			// MobilityRestrictedSuitability
			line.setMobilityRestrictedSuitable(xmlLineExtension.getMobilityRestrictedSuitability());
			
			if(xmlLineExtension.getAccessibilitySuitabilityDetails() != null){
				for(AccessibilitySuitabilityDetailsItem xmlAccessibilitySuitabilityDetailsItem : xmlLineExtension.getAccessibilitySuitabilityDetails().getAccessibilitySuitabilityDetailsItem()){
					if(xmlAccessibilitySuitabilityDetailsItem.getUserNeedGroup() != null){
						try{
							line.addUserNeed(UserNeedEnum.fromValue(xmlAccessibilitySuitabilityDetailsItem.getUserNeedGroup().getChoiceValue().toString()));
						}
						catch (IllegalArgumentException e) 
						{
							// TODO: traiter le cas de non correspondance
						}
					}
				}
			}

		}
		
		return line;
	}

}
