package fr.certu.chouette.modele;

public class LienTMCourse extends BaseObjet
{
	private Long idTableauMarche;
	private Long idCourse;
	
	public Long getIdCourse() {
		return idCourse;
	}

	public void setIdCourse( final Long idCourse) {
		this.idCourse = idCourse;
	}

	public Long getIdTableauMarche() {
		return idTableauMarche;
	}

	public void setIdTableauMarche( final Long idTableauMarche) {
		this.idTableauMarche = idTableauMarche;
	}
	
	
}
