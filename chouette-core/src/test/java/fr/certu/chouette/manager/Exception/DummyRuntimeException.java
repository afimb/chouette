package fr.certu.chouette.manager.exception;

import fr.certu.chouette.common.ChouetteRuntimeException;


@SuppressWarnings("serial")
public class DummyRuntimeException extends ChouetteRuntimeException 
{

	@Override
	public String getPrefix() {
		// TODO Auto-generated method stub
		return "DUM";
	}

	@Override
	public String getCode() {
		// TODO Auto-generated method stub
		return "DUMMY_RUNTIME";
	}

	/**
	 * 
	 */
	public DummyRuntimeException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DummyRuntimeException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public DummyRuntimeException(String... args) {
		super(args);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public DummyRuntimeException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 * @param args
	 */
	public DummyRuntimeException(Throwable cause, String... args) {
		super(cause, args);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public DummyRuntimeException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
