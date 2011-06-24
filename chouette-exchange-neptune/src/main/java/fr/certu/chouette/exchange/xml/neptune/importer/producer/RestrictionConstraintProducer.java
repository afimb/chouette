package fr.certu.chouette.exchange.xml.neptune.importer.producer;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chouette.schema.ITL;
import fr.certu.chouette.model.neptune.RestrictionConstraint;
import fr.certu.chouette.plugin.report.ReportItem;

public class RestrictionConstraintProducer extends AbstractProducer
{
	public List<RestrictionConstraint> produce(ITL[] itls,ReportItem report) 
	{
		Map<String,RestrictionConstraint> constraintMap = new HashMap<String, RestrictionConstraint>();
		List<RestrictionConstraint> restrictions = new ArrayList<RestrictionConstraint>();

		for (ITL itl : itls) 
		{
			String name = getNonEmptyTrimedString(itl.getName());
			RestrictionConstraint restriction = constraintMap.get(name);
			if (restriction == null)
			{
				restriction = new RestrictionConstraint();
				// Name mandatory
				restriction.setName(name);

				// LineIdShortCut mandatory
				restriction.setLineIdShortCut(getNonEmptyTrimedString(itl.getLineIdShortCut()));

				// build objectId with name and lineId prefix
				String[] ids = restriction.getLineIdShortCut().split(":");
				if (ids.length == 3)
				{
					String objectId = ids[0]+":RestrictionConstraint:"+ids[2]+"_"+name.replaceAll(" ", "_");
					restriction.setObjectId(objectId);
				}
				constraintMap.put(name, restriction);
				restrictions.add(restriction);
			}

			// Area mandatory
			restriction.addAreaId(getNonEmptyTrimedString(itl.getAreaId()));
		}
		return restrictions;
	}
}
