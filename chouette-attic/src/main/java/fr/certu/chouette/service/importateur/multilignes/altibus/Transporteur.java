package fr.certu.chouette.service.importateur.multilignes.altibus;

import altibus.schema.Altibus;
import java.util.HashMap;
import java.util.Map;

public class Transporteur {
	
	private String                     name;
	private Map<String, Ligne>         lignes;
	private Map<String, Arret>         arrets;
	private Map<String, TableauMarche> tableauxMarche;
	
	public Transporteur() {
		super();
		lignes = new HashMap<String, Ligne>();
		arrets = new HashMap<String, Arret>();
		tableauxMarche = new HashMap<String, TableauMarche>();
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setLignes(Map<String, Ligne> lignes) {
		this.lignes = lignes;
	}
	
	public Map<String, Ligne> getLignes() {
		return lignes;
	}
	
	public void setArrets(Map<String, Arret> arrets) {
		this.arrets = arrets;
	}
	
	public Map<String, Arret> getArrets() {
		return arrets;
	}
	
	public void setTableauxMarche(Map<String, TableauMarche> tableauxMarche) {
		this.tableauxMarche = tableauxMarche;
	}
	
	public Map<String, TableauMarche> getTableauxMarche() {
		return tableauxMarche;
	}
	
	public Map<String, Ligne> getLignes(Altibus altibus) {
		altibus.schema.Lignes[] ligs = altibus.getLignes();
		for (int i = 0; i < ligs.length; i++) {
			altibus.schema.Ligne[] liggs = ligs[i].getLigne();
			for (int j = 0; j < liggs.length; j++) {
				altibus.schema.Ligne lig = liggs[j];
				Ligne ligne = new Ligne();
				ligne.setShortName(lig.getNomLigne());
				ligne.setReg(lig.getRefLigne());
				ligne.setName(ligne.getShortName()+" : "+ligne.getReg()+" : FROM "+lig.getRefGareDepart()+" TO "+lig.getRefGareArrivee());
				lignes.put(getName(), ligne);
			}
		}
		return lignes;
	}
}
