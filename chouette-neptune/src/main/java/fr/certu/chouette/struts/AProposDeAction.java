package fr.certu.chouette.struts;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AProposDeAction extends GeneriqueAction {

    private static final Log log = LogFactory.getLog(AProposDeAction.class);
    private String versionApplication;
    private String releaseApplication;

    public void setMenuSelectionne(String menuSelectionne) {
        session.put("menuSelectionne", menuSelectionne);
    }

    public String getVersionApplication() {
        log.info("versionApplication = "+versionApplication);
        return versionApplication;
    }

    public void setVersionApplication(String versionApplication) {
        log.info("setVersionApplication("+versionApplication+")");
        this.versionApplication = versionApplication;
    }

    public String getReleaseApplication() {
        log.info("releaseApplication = "+releaseApplication);
        return releaseApplication;
    }

    public void setReleaseApplication(String releaseApplication) {
        log.info("setReleaseApplication("+releaseApplication+")");
        this.releaseApplication = releaseApplication;
    }
}
