package mobi.chouette.api;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.text.MessageFormat;
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
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.dao.JobDAO;
import mobi.chouette.dao.SchemaDAO;
import mobi.chouette.model.api.Job;
import mobi.chouette.model.api.Job.STATUS;
import mobi.chouette.model.api.Link;
import mobi.chouette.scheduler.Constant;
import mobi.chouette.scheduler.Scheduler;

import org.apache.commons.io.FileUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

@Path("/referentials")
@Log4j
@RequestScoped
public class Service implements Constant {

	public static final String REPORT = "report.json";
	public static final String REPORT_VALIDATION = "validation.json";
	public static final String EXPORTED_DATA = "data.zip";

	@Inject
	JobDAO jobDAO;

	@Inject
	SchemaDAO schemas;

	@Inject
	Scheduler scheduler;

	@GET
	@Path("/todo")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response todo() {
		Job job = new Job();
		jobDAO.create(job);
		log.info(Color.SUCCESS + job + Color.NORMAL);
		ResponseBuilder builder = Response.ok(job);

		CacheControl cc = new CacheControl();
		cc.setMaxAge(10);
		builder.cacheControl(cc);
		Response result = builder.build();
		return result;
	}

	// post asynchronous job
	@POST
	@Path("/{ref}/{action}/{type}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response upload(@PathParam("ref") String referential,
			@PathParam("action") String action, @PathParam("type") String type,
			MultipartFormDataInput input) {
		Response result = null;

		// check params
		if (!schemas.getSchemaListing().contains(referential)) {
			throw new WebApplicationException(Status.BAD_REQUEST);
		}
		if (action == null || action.isEmpty()) {
			throw new WebApplicationException(Status.BAD_REQUEST);
		}
		if (type == null || type.isEmpty()) {
			throw new WebApplicationException(Status.BAD_REQUEST);
		}

		try {

			// create job
			Job job = new Job();
			job.setReferential(referential);
			job.setAction(action);
			job.setType(type);
			jobDAO.create(job);

			// add location link
			Link link = new Link();
			link.setType(MediaType.APPLICATION_JSON);
			link.setRel(Link.LOCATION_REL);
			link.setMethod(Link.GET_METHOD);
			link.setHref(MessageFormat.format("/{0}/{1}/jobs/{2}", ROOT_PATH,
					job.getReferential(), job.getId()));
			job.getLinks().add(link);

			// add parameters link
			link = new Link();
			link.setType(MediaType.APPLICATION_JSON);
			link.setRel(Link.PARAMETERS_REL);
			link.setMethod(Link.GET_METHOD);
			link.setHref(MessageFormat.format("/{0}/data/{1}/{2}", ROOT_PATH,
					job.getReferential(), job.getId(), REPORT));
			job.getLinks().add(link);

			// add cancel link
			link = new Link();
			link.setType(MediaType.APPLICATION_JSON);
			link.setRel(Link.CANCEL_REL);
			link.setMethod(Link.DELETE_METHOD);
			link.setHref(MessageFormat.format("/{0}/{1}/jobs/{2}", ROOT_PATH,
					job.getReferential(), job.getId()));
			job.getLinks().add(link);

			// mkdir
			java.nio.file.Path dir = Paths.get(System.getProperty("user.home"),
					ROOT_PATH, job.getReferential(), "data", job.getId()
							.toString());
			if (Files.exists(dir)) {
				jobDAO.delete(job);
				throw new WebApplicationException(Status.BAD_REQUEST);
			} else {
				Files.createDirectories(dir);
				job.setPath(dir.toString());
			}

			// upload data
			Map<String, List<InputPart>> map = input.getFormDataMap();
			List<InputPart> list = map.get("file");
			for (InputPart part : list) {
				MultivaluedMap<String, String> headers = part.getHeaders();
				String header = headers
						.getFirst(HttpHeaders.CONTENT_DISPOSITION);
				String filename = getFilename(header);

				if (filename.equals("parameters.json")) {
					InputStream in = part.getBody(InputStream.class, null);
					java.nio.file.Path path = Paths.get(dir.toString(),
							filename);
					Files.copy(in, path);
				} else {
					InputStream in = part.getBody(InputStream.class, null);
					if (in == null || filename == null || filename.isEmpty()) {
						throw new WebApplicationException(Status.BAD_REQUEST);
					}

					java.nio.file.Path path = Paths.get(System
							.getProperty("user.home"), ROOT_PATH, job
							.getReferential(), "data", job.getId().toString(),
							filename);
					job.setFilename(filename);

					if (Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
						throw new WebApplicationException(Status.BAD_REQUEST);
					} else {
						Files.createDirectories(dir);
						Files.copy(in, path);
					}
				}
			}

			// schedule job
			jobDAO.update(job);
			scheduler.schedule(job.getReferential());

			// build response
			ResponseBuilder builder = Response.accepted();
			builder.location(URI.create(MessageFormat.format("{0}/jobs/{1}",
					ROOT_PATH, job.getId())));
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

	// download attached file
	@GET
	@Path("/{ref}/data/{id}/{filepath: .*}")
	@Produces({ MediaType.APPLICATION_OCTET_STREAM })
	public Response download(@PathParam("ref") String referential,
			@PathParam("id") Long id, @PathParam("filepath") String filename) {
		Response result = null;

		// check params
		if (!schemas.getSchemaListing().contains(referential)) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		java.nio.file.Path path = Paths.get(System.getProperty("user.home"),
				ROOT_PATH, referential, "data", id.toString(), filename);
		if (!Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}

		// build response
		File file = new File(path.toString());
		ResponseBuilder builder = Response.ok(file);
		builder.header(HttpHeaders.CONTENT_DISPOSITION,
				MessageFormat.format("attachment; filename=\"{0}\"", filename));

		// cache control
		CacheControl cc = new CacheControl();
		cc.setMaxAge(Integer.MAX_VALUE);
		builder.cacheControl(cc);

		result = builder.build();
		return result;
	}

	// jobs listing
	@GET
	@Path("/{ref}/jobs")
	@Produces({ MediaType.APPLICATION_JSON })
	public JobListing jobs(@PathParam("ref") String referential,
			@DefaultValue("0") @QueryParam("version") final Long version,
			@QueryParam("action") final String action) {

		// check params
		if (!schemas.getSchemaListing().contains(referential)) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}

		// create jobs listing
		JobListing result = new JobListing();
		List<Job> list = jobDAO.findByReferential(referential);
		Collection<Job> jobs = list;

		// TODO [DSU] create finder by criteria
		result.setList(Collections2.filter(list, new Predicate<Job>() {
			@Override
			public boolean apply(Job job) {

				boolean result = ((version > 0) ? job.getUpdated().getTime() > version
						: true)
						&& ((action != null) ? job.getAction().equals(action)
								: true);
				return result;
			}
		}));

		return result;
	}

	// view job
	@GET
	@Path("/{ref}/jobs/{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response job(@PathParam("ref") String referential,
			@PathParam("id") Long id) {
		Response result = null;

		// check params
		if (!schemas.getSchemaListing().contains(referential)) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}

		Job job = jobDAO.find(id);
		if (job == null) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}

