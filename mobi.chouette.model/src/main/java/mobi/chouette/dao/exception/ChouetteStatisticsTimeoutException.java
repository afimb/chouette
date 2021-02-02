package mobi.chouette.dao.exception;

/**
 * Thrown if the statistics query times out, most likely due to an import process locking the database tables (TRUNCATE operations block SELECT queries)
 */
public class ChouetteStatisticsTimeoutException extends Exception {
    public ChouetteStatisticsTimeoutException(Throwable cause) {
        super(cause);
    }

}
