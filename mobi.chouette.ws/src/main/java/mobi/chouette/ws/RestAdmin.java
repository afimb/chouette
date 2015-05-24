package mobi.chouette.ws;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
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
import mobi.chouette.model.api.Job;
import mobi.chouette.service.JobService;
import mobi.chouette.service.JobServiceManager;
import mobi.chouette.service.PropertyNames;

@Path("/admin")
@Log4j
@RequestScoped
public class RestAdmin implements Constant {

	private static String api_version_key = "X-ChouetteIEV-Media-Type";
	private static String api_version = "iev_admin.v1.0; format=txt";

	@Inject
	JobServiceManager jobServiceManager;

	@Context
	UriInfo uriInfo;

	// jobs listing
	@GET
	@Path("/active_jobs")
	@Produces({ MediaType.TEXT_PLAIN })
	public Response activeJobs(@QueryParam("key") final String authorisationKey) {

		log.info(Color.BLUE + "Call Admin active_jobs" + Color.NORMAL);
		System.getProperty(PropertyNames.ADMIN_KEY);

		if (authorisationKey == null || authorisationKey.isEmpty()) {
			log.warn("admin call without key");
			ResponseBuilder builder = Response.status(Status.UNAUTHORIZED);
			builder.header(api_version_key, api_version);
			return builder.build();
		}
		String securityToken = System.getProperty(PropertyNames.ADMIN_KEY);
		if (securityToken == null) {
			log.warn("admin call without property " + PropertyNames.ADMIN_KEY + " set");
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

		try {
			// create jobs listing
			List<JobService> jobServices = jobServiceManager.activeJobs();

			// re factor Parameters dependencies
			JobStat globalStat = new JobStat();
			Map<String, JobStat> byReferential = new HashMap<>();

			globalStat.jobCount = jobServices.size();
			for (JobService jobService : jobServices) {
				String referential = jobService.getReferential();
				JobStat refStat = byReferential.get(referential);
				if (refStat == null) {
					refStat = new JobStat();
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

			StringBuilder result = new StringBuilder();
			result.append(globalStat.toString("Global"));
			for (Entry<String, JobStat> entry : byReferential.entrySet()) {
				result.append(entry.getValue().toString(entry.getKey()));
			}

			ResponseBuilder builder = Response.ok(result);
			builder.header(api_version_key, api_version);

			return builder.build();
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new WebApplicationException("INTERNAL_ERROR", Status.INTERNAL_SERVER_ERROR);
		}
	}

	private class JobStat {
		int jobCount = 0;
		int scheduledJobCount = 0;
		int startedJobCount = 0;

		String toString(String key) {
			StringBuilder builder = new StringBuilder();
			builder.append(key + ".jobCount=" + jobCount + "\n");
			builder.append(key + ".scheduledJobCount=" + scheduledJobCount + "\n");
			builder.append(key + ".startedJobCount=" + startedJobCount + "\n");
			return builder.toString();
		}
	}

}
