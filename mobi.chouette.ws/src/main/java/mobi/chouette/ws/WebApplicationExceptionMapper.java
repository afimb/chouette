package mobi.chouette.ws;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import mobi.chouette.service.RequestExceptionCode;
import mobi.chouette.service.ServiceExceptionCode;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;


public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

	private static final List<String> valid_codes = new ArrayList<>();

	@Override
	public Response toResponse(WebApplicationException exception) {
		String code = "OTHER_ERROR";
		String description = null;
		if (exception.getMessage() != null && !exception.getMessage().isEmpty()) {
			if (valid_codes.contains(exception.getMessage())) {
				code = exception.getMessage();
				if (exception.getCause() != null) {
					description = exception.getCause().getMessage();
				}
			} else {
				description = exception.getMessage();
			}
		} else {
			description = exception.toString();
		}
		try {
			JSONObject response = new JSONObject();
			response.put("error_code", code);
			if (description != null) {
				response.put("error_description", description);
			}
			return Response.status(exception.getResponse().getStatus()).entity(response.toString()).build();
		} catch (JSONException e) {
			return exception.getResponse();
		}

	}

	static {
		for (ServiceExceptionCode code : ServiceExceptionCode.values()) {
			valid_codes.add(code.name());
		}
		for (RequestExceptionCode code : RequestExceptionCode.values()) {
			valid_codes.add(code.name());
		}
	}

}
