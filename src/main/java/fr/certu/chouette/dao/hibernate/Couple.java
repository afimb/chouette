package fr.certu.chouette.dao.hibernate;

public class Couple {
	public Long premier;
	public Long deuxieme;
	public Couple(Long premier, Long deuxieme) {
		this.premier = premier;
		this.deuxieme = deuxieme;
	}
	public String toString() {
		return "[[premier : " + premier + "], [deuxieme : " + deuxieme + "]]";
	}
}