		// build response
		ResponseBuilder builder = null;
		if (job.getStatus().ordinal() < STATUS.TERMINATED.ordinal()) {

			java.nio.file.Path report = Paths.get(
					System.getProperty("user.home"), ROOT_PATH, referential,
					"data", id.toString(), REPORT);
			if (!Files.exists(report, LinkOption.NOFOLLOW_LINKS)) {
				File file = new File(report.toString());
				builder = Response.ok(file);
				builder.header(HttpHeaders.CONTENT_DISPOSITION, MessageFormat
						.format("attachment; filename=\"{0}\"",
								report.getFileName()));
			} else {
				builder = Response.ok();
			}

			// add parameters link
			java.nio.file.Path parameters = Paths.get(
					System.getProperty("user.home"), ROOT_PATH, referential,
					"data", id.toString(), REPORT);
			if (Files.exists(parameters)) {
				builder.link(URI.create(MessageFormat.format(
						"/{0}/data/{1}/{2}", ROOT_PATH, job.getReferential(),
						job.getId(), REPORT)), Link.PARAMETERS_REL);
			}

			// add link to cancel
			if (job.getStatus().equals(STATUS.CREATED)) {
				builder.link(URI.create(MessageFormat.format(
						"/{0}/{1}/jobs/{2}", ROOT_PATH, job.getReferential(),
						job.getId())), Link.CANCEL_REL);
			}

		} else {
			builder = Response.seeOther(URI.create(MessageFormat.format(
					"/{0}/{1}/reports/{2}", ROOT_PATH, job.getReferential(),
					job.getId())));
		}

