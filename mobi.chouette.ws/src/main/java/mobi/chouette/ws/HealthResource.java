package mobi.chouette.ws;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import lombok.extern.log4j.Log4j;
import mobi.chouette.service.HealthService;

import org.springframework.stereotype.Component;

@Component
@Log4j
@Produces("application/json")
@Path("health")
public class HealthResource {

	@Inject
	private HealthService healthService;

	@GET
	@Path("/ready")
	public Response isReady() {
		log.debug("Checking readiness...");
		if (healthService.isReady()) {
			return Response.ok().build();
		} else {
			return Response.serverError().build();
		}
	}

	@GET
	@Path("/live")
	public Response isLive() {
		log.debug("Checking liveness...");
		if (healthService.isLive()) {
			return Response.ok().build();
		} else {
			return Response.serverError().build();
		}
	}

}
