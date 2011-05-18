package fr.certu.chouette.manager;

import org.apache.log4j.Logger;

import fr.certu.chouette.model.neptune.AccessLink;

/**
 * 
 * @author mamadou keira
 *
 */
public class AccessLinkManager extends AbstractNeptuneManager<AccessLink> {

	public AccessLinkManager() {
		super(AccessLink.class);
	}

	@Override
	protected Logger getLogger() {
		return null;
	}

}
