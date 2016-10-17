package mobi.chouette.exchange.netexprofile.importer;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.IO_TYPE;
import mobi.chouette.model.util.Referential;
import no.rutebanken.netex.model.PublicationDeliveryStructure;

import javax.naming.InitialContext;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Log4j
public class NetexCommonParserCommand implements Command, Constant {

	public static final String COMMAND = "NetexCommonParserCommand";

	@Getter
	@Setter
	private File file;

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;

		Monitor monitor = MonitorFactory.start(COMMAND);

		// report service
		ActionReporter actionReporter = ActionReporter.Factory.getInstance();
		String fileName = file.getName();
		context.put(FILE_NAME, fileName);

		try {

			log.info("parsing file : " + file.getAbsolutePath());

			// Fetch other common data, append to this
			
			Referential referential = (Referential) context.get(REFERENTIAL);
			if (referential != null) {
				referential.clear(true);
			}

			NetexImporter importer = (NetexImporter) context.get(IMPORTER);
			PublicationDeliveryStructure commonDeliveryStructure =importer.unmarshal(file);
			
			@SuppressWarnings("unchecked")
			List<PublicationDeliveryStructure> commonDeliveries = (List<PublicationDeliveryStructure>) context.get("COMMON_DATA");
			if(commonDeliveries == null) {
				commonDeliveries = new ArrayList<>();
				context.put(NETEX_COMMON_DATA, commonDeliveries);
			}
			
			commonDeliveries.add(commonDeliveryStructure);
			
			// report service
			actionReporter.setFileState(context, fileName, IO_TYPE.INPUT, ActionReporter.FILE_STATE.OK);

			result = SUCCESS;
		} catch (Exception e) {
			// report service
			actionReporter.addFileErrorInReport(context, fileName, ActionReporter.FILE_ERROR_CODE.INTERNAL_ERROR, e.toString());
			log.error("parsing failed ", e);
		} finally {
			log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		}

		return result;
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = new NetexCommonParserCommand();
			return result;
		}
	}

	static {
		CommandFactory.factories.put(NetexCommonParserCommand.class.getName(),
				new DefaultCommandFactory());
	}
}
