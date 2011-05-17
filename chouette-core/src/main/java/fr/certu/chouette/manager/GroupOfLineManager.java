package fr.certu.chouette.manager;

import org.apache.log4j.Logger;

import fr.certu.chouette.model.neptune.GroupOfLine;

/**
 * 
 * @author mamadou keira
 *
 */
public class GroupOfLineManager extends AbstractNeptuneManager<GroupOfLine>{

	public GroupOfLineManager() {
		super(GroupOfLine.class);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Logger getLogger() {
		// TODO Auto-generated method stub
		return null;
	}

}
