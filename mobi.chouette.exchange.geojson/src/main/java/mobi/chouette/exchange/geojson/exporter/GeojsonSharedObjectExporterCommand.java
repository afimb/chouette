package mobi.chouette.exchange.geojson.exporter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import javax.naming.InitialContext;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.JobData;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.geojson.Feature;
import mobi.chouette.exchange.geojson.FeatureCollection;
import mobi.chouette.exchange.geojson.JAXBSerializer;
import mobi.chouette.exchange.geojson.exporter.GeojsonLineExporterCommand.SharedData;
import mobi.chouette.exchange.report.FileInfo.FILE_STATE;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
public class GeojsonSharedObjectExporterCommand implements Command, Constant {
	public static final String COMMAND = "GeojsonSharedObjectExporterCommand";

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;
		Monitor monitor = MonitorFactory.start(COMMAND);
		try {

			SharedData shared = (SharedData) context.get(SHARED_DATA);
			if (shared == null) {
				return result;
			}

			JobData jobData = (JobData) context.get(JOB_DATA);
			Path path = Paths.get(jobData.getPathName(), OUTPUT);

			// save features collections
			if (save(path, "physical_stop_areas.json", shared.getPhysicalStops().values()))
				Report.addFileInfo(context, "physical_stop_areas.json", FILE_STATE.OK);

			if (save(path, "commercial_stop_areas.json", shared.getCommercialStops().values()))
				Report.addFileInfo(context, "commercial_stop_areas.json", FILE_STATE.OK);

			if (save(path, "access_points.json", shared.getAccessPoints().values()))
				Report.addFileInfo(context, "access_points.json", FILE_STATE.OK);

			if (save(path, "access_links.json", shared.getAccessLinks().values()))
				Report.addFileInfo(context, "access_links.json", FILE_STATE.OK);

			if (save(path, "connection_links.json", shared.getConnectionLinks().values()))
				Report.addFileInfo(context, "connection_links.json", FILE_STATE.OK);

			result = SUCCESS;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		}
		return result;
	}

	private boolean save(Path path, String filename, Collection<Feature> features) throws IOException {
		if (features.isEmpty())
			return false;
		FeatureCollection target = new FeatureCollection(features);
		File file = new File(path.toFile(), filename);
		JAXBSerializer.writeTo(target, file);
		return true;
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = new GeojsonSharedObjectExporterCommand();
			return result;
		}
	}

	static {
		CommandFactory.factories.put(GeojsonSharedObjectExporterCommand.class.getName(), new DefaultCommandFactory());
	}

}
