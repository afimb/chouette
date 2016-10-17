package mobi.chouette.exchange.neptune.importer;

import java.io.IOException;

import javax.naming.InitialContext;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.importer.AbstractDisposeImportCommand;
import mobi.chouette.exchange.neptune.Constant;
import mobi.chouette.exchange.neptune.model.NeptuneObjectFactory;
import mobi.chouette.exchange.neptune.validation.AbstractValidator;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.ActionReporter.OBJECT_TYPE;
import mobi.chouette.model.util.Referential;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
public class NeptuneDisposeImportCommand extends AbstractDisposeImportCommand implements Constant {

	public static final String COMMAND = "NeptuneDisposeImportCommand";

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;

		Monitor monitor = MonitorFactory.start(COMMAND);

		try {
			// add stats on shared objects
			Referential referential = (Referential) context.get(REFERENTIAL);
			if (referential != null) {
				ActionReporter reporter = ActionReporter.Factory.getInstance();
				reporter.addStatToObjectReport(context, "", OBJECT_TYPE.NETWORK, OBJECT_TYPE.NETWORK, referential.getSharedPTNetworks().size());
				reporter.addStatToObjectReport(context, "", OBJECT_TYPE.STOP_AREA, OBJECT_TYPE.STOP_AREA, referential.getSharedStopAreas().size());
				reporter.addStatToObjectReport(context, "", OBJECT_TYPE.COMPANY, OBJECT_TYPE.COMPANY, referential.getSharedCompanies().size());
				reporter.addStatToObjectReport(context, "", OBJECT_TYPE.CONNECTION_LINK, OBJECT_TYPE.CONNECTION_LINK,
						referential.getSharedConnectionLinks().size());
				reporter.addStatToObjectReport(context, "", OBJECT_TYPE.ACCESS_POINT, OBJECT_TYPE.ACCESS_POINT,
						referential.getSharedAccessPoints().size());
				reporter.addStatToObjectReport(context, "", OBJECT_TYPE.TIMETABLE, OBJECT_TYPE.TIMETABLE,
						referential.getSharedTimetables().size());
			}
			super.execute(context);
			AbstractValidator.resetContext(context);
			NeptuneObjectFactory factory = (NeptuneObjectFactory) context.get(NEPTUNE_OBJECT_FACTORY);
			if (factory != null)
				factory.dispose();
			result = SUCCESS;

		} catch (Exception e) {
			log.error(e, e);
			throw e;
		} finally {
			log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		}

		return result;
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = new NeptuneDisposeImportCommand();
			return result;
		}
	}

	static {
		CommandFactory.factories.put(NeptuneDisposeImportCommand.class.getName(), new DefaultCommandFactory());
	}

}
