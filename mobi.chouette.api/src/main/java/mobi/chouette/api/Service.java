package mobi.chouette.api;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.CacheControl;
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
import mobi.chouette.common.JSONUtil;
import mobi.chouette.dao.JobDAO;
import mobi.chouette.dao.SchemaDAO;
import mobi.chouette.model.api.Job;
import mobi.chouette.model.api.Job.STATUS;
import mobi.chouette.model.api.Link;
import mobi.chouette.scheduler.CommandNamingRules;
import mobi.chouette.scheduler.Parameters;
import mobi.chouette.scheduler.Scheduler;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

@Path("/referentials")
@Log4j
@RequestScoped
public class Service implements Constant {

	private static String api_version_key = "X-ChouetteIEV-Media-Type";
	private static String api_version = "iev.v1.0; format=json";

	@Inject
	JobDAO jobDAO;

	@Inject
	SchemaDAO schemas;

	@Inject
	Scheduler scheduler;

	@Context
	UriInfo uriInfo;

	// post asynchronous job
	@POST
	@Path("/{ref}/{action}{type:(/[^/]+?)?}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({ MediaType.APPLICATION_JSON })
	public Response upload(@PathParam("ref") String referential, @PathParam("action") String action,
			@PathParam("type") String type, MultipartFormDataInput input) {
		Response result = null;

		if (type != null && type.startsWith("/")) {
			type = type.substring(1);
		}
		// check params
		if (!schemas.getSchemaListing().contains(referential)) {
			throw new WebApplicationException(Status.BAD_REQUEST);
		}
		if (!checkCommand(action, type)) {
			throw new WebApplicationException(Status.BAD_REQUEST);
		}

		log.info(Color.YELLOW + "[DSU] schedule action referential : " + referential + " action: " + action
				+ (type == null ? "" : " type : " + type) + Color.NORMAL);

		try {
			// create job
			Job job = new Job(referential, action, type);
			jobDAO.create(job);

			// add location link
			{
				String href = MessageFormat.format("/{0}/{1}/scheduled_jobs/{2,number,#}", ROOT_PATH,
						job.getReferential(), job.getId());
				Link link = new Link(MediaType.APPLICATION_JSON, Link.LOCATION_REL, Link.GET_METHOD, href);
				job.getLinks().add(link);
			}
			// add cancel link
			{
				String href = MessageFormat.format("/{0}/{1}/scheduled_jobs/{2,number,#}", ROOT_PATH,
						job.getReferential(), job.getId());
				Link link = new Link(MediaType.APPLICATION_JSON, Link.CANCEL_REL, Link.DELETE_METHOD, href);
				job.getLinks().add(link);
			}

			// mkdir
			java.nio.file.Path dir = getJobDataDirectory(job.getReferential(), job.getId());
			if (Files.exists(dir)) {
				jobDAO.delete(job);
			}
			Files.createDirectories(dir);
			job.setPath(dir.toString());

			// upload data
			uploadParts(input, job);
			if (job.getAction().equals(EXPORTER)) {
				job.setFilename("export_" + job.getType() + "_" + job.getId() + ".zip");
			}

			// schedule job
			jobDAO.update(job);
			scheduler.schedule(job.getReferential());

			// // TODO for debug
			// java.nio.file.Path path = Paths.get(
			// System.getProperty("user.home"), ROOT_PATH,
			// job.getReferential(), "data", job.getId().toString(),
			// REPORT_FILE);
			// Files.createFile(path);
			// path = Paths.get(System.getProperty("user.home"), ROOT_PATH,
			// job.getReferential(), "data", job.getId().toString(),
			// VALIDATION_FILE);
			// Files.createFile(path);

			// build response
			ResponseBuilder builder = Response.accepted();
			builder.location(URI.create(MessageFormat.format("{0}/{1}/scheduled_jobs/{2,number,#}", ROOT_PATH,
					job.getReferential(), job.getId())));
			result = builder.build();
		} catch (WebApplicationException e) {
			log.error(e);
			throw e;
		} catch (IOException e) {
			log.error(e);
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
		}

		return result;
	}

	private void uploadParts(MultipartFormDataInput input, Job job) throws IOException {
		Map<String, List<InputPart>> map = input.getFormDataMap();
		List<InputPart> list = map.get("file");
		for (InputPart part : list) {
			MultivaluedMap<String, String> headers = part.getHeaders();
			String header = headers.getFirst(HttpHeaders.CONTENT_DISPOSITION);
			String filename = getFilename(header);

			if (filename.equals(PARAMETERS_FILE)) {
				uploadParametersPart(part, filename, job);
			} else {
				uploadDataPart(part, filename, job);
			}

		}
	}

	private void uploadDataPart(InputPart part, String filename, Job job) throws IOException {
		InputStream in = part.getBody(InputStream.class, null);
		if (in == null || filename == null || filename.isEmpty()) {
			throw new WebApplicationException(Status.BAD_REQUEST);
		}

		java.nio.file.Path path = Paths.get(job.getPath(), filename);

		if (Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
			throw new WebApplicationException(Status.BAD_REQUEST);
		} else {
			job.setFilename(filename);
			Files.copy(in, path);

			// add data upload link
			String href = MessageFormat.format("/{0}/{1}/data/{2,number,#}/{3}", ROOT_PATH, job.getReferential(), job.getId(),
					job.getFilename());
			Link link = new Link(MediaType.APPLICATION_OCTET_STREAM,Link.DATA_REL,Link.GET_METHOD,href);
			job.getLinks().add(link);
		}
	}

	private void uploadParametersPart(InputPart part, String filename, Job job) throws IOException {
		InputStream in = part.getBody(InputStream.class, null);
		java.nio.file.Path path = Paths.get(job.getPath(), filename);
		Files.copy(in, path);

		processParameters(job);
	}

	// download attached file
	@GET
	@Path("/{ref}/data/{id}/{filepath: .*}")
	@Produces({ MediaType.APPLICATION_OCTET_STREAM, MediaType.APPLICATION_JSON })
	public Response download(@PathParam("ref") String referential, @PathParam("id") Long id,
			@PathParam("filepath") String filename) {
		Response result = null;

		Job job = getJob(id, referential);

		java.nio.file.Path path = Paths.get(job.getPath(), filename);
		if (!Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}

		// build response
		File file = new File(path.toString());
		ResponseBuilder builder = Response.ok(file);
		builder.header(HttpHeaders.CONTENT_DISPOSITION, MessageFormat.format("attachment; filename=\"{0}\"", filename));

		MediaType type = null;
		if (FilenameUtils.getExtension(filename).toLowerCase().equals("json")) {
			type = MediaType.APPLICATION_JSON_TYPE;
			builder.header(api_version_key, api_version);
		} else {
			type = MediaType.APPLICATION_OCTET_STREAM_TYPE;
		}

		// cache control
		if (job.getStatus().ordinal() >= Job.STATUS.TERMINATED.ordinal()) {
			CacheControl cc = new CacheControl();
			cc.setMaxAge(Integer.MAX_VALUE);
			builder.cacheControl(cc);
		}

		result = builder.type(type).build();
		return result;
	}

	// jobs listing
	@GET
	@Path("/{ref}/jobs")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response jobs(@PathParam("ref") String referential,
			@DefaultValue("0") @QueryParam("version") final Long version, @QueryParam("action") final String action) {

		// check params
		if (!schemas.getSchemaListing().contains(referential)) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}

		// create jobs listing
		List<Job> list = jobDAO.findByReferential(referential);
		// Collection<Job> jobs = list;

		// TODO [DSU] create finder by criteria
		Collection<Job> filtered = Collections2.filter(list, new Predicate<Job>() {
			@Override
			public boolean apply(Job job) {
				// filter on update time if given, otherwise don't return
				// deleted jobs
				boolean result = ((version > 0) ? job.getUpdated().getTime() > version : true)
						&& ((action != null) ? job.getAction().equals(action) : true)
						&& (version == 0 ? job.getStatus().ordinal() < STATUS.DELETED.ordinal() : true);
				return result;
			}
		});

		// re factor Parameters dependencies
		List<JobInfo> result = new ArrayList<>(build(filtered));
		for (JobInfo job : result) {

			java.nio.file.Path path = Paths.get(getJobDataDirectory(job.getReferential(), job.getId()).toString(),
					PARAMETERS_FILE);

			Parameters payload = JSONUtil.fromJSON(path, Parameters.class);
			if (payload != null) {
				job.setActionParameters(payload.getConfiguration());
			}
		}

		// cache control
		ResponseBuilder builder = Response.ok(result);
		builder.header(api_version_key, api_version);
		// CacheControl cc = new CacheControl();
		// cc.setMaxAge(-1);
		// builder.cacheControl(cc);

		return builder.build();
	}

