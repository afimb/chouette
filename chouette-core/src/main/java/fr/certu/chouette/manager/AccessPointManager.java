package fr.certu.chouette.manager;

import org.apache.log4j.Logger;

import fr.certu.chouette.model.neptune.AccessPoint;

/**
 * 
 * @author mamadou keira
 *
 */
public class AccessPointManager extends AbstractNeptuneManager<AccessPoint> {

	public AccessPointManager() {
		super(AccessPoint.class);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Logger getLogger() {
		// TODO Auto-generated method stub
		return null;
	}

}
