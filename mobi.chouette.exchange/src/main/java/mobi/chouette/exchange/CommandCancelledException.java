package mobi.chouette.exchange;

public class CommandCancelledException extends RuntimeException {

	public CommandCancelledException(String commandCancelled) {
		super(commandCancelled);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
