package fr.certu.chouette.manager;

import org.apache.log4j.Logger;

import fr.certu.chouette.model.neptune.Facility;

/**
 * 
 * @author mamadou keira
 *
 */
public class FacilityManager extends AbstractNeptuneManager<Facility>{

	public FacilityManager() {
		super(Facility.class);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Logger getLogger() {
		// TODO Auto-generated method stub
		return null;
	}

}
