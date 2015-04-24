/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package mobi.chouette.core;


@SuppressWarnings("serial")
public class CoreException extends ChouetteException {
	private static final String PREFIX = "COR";
	private CoreExceptionCode code;

	public CoreException(CoreExceptionCode code, String message) {
		super(message);
		this.code = code;
	}

	public CoreException(CoreExceptionCode code, Throwable cause,
			String message) {
		super(message, cause);
		this.code = code;
	}

	public CoreExceptionCode getExceptionCode() {
		return code;
	}

	public String getCode() {
		return code.name();
	}

	@Override
	public String getPrefix() {

		return PREFIX;
	}

}
