package mobi.chouette.exchange.importer.geometry.osrm;

/**
 * Exception raised when accessing the OSRM route section generator.
 */
public class OsrmRouteSectionException extends RuntimeException {
    public OsrmRouteSectionException(String message, Throwable cause) {
        super(message,cause);
    }

    public OsrmRouteSectionException(String message) {
        super(message);
    }
}
