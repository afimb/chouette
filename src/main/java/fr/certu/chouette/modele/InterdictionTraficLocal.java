package fr.certu.chouette.modele;

import java.util.List;

public class InterdictionTraficLocal extends BaseObjet
{
	private String nom;
	private String objectId;
	private Long idLigne;
	private List<Long> arretPhysiqueIds;
	
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getObjectId() {
		return objectId;
	}
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	public Long getIdLigne() {
		return idLigne;
	}
	public void setIdLigne(Long idLigne) {
		this.idLigne = idLigne;
	}
	public List<Long> getArretPhysiqueIds() {
		return arretPhysiqueIds;
	}
	public void setArretPhysiqueIds(List<Long> arretPhysiqueIds) {
		this.arretPhysiqueIds = arretPhysiqueIds;
	}
	
}
