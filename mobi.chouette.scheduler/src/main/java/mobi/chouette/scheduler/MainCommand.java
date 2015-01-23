package mobi.chouette.scheduler;

import java.io.IOException;
import java.text.MessageFormat;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.ws.rs.core.MediaType;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.dao.JobDAO;
import mobi.chouette.model.api.Job;
import mobi.chouette.model.api.Link;
import mobi.chouette.model.api.Job.STATUS;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

@Stateless(name = MainCommand.COMMAND)
@Log4j
public class MainCommand implements Command, Constant {

	public static final String COMMAND = "MainCommand";

	@EJB
	JobDAO dao;

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = false;
		InitialContext ctx = new InitialContext();
		Command command = CommandFactory.create(ctx,
				WaitCommand.class.getName());
		command.execute(context);

		Job job = (Job) context.get(JOB);
		job.setStatus(STATUS.TERMINATED);

		// remove location link
		Iterables.removeIf(job.getLinks(), new Predicate<Link>() {
			@Override
			public boolean apply(Link link) {
				return link.getRel().equals(Link.LOCATION_REL);
			}
		});

		// add download link
		Link link = new Link();
		link.setType(MediaType.APPLICATION_JSON);
		link.setRel(Link.DOWNLOAD_REL);
		link.setMethod(Link.GET_METHOD);
		link.setHref(MessageFormat.format("/{0}/{1}/reports/{2}", ROOT_PATH,
				job.getReferential(), job.getId()));
		job.getLinks().add(link);

		// add delete link
		link = new Link();
		link.setType("application/json");
		link.setRel(Link.DELETE_REL);
		link.setMethod(Link.DELETE_METHOD);
		link.setHref(MessageFormat.format("/{0}/{1}/reports/{2}", ROOT_PATH,
				job.getReferential(), job.getId()));
		job.getLinks().add(link);

		// TODO dao.update(job);

		return result;
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = null;
			try {
				result = (Command) context.lookup(JAVA_MODULE + COMMAND);
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