		result = builder.build();
		return result;
	}

	// cancel job
	@DELETE
	@Path("/{ref}/jobs/{id}")
	public Response cancel(@PathParam("ref") String referential,
			@PathParam("id") Long id) {
		Response result = null;

		// check params
		if (!schemas.getSchemaListing().contains(referential)) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}

		Job job = jobDAO.find(id);
		if (job == null) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}

		// build response
		ResponseBuilder builder = null;
		if (scheduler.cancel(job)) {
			builder = Response.ok();
		} else {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		result = builder.build();

		return result;
	}

	// download report
	@GET
	@Path("/{ref}/reports/{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response report(@PathParam("ref") String referential,
			@PathParam("id") Long id) {
		Response result = null;

		// check params
		if (!schemas.getSchemaListing().contains(referential)) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}

		Job job = jobDAO.find(id);
		if (job == null) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}

		if (job.getStatus().ordinal() < STATUS.TERMINATED.ordinal()) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}

		java.nio.file.Path report = Paths.get(System.getProperty("user.home"),
				ROOT_PATH, referential, "data", id.toString(), REPORT);
		if (!Files.exists(report, LinkOption.NOFOLLOW_LINKS)) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}

		// build response
		File file = new File(report.toString());
		ResponseBuilder builder = Response.ok(file);
		builder.header(
				HttpHeaders.CONTENT_DISPOSITION,
				MessageFormat.format("attachment; filename=\"{0}\"",
						report.getFileName()));

		// cache control
		CacheControl cc = new CacheControl();
		cc.setMaxAge(Integer.MAX_VALUE);
		builder.cacheControl(cc);

		// add link to validation report
		java.nio.file.Path validation = Paths.get(
				System.getProperty("user.home"), ROOT_PATH, referential,
				"data", id.toString(), REPORT_VALIDATION);
		if (!Files.exists(validation, LinkOption.NOFOLLOW_LINKS)) {
			builder.link(URI.create(MessageFormat.format(
					"/{0}/{1}/data/{2}/{3}", ROOT_PATH, job.getReferential(),
					job.getId(), REPORT_VALIDATION)), "validation");
		}

		// add link to exported data
		java.nio.file.Path data = Paths.get(System.getProperty("user.home"),
				ROOT_PATH, referential, "data", id.toString(), EXPORTED_DATA);
		if (!Files.exists(data, LinkOption.NOFOLLOW_LINKS)) {
			builder.link(URI.create(MessageFormat.format(
					"/{0}/{1}/data/{2}/{3}", ROOT_PATH, job.getReferential(),
					job.getId(), REPORT_VALIDATION)), "data");
		}

		// add parameters link
		java.nio.file.Path parameters = Paths.get(
				System.getProperty("user.home"), ROOT_PATH, referential,
				"data", id.toString(), REPORT);
		if (Files.exists(parameters)) {
			builder.link(URI.create(MessageFormat.format("/{0}/data/{1}/{2}",
					ROOT_PATH, job.getReferential(), job.getId(), REPORT)),
					Link.PARAMETERS_REL);
		}

		result = builder.build();

		return result;
	}

	// delete report
	@DELETE
	@Path("/{ref}/reports/{id}")
	public Response remove(@PathParam("ref") String referential,
			@PathParam("id") Long id) {
		Response result = null;

		// check params
		if (!schemas.getSchemaListing().contains(referential)) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}

		Job job = jobDAO.find(id);
		if (job == null) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}

		// build response
		ResponseBuilder builder = null;
		if (scheduler.delete(job)) {
			java.nio.file.Path path = Paths.get(
					System.getProperty("user.home"), ROOT_PATH,
					job.getReferential(), "data", job.getId().toString());
			try {
				FileUtils.deleteDirectory(path.toFile());
			} catch (IOException e) {
				throw new WebApplicationException(Status.NOT_FOUND);
			}
			builder = Response.ok();
		} else {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		result = builder.build();

		return result;
	}

	// delete referential
	@DELETE
	@Path("/{ref}")
	public Response drop(@PathParam("ref") String referential) {
		Response result = null;

		// check params
		if (!schemas.getSchemaListing().contains(referential)) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}

		// build response
		ResponseBuilder builder = null;
		java.nio.file.Path path = Paths.get(System.getProperty("user.home"),
				ROOT_PATH, referential);
		if (Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
			try {
				FileUtils.deleteDirectory(path.toFile());
			} catch (IOException e) {
				throw new WebApplicationException(Status.NOT_FOUND);
			}
			builder = Response.ok();
		} else {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		result = builder.build();

		return result;
	}

	private String getFilename(String header) {
		String result = null;

		if (header != null) {
			for (String token : header.split(";")) {
				if (token.trim().startsWith("filename")) {
					result = token.substring(token.indexOf('=') + 1).trim()
							.replace("\"", "");
					break;
				}
			}
		}
		return result;
	}
}