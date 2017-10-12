package mobi.chouette.common.file;

/**
 * Exception thrown when file access fails.
 */
public class FileServiceException extends RuntimeException {

	public FileServiceException() {
	}

	public FileServiceException(String message) {
		super(message);
	}

	public FileServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public FileServiceException(Throwable cause) {
		super(cause);
	}
}
