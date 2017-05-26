package mobi.chouette.ws;

import lombok.extern.log4j.Log4j;
import mobi.chouette.service.StopAreaService;
import mobi.chouette.common.Color;
import mobi.chouette.service.JobServiceManager;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;

@Log4j
@RequestScoped
@Path("/stop_place")
public class RestNetexStopPlaceService {

    private static String api_version_key = "X-ChouetteIEV-Media-Type";
    private static String api_version = "iev.v1.0; format=json";

    @Inject
    private StopAreaService stopAreaService;
    @Inject
    private JobServiceManager jobServiceManager;

    @POST
    @Consumes({MediaType.APPLICATION_XML})
    public Response createOrUpdateStops(InputStream inputStream) {
        try {
            if (existsActiveJobs()) {
                return Response.status(423).entity("Cannot update stops for referential with active jobs").build();
            }
            log.info(Color.CYAN + "Create or update stop places");
            stopAreaService.createOrUpdateStopPlacesFromNetexStopPlaces(inputStream);
            Response.ResponseBuilder builder = Response.ok();
            builder.header(api_version_key, api_version);
            return builder.build();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new WebApplicationException("INTERNAL_ERROR: " + ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean existsActiveJobs() {
        return !jobServiceManager.activeJobs().isEmpty();
    }

}
