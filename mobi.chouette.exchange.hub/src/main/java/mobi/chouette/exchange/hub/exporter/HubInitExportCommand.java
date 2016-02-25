package mobi.chouette.exchange.hub.exporter;

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
import mobi.chouette.exchange.hub.Constant;
import mobi.chouette.exchange.hub.model.exporter.HubExporter;
import mobi.chouette.exchange.metadata.Metadata;
import mobi.chouette.model.util.Referential;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
public class HubInitExportCommand implements Command, Constant {

	public static final String COMMAND = "HubInitExportCommand";

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;

		Monitor monitor = MonitorFactory.start(COMMAND);

		try {
			JobData jobData = (JobData) context.get(JOB_DATA);
			jobData.setOutputFilename("export_" + jobData.getType() + "_" + jobData.getId() + ".zip");

			context.put(REFERENTIAL, new Referential());
			Metadata metadata = new Metadata(); // if not asked, will be used as
												// dummy
			metadata.setDate(Calendar.getInstance());
			metadata.setFormat("text");
			metadata.setTitle("Export hub ");
			try {
				metadata.setRelation(new URL("http://www.cityway.fr"));
			} catch (MalformedURLException e1) {
				log.error("problem with http://www.cityway.fr url", e1);
			}

			context.put(METADATA, metadata);
			// prepare exporter
			Path path = Paths.get(jobData.getPathName(), OUTPUT);
			if (!Files.exists(path)) {
				Files.createDirectories(path);
			}
			HubExporter hubExporter = new HubExporter(path.toString());
			initExporter(hubExporter);
			context.put(HUB_EXPORTER, hubExporter);
			result = SUCCESS;

		} catch (Exception e) {
			log.error(e, e);
			throw e;
		} finally {
			log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		}

		return result;
	}

	private void initExporter(HubExporter hubExporter)
	{
		// create all files event if empty
		hubExporter.getArretExporter();
		hubExporter.getCheminExporter();
		hubExporter.getCommuneExporter();
		hubExporter.getCorrespondanceExporter();
		hubExporter.getCourseExporter();
		hubExporter.getCourseOperationExporter();
		hubExporter.getDirectionExporter();
		hubExporter.getGroupeDeLigneExporter();
		hubExporter.getHoraireExporter();
		hubExporter.getItlExporter();
		hubExporter.getLigneExporter();
		hubExporter.getModeTransportExporter();
		hubExporter.getPeriodeExporter();
		hubExporter.getRenvoiExporter();
		hubExporter.getReseauExporter();
		hubExporter.getSchemaExporter();
		hubExporter.getTransporteurExporter();
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = new HubInitExportCommand();
			return result;
		}
	}

	static {
		CommandFactory.factories.put(HubInitExportCommand.class.getName(), new DefaultCommandFactory());
	}

}
