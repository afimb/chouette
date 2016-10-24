package mobi.chouette.ws;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Constant;
import mobi.chouette.model.statistics.LineStatistics;
import mobi.chouette.service.TransitDataStatisticsService;

@Log4j
@RequestScoped
@Path("/statistics")
public class RestStatisticsService implements Constant {

	private static String api_version_key = "X-ChouetteIEV-Media-Type";
	private static String api_version = "iev.v1.0; format=json";

	@Inject
	TransitDataStatisticsService statisticsService;
	
	@GET
	@Path("/{ref}/line")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response lineStats(@PathParam("ref") String referential) {
		try {
			log.info(Color.CYAN + "Call lineStats referential = " + referential  + Color.NORMAL);

			LineStatistics lineStatistics = statisticsService.getLineStatisticsByLineNumber(referential);
			ResponseBuilder builder = Response.ok(lineStatistics);
			builder.header(api_version_key, api_version);
			return builder.build();

		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new WebApplicationException("INTERNAL_ERROR: "+ex.getMessage(), Status.INTERNAL_SERVER_ERROR);
		}
	}
	


}
