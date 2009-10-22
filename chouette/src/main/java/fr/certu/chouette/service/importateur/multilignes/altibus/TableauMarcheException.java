package fr.certu.chouette.service.importateur.multilignes.altibus;

public class TableauMarcheException extends Exception {
	
	public TableauMarcheException() {
		super();
	}
    
	public TableauMarcheException(String message) {
		super(message);
    }
	
	public TableauMarcheException(String message, Throwable cause) {
		super(message, cause);
	}
    
	public TableauMarcheException(Throwable cause) {
		super(cause);
	}
}
