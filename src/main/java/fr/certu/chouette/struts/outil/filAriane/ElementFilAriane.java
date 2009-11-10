package fr.certu.chouette.struts.outil.filAriane;

public class ElementFilAriane {
	
	private String cleTexte;
	private String parametreTexte;
	private String url;
	
	public ElementFilAriane(String cleTexte, String parametreTexte, String url) {
		this.cleTexte = cleTexte;
		this.url = url;
		this.parametreTexte = parametreTexte;
	}
	
	public String getCleTexte() {
		return cleTexte;
	}
	
	public String getParametreTexte() {
		return parametreTexte;
	}
	
	public String getUrl() {
		return url;
	}
}
