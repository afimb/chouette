/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.manager;

import java.util.List;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.filter.DetailLevelEnum;
import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.user.User;

/**
 * @author michel
 *
 */
@SuppressWarnings("unchecked")
public class CompanyManager extends AbstractNeptuneManager<Company> 
{

	public CompanyManager()
	{
		super(Company.class);
	}
	@Override
	public void remove(User user,Company company,boolean propagate) throws ChouetteException
	{
		INeptuneManager<Line> lineManager = (INeptuneManager<Line>) getManager(Line.class);
		INeptuneManager<VehicleJourney> vjManager = (INeptuneManager<VehicleJourney>)getManager(VehicleJourney.class);
		Filter filter = Filter.getNewEqualsFilter("company.id", company.getId());
		DetailLevelEnum level = DetailLevelEnum.ATTRIBUTE;
		List<Line> lines = lineManager.getAll(null, filter, level);
		List<VehicleJourney> vehicleJourneys = vjManager.getAll(null, filter, level);
		if(propagate)
		{
			lineManager.removeAll(null, lines);
			vjManager.removeAll(null, vehicleJourneys);
		}else 
		{
			for (Line line : lines) {
				line.setCompany(null);
				lineManager.update(null, line);
			}
			for (VehicleJourney vehicleJourney : vehicleJourneys) {
				vehicleJourney.setCompany(null);
				vjManager.update(null, vehicleJourney);
			}
		}
		super.remove(null, company, propagate);
	}
}
