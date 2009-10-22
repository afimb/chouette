package fr.certu.chouette.service.importateur.multilignes.altibus;

import altibus.schema.Altibus;
import altibus.schema.Calendrier;
import altibus.schema.Calendriers;
import altibus.schema.Station;
import altibus.schema.Stations;
import java.util.HashMap;
import java.util.Map;

public class Reseau {
	
	private String                     name;
	private Map<String, Transporteur>  transporteurs;
	private Map<String, Arret>         arrets;
	private Map<String, TableauMarche> tableauxMarche;
	
	public Reseau() {
		super();
		transporteurs = new HashMap<String, Transporteur>();
		arrets = new HashMap<String, Arret>();
		tableauxMarche = new HashMap<String, TableauMarche>();
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setTransporteurs(Map<String, Transporteur> transporteurs) {
		this.transporteurs = transporteurs;
	}
	
	public Map<String, Transporteur> getTransporteurs() {
		return transporteurs;
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
	
	public Transporteur getTransporteur(String exploitantName) {
		Transporteur transporteur = transporteurs.get(exploitantName);
		if (transporteur == null) {
			transporteur = new Transporteur();
			transporteur.setName(exploitantName);
			transporteurs.put(exploitantName, transporteur);
		}
		return transporteur;
	}

	public Map<String, Arret> getArrets(Stations[] stations) {
		if ((stations == null) || (stations.length == 0))
			return null;
		Map<String, Arret> transporteursArrets = new HashMap<String, Arret>();
		for (int i = 0; i < stations.length; i++) {
			Station[] sts = stations[i].getStation();
			for (int j = 0; j < sts.length; j++) {
				String reg = sts[j].getNumEnregistrementStation();
				Arret arret = arrets.get(reg);
				if (arret == null) {
					arret = new Arret();
					arret.setName(sts[j].getNomStation());
					arret.setRef(sts[j].getRefStation());
					arret.setReg(reg);
					arrets.put(reg, arret);
				}
				transporteursArrets.put(reg, arret);
			}
		}
		return transporteursArrets;
	}
	
	public Map<String, TableauMarche> getTableauxMarche(Altibus altibus) throws TableauMarcheException {
		Calendriers[] calendriers = altibus.getCalendriers();
		if ((calendriers == null) || (calendriers.length == 0))
			return null;
		Map<String, TableauMarche> transporteursTableauxMarche = new HashMap<String, TableauMarche>();
		for (int i = 0; i < calendriers.length; i++) {
			Calendrier[] cals = calendriers[i].getCalendrier();
			for (int j = 0; j < cals.length; j++) {
				String ref = cals[j].getRefCalendrier() + "_" + altibus.getExploitant().getNomExploitant();
				TableauMarche tableauMarche = tableauxMarche.get(ref);
				if (tableauMarche == null) {
					tableauMarche = new TableauMarche();
					tableauMarche.setName(cals[j].getNomCalendrier());
					tableauMarche.setRef(ref);
					tableauMarche.setPeriodes(tableauMarche.getIntervalles(altibus.getIntervalles()));
					tableauxMarche.put(ref, tableauMarche);
				}
				transporteursTableauxMarche.put(ref, tableauMarche);
			}
		}
		return transporteursTableauxMarche;
	}
}
