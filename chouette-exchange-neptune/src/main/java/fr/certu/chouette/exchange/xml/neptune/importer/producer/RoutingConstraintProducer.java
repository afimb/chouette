package fr.certu.chouette.exchange.xml.neptune.importer.producer;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chouette.schema.ITL;
import fr.certu.chouette.exchange.xml.neptune.model.NeptuneRoutingConstraint;
import fr.certu.chouette.plugin.report.ReportItem;

public class RoutingConstraintProducer extends AbstractProducer
{
	public List<NeptuneRoutingConstraint> produce(ITL[] itls,ReportItem report) 
	{
		Map<String,NeptuneRoutingConstraint> constraintMap = new HashMap<String, NeptuneRoutingConstraint>();
		List<NeptuneRoutingConstraint> restrictions = new ArrayList<NeptuneRoutingConstraint>();

		for (ITL itl : itls) 
		{
		   String lineId = getNonEmptyTrimedString(itl.getLineIdShortCut());
			NeptuneRoutingConstraint restriction = constraintMap.get(lineId);
			if (restriction == null)
			{
				restriction = new NeptuneRoutingConstraint();

				// LineIdShortCut mandatory
				restriction.setLineId(lineId);

				constraintMap.put(restriction.getLineId(), restriction);
				restrictions.add(restriction);
			}

			// Area mandatory
			restriction.addRoutingConstraintId(getNonEmptyTrimedString(itl.getAreaId()));
		}
		return restrictions;
	}
}
