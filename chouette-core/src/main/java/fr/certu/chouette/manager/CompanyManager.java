/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.manager;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.user.User;

/**
 * 
 * 
 * @author michel
 *
 */
@SuppressWarnings("unchecked")
public class CompanyManager extends AbstractNeptuneManager<Company> 
{
	private static final Logger logger = Logger.getLogger(CompanyManager.class);

	public CompanyManager()
	{
		super(Company.class,Company.COMPANY_KEY);
	}
	@Transactional
	@Override
	public void removeAll(User user,List<Company> companies,boolean propagate) throws ChouetteException
	{
		for (Company company : companies) 
		{

			INeptuneManager<Line> lineManager = (INeptuneManager<Line>) getManager(Line.class);
			INeptuneManager<VehicleJourney> vjManager = (INeptuneManager<VehicleJourney>)getManager(VehicleJourney.class);
			Filter filter = Filter.getNewEqualsFilter("company.id", company.getId());
			List<Line> lines = lineManager.getAll(user, filter);
			List<VehicleJourney> vehicleJourneys = vjManager.getAll(user, filter);
			if(propagate)
			{
				lineManager.removeAll(user, lines,propagate);
				vjManager.removeAll(user, vehicleJourneys,propagate);
			}
			else 
			{
				for (Line line : lines) 
				{
					line.setCompany(null);
					lineManager.update(user, line);
				}
				for (VehicleJourney vehicleJourney : vehicleJourneys) 
				{
					vehicleJourney.setCompany(null);
					vjManager.update(user, vehicleJourney);
				}
			}
		}
		super.removeAll(user, companies, propagate);
	}

	@Override
	protected Logger getLogger()
	{
		return logger;
	}
}
