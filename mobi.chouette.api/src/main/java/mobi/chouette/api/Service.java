package mobi.chouette.api;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
import mobi.chouette.scheduler.Parameters;
import mobi.chouette.scheduler.Scheduler;
import mobi.chouette.service.JobService;
import mobi.chouette.service.JobServiceManager;
import mobi.chouette.service.ServiceException;

import org.apache.commons.io.FilenameUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import mobi.chouette.service.ServiceExceptionCode;

@Path("/referentials")
@Log4j
@RequestScoped
public class Service implements Constant {

    private static String api_version_key = "X-ChouetteIEV-Media-Type";
    private static String api_version = "iev.v1.0; format=json";

    @Inject
    JobDAO jobDAO;

    @Inject
    JobServiceManager jobServiceManager;

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
    @Produces({MediaType.APPLICATION_JSON})
    public Response upload(@PathParam("ref") String referential, @PathParam("action") String action,
            @PathParam("type") String type, MultipartFormDataInput input) {

        log.info(Color.CYAN + "Call upload referential = " + referential + ", action = " + action
                + (type == null ? "" : ", type = " + type) + Color.NORMAL);

        // Convertir les parametres fournis 
        type = parseType(type);
        Map<String, InputStream> inputStreamByName = readParts(input);

        JobService job = null;
        try {
            // Relayer le service au JobServiceManager
            job = jobServiceManager.upload(referential, action, type, inputStreamByName);

            // TODO à reporter dans MainCommand
            if (job.getAction().equals(EXPORTER)) {
                job.setFilename("export_" + job.getType() + "_" + job.getId() + ".zip");
            }

            // Produire la vue
            ResponseBuilder builder = Response.accepted();
            builder.location(URI.create(MessageFormat.format("{0}/{1}/scheduled_jobs/{2,number,#}", ROOT_PATH,
                    job.getReferential(), job.getId())));

            return builder.build();
        } catch (ServiceException e) {
            log.error(e);
            throw toWebApplicationException(e);
        } catch (Exception e) {
            log.error(e);
            throw new WebApplicationException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
        }
    }

    private WebApplicationException toWebApplicationException(ServiceException exception) {
        return new WebApplicationException(exception.getMessage(), toWebApplicationCode(exception.getExceptionCode()));
    }

    private Status toWebApplicationCode(ServiceExceptionCode code) {
        if (code.equals(ServiceExceptionCode.INTERNAL_ERROR)) {
            return Status.INTERNAL_SERVER_ERROR;
        }
        return Status.BAD_REQUEST;
    }

    private String parseType(String type) {
        if (type != null && type.startsWith("/")) {
            return type.substring(1);
        }
        return type;
    }

    private Map<String, InputStream> readParts(MultipartFormDataInput input) {
        try {
            Map<String, InputStream> result = new HashMap<String, InputStream>();

            for (InputPart part : input.getParts()) {
                MultivaluedMap<String, String> headers = part.getHeaders();
                String header = headers.getFirst(HttpHeaders.CONTENT_DISPOSITION);
                String filename = getFilename(header);

                if (filename == null) {
                    throw new WebApplicationException("missing filename in part", Status.BAD_REQUEST);
                }
                result.put(filename, part.getBody(InputStream.class, null));
            }
            return result;
        } catch (Exception e) {
            throw new WebApplicationException(e.getMessage(), Status.BAD_REQUEST);
        }
    }


