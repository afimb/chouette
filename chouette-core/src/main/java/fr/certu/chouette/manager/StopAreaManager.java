/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.manager;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.model.neptune.ConnectionLink;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.user.User;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.validation.ValidationParameters;
import fr.certu.chouette.plugin.validation.ValidationReport;

/**
 * @author michel
 *
 */
public class StopAreaManager extends AbstractNeptuneManager<StopArea> 
{

	public StopAreaManager() 
	{
		super(StopArea.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Report propagateValidation(User user, List<StopArea> beans,
			ValidationParameters parameters,boolean propagate) 
	throws ChouetteException 
	{
		Report globalReport = new ValidationReport();

		// aggregate dependent objects for validation
		Set<ConnectionLink> links = new HashSet<ConnectionLink>();
		for (StopArea bean : beans) 
		{
			if (bean.getConnectionLinks() != null)
			{
				links.addAll(bean.getConnectionLinks());
			}

		}

		// propagate validation on ConnectionLink
		if (links.size() > 0)
		{
			Report report = null;
			AbstractNeptuneManager<ConnectionLink> manager = (AbstractNeptuneManager<ConnectionLink>) getManager(ConnectionLink.class);
			if (manager.canValidate())
			{
				report = manager.validate(user, Arrays.asList(links.toArray(new ConnectionLink[0])), parameters,propagate);
			}
			else
			{
				report = manager.propagateValidation(user, Arrays.asList(links.toArray(new ConnectionLink[0])), parameters,propagate);
			}
			if (report != null)
			{
				globalReport.addAll(report.getItems());
				globalReport.updateStatus(report.getStatus());
			}
		}


		return globalReport;
	}

	
}
