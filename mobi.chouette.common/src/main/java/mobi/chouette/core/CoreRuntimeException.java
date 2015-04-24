/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package mobi.chouette.core;


@SuppressWarnings("serial")
public class CoreRuntimeException extends ChouetteRuntimeException {
	private static final String PREFIX = "COR";
	private CoreExceptionCode code;

	public CoreRuntimeException(CoreExceptionCode code, String message) {
		super(message);
		this.code = code;
	}

	public CoreRuntimeException(CoreExceptionCode code, Throwable cause,
			String message) {
		super(message,cause);
		this.code = code;
	}

	public CoreExceptionCode getExceptionCode() {
		return code;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.certu.chouette.common.ChouetteRuntimeException#getCode()
	 */
	public String getCode() {
		return code.name();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.certu.chouette.common.ChouetteRuntimeException#getPrefix()
	 */
	@Override
	public String getPrefix() {
		return PREFIX;
	}

}
