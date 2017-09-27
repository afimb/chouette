package mobi.chouette.ws;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Constant;
import mobi.chouette.common.ContenerChecker;
import mobi.chouette.common.PropertyNames;
import mobi.chouette.exchange.TestDescription;
import mobi.chouette.model.iev.Job;
import mobi.chouette.model.iev.Stat;
import mobi.chouette.service.JobService;
import mobi.chouette.service.JobServiceManager;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

@Path("/admin")
@Log4j
@RequestScoped
public class RestAdmin implements Constant {

	private static String api_version_key = "X-ChouetteIEV-Media-Type";
	private static String api_version = "iev_admin.v1.0; format=txt";

	private static String GLOBAL_KEY = "Global";
	private static String REFERENTIAL_KEY = "Referentials";
	private static String TEST_KEY = "Tests";
	private static String STAT_KEY = "Stats";

	@Inject
	JobServiceManager jobServiceManager;

	@Inject
	ContenerChecker checker;

	@Context
	UriInfo uriInfo;

	// jobs listing
	@GET
	@Path("/active_jobs{format:(\\.[^\\.]+?)?}")
	@Produces({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON })
	public Response activeJobs(@PathParam("format") String format, @QueryParam("key") final String authorisationKey) {

		log.info(Color.BLUE + "Call Admin active_jobs" + Color.NORMAL);
		Response r = checkKey(authorisationKey);
		if (r != null)
			return r; // invalid key

		if (format == null)
			format = ".json";
		format = format.toLowerCase();
		if (!format.equals(".txt") && !format.equals(".json")) {
			log.warn("admin call with invalid output format = " + format);
			ResponseBuilder builder = Response.status(Status.BAD_REQUEST);
			builder.header(api_version_key, api_version);
			return builder.build();
		}

		try {
			// create jobs listing
			JobStat globalStat = new JobStat(GLOBAL_KEY);
			Map<String, JobStat> byReferential = new HashMap<>();
			{
				List<JobService> jobServices = jobServiceManager.activeJobs();

				// re factor Parameters dependencies

				globalStat.jobCount = jobServices.size();
				for (JobService jobService : jobServices) {
					String referential = jobService.getReferential();
					JobStat refStat = byReferential.get(referential);
					if (refStat == null) {
						refStat = new JobStat(referential);
						byReferential.put(referential, refStat);
					}
					refStat.jobCount++;
					if (jobService.getStatus().equals(Job.STATUS.STARTED)) {
						refStat.startedJobCount++;
						globalStat.startedJobCount++;
					} else {
						refStat.scheduledJobCount++;
						globalStat.scheduledJobCount++;
					}
				}
				jobServices.clear();
			}

			ResponseBuilder builder = null;
			if (format.equals(".json")) {
				JSONObject resjson = new JSONObject();
				resjson.put(GLOBAL_KEY, globalStat.toJson());
				JSONArray resrefs = new JSONArray();
				resjson.put(REFERENTIAL_KEY, resrefs);
				for (Entry<String, JobStat> entry : byReferential.entrySet()) {
					resrefs.put(entry.getValue().toJson());
				}
				builder = Response.ok(resjson.toString(2)).type(MediaType.APPLICATION_JSON_TYPE);
			} else {
				StringBuilder result = new StringBuilder();
				result.append(globalStat.toString());
				for (Entry<String, JobStat> entry : byReferential.entrySet()) {
					result.append(entry.getValue().toString());
				}
				builder = Response.ok(result).type(MediaType.TEXT_PLAIN);
			}
			builder.header(api_version_key, api_version);

			return builder.build();
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new WebApplicationException("INTERNAL_ERROR", Status.INTERNAL_SERVER_ERROR);
		}
	}

	@DELETE
	@Path("/completed_jobs")
	public void removeOldJobs(@QueryParam("keepDays") @DefaultValue("100")  final int keepDays, @QueryParam("keepJobs") @DefaultValue("100") final int keepJobs) {
		log.info(Color.BLUE + "Call Admin removeOldJobs, keepDays=" + keepDays + ", keepJobs=" + keepJobs + Color.NORMAL);
		try {
			jobServiceManager.removeOldJobs(keepDays, keepJobs);
			log.info(Color.BLUE + "Finished removeOldJobs, keepDays=" + keepDays + ", keepJobs=" + keepJobs + Color.NORMAL);
		} catch (Exception ex) {
			log.error("removeOldJobs failed with exception:" + ex.getMessage(), ex);
			throw new WebApplicationException("INTERNAL_ERROR", Status.INTERNAL_SERVER_ERROR);
		}
	}

