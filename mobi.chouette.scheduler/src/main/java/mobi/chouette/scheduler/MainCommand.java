package mobi.chouette.scheduler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.ws.rs.core.MediaType;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.JSONUtil;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.dao.JobDAO;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.validator.report.ValidationReport;
import mobi.chouette.model.api.Job;
import mobi.chouette.model.api.Job.STATUS;
import mobi.chouette.model.api.Link;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

@Log4j
@Stateless(name = MainCommand.COMMAND)
public class MainCommand implements Command, Constant {

	public static final String COMMAND = "MainCommand";

	@EJB
	JobDAO jobDAO;
	

	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public boolean execute(Context context) throws Exception {
		boolean result = false;

		Long id = (Long) context.get(JOB_ID);
		Job job = jobDAO.find(id);
	    // set job status to started
		job.setStatus(STATUS.STARTED);
		// add action report link
		Link link = new Link();
		link.setType(MediaType.APPLICATION_JSON);
		link.setRel(Link.REPORT_REL);
		link.setMethod(Link.GET_METHOD);
		String href = MessageFormat.format(
				"/{0}/{1}/data/{2,number,#}/{3}", ROOT_PATH,
				job.getReferential(), job.getId(),REPORT_FILE);
		link.setHref(href);
		job.getLinks().add(link);
		// add validation report link
		link = new Link();
		link.setType(MediaType.APPLICATION_JSON);
		link.setRel(Link.VALIDATION_REL);
		link.setMethod(Link.GET_METHOD);
		 href = MessageFormat.format(
				"/{0}/{1}/data/{2,number,#}/{3}", ROOT_PATH,
				job.getReferential(), job.getId(),VALIDATION_FILE);
		link.setHref(href);
		job.getLinks().add(link);
		jobDAO.update(job);

		context.put(ARCHIVE, job.getFilename());
		java.nio.file.Path path = Paths.get(System.getProperty("user.home"),
				ROOT_PATH, job.getReferential(), "data",
				job.getId().toString(), PARAMETERS_FILE);
		Parameters parameters = JSONUtil.fromJSON(path, Parameters.class);
		context.put(PARAMETERS, parameters);
		context.put(CONFIGURATION, parameters.getConfiguration());
		context.put(VALIDATION, parameters.getValidation());
		context.put(JOB_REFERENTIAL, job.getReferential());
		context.put(REPORT, new ActionReport());
		context.put(MAIN_VALIDATION_REPORT, new ValidationReport());
		context.put(ACTION, job.getAction());
		context.put(TYPE, job.getType());

		String name = CommandNamingRules.getCommandName(job.getAction(),job.getType());

		InitialContext ctx = (InitialContext) context.get(INITIAL_CONTEXT);
		Command command = CommandFactory.create(ctx, name);
		command.execute(context);

		job.setStatus(STATUS.TERMINATED);

		// remove location cancellink
		Iterables.removeIf(job.getLinks(), new Predicate<Link>() {
			@Override
			public boolean apply(Link link) {
				return link.getRel().equals(Link.LOCATION_REL) 
						|| link.getRel().equals(Link.CANCEL_REL)  ;
			}
		});

		// add location link
		link = new Link();
		link.setType(MediaType.APPLICATION_JSON);
		link.setRel(Link.LOCATION_REL);
		link.setMethod(Link.GET_METHOD);
		href = MessageFormat.format("/{0}/{1}/terminated_jobs/{2,number,#}",
				ROOT_PATH, job.getReferential(), job.getId());
		link.setHref(href);
		job.getLinks().add(link);

		// add delete link
		link = new Link();
		link.setType("application/json");
		link.setRel(Link.DELETE_REL);
		link.setMethod(Link.DELETE_METHOD);
		href = MessageFormat.format("/{0}/{1}/terminated_jobs/{2,number,#}",
				ROOT_PATH, job.getReferential(), job.getId());
		link.setHref(href);
		job.getLinks().add(link);


		// add data upload link
		if (job.getAction().equals(EXPORTER)) {

			href = MessageFormat.format(
					"/{0}/{1}/data/{2,number,#}/{3}", ROOT_PATH,
					job.getReferential(), job.getId(), job.getFilename());

			if (Files.exists(Paths.get(System.getProperty("user.home"), href))) {
				link = new Link();
				link.setType(MediaType.APPLICATION_OCTET_STREAM);
				link.setRel(Link.DATA_REL);
				link.setMethod(Link.GET_METHOD);
				link.setHref(href);

				job.getLinks().add(link);
			}
		}

		jobDAO.update(job);

		return result;
	}
	
	

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = null;
			try {
				String name = "java:app/mobi.chouette.scheduler/" + COMMAND;
				result = (Command) context.lookup(name);
			} catch (Exception e) {
				log.error(e);
			}
			return result;
		}
	}

	static {
		CommandFactory.factories.put(MainCommand.class.getName(),
				new DefaultCommandFactory());
	}
}