	// view scheduled job
	@GET
	@Path("/{ref}/scheduled_jobs/{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response scheduledJob(@PathParam("ref") String referential, @PathParam("id") Long id) {
		Response result = null;

		// check params
		if (!schemas.getSchemaListing().contains(referential)) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}

		Job job = getJob(id, referential);

		// build response
		ResponseBuilder builder = null;
		if (job.getStatus().ordinal() <= STATUS.STARTED.ordinal()) {

			JobInfo info = new JobInfo(job, true, uriInfo);
			java.nio.file.Path path = Paths.get(System.getProperty("user.home"), ROOT_PATH, job.getReferential(),
					"data", job.getId().toString(), PARAMETERS_FILE);

			Parameters payload = JSONUtil.fromJSON(path, Parameters.class);
			if (payload != null) {
				info.setActionParameters(payload.getConfiguration());
			}
			builder = Response.ok(info);

			// add links
			for (Link link : job.getLinks()) {
				URI uri = URI.create(uriInfo.getBaseUri() + link.getHref().substring(1));
				builder.link(uri, link.getRel());
			}

		} else {
			builder = Response.seeOther(URI.create(MessageFormat.format("/{0}/{1}/terminated_jobs/{2,number,#}",
					ROOT_PATH, job.getReferential(), job.getId())));
		}

