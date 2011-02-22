/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.manager;

import fr.certu.chouette.model.neptune.ConnectionLink;

/**
 * @author michel
 *
 */
public class ConnectionLinkManager extends AbstractNeptuneManager<ConnectionLink> 
{


    public ConnectionLinkManager()
	{
		super(ConnectionLink.class);
	}
    


}
