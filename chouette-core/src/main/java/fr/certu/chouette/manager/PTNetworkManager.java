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
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.model.user.User;

/**
 * 
 */
@SuppressWarnings("unchecked")
public class PTNetworkManager extends AbstractNeptuneManager<PTNetwork>
{
	public PTNetworkManager() 
	{
		super(PTNetwork.class);
	}

	@Override
	public void remove(User user,PTNetwork ptNetwork,boolean propagate) throws ChouetteException
	{
		if(propagate)
		{
			INeptuneManager<Line> lineManager = (INeptuneManager<Line>) getManager(Line.class);
			DetailLevelEnum level = DetailLevelEnum.ATTRIBUTE;
			List<Line> lines = lineManager.getAll(null, Filter.getNewEqualsFilter("ptNetwork.id", ptNetwork.getId()), level);
			for (Line line : lines) {
				lineManager.remove(null, line,propagate);
			}
		}
		super.remove(null, ptNetwork,propagate);
	}
}
