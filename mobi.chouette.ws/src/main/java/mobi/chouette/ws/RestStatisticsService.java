package mobi.chouette.ws;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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

    private static final String PARAM_CATEGORY_SEPARATOR = ";";

    @GET
    @Path("/{ref}/line")
    @Produces({MediaType.APPLICATION_JSON})
    public Response lineStats(@PathParam("ref") String referential, @QueryParam("startDate") Date startDate, @QueryParam("days") int days,
                              @QueryParam("minDaysValidityCategory") String minDaysValidityCategories[]) {
        try {
            log.info(Color.CYAN + "Call lineStats referential = " + referential + Color.NORMAL);


            Map<Integer, String> minDaysValidityCategoryMap = parseCategoryMap(minDaysValidityCategories);

            LineStatistics lineStatistics = statisticsService.getLineStatisticsByLineNumber(referential, startDate, days,
                    minDaysValidityCategoryMap);
            ResponseBuilder builder = Response.ok(lineStatistics);
            builder.header(api_version_key, api_version);
            return builder.build();

        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new WebApplicationException("INTERNAL_ERROR: " + ex.getMessage(), Status.INTERNAL_SERVER_ERROR);
        }
    }

    @GET
    @Path("/line")
    @Produces({MediaType.APPLICATION_JSON})
    public Response lineStats(@QueryParam("startDate") Date startDate, @QueryParam("days") int days,
                              @QueryParam("minDaysValidityCategory") String minDaysValidityCategories[], @QueryParam("referential") String referentials[]) {
        try {
            log.info(Color.CYAN + "Call lineStats for referentials:" + Arrays.toString(referentials) + Color.NORMAL);

            Map<Integer, String> minDaysValidityCategoryMap = parseCategoryMap(minDaysValidityCategories);

            Map<String, LineStatistics> lineStatsPerReferential = new HashMap<>();

            for (String referential : referentials) {
                lineStatsPerReferential.put(referential, statisticsService.getLineStatisticsByLineNumber(referential, startDate, days,
                        minDaysValidityCategoryMap));
            }
            ResponseBuilder builder = Response.ok(lineStatsPerReferential);
            builder.header(api_version_key, api_version);
            return builder.build();

        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new WebApplicationException("INTERNAL_ERROR: " + ex.getMessage(), Status.INTERNAL_SERVER_ERROR);
        }

    }


    private Map<Integer, String> parseCategoryMap(@QueryParam("minDaysValidityCategory") String[] minDaysValidityCategories) {
        Map<Integer, String> minDaysValidityCategoryMap = new HashMap<>();
        if (minDaysValidityCategories != null) {
            for (String minDaysValidityCategory : minDaysValidityCategories) {
                if (minDaysValidityCategory.contains(PARAM_CATEGORY_SEPARATOR)) {
                    String[] tokens = minDaysValidityCategory.split(PARAM_CATEGORY_SEPARATOR);
                    minDaysValidityCategoryMap.put(Integer.valueOf(tokens[0]), tokens[1]);
                } else {
                    minDaysValidityCategoryMap.put(Integer.valueOf(minDaysValidityCategory),
                            minDaysValidityCategory);
                }

            }
        }
        return minDaysValidityCategoryMap;
    }


}
