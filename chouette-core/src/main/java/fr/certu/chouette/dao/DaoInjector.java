/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.dao;

import lombok.Getter;
import lombok.Setter;
import fr.certu.chouette.manager.INeptuneManager;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.model.neptune.PTLink;
import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopPoint;

/**
 * @author michel
 *
 */
public class DaoInjector <T extends NeptuneIdentifiedObject>
{
	public static final DaoInjector<Line> createLineDaoInjector()
	{
		return new DaoInjector<Line>();
	}
	public static final DaoInjector<PTNetwork> createPTNetworkDaoInjector()
	{
		return new DaoInjector<PTNetwork>();
	}
	public static final DaoInjector<Company> createCompanyDaoInjector()
	{
		return new DaoInjector<Company>();
	}
	public static final DaoInjector<Route> createRouteDaoInjector()
	{
		return new DaoInjector<Route>();
	}
	public static final DaoInjector<StopPoint> createStopPointDaoInjector()
	{
		return new DaoInjector<StopPoint>();
	}
	public static final DaoInjector<JourneyPattern> createJourneyPatternDaoInjector()
	{
		return new DaoInjector<JourneyPattern>();
	}
	public static final DaoInjector<PTLink> createPTLinkDaoInjector()
	{
		return new DaoInjector<PTLink>();
	}
	
	
	@Getter @Setter private INeptuneManager<T> manager;
	@Getter @Setter private IDaoTemplate<T> dao;
	
	public void init()
	{
		manager.setDao(dao);
	}
}

