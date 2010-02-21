package fr.certu.chouette.struts;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.dispatcher.SessionMap;

@SuppressWarnings("serial")
public class DeconnexionAction extends GeneriqueAction {
	
	private final Log log = LogFactory.getLog(DeconnexionAction.class);
	private boolean busySession = false;
	
	
	@Override
	public String execute() throws Exception 
	{
		//		  Code fragment from class implementing SessionAware containing the 
		//		  session map in a instance variable "session". Attempting to invalidate 
		//		  an already-invalid session will result in an IllegalStateException.
		if (session instanceof SessionMap) 
		{
			try 
			{
				((SessionMap)session).invalidate();
				//addActionMessage(getText("busy.session.notification"));
			}
			catch (Exception e) 
			{
				log.error(e.getMessage(), e);
			}
		}
		return SUCCESS;
	}

	public void setBusySession(boolean busySession) 
	{
		this.busySession = busySession;
	}

	public boolean isBusySession() 
	{
		return busySession;
	}
}