    // download attached file
    @GET
    @Path("/{ref}/data/{id}/{filepath: .*}")
    @Produces({MediaType.APPLICATION_OCTET_STREAM, MediaType.APPLICATION_JSON})
    public Response download(@PathParam("ref") String referential, @PathParam("id") Long id,
            @PathParam("filepath") String filename) {
        Response result = null;

        Job job = getJob(id, referential);

        java.nio.file.Path path = Paths.get(job.getPath(), filename);
        if (!Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            throw new WebApplicationException("request conflict", Status.NOT_FOUND);
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
    @Produces({MediaType.APPLICATION_JSON})
    public Response jobs(@PathParam("ref") String referential,
            @DefaultValue("0") @QueryParam("version") final Long version, @QueryParam("action") final String action) {

        // check params
        if (!schemas.getSchemaListing().contains(referential)) {
            throw new WebApplicationException("unknown referential", Status.NOT_FOUND);
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

            try {
                Parameters payload = JSONUtil.fromJSON(path, Parameters.class);
                if (payload != null) {
                    job.setActionParameters(payload.getConfiguration());
                }
            } catch (Exception ex) {
                // don't add invalid parameters
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
    @Produces({MediaType.APPLICATION_JSON})
    public Response scheduledJob(@PathParam("ref") String referential, @PathParam("id") Long id) {
        Response result = null;

        // check params
        if (!schemas.getSchemaListing().contains(referential)) {
            throw new WebApplicationException("unknown referential", Status.NOT_FOUND);
        }

        Job job = getJob(id, referential);

        // build response
        ResponseBuilder builder = null;
        if (job.getStatus().ordinal() <= STATUS.STARTED.ordinal()) {

            JobInfo info = new JobInfo(job, true, uriInfo);
            java.nio.file.Path path = Paths.get(System.getProperty("user.home"), ROOT_PATH, job.getReferential(),
                    "data", job.getId().toString(), PARAMETERS_FILE);

            try {
                Parameters payload = JSONUtil.fromJSON(path, Parameters.class);
                if (payload != null) {
                    info.setActionParameters(payload.getConfiguration());
                }
            } catch (Exception ex) {
                // don't add invalid parameters
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
    public Response cancel(@PathParam("ref") String referential, @PathParam("id") Long id, String dummy) {
        // dummy uses when sender call url with content (prevent a NullPointerException)
        log.info(Color.CYAN + "Call cancel referential = " + referential + ", id = " + id + Color.NORMAL);

        Response result = null;

        try {
            jobServiceManager.cancel(referential, id);
        } catch (ServiceException ex) {
            throw new WebApplicationException(ex.getCode(), Status.NOT_FOUND);
        } catch (Exception ex) {
            log.error("cancel failure", ex);
            throw new WebApplicationException(ex.getMessage(), Status.INTERNAL_SERVER_ERROR);
        }
        ResponseBuilder builder = Response.ok();
        result = builder.build();
        builder.header(api_version_key, api_version);

        return result;
    }

    // download report
    @GET
    @Path("/{ref}/terminated_jobs/{id}")
    @Produces({MediaType.APPLICATION_JSON})
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

        try {
            Parameters payload = JSONUtil.fromJSON(path, Parameters.class);
            if (payload != null) {
                info.setActionParameters(payload.getConfiguration());
            }
        } catch (Exception ex) {
            // don't add invalid parameters
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
    public Response remove(@PathParam("ref") String referential, @PathParam("id") Long id, String dummy) {
        // dummy uses when sender call url with content (prevent a NullPointerException)
        Response result = null;

        try {
            jobServiceManager.remove(referential, id);
        } catch (ServiceException ex) {
            throw new WebApplicationException(ex.getCode(), Status.NOT_FOUND);
        } catch (Exception ex) {
            log.error("remove failure", ex);
            throw new WebApplicationException(ex.getMessage(), Status.INTERNAL_SERVER_ERROR);
        }

        // build response
        ResponseBuilder builder = Response.ok("deleted");
        builder.header(api_version_key, api_version);
        result = builder.build();

        return result;
    }

    // delete referential
    @DELETE
    @Path("/{ref}/jobs")
    public Response drop(@PathParam("ref") String referential, String dummy) {
        // dummy uses when sender call url with content (prevent a NullPointerException)
        Response result = null;

        try {
            jobServiceManager.drop(referential);
        } catch (ServiceException ex) {
            throw new WebApplicationException(ex.getCode(), Status.NOT_FOUND);
        } catch (Exception ex) {
            log.error("drop failure", ex);
            throw new WebApplicationException(ex.getMessage(), Status.INTERNAL_SERVER_ERROR);
        }

        // build response
        ResponseBuilder builder = Response.ok("");
        builder.header(api_version_key, api_version);
        result = builder.build();

        return result;
    }

    /**
     * find job by Id and check if referential is correct <br/>
     * send HTTP NOT_FOUND if referential not found or id not found or job
     * belongs to another referential
     *
     * @param id job id
     * @param referential referential name
     * @return valid job
     *
     */
    private Job getJob(Long id, String referential) {
        // check params
        if (!schemas.getSchemaListing().contains(referential)) {
            throw new WebApplicationException("unknown referential", Status.NOT_FOUND);
        }
        Job job = jobDAO.find(id);
        if (job == null) {
            throw new WebApplicationException("unknown job", Status.NOT_FOUND);
        }
        if (!job.getReferential().equals(referential)) {
            throw new WebApplicationException("unknown job", Status.NOT_FOUND);
        }
        return job;
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
