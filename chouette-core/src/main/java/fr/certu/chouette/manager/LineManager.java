/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.manager;

import java.util.ArrayList;
import java.util.List;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.user.User;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.validation.ValidationParameters;
import fr.certu.chouette.plugin.validation.ValidationReport;

/**
 * 
 */
public class LineManager extends AbstractNeptuneManager<Line>
{

	public LineManager()
	{
		super(Line.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Report propagateValidation(User user, List<Line> beans,
			ValidationParameters parameters) 
	throws ChouetteException 
	{
		Report globalReport = new ValidationReport();

		// aggregate dependent objects for validation
		List<PTNetwork> networks = new ArrayList<PTNetwork>();
		List<Company> companies = new ArrayList<Company>();
		List<Route> routes = new ArrayList<Route>();
		for (Line line : beans) 
		{
			if (line.getPtNetwork() != null)
				networks.add(line.getPtNetwork());
			if (line.getCompany() != null)
				companies.add(line.getCompany());
			if (line.getRoutes() != null)
			{
				routes.addAll(line.getRoutes());
			}
		}

		// propagate validation on networks
		if (networks.size() > 0)
		{
			Report report = null;
			AbstractNeptuneManager<PTNetwork> manager = (AbstractNeptuneManager<PTNetwork>) getManager(PTNetwork.class);
			if (manager.canValidate())
			{
				report = manager.validate(user, networks, parameters);
			}
			else
			{
				report = manager.propagateValidation(user, networks, parameters);
			}
			if (report != null)
			{
				globalReport.addAll(report.getItems());
				globalReport.updateStatus(report.getStatus());
			}
		}

		// propagate validation on companies
		if (companies.size() > 0)
		{
			Report report = null;
			AbstractNeptuneManager<Company> manager = (AbstractNeptuneManager<Company>) getManager(Company.class);
			if (manager.canValidate())
			{
				report = manager.validate(user, companies, parameters);
			}
			else
			{
				report = manager.propagateValidation(user, companies, parameters);
			}
			if (report != null)
			{
				globalReport.addAll(report.getItems());
				globalReport.updateStatus(report.getStatus());
			}
		}

		// propagate validation on routes
		if (routes.size() > 0)
		{
			Report report = null;
			AbstractNeptuneManager<Route> manager = (AbstractNeptuneManager<Route>) getManager(Route.class);
			if (manager.canValidate())
			{
				report = manager.validate(user, routes, parameters);
			}
			else
			{
				report = manager.propagateValidation(user, routes, parameters);
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
