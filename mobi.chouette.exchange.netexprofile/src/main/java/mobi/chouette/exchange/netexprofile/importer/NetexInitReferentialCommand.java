package mobi.chouette.exchange.netexprofile.importer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.w3c.dom.Document;

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
import mobi.chouette.exchange.netexprofile.importer.util.NetexReferential;
import mobi.chouette.exchange.netexprofile.importer.validation.AbstractNetexProfileValidator;
import mobi.chouette.exchange.netexprofile.importer.validation.NetexProfileValidator;
import mobi.chouette.exchange.netexprofile.importer.validation.norway.NorwayLineNetexProfileValidator;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.IO_TYPE;
import mobi.chouette.exchange.validation.report.DataLocation;
import mobi.chouette.exchange.validation.report.ValidationReporter;

@Log4j
public class NetexInitReferentialCommand implements Command, Constant {

	public static final String COMMAND = "NetexInitReferentialCommand";


	@Getter
	@Setter
	private String fileURL;
	
	@Getter
	@Setter
	private boolean lineFile;

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = SUCCESS;
		Monitor monitor = MonitorFactory.start(COMMAND);
		context.put(FILE_URL, fileURL);

		ActionReporter reporter = ActionReporter.Factory.getInstance();
		ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
		validationReporter.addItemToValidationReport(context, AbstractNetexProfileValidator._1_NETEX_UNKNOWN_PROFILE, "E");

		File file = new File(new URL(fileURL).toURI());
		String fileName = file.getName();
		reporter.addFileReport(context, fileName, IO_TYPE.INPUT);
		context.put(FILE_NAME, fileName);

		try {

			NetexImporter importer = (NetexImporter) context.get(IMPORTER);
			Document netexDom = importer.parseFileToDom(file);
			PublicationDeliveryStructure netexJava = importer.unmarshal(netexDom);
			
			context.put(NETEX_DATA_JAVA, netexJava);
			context.put(NETEX_DATA_DOM, netexDom);
			if(lineFile) {
				context.put(NETEX_WITH_COMMON_DATA, Boolean.FALSE);
				context.put(NETEX_REFERENTIAL, new NetexReferential());
			} else {
				context.put(NETEX_WITH_COMMON_DATA, Boolean.TRUE);
			}

			Map<String, NetexProfileValidator> availableProfileValidators = (Map<String, NetexProfileValidator>) context.get(NETEX_PROFILE_VALIDATORS);

			String profileVersion = netexJava.getVersion();
			if(!lineFile) {
				profileVersion += "-common";
			}
			
			NetexProfileValidator profileValidator = availableProfileValidators.get(profileVersion);
			if (profileValidator != null) {
				profileValidator.initializeCheckPoints(context);
				context.put(NETEX_PROFILE_VALIDATOR, profileValidator);
				validationReporter.reportSuccess(context, AbstractNetexProfileValidator._1_NETEX_UNKNOWN_PROFILE);
			} else {
				log.error("Unsupported NeTEx profile in PublicationDelivery/@version: " + profileVersion);
				// TODO fix reporting with lineNumber etc
				validationReporter.addCheckPointReportError(context, AbstractNetexProfileValidator._1_NETEX_UNKNOWN_PROFILE, null,new DataLocation(fileName),profileVersion);
				result = ERROR;
			}

		} catch (Exception e) {
			reporter.addFileErrorInReport(context, fileName, ActionReporter.FILE_ERROR_CODE.INTERNAL_ERROR, e.toString());
			log.error("Netex referential initialization failed ", e);
			throw e;
		} finally {
			log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		}
		return result;
	}

	public static class DefaultCommandFactory extends CommandFactory {
		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = new NetexInitReferentialCommand();
			return result;
		}
	}

	static {
		CommandFactory.factories.put(NetexInitReferentialCommand.class.getName(), new NetexInitReferentialCommand.DefaultCommandFactory());
	}

}
