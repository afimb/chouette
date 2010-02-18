package fr.certu.chouette.service.importateur.multilignes.pegase;

public class LigneException extends Exception {
	
	public LigneException() {
		super();
	}
	
	public LigneException(String message) {
		super(message);
	}
	
	public LigneException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public LigneException(Throwable cause) {
		super(cause);
	}
}
