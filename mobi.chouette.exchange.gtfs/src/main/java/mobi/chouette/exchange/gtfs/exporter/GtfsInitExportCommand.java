package mobi.chouette.exchange.gtfs.exporter;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;

import javax.naming.InitialContext;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.JobData;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.gtfs.Constant;
import mobi.chouette.exchange.gtfs.model.exporter.GtfsExporter;
import mobi.chouette.exchange.metadata.Metadata;
import mobi.chouette.model.util.Referential;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
public class GtfsInitExportCommand implements Command, Constant {

	public static final String COMMAND = "GtfsInitExportCommand";

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;
		log.info("GtfsInitExportCommand 1");
		Monitor monitor = MonitorFactory.start(COMMAND);

		try {
			JobData jobData = (JobData) context.get(JOB_DATA);
			jobData.setOutputFilename("export_" + jobData.getType() + "_" + jobData.getId() + ".zip");
			context.put(REFERENTIAL, new Referential());
			log.info("GtfsInitExportCommand 2");
			Metadata metadata = new Metadata(); // if not asked, will be used as dummy
			metadata.setDate(Calendar.getInstance());
			metadata.setFormat("text/csv");
			metadata.setTitle("Export GTFS ");
			try
			{
				log.info("GtfsInitExportCommand 3");
				metadata.setRelation(new URL("https://developers.google.com/transit/gtfs/reference"));
				log.info("GtfsInitExportCommand 4");
			}
			catch (MalformedURLException e1)
			{
				log.error("problem with https://developers.google.com/transit/gtfs/reference url", e1);
			}

			context.put(METADATA, metadata);
			log.info("GtfsInitExportCommand 5");
			// prepare exporter
			Path path = Paths.get(jobData.getPathName(), OUTPUT);
			log.info("GtfsInitExportCommand 6");
			if (!Files.exists(path)) {
				log.info("GtfsInitExportCommand 6 bis");
				Files.createDirectories(path);
			}
			log.info("GtfsInitExportCommand 7");
			GtfsExporter gtfsExporter = new GtfsExporter(path.toString());
			log.info("GtfsInitExportCommand 8");
			context.put(GTFS_EXPORTER, gtfsExporter);
			result = SUCCESS;
			log.info("GtfsInitExportCommand 9");
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
			Command result = new GtfsInitExportCommand();
			return result;
		}
	}

	static {
		CommandFactory.factories.put(GtfsInitExportCommand.class.getName(), new DefaultCommandFactory());
	}

}
