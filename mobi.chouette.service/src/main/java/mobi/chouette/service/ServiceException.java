/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mobi.chouette.service;

import mobi.chouette.core.ChouetteException;

/**
 *
 * @author marc
 */
public class ServiceException extends ChouetteException {
	
	private static final long serialVersionUID = 6057054896800493583L;
	
	private static final String PREFIX = "SVR";
	private ServiceExceptionCode code;

	public ServiceException(ServiceExceptionCode code, String message) {
		super(message);
		this.code = code;
	}

	public ServiceException(ServiceExceptionCode code, Throwable cause) {
		super(code.toString(), cause);
		this.code = code;
	}
	
	public ServiceException(ServiceExceptionCode code, String message, Throwable cause
			) {
		super(message, cause);
		this.code = code;
	}

	public ServiceExceptionCode getExceptionCode() {
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
