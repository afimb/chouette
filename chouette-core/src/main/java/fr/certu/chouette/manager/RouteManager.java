/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.manager;

import fr.certu.chouette.model.neptune.Route;

/**
 * @author michel
 *
 */
public class RouteManager extends AbstractNeptuneManager<Route> 
{
    public RouteManager() 
    {
		super(Route.class);
	}
	

}
