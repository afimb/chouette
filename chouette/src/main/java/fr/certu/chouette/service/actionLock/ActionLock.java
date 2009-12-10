package fr.certu.chouette.service.actionLock;

import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ActionLock {
	
	private static final Log logger = LogFactory.getLog(ActionLock.class);
	private int timeout;
	private Calendar creationTime;
	

	public int getTimeout() 
	{
		return timeout;
	}

	public void setTimeout(int timeout) 
	{
		logger.info("Action Lock Timeout set to " + timeout);
		this.timeout = timeout;
	}

	public Calendar getCreationTime() 
	{
		return creationTime;
	}

	public void setCreationTime(Calendar creationTime) 
	{
		this.creationTime = creationTime;
	}	

	private boolean isAvailable()
	{		
		if (creationTime == null)
		{
			return true;
		}
		Calendar expireAt = creationTime;
		expireAt.add(Calendar.SECOND, timeout);
		if (expireAt.getTime().before(Calendar.getInstance().getTime()))
		{
			return true;
		}
		return false;
	}
	
	public boolean reserveToken() 
	{
		if (isAvailable())
		{
			creationTime = Calendar.getInstance();
			logger.info("Action Lock -- Token Reserve");
			return true;
		}
		return false;
	}
	
	public void releaseToken()
	{
		logger.info("Action Lock -- Token Release");
		creationTime = null;
	}
}
