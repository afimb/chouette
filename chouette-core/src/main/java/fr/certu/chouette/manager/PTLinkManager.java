/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.manager;

import org.apache.log4j.Logger;

import fr.certu.chouette.model.neptune.PTLink;

/**
 * @author michel
 *
 */
public class PTLinkManager extends AbstractNeptuneManager<PTLink> {

	public PTLinkManager() 
	{
		super(PTLink.class);
	}

	@Override
	protected Logger getLogger() {
		// TODO Auto-generated method stub
		return null;
	}


}
