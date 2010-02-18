package fr.certu.chouette.struts.interceptor;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ValidationAware;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

import fr.certu.chouette.manager.SingletonManager;
import fr.certu.chouette.service.actionLock.ActionLock;

@SuppressWarnings("serial")
public class SingleActionInterceptor extends AbstractInterceptor {

	private static final Log logger = LogFactory.getLog(SingleActionInterceptor.class);
	
	@Override
	public String intercept(ActionInvocation invocation) throws Exception
	{
		
		ApplicationContext applicationContext = SingletonManager.getApplicationContext(); 
		ActionLock actionLock = (ActionLock) applicationContext.getBean("actionLock");
		
		// Action already running, so send  to error page
		if (! actionLock.reserveToken())
		{
			logger.info("Unvailable action, one already running");
			Object action = invocation.getAction ();			
            if (action instanceof ValidationAware) 
            {
            	String lockError;
            	if (action instanceof ActionSupport)
            	{
            		lockError = ((ActionSupport) action).getText("interceptor.singleAction.lockErrorMessage");            		
            	}
            	else
            	{
            		lockError = "Actions locked by another one";
            	}
            	((ValidationAware) action).addActionError(lockError);
            }
			return "locked-action-error";
		}
		// invoke action and release token at the end
		// return global jsp error if invocation failed
		String result = "error";
		try
		{			
			result = invocation.invoke ();
		}
		catch(Exception e)
		{
			actionLock.releaseToken();
			logger.info("Action ended uncorrectly, running action token released");
			throw e;
		}
		actionLock.releaseToken();
		logger.info("Action ended, running action token released");
		return result;
	}
}
