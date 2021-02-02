package mobi.chouette.ws;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import mobi.chouette.service.ReferentialService;
import mobi.chouette.service.RequestExceptionCode;
import mobi.chouette.service.RequestServiceException;
import mobi.chouette.service.TransitDataStatisticsService;

@Log4j
@RequestScoped
@Path("/statistics")
public class RestStatisticsService implements Constant {

	private static String api_version_key = "X-ChouetteIEV-Media-Type";
	private static String api_version = "iev.v1.0; format=json";

	@Inject
	TransitDataStatisticsService statisticsService;

	@Inject
	ReferentialService referentialService;

	private static final String PARAM_CATEGORY_SEPARATOR = ";";

	@GET
	@Path("/{ref}/line")
	@Produces({MediaType.APPLICATION_JSON})
	public Response lineStats(@PathParam("ref") String referential, @QueryParam("startDate") Date startDate, @QueryParam("days") int days,
							  @QueryParam("minDaysValidityCategory") String minDaysValidityCategories[]) {
		try {
			log.info(Color.CYAN + "Calculating line statistics for referential " + referential + Color.NORMAL);
			Map<Integer, String> minDaysValidityCategoryMap = parseCategoryMap(minDaysValidityCategories);
			try {
				LineStatistics lineStatistics = statisticsService.getLineStatisticsByLineNumber(referential, startDate, days, minDaysValidityCategoryMap);
				log.info(Color.CYAN + "Calculated lineStats for referential " + referential + Color.NORMAL);
				return Response.ok(lineStatistics).header(api_version_key, api_version).build();
			} catch (RequestServiceException e) {
				if (e.getRequestExceptionCode() == RequestExceptionCode.REFERENTIAL_BUSY) {
					log.warn(Color.CYAN + "Statistic Query timeout for referential: " + referential + ". An import process is probably in progress.", e);
					return Response.status(423).header(api_version_key, api_version).entity("The referential is busy, cannot update statistics.").build();
				} else {
					throw e;
				}
			}
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new WebApplicationException("INTERNAL_ERROR: " + ex.getMessage(), Status.INTERNAL_SERVER_ERROR);
		}
	}

	@GET
	@Path("/line")
	@Produces({MediaType.APPLICATION_JSON})
	public Response lineStats(@QueryParam("startDate") Date startDate, @QueryParam("days") int days,
							  @QueryParam("minDaysValidityCategory") String minDaysValidityCategories[], @QueryParam("referentials") String referentials) {
		try {
			log.info(Color.CYAN + "Calculating line statistics for referentials " + referentials + Color.NORMAL);

			Map<Integer, String> minDaysValidityCategoryMap = parseCategoryMap(minDaysValidityCategories);

			Map<String, LineStatistics> lineStatsPerReferential = new HashMap<>();

			List<String> availableReferentials = referentialService.getReferentialCodes();

			for (String referential : referentials.split(",")) {
				if (availableReferentials.contains(referential)) {
					try {
						LineStatistics lineStatisticsByLineNumber = statisticsService.getLineStatisticsByLineNumber(referential, startDate, days,
								minDaysValidityCategoryMap);
						lineStatsPerReferential.put(referential, lineStatisticsByLineNumber);
					} catch (RequestServiceException e) {
						if (e.getRequestExceptionCode() == RequestExceptionCode.REFERENTIAL_BUSY) {
							log.warn(Color.CYAN + "Statistic Query timeout for referential: " + referential + ". A data import is probably in progress. Ignoring request for line statistics for this referential", e);
						} else {
							throw e;
						}
					}
				} else {
					log.warn(Color.CYAN + "Ignoring request for lineStats for unknown referential: " + referential);
				}
			}
			ResponseBuilder builder = Response.ok(lineStatsPerReferential);
			builder.header(api_version_key, api_version);
			log.info(Color.CYAN + "Calculated line statistics for referentials " + referentials + Color.NORMAL);
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
