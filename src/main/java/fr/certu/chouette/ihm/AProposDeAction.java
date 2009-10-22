package fr.certu.chouette.ihm;

@SuppressWarnings({"serial", "unchecked"})
public class AProposDeAction extends GeneriqueAction {
	
	private String versionApplication;
	
	public void setMenuSelectionne(String menuSelectionne) {
		session.put("menuSelectionne", menuSelectionne);
	}
	
	public String getVersionApplication() {
		return versionApplication;
	}
	
	public void setVersionApplication(String versionApplication) {
		this.versionApplication = versionApplication;
	}
}
