/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.manager;

import fr.certu.chouette.model.neptune.StopArea;

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

}
