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
	private static final long serialVersionUID = 568796258783939693L;
	
	private static final String PREFIX = "SVR-REQ";
	private RequestExceptionCode requestExceptionCode;
    

	public RequestServiceException(RequestExceptionCode requestExceptionCode, String message) {
		super(ServiceExceptionCode.INVALID_REQUEST, message);
		this.requestExceptionCode = requestExceptionCode;
	}

	public RequestServiceException(RequestExceptionCode requestExceptionCode, String message, Throwable cause
			) {
		super(ServiceExceptionCode.INVALID_REQUEST, message, cause);
		this.requestExceptionCode = requestExceptionCode;
	}
	
	public RequestServiceException(RequestExceptionCode requestExceptionCode, Throwable cause
			) {
		super(ServiceExceptionCode.INVALID_REQUEST, cause);
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
