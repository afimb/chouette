package fr.certu.chouette.service.importateur.multilignes.altibus;

import java.util.Date;

public class Periode {
	
	private Date    dateDebut;
	private Date    dateFin;
	private String  ref;
	private boolean dimanche;
	private boolean lundi;
	private boolean mardi;
	private boolean mercredi;
	private boolean jeudi;
	private boolean vendredi;
	private boolean samedi;
	
	public void setDateDebut(Date dateDebut) {
		this.dateDebut = dateDebut;
	}
	
	public Date getDateDebut() {
		return dateDebut;
	}
	
	public void setDateFin(Date dateFin) {
		this.dateFin = dateFin;
	}
	
	public Date getDateFin() {
		return dateFin;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}
	
	public String getRef() {
		return ref;
	}
	
	public void setDimanche(boolean dimanche) {
		this.dimanche = dimanche;
	}
	
	public boolean getDimanche() {
		return dimanche;
	}
	
	public void setLundi(boolean lundi) {
		this.lundi = lundi;
	}
	
	public boolean getLundi() {
		return lundi;
	}
	
	public void setMardi(boolean mardi) {
		this.mardi = mardi;
	}
	
	public boolean getMardi() {
		return mardi;
	}
	
	public void setMercredi(boolean mercredi) {
		this.mercredi = mercredi;
	}
	
	public boolean getMercredi() {
		return mercredi;
	}
	
	public void setJeudi(boolean jeudi) {
		this.jeudi = jeudi;
	}
	
	public boolean getJeudi() {
		return jeudi;
	}
	
	public void setVendredi(boolean vendredi) {
		this.vendredi = vendredi;
	}
	
	public boolean getVendredi() {
		return vendredi;
	}
	
	public void setSamedi(boolean samedi) {
		this.samedi = samedi;
	}
	
	public boolean getSamedi() {
		return samedi;
	}
}
