package fr.certu.chouette.dao.hibernate;

public class Triplet {
	public Long premier;
	public Long deuxieme;
	public Long troisieme;
	public Triplet(Long premier, Long deuxieme, Long troisieme) {
		this.premier = premier;
		this.deuxieme = deuxieme;
		this.troisieme = troisieme;
	}
}
