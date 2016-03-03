package mobi.chouette.exchange.regtopp.importer;

import java.io.IOException;

import javax.naming.InitialContext;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.regtopp.Constant;
import mobi.chouette.exchange.regtopp.model.importer.RegtoppImporter;
import mobi.chouette.exchange.regtopp.parser.RegtoppStopParser;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.DataStats;
import mobi.chouette.model.util.Referential;

@Log4j
public class RegtoppStopParserCommand implements Command, Constant {

	public static final String COMMAND = "RegtoppStopParserCommand";

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;
		
		Monitor monitor = MonitorFactory.start(COMMAND);

		try {
			Referential referential = (Referential) context.get(REFERENTIAL);
			RegtoppImportParameters configuration = (RegtoppImportParameters) context.get(CONFIGURATION);
			ActionReport report = (ActionReport) context.get(REPORT);
			if (referential != null) {
				referential.clear(true);
			}

			RegtoppImporter importer = (RegtoppImporter) context.get(PARSER);

			// StopArea
			if (referential.getSharedStopAreas().isEmpty()) {
				RegtoppStopParser stopParser = (RegtoppStopParser) ParserFactory
						.create(RegtoppStopParser.class.getName());
				stopParser.parse(context);
			}
//
//			// ConnectionLink
//			if (importer.hasTransferImporter()) {
//				if (referential.getSharedConnectionLinks().isEmpty()) {
//					GtfsTransferParser gtfsTransferParser = (GtfsTransferParser) ParserFactory
//							.create(GtfsTransferParser.class.getName());
//					gtfsTransferParser.parse(context);
//				}
//			}
			if (configuration.getMaxDistanceForCommercial() > 0)
			{
				CommercialStopGenerator commercialStopGenerator = new CommercialStopGenerator();
				commercialStopGenerator.createCommercialStopPoints(context);
			}
			
			if (configuration.getMaxDistanceForConnectionLink() > 0)
			{
			    ConnectionLinkGenerator connectionLinkGenerator = new ConnectionLinkGenerator();
				connectionLinkGenerator.createConnectionLinks(context);
				
			}
			

			addStats(report, referential);
		    
			result = SUCCESS;
		} catch (Exception e) {
			log.error("[DSU] error : ", e);
			throw e;
		}
		
		log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		return result;
	}

	
	private void addStats(ActionReport report, Referential referential) {
		DataStats globalStats = report.getStats();
		globalStats.setConnectionLinkCount(referential.getSharedConnectionLinks().size());
		globalStats.setStopAreaCount(referential.getSharedStopAreas().size());

	}


	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = new RegtoppStopParserCommand();
			return result;
		}
	}

	static {
		CommandFactory.factories.put(RegtoppStopParserCommand.class.getName(),
				new DefaultCommandFactory());
	}
}
