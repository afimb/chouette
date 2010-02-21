package fr.certu.chouette.struts.interceptor;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;
import org.springframework.context.ApplicationContext;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ValidationAware;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

import fr.certu.chouette.manager.SingletonManager;
import fr.certu.chouette.service.actionLock.ActionLock;

@SuppressWarnings("serial")
public class SingleActionInterceptor extends AbstractInterceptor 
{

	private static final Log logger = LogFactory.getLog(SingleActionInterceptor.class);
	
	@Override
	public String intercept(ActionInvocation invocation) throws Exception
	{
		
		ApplicationContext applicationContext = SingletonManager.getApplicationContext(); 
		ActionLock actionLock = (ActionLock) applicationContext.getBean("actionLock");
		String sessionId = ServletActionContext.getRequest().getSession().getId();		
		logger.debug("*** EZ SessionId : " + sessionId);
		
		// check if requested action is deconnexion one
		Object action = invocation.getAction();		
		String actionName = action.getClass().getSimpleName();
		logger.debug("*** EZ Action Name: " + actionName);
		
		// check if request action is deconnection. If so and if the requester is
		// the current owner of lock, the lock is released. 
		if (actionName.equals("DeconnexionAction"))
		{
			// Release token if deconnexion request comes from current lock owner
			if (actionLock.getSessionId() != null && actionLock.getSessionId().equals(sessionId))
			{
				actionLock.releaseToken();
			}
		}
		// case "not available session" : user will be redirect to deconnection action,
		// through "locked-action-error" result (see struts workflow configuration)
		else if (! actionLock.reserveToken(sessionId))
		{	
			return "locked-action-error";
		}
		//lock is free, update lock taking time 
		else
		{
			actionLock.initTakenAt();
		}
		
		// action invocation
		return invocation.invoke(); 
	}
}
