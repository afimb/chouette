package fr.certu.chouette.service.actionLock;

import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ActionLock {
	
	private static final Log logger = LogFactory.getLog(ActionLock.class);
	private int timeout;
	private Calendar creationTime = null;
	

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
		//logger.info("Set lock creation time to : " + this.creationTime.toString());
	}	

	private boolean isAvailable()
	{		
		if (this.creationTime == null)
		{
			return true;
		}		
		Calendar expireAt = (Calendar) this.creationTime.clone();
		expireAt.add(Calendar.SECOND, timeout);
		//logger.debug("Lock expires at : " + expireAt.getTime().toString());		
		if (expireAt.getTime().before(Calendar.getInstance().getTime()))
		{
			//logger.debug("Lock timeout doesn't expire yet");
			return true;
		}
		return false;
	}
	
	public boolean reserveToken() 
	{
		if (isAvailable())
		{
			setCreationTime(Calendar.getInstance());			
			logger.info("Reserve Actions Lock");
			return true;
		}
		return false;
	}
	
	public void releaseToken()
	{
		logger.info("Release Actions Lock");
		this.creationTime = null;
	}
}
