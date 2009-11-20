package fr.certu.chouette.struts;

import com.opensymphony.xwork2.ActionSupport;
import fr.certu.chouette.struts.outil.filAriane.FilAriane;
import java.util.Map;
import org.apache.struts2.interceptor.PrincipalAware;
import org.apache.struts2.interceptor.PrincipalProxy;
import org.apache.struts2.interceptor.RequestAware;
import org.apache.struts2.interceptor.SessionAware;

@SuppressWarnings({"serial", "unchecked"})
public class GeneriqueAction extends ActionSupport implements RequestAware, SessionAware, PrincipalAware {

  public static final String EDIT = "edit";
  public static final String REDIRECT = "redirect";
  public static final String SAVE = "save";
  public static final String UPDATE = "update";
	public static final String CREATEANDEDIT = "createAndedit";
  public static final String LIST = "list";
  public static final String REDIRECTLIST = "redirectList";
  public static final String REDIRECTEDIT = "redirectEdit";
  public static final String SEARCH = "search";

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
