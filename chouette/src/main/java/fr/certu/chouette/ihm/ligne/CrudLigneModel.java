package fr.certu.chouette.ihm.ligne;

import fr.certu.chouette.ihm.struts.PreparableModel;
import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.service.database.ILigneManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CrudLigneModel extends SharedLigneModel implements PreparableModel {
	
	private final Log   log = LogFactory.getLog(CrudLigneModel.class);
	private       Ligne ligne;
	private       Long  idLigne;
	
	public Ligne getLigne() {
		return ligne;
	}

	public void setLigne(Ligne ligne) {
		this.ligne = ligne;
	}

	public Long getIdLigne() {
		return idLigne;
	}

	public void setIdLigne(Long idLigne) {
		this.idLigne = idLigne;
	}

	public void prepare(Object ... managers) {
		ILigneManager ligneManager = (ILigneManager)managers[0];
		if (this.getIdLigne() != null)
			this.setLigne(ligneManager.lire(this.getIdLigne()));
	}
}
