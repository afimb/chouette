package mobi.chouette.exchange.neptune.importer;

import java.io.IOException;

import javax.naming.InitialContext;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.neptune.Constant;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.ActionReporter.OBJECT_STATE;
import mobi.chouette.exchange.report.ActionReporter.OBJECT_TYPE;
import mobi.chouette.exchange.report.IO_TYPE;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.model.util.Referential;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
public class NeptuneInitImportCommand implements Command, Constant {

	public static final String COMMAND = "NeptuneInitImportCommand";

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;

		Monitor monitor = MonitorFactory.start(COMMAND);

		try {
			context.put(REFERENTIAL, new Referential());
			if (context.get(VALIDATION) != null)
			   context.put(VALIDATION_DATA, new ValidationData());
			// prepare entries for shared objects 
			ActionReporter reporter = ActionReporter.Factory.getInstance();
            reporter.addObjectReport(context, "merged", OBJECT_TYPE.NETWORK, "networks", OBJECT_STATE.OK, IO_TYPE.INPUT);
            reporter.addObjectReport(context, "merged", OBJECT_TYPE.STOP_AREA, "stop areas", OBJECT_STATE.OK, IO_TYPE.INPUT);
            reporter.addObjectReport(context, "merged", OBJECT_TYPE.COMPANY, "companies", OBJECT_STATE.OK, IO_TYPE.INPUT);
            reporter.addObjectReport(context, "merged", OBJECT_TYPE.CONNECTION_LINK, "connection links", OBJECT_STATE.OK, IO_TYPE.INPUT);
            reporter.addObjectReport(context, "merged", OBJECT_TYPE.ACCESS_POINT, "access points", OBJECT_STATE.OK, IO_TYPE.INPUT);
            reporter.addObjectReport(context, "merged", OBJECT_TYPE.TIMETABLE, "calendars", OBJECT_STATE.OK, IO_TYPE.INPUT);
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
			Command result = new NeptuneInitImportCommand();
			return result;
		}
	}

	static {
		CommandFactory.factories.put(NeptuneInitImportCommand.class.getName(), new DefaultCommandFactory());
	}

}