		builder.header(api_version_key, api_version);
		result = builder.build();
		return result;
	}

	// cancel job
	@DELETE
	@Path("/{ref}/scheduled_jobs/{id}")
	public Response cancel(@PathParam("ref") String referential, @PathParam("id") Long id) {
		Response result = null;

		Job job = getJob(id, referential);

		// build response
		ResponseBuilder builder = null;
		if (scheduler.cancel(job.getId())) {
			builder = Response.ok();
		} else {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		result = builder.build();
		builder.header(api_version_key, api_version);

		return result;
	}

	// download report
	@GET
	@Path("/{ref}/terminated_jobs/{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response terminatedJob(@PathParam("ref") String referential, @PathParam("id") Long id) {
		Response result = null;

		Job job = getJob(id, referential);

		if (job.getStatus().ordinal() < STATUS.TERMINATED.ordinal()
				|| job.getStatus().ordinal() == STATUS.DELETED.ordinal()) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}

		JobInfo info = new JobInfo(job, true, uriInfo);
		java.nio.file.Path path = Paths.get(System.getProperty("user.home"), ROOT_PATH, job.getReferential(), "data",
				job.getId().toString(), PARAMETERS_FILE);

		Parameters payload = JSONUtil.fromJSON(path, Parameters.class);
		if (payload != null) {
			info.setActionParameters(payload.getConfiguration());
		}

		ResponseBuilder builder = Response.ok(info);

		// cache control
		CacheControl cc = new CacheControl();
		cc.setMaxAge(Integer.MAX_VALUE);
		builder.cacheControl(cc);

		// add links
		for (Link link : job.getLinks()) {
			URI uri = URI.create(uriInfo.getBaseUri() + link.getHref().substring(1));
			builder.link(URI.create(uri.toASCIIString()), link.getRel());
		}

		builder.header(api_version_key, api_version);
		result = builder.build();

		return result;
	}

	// delete report
	@DELETE
	@Path("/{ref}/terminated_jobs/{id}")
	public Response remove(@PathParam("ref") String referential, @PathParam("id") Long id) {
		Response result = null;

		Job job = getJob(id, referential);

		// build response
		ResponseBuilder builder = null;
		if (scheduler.delete(job.getId())) {
			java.nio.file.Path path = Paths.get(System.getProperty("user.home"), ROOT_PATH, job.getReferential(),
					"data", job.getId().toString());
			try {
				FileUtils.deleteDirectory(path.toFile());
			} catch (IOException e) {
				throw new WebApplicationException(Status.NOT_FOUND);
			}
			log.info("[DSU] job deleted : " + job.getId());
			builder = Response.ok();
		} else {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		builder.header(api_version_key, api_version);
		result = builder.build();

		return result;
	}

	// delete referential
	@DELETE
	@Path("/{ref}/jobs")
	public Response drop(@PathParam("ref") String referential) {
		Response result = null;

		// check params
		if (!schemas.getSchemaListing().contains(referential)) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}

		// build response
		ResponseBuilder builder = null;
		if (scheduler.deleteAll(referential)) {
			java.nio.file.Path path = Paths.get(System.getProperty("user.home"), ROOT_PATH, referential);

			try {
				FileUtils.deleteDirectory(path.toFile());
			} catch (IOException e) {
				throw new WebApplicationException(Status.NOT_FOUND);
			}
			log.info("[DSU] referential deleted : " + referential);
			builder = Response.ok();
		} else {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		builder.header(api_version_key, api_version);
		result = builder.build();

		return result;
	}

	/**
	 * find job by Id and check if referential is correct <br/>
	 * send HTTP NOT_FOUND if referential not found or id not found or job
	 * belongs to another referential
	 * 
	 * @param id
	 *            job id
	 * @param referential
	 *            referential name
	 * @return valid job
	 * 
	 */
	private Job getJob(Long id, String referential) {
		// check params
		if (!schemas.getSchemaListing().contains(referential)) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		Job job = jobDAO.find(id);
		if (job == null) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		if (!job.getReferential().equals(referential)) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		return job;
	}
	
	private void processParameters(Job job) {
		java.nio.file.Path path = Paths.get(job.getPath(), PARAMETERS_FILE);
		// save separately action and validation parameters if possible
		Parameters payload = JSONUtil.fromJSON(path, Parameters.class);
		if (payload != null) {
			java.nio.file.Path actionPath = Paths.get(job.getPath(), ACTION_PARAMETERS_FILE);
			if (JSONUtil.toJSON(actionPath, payload.getConfiguration())) {
				// add link
				String href = MessageFormat.format("/{0}/{1}/data/{2,number,#}/{3}", ROOT_PATH, job.getReferential(),
						job.getId(), ACTION_PARAMETERS_FILE);
				Link link = new Link(MediaType.APPLICATION_JSON,Link.ACTION_PARAMETERS_REL,Link.GET_METHOD,href);
				job.getLinks().add(link);
			}
			if (payload.getValidation() != null) {
				java.nio.file.Path validationPath = Paths.get(job.getPath(), VALIDATION_PARAMETERS_FILE);
				if (JSONUtil.toJSON(validationPath, payload.getValidation())) {
					// add link
					String href = MessageFormat.format("/{0}/{1}/data/{2,number,#}/{3}", ROOT_PATH, job.getReferential(),
							job.getId(), VALIDATION_PARAMETERS_FILE);
					Link link = new Link(MediaType.APPLICATION_JSON,Link.VALIDATION_PARAMETERS_REL,Link.GET_METHOD,href);
					job.getLinks().add(link);
				}
			}
		} else {
			// add parameters link when invalid
			String href = MessageFormat.format("/{0}/{1}/data/{2,number,#}/{3}", ROOT_PATH, job.getReferential(), job.getId(),
					PARAMETERS_FILE);
			Link link = new Link(MediaType.APPLICATION_JSON,Link.PARAMETERS_REL,Link.GET_METHOD,href);
			job.getLinks().add(link);
		}

	}

	private boolean checkCommand(String action, String type) {
	try {
		Class.forName(CommandNamingRules.getCommandName(action, type));
	} catch (ClassNotFoundException e) {
		return false;
	}
	return true;
}


	private Collection<JobInfo> build(Collection<Job> list) {

		Collection<JobInfo> result = new ArrayList<>();
		for (Job job : list) {
			result.add(new JobInfo(job, true, uriInfo));
		}
		return result;
	}

	private java.nio.file.Path getJobDataDirectory(String referential, Long id) {
		return Paths.get(System.getProperty("user.home"), ROOT_PATH, referential, "data", id.toString());
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