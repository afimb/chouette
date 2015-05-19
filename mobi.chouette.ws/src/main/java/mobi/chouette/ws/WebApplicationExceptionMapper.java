package mobi.chouette.ws;

import java.text.MessageFormat;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {
	
	private static final String jsonTemplate = "'{' \"error_code\" : \"{0}\"'}'";

	@Override
	public Response toResponse(WebApplicationException exception) {
		if (exception.getMessage() != null && !exception.getMessage().isEmpty())
		  return Response.status(exception.getResponse().getStatus()).entity(MessageFormat.format(jsonTemplate, exception.getMessage())).build();
        return exception.getResponse();
	}

}
