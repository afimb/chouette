package mobi.chouette.ws;

import java.io.InputStream;
import java.net.URI;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Constant;
import mobi.chouette.common.ContenerChecker;
import mobi.chouette.common.PropertyNames;
import mobi.chouette.exchange.Test;
import mobi.chouette.model.iev.Job;
import mobi.chouette.service.JobService;
import mobi.chouette.service.JobServiceManager;
import mobi.chouette.service.RequestExceptionCode;
import mobi.chouette.service.RequestServiceException;
import mobi.chouette.service.ServiceException;
import mobi.chouette.service.ServiceExceptionCode;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

@Path("/admin")
@Log4j
@RequestScoped
public class RestAdmin implements Constant {

	private static String api_version_key = "X-ChouetteIEV-Media-Type";
	private static String api_version = "iev_admin.v1.0; format=txt";

	private static String GLOBAL_KEY = "Global";
	private static String REFERENTIAL_KEY = "Referentials";
	private static String TEST_KEY = "Tests";

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
		if (authorisationKey == null || authorisationKey.isEmpty()) {
			log.warn("admin call without key");
			ResponseBuilder builder = Response.status(Status.UNAUTHORIZED);
			builder.header(api_version_key, api_version);
			return builder.build();
		}
		String securityToken = System.getProperty(checker.getContext()+PropertyNames.ADMIN_KEY);
		if (securityToken == null || securityToken.isEmpty()) {
			log.warn("admin call without property " + checker.getContext()+PropertyNames.ADMIN_KEY + " set");
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
	
	@GET
	@Path("/{action}{type:(/[^/]+?)?}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getTestList(@PathParam("action") String action,
			@PathParam("type") String type, MultipartFormDataInput input) {
		Map<String, InputStream> inputStreamByName = null;
		
			log.info(Color.CYAN + "Call getTestList action = " + action
					+ (type == null ? "" : ", type = " + type) + Color.NORMAL);
			
			// Convertir les parametres fournis
			type = parseType(type);
			inputStreamByName = readParts(input);
					
			try {
				List<Test> lstTest = jobServiceManager.getTestList();
				ResponseBuilder builder = null;
				JSONObject resjson = new JSONObject();
				JSONArray restests = new JSONArray();
				resjson.put(TEST_KEY, restests);

				for (Test test : lstTest) {
					JSONObject result = new JSONObject();
					result.put("level", test.getLevel());
					result.put("code", test.getCode());
					result.put("severity", test.getSeverity());
					
					restests.put(test);
				}

				builder = Response.ok(resjson.toString(2)).type(MediaType.APPLICATION_JSON_TYPE);

				builder.header(api_version_key, api_version);

				return builder.build();
			} catch (Exception ex) {
				log.error(ex.getMessage(), ex);
				throw new WebApplicationException("INTERNAL_ERROR", Status.INTERNAL_SERVER_ERROR);
			}
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
	
	private Map<String, InputStream> readParts(MultipartFormDataInput input) throws Exception {

		Map<String, InputStream> result = new HashMap<String, InputStream>();

		for (InputPart part : input.getParts()) {
			MultivaluedMap<String, String> headers = part.getHeaders();
			String header = headers.getFirst(HttpHeaders.CONTENT_DISPOSITION);
			String filename = getFilename(header);

			if (filename == null) {
				throw new ServiceException(ServiceExceptionCode.INVALID_REQUEST, "missing filename in part");
			}
			// protect filename from invalid url chars
			filename = removeSpecialChars(filename);
			result.put(filename, part.getBody(InputStream.class, null));
		}
		return result;
	}
	
	private String removeSpecialChars(String filename) {
		return filename.replaceAll("[^\\w-_\\.]", "_");
	}
	
	private String getFilename(String header) {
		String result = null;

		if (header != null) {
			for (String token : header.split(";")) {
				if (token.trim().startsWith("filename")) {
					result = token.substring(token.indexOf('=') + 1).trim().replace("\"", "");
					break;
				}
			}
		}
		return result;
	}


}
