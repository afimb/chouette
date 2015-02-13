package mobi.chouette.scheduler;

import java.io.IOException;
import java.nio.file.Paths;
import java.text.MessageFormat;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.ws.rs.core.MediaType;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.JSONUtils;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.dao.JobDAO;
import mobi.chouette.model.api.Job;
import mobi.chouette.model.api.Job.STATUS;
import mobi.chouette.model.api.Link;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

@Stateless(name = MainCommand.COMMAND)
@Log4j
public class MainCommand implements Command, Constant {

	public static final String COMMAND = "MainCommand";

	@EJB
	JobDAO jobDAO;

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = false;

		Long id = (Long) context.get(JOB_ID);
		Job job = jobDAO.find(id);
		
		context.put(PATH, job.getPath());
		context.put(ARCHIVE, job.getFilename());
		java.nio.file.Path path = Paths.get(
				System.getProperty("user.home"), ROOT_PATH,
				job.getReferential(), "data", job.getId().toString(),
				PARAMETERS_FILE);
		Parameters parameters = JSONUtils.fromJSON(path, Parameters.class);
		context.put(PARAMETERS, parameters);
		context.put(CONFIGURATION, parameters.getConfiguration());
		context.put(VALIDATION, parameters.getValidation());
		context.put(ACTION, job.getAction());
		context.put(TYPE, job.getType());
		
		String name = "mobi.chouette.exchange." + job.getType() + "."
				+ job.getAction() + "." + StringUtils.capitalize(job.getType())
				+ StringUtils.capitalize(job.getAction()) + "Command";

		InitialContext ctx = (InitialContext) context.get(INITIAL_CONTEXT);
		Command command = CommandFactory.create(ctx, name);
		// Command command = CommandFactory.create(ctx,
		// WaitCommand.class.getName());
		command.execute(context);

		job.setStatus(STATUS.TERMINATED);

		// remove location link
		Iterables.removeIf(job.getLinks(), new Predicate<Link>() {
			@Override
			public boolean apply(Link link) {
				return link.getRel().equals(Link.LOCATION_REL);
			}
		});

		// add location link
		Link link = new Link();
		link.setType(MediaType.APPLICATION_JSON);
		link.setRel(Link.LOCATION_REL);
		link.setMethod(Link.GET_METHOD);
		link.setHref(MessageFormat.format("/{0}/{1}/reports/{2,number,#}",
				ROOT_PATH, job.getReferential(), job.getId()));
		job.getLinks().add(link);

		// add delete link
		link = new Link();
		link.setType("application/json");
		link.setRel(Link.DELETE_REL);
		link.setMethod(Link.DELETE_METHOD);
		link.setHref(MessageFormat.format("/{0}/{1}/reports/{2,number,#}",
				ROOT_PATH, job.getReferential(), job.getId()));
		job.getLinks().add(link);

		// add validation download link
		link = new Link();
		link.setType(MediaType.APPLICATION_JSON);
		link.setRel(Link.DOWNLOAD_REL);
		link.setMethod(Link.GET_METHOD);
		link.setHref(MessageFormat.format(
				"/{0}/{1}/data/{2,number,#}/validation.json", ROOT_PATH,
				job.getReferential(), job.getId()));
		job.getLinks().add(link);

		jobDAO.update(job);

		return result;
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = null;
			try {
				String name = "java:app/mobi.chouette.scheduler/"
						+ COMMAND;
				result = (Command) context.lookup(name);
			} catch (Exception e) {
				log.error(e);
			}
			return result;
		}
	}

	static {
		CommandFactory factory = new DefaultCommandFactory();
		CommandFactory.factories.put(MainCommand.class.getName(), factory);
	}
}
