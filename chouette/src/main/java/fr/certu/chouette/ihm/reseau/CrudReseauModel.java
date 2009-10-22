package fr.certu.chouette.ihm.reseau;

import fr.certu.chouette.ihm.struts.PreparableModel;
import fr.certu.chouette.modele.Reseau;
import fr.certu.chouette.service.database.IReseauManager;

public class CrudReseauModel extends SharedReseauModel implements PreparableModel {
	
	private Reseau reseau;
	private Long   idReseau;
	
	public Reseau getReseau() {
		return reseau;
	}
	
	public void setReseau(Reseau reseau) {
		this.reseau = reseau;
	}
	
	public Long getIdReseau() {
		return idReseau;
	}
	
	public void setIdReseau(Long idReseau) {
		this.idReseau = idReseau;
	}
	
	public void prepare(Object ... managers) {
		IReseauManager reseauManager = (IReseauManager)managers[0];
		if (this.getIdReseau() != null)
			this.setReseau(reseauManager.lire(this.getIdReseau()));
	}
}
