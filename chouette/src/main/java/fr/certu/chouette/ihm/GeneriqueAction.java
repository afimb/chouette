package fr.certu.chouette.ihm;

import com.opensymphony.xwork2.ActionSupport;
import fr.certu.chouette.ihm.outil.filAriane.FilAriane;
import java.util.Map;
import org.apache.struts2.interceptor.PrincipalAware;
import org.apache.struts2.interceptor.PrincipalProxy;
import org.apache.struts2.interceptor.RequestAware;
import org.apache.struts2.interceptor.SessionAware;

@SuppressWarnings({"serial", "unchecked"})
public class GeneriqueAction extends ActionSupport implements RequestAware, SessionAware, PrincipalAware {
	
	protected Map            session;
	protected Map            request;
	protected PrincipalProxy principalProxy;
	
	public void setSession(Map session) {
		this.session = session;
	}
	
	public void setRequest(Map request) {
		this.request = request;
	}
	
	public void setPrincipalProxy(PrincipalProxy principalProxy) {
		this.principalProxy = principalProxy;
	}
	
	public PrincipalProxy getPrincipalProxy() {
		return principalProxy;
	}
	
	public FilAriane getFilAriane() {
		if (session.get("filAriane") == null) {
			FilAriane filAriane = new FilAriane();
			session.put("filAriane", filAriane); 
			return filAriane;
		}
		else
			return (FilAriane)session.get("filAriane");
	}
}
