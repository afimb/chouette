package mobi.chouette.exchange.importer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import javax.naming.InitialContext;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.util.Referential;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
public class StopAreaRegisterCommand implements Command {

	private Predicate<StopArea> predicate = new Predicate<StopArea>() {
		@Override
		public boolean apply(StopArea area) {
			return area.getAreaType().equals(ChouetteAreaEnum.Quay)
					|| area.getAreaType().equals(ChouetteAreaEnum.BoardingPosition);
		}
	};

	public static final String COMMAND = "StopAreaRegisterCommand";

	private static final int batchSizeA = 30000;
	private static final int batchSizeC = 10000;

	@Override
	public boolean execute(Context context) throws Exception {


		boolean result = ERROR;
		InitialContext initialContext = (InitialContext) context.get(INITIAL_CONTEXT);
		Command command = CommandFactory.create(initialContext, StopAreaRegisterBlocCommand.class.getName());
		Command commandLink = CommandFactory.create(initialContext, ConnectionLinkRegisterBlocCommand.class.getName());
		Monitor monitor = MonitorFactory.start(COMMAND);
		try {
			Referential referential = (Referential) context.get(REFERENTIAL);
			Collection<StopArea> orderedAreas = Collections2.filter(referential.getStopAreas().values(), predicate);
			Iterable<List<StopArea>> iterator = Iterables.partition(orderedAreas, batchSizeA);
			int count = 0;
			for (List<StopArea> areas : iterator) {
				count += areas.size();
				context.put(AREA_BLOC, areas);
				command.execute(context);
				// executeBloc( context, areas);
				log.info("Areas proceded :" + count + "/" + orderedAreas.size());
			}
			Collection<ConnectionLink> orderedlinks = referential.getConnectionLinks().values();
			Iterable<List<ConnectionLink>> iterator2 = Iterables.partition(orderedlinks, batchSizeC);
			count = 0;
			for (List<ConnectionLink> links : iterator2) {
				count += links.size();
				context.put(CONNECTION_LINK_BLOC, links);
				commandLink.execute(context);
				// executeBloc( context, areas);
				log.info("ConnectionLinks proceded :" + count + "/" + orderedlinks.size());
			}
		} catch (Exception ex) {
			log.error("unable to save stops and connection links "+ex.getMessage(),ex);
			ActionReporter reporter = ActionReporter.Factory.getInstance();
			if (ex.getCause() != null) {
				Throwable e = ex.getCause();
				while (e.getCause() != null) {
					log.error(e.getMessage());
					e = e.getCause();
				}
				if (e instanceof SQLException) {
					e = ((SQLException) e).getNextException();
					reporter.setActionError(context, ActionReporter.ERROR_CODE.INTERNAL_ERROR, e.getMessage());
				} else {
					reporter.setActionError(context, ActionReporter.ERROR_CODE.INTERNAL_ERROR, e.getMessage());
				}
			} else {
				reporter.setActionError(context, ActionReporter.ERROR_CODE.INTERNAL_ERROR, ex.getMessage());
			}

		} finally {
			log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		}
		return result;

	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {

			Command result = new StopAreaRegisterCommand();
			return result;
		}
	}

	static {
		CommandFactory.factories.put(StopAreaRegisterCommand.class.getName(), new DefaultCommandFactory());
	}
}
