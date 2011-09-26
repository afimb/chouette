package fr.certu.chouette.struts;

import java.util.Calendar;

import org.apache.log4j.Logger;


public class ActionLock 
{	
	private static final Logger logger = Logger.getLogger(ActionLock.class);

	private static int timeout;
	private Calendar takenAt = null;
	private String sessionId = null;
        private static ActionLock current;
        
        public ActionLock() {
            if (current == null)
                current = this;
        }
	
        public static ActionLock getInstance() {
            if (current == null)
                new ActionLock();
            return current;
        }
        
	public void reserveLock(String sessionId)
	{
		initTakenAt();
		setSessionId(sessionId);
	}
	
	public void releaseLock()
	{
		logger.info("Session Lock Released");
		setTakenAt(null);
		setSessionId(null);
	}
	
	public boolean isTimeoutExpired()
	{
				
		// Third true condition : lock has expired
		Calendar expireAt = (Calendar) this.takenAt.clone();
		expireAt.add(Calendar.SECOND, timeout);				
		if (expireAt.getTime().before(Calendar.getInstance().getTime()))
		{			
			logger.info("session lock has expired");
			return true;
		}
		return false;
	}
	
	public void initTakenAt()
	{
		setTakenAt(Calendar.getInstance());
	}
		

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
