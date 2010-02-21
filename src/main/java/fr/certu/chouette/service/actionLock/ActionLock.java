package fr.certu.chouette.service.actionLock;

import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ActionLock 
{
	
	private static final Log logger = LogFactory.getLog(ActionLock.class);
	private static int timeout;
	private Calendar takenAt = null;
	private String sessionId = null; 
	

	public int getTimeout() 
	{
		return timeout;
	}

	public void setTimeout(int timeout) 
	{
		logger.info("Action Lock Timeout set to " + timeout);
		ActionLock.timeout = timeout;
	}

	public Calendar getTakenAt() 
	{
		return takenAt;
	}

	public void setTakenAt(Calendar takenAt) 
	{
		this.takenAt = takenAt;
		//logger.info("Set lock creation time to : " + this.creationTime.toString());
	}	

	private boolean isAvailable(String sessionId)
	{		
		// First true condition : lock is free
		if (this.takenAt == null)
		{
			logger.debug("lock : null takenAt");
			return true;
		}
		
		// Second true condition : lock is taken by current user
		if (this.sessionId.equals(sessionId))
		{
			logger.debug("lock : requester is owner, sessionId : " + sessionId);
			return true;
		}
		
		// Third true condition : lock has expired
		Calendar expireAt = (Calendar) this.takenAt.clone();
		expireAt.add(Calendar.SECOND, timeout);				
		if (expireAt.getTime().before(Calendar.getInstance().getTime()))
		{			
			logger.debug("lock : timeout as expired");
			return true;
		}
		return false;
	}
	
	public void initTakenAt()
	{
		setTakenAt(Calendar.getInstance());
	}
	
	public boolean reserveToken(String sessionId) 
	{
		if (isAvailable(sessionId))
		{
			initTakenAt();
			setSessionId(sessionId);
			logger.info("Reserve Actions Lock");
			return true;
		}
		return false;
	}
	
	public void releaseToken()
	{
		logger.info("Release Actions Lock");
		this.takenAt = null;
		this.sessionId = null;
	}

	public void setSessionId(String sessionId) 
	{
		this.sessionId = sessionId;
	}

	public String getSessionId() 
	{
		return sessionId;
	}
}
