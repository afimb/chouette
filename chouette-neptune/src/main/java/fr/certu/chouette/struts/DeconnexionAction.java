package fr.certu.chouette.struts;

import org.apache.log4j.Logger;
import org.apache.struts2.dispatcher.SessionMap;

@SuppressWarnings("serial")
public class DeconnexionAction extends GeneriqueAction {
	
	private final Logger logger = Logger.getLogger(DeconnexionAction.class);
	
	@SuppressWarnings("rawtypes")
   public String execute() throws Exception 
	{
		// Code fragment from class implementing SessionAware containing the 
		// session map in a instance variable "session". Attempting to invalidate 
		// an already-invalid session will result in an IllegalStateException.
		if (session instanceof SessionMap) 
		{
			try 
			{
				((SessionMap)session).invalidate();
			}
			catch (Exception e) 
			{
			   logger.error(e.getMessage(), e);
			}
		}
		return SUCCESS;
	}
}
