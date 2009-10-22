package fr.certu.chouette.ihm.transporteur;

import fr.certu.chouette.ihm.struts.PreparableModel;
import fr.certu.chouette.modele.Transporteur;
import fr.certu.chouette.service.database.ITransporteurManager;

public class CrudTransporteurModel extends SharedTransporteurModel implements PreparableModel {
	
	private Transporteur transporteur;
	private Long         idTransporteur;
	
	public Transporteur getTransporteur() {
		return transporteur;
	}
	
	public void setTransporteur(Transporteur transporteur) {
		this.transporteur = transporteur;
	}
	
	public Long getIdTransporteur() {
		return idTransporteur;
	}
	
	public void setIdTransporteur(Long idTransporteur) {
		this.idTransporteur = idTransporteur;
	}
	
	public void prepare(Object ... managers) {
		ITransporteurManager transporteurManager = (ITransporteurManager)managers[0];
		if (this.getIdTransporteur() != null)
			this.setTransporteur(transporteurManager.lire(this.getIdTransporteur()));
	}
}
