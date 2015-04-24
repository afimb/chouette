/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mobi.chouette.service;

/**
 *
 * @author marc
 */
public class RequestServiceException extends ServiceException {
	private static final String PREFIX = "SVR-REQ";
	private RequestExceptionCode requestExceptionCode;
    

	public RequestServiceException(RequestExceptionCode requestExceptionCode, String... args) {
		super(ServiceExceptionCode.INVALID_REQUEST, args);
		this.requestExceptionCode = requestExceptionCode;
	}

	public RequestServiceException(RequestExceptionCode requestExceptionCode, Throwable cause,
			String... args) {
		super(ServiceExceptionCode.INVALID_REQUEST, cause, args);
		this.requestExceptionCode = requestExceptionCode;
	}

	public RequestExceptionCode getRequestExceptionCode() {
		return requestExceptionCode;
	}

	public String getRequestCode() {
		return requestExceptionCode.name();
	}

	@Override
	public String getPrefix() {
		return PREFIX;
	}
}
