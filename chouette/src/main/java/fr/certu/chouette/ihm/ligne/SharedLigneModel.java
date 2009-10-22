package fr.certu.chouette.ihm.ligne;

import fr.certu.chouette.modele.Reseau;
import fr.certu.chouette.modele.Transporteur;
import java.util.List;
import java.util.Map;

public abstract class SharedLigneModel {

	private List<Reseau>            reseaux;
	private Map<Long, Reseau>       reseauParId;
	private List<Transporteur>      transporteurs;
	private Map<Long, Transporteur> transporteurParId;
	
	public List<Reseau> getReseaux() {
		return reseaux;
	}
	
	public void setReseaux(List<Reseau> reseaux) {
		this.reseaux = reseaux;
	}
	
	public Map<Long, Reseau> getReseauParId() {
		return reseauParId;
	}
	
	public void setReseauParId(Map<Long, Reseau> reseauParId) {
		this.reseauParId = reseauParId;
	}
	
	public List<Transporteur> getTransporteurs() {
		return transporteurs;
	}
	
	public void setTransporteurs(List<Transporteur> transporteurs) {
		this.transporteurs = transporteurs;
	}
	
	public Map<Long, Transporteur> getTransporteurParId() {
		return transporteurParId;
	}
	
	public void setTransporteurParId(Map<Long, Transporteur> transporteurParId) {
		this.transporteurParId = transporteurParId;
	}
}
