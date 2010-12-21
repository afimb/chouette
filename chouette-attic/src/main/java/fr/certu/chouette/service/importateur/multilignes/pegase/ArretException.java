package fr.certu.chouette.service.importateur.multilignes.pegase;

public class ArretException extends Exception {
	
	public ArretException() {
		super();
	}
	
	public ArretException(String message) {
		super(message);
	}
	
	public ArretException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public ArretException(Throwable cause) {
		super(cause);
	}
}
