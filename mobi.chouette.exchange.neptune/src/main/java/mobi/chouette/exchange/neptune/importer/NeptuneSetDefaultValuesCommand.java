package mobi.chouette.exchange.neptune.importer;

import java.io.IOException;
import java.util.Collection;

import javax.naming.InitialContext;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.neptune.Constant;
import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.GroupOfLine;
import mobi.chouette.model.Line;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.util.Referential;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
public class NeptuneSetDefaultValuesCommand implements Command, Constant{

	public static final String COMMAND = "NeptuneSetDefaultValuesCommand";

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;

		Monitor monitor = MonitorFactory.start(COMMAND);

		// report service

		try {

			Referential referential = (Referential) context.get(REFERENTIAL);

			processDefaulValues(referential);

			result = SUCCESS;
		} catch (Exception e) {

			// report service
			log.error("parsing failed ", e);
			throw e;
		} finally {
			log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		}

		return result;
	}

	private void processDefaulValues(Referential referential) {

		// order should respect Neptune Xml file
		processLines(referential.getLines().values());

	}

	private void processLines(Collection<Line> values) {

		for (Line line : values) {
			// default name = number ou published name
			if (line.getName() == null)
			{
				line.setName(line.getNumber() != null? line.getNumber(): line.getPublishedName());
			}
		}
	}

	private void processTimetables(Collection<Timetable> values) {

		for (Timetable timetable : values) {
			// timetable add 
		}
	}


	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = new NeptuneSetDefaultValuesCommand();
			return result;
		}
	}

	static {
		CommandFactory.factories.put(NeptuneSetDefaultValuesCommand.class.getName(), new DefaultCommandFactory());
	}
}
