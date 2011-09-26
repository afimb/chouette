package fr.certu.chouette.struts;

import org.apache.log4j.Logger;

public class AProposDeAction extends GeneriqueAction {

   private static final long serialVersionUID = 8492726992652737155L;
   private static final Logger logger = Logger.getLogger(AProposDeAction.class);
   private String versionApplication;
   private String releaseApplication;

   @SuppressWarnings("unchecked")
   public void setMenuSelectionne(String menuSelectionne) {
      session.put("menuSelectionne", menuSelectionne);
   }

   public String getVersionApplication() {
      logger.info("versionApplication = "+versionApplication);
      return versionApplication;
   }

   public void setVersionApplication(String versionApplication) {
      logger.info("setVersionApplication("+versionApplication+")");
      this.versionApplication = versionApplication;
   }

   public String getReleaseApplication() {
      logger.info("releaseApplication = "+releaseApplication);
      return releaseApplication;
   }

   public void setReleaseApplication(String releaseApplication) {
      logger.info("setReleaseApplication("+releaseApplication+")");
      this.releaseApplication = releaseApplication;
   }
}
