package fr.certu.chouette.manager;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.model.neptune.ConnectionLink;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.user.User;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.validation.ValidationParameters;
import fr.certu.chouette.plugin.validation.ValidationReport;


public class StopPointManager extends AbstractNeptuneManager<StopPoint> 
{
	public StopPointManager() 
	{
		super(StopPoint.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Report propagateValidation(User user, List<StopPoint> beans,
			ValidationParameters parameters) 
	throws ChouetteException 
	{
		Report globalReport = new ValidationReport();

		// aggregate dependent objects for validation
		Set<StopArea> areas = new HashSet<StopArea>();
		for (StopPoint bean : beans) 
		{
			if (bean.getContainedInStopArea() != null)
			{
				addParentHierarchy(areas,bean.getContainedInStopArea());
			}

		}

		// propagate validation on StopArea
		if (areas.size() > 0)
		{
			Report report = null;
			AbstractNeptuneManager<StopArea> manager = (AbstractNeptuneManager<StopArea>) getManager(StopArea.class);
			if (manager.canValidate())
			{
				report = manager.validate(user, Arrays.asList(areas.toArray(new StopArea[0])), parameters);
			}
			else
			{
				report = manager.propagateValidation(user, Arrays.asList(areas.toArray(new StopArea[0])), parameters);
			}
			if (report != null)
			{
				globalReport.addAll(report.getItems());
				globalReport.updateStatus(report.getStatus());
			}
		}


		return globalReport;
	}

	private void addParentHierarchy(Set<StopArea> areas,StopArea area) 
	{
		if (area == null) return;
		if (areas.contains(area)) return;
		areas.add(area);
		addParentHierarchy(areas,area.getParentStopArea());
		if (area.getConnectionLinks() != null)
		{
            for (ConnectionLink link : area.getConnectionLinks())
            {
				StopArea start = link.getStartOfLink();
				StopArea end = link.getEndOfLink();
				if (start != null && !areas.contains(start))
				{
					areas.add(start);
				}
				if (end != null && !areas.contains(end))
				{
					areas.add(end);
				}
			}
		}
		return ;
	}


}
