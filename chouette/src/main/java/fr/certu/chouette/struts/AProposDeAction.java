package fr.certu.chouette.struts;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AProposDeAction extends GeneriqueAction {

  private static final Log log = LogFactory.getLog(AProposDeAction.class);
	private String versionApplication;
	
	public void setMenuSelectionne(String menuSelectionne) {
		session.put("menuSelectionne", menuSelectionne);
	}
	
	public String getVersionApplication() {
    log.error(versionApplication);
		return versionApplication;
	}
	
	public void setVersionApplication(String versionApplication) {
		    log.error(versionApplication);
    this.versionApplication = versionApplication;
	}
  
}
