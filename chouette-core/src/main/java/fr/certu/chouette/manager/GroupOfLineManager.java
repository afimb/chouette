package fr.certu.chouette.manager;

import org.apache.log4j.Logger;

import fr.certu.chouette.model.neptune.GroupOfLine;

/**
 * 
 * @author mamadou keira
 *
 */
public class GroupOfLineManager extends AbstractNeptuneManager<GroupOfLine>
{

	private static final Logger logger = Logger.getLogger(GroupOfLineManager.class); 
	public GroupOfLineManager() 
	{
		super(GroupOfLine.class,GroupOfLine.GROUPOFLINE_KEY);
	}

	@Override
	protected Logger getLogger() 
	{
		return logger;
	}

}
