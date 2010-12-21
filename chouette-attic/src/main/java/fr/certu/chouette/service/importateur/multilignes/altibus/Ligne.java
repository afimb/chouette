package fr.certu.chouette.service.importateur.multilignes.altibus;

public class Ligne {
	
	private String name;
	private String shortName;
	private String reg;
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	
	public String getShortName() {
		return shortName;
	}
	
	public void setReg(String reg) {
		this.reg = reg;
	}
	
	public String getReg() {
		return reg;
	}
}