	@GET
	@Path("/test_list/{action}{type:(/[^/]+?)?}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getTestList(@PathParam("action") String action,
			@PathParam("type") String type) {

			log.info(Color.BLUE + "Call getTestList action = " + action
					+ (type == null ? "" : ", type = " + type) + Color.NORMAL);

			// Convertir les parametres fournis
			type = parseType(type);
			try {
				List<TestDescription> lstTest = jobServiceManager.getTestList(action, type);
				ResponseBuilder builder = null;
				JSONObject resjson = new JSONObject();
				JSONArray restests = new JSONArray();
				resjson.put(TEST_KEY, restests);

				for (TestDescription test : lstTest) {
					JSONObject result = new JSONObject();
					result.put("level", test.getLevel());
					result.put("code", test.getCode());
					result.put("severity", test.getSeverity());

					restests.put(result);
				}

				builder = Response.ok(resjson.toString(2)).type(MediaType.APPLICATION_JSON_TYPE);

				builder.header(api_version_key, api_version);

				return builder.build();
			} catch (Exception ex) {
				log.error(ex.getMessage(), ex);
				throw new WebApplicationException("INTERNAL_ERROR", Status.INTERNAL_SERVER_ERROR);
			} finally {

				log.info(Color.BLUE + "getTestList returns" + Color.NORMAL);
			}
	}

	// global stat listing
	@GET
	@Path("/get_monthly_stats")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response monthlyStats(@QueryParam("key") final String authorisationKey) {
		log.info(Color.BLUE + "Call Admin get_monthly_stats" + Color.NORMAL);
		Response r = checkKey(authorisationKey);
		if (r != null)
			return r; // invalid key
		try {
			List<Stat> lstStat = jobServiceManager.getMontlyStats();
			ResponseBuilder builder = null;
			JSONObject resjson = new JSONObject();
			JSONArray resstats = new JSONArray();
			resjson.put(STAT_KEY, resstats);

			for (Stat stat : lstStat) {
				JSONObject result = new JSONObject();
				result.put("id", stat.getId());
				result.put("date", stat.getDate().toDate());
				result.put("referential", stat.getReferential());
				result.put("action", stat.getAction());
				if (stat.getFormat() != null)
					result.put("format", stat.getFormat());
				resstats.put(result);
			}

			builder = Response.ok(resjson.toString(2)).type(MediaType.APPLICATION_JSON_TYPE);

			builder.header(api_version_key, api_version);

			return builder.build();
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new WebApplicationException("INTERNAL_ERROR", Status.INTERNAL_SERVER_ERROR);
		}
	}

	private Response checkKey(final String authorisationKey) {
		if (authorisationKey == null || authorisationKey.isEmpty()) {
			log.warn("admin call without key");
			ResponseBuilder builder = Response.status(Status.UNAUTHORIZED);
			builder.header(api_version_key, api_version);
			return builder.build();
		}
		String securityToken = System.getProperty(checker.getContext() + PropertyNames.ADMIN_KEY);
		if (securityToken == null || securityToken.isEmpty()) {
			log.warn("admin call without property " + checker.getContext() + PropertyNames.ADMIN_KEY + " set");
			ResponseBuilder builder = Response.status(Status.FORBIDDEN);
			builder.header(api_version_key, api_version);
			return builder.build();
		}
		if (!securityToken.equals(authorisationKey)) {
			log.warn("admin call with invalid key = " + authorisationKey);
			ResponseBuilder builder = Response.status(Status.UNAUTHORIZED);
			builder.header(api_version_key, api_version);
			return builder.build();
		}
		return null;
	}

	private class JobStat {
		String key;
		int jobCount = 0;
		int scheduledJobCount = 0;
		int startedJobCount = 0;

		JobStat(String key) {
			this.key = key;
		}

		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append(key + ".jobCount=" + jobCount + "\n");
			builder.append(key + ".scheduledJobCount=" + scheduledJobCount + "\n");
			builder.append(key + ".startedJobCount=" + startedJobCount + "\n");
			return builder.toString();
		}

		public JSONObject toJson() throws JSONException {
			JSONObject result = new JSONObject();
			result.put("name", key);
			result.put("job_count", jobCount);
			result.put("scheduled_job_count", scheduledJobCount);
			result.put("started_job_count", startedJobCount);
			return result;
		}
	}
	
	private String parseType(String type) {
		if (type != null && type.startsWith("/")) {
			return type.substring(1);
		}
		return type;
	}
	
	

}
