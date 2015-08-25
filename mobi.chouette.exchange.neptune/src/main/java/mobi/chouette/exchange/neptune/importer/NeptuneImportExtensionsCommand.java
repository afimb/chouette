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
import mobi.chouette.exchange.neptune.extension.CommentExtension;
import mobi.chouette.model.GroupOfLine;
import mobi.chouette.model.Line;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.util.Referential;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
public class NeptuneImportExtensionsCommand implements Command, Constant{

	public static final String COMMAND = "NeptuneImportExtensionsCommand";

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;

		Monitor monitor = MonitorFactory.start(COMMAND);

		// report service

		try {

			Referential referential = (Referential) context.get(REFERENTIAL);

			processExtensions(referential);

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

	private void processExtensions(Referential referential) {

		// order should respect Neptune Xml file
		CommentExtension commentExtension = new CommentExtension();
		processGroupOfLineExtension(commentExtension,referential.getGroupOfLines().values());
		processStopAreaExtension(commentExtension,referential.getStopAreas().values());
		processLineExtension(commentExtension,referential.getLines().values());
		processStopPointExtension(commentExtension,referential.getStopPoints().values());
		processVehicleJourneyExtension(commentExtension,referential.getVehicleJourneys().values());

	}

	private void processLineExtension(CommentExtension commentExtension,Collection<Line> values) {

		for (Line line : values) {
			commentExtension.parseJsonComment(line);
		}
	}

	private void processStopAreaExtension(CommentExtension commentExtension,Collection<StopArea> values) {
		for (StopArea area : values) {
			commentExtension.parseJsonComment(area);
		}

	}

	private void processGroupOfLineExtension(CommentExtension commentExtension, Collection<GroupOfLine> values) {
		for (GroupOfLine group : values) {
			commentExtension.parseJsonComment(group);
		}
	}

	private void processStopPointExtension(CommentExtension commentExtension,Collection<StopPoint> values) {
		for (StopPoint point : values) {
			commentExtension.parseJsonComment(point);
		}
	}
	private void processVehicleJourneyExtension(CommentExtension commentExtension,Collection<VehicleJourney> values) {
		for (VehicleJourney vj : values) {
			commentExtension.parseJsonComment(vj);
		}

	}


	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = new NeptuneImportExtensionsCommand();
			return result;
		}
	}

	static {
		CommandFactory.factories.put(NeptuneImportExtensionsCommand.class.getName(), new DefaultCommandFactory());
	}
}
