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
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.importer.util.NetexReferential;
import mobi.chouette.exchange.netexprofile.importer.validation.NetexProfileValidator;
import mobi.chouette.exchange.netexprofile.parser.NetexParser;
import mobi.chouette.exchange.netexprofile.parser.PublicationDeliveryParser;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.FileError;
import mobi.chouette.exchange.report.FileInfo;
import mobi.chouette.exchange.report.FileInfo.FILE_STATE;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.model.util.Referential;
import no.rutebanken.netex.model.LinkSequence_VersionStructure;
import no.rutebanken.netex.model.PublicationDeliveryStructure;
import no.rutebanken.netex.model.RoutesInFrame_RelStructure;
import no.rutebanken.netex.model.Route;
import org.w3c.dom.Document;

import javax.naming.InitialContext;
import javax.xml.bind.JAXBElement;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Log4j
public class NetexLineParserCommand implements Command, Constant {

	public static final String COMMAND = "NetexLineParserCommand";


	@Getter
	@Setter
	private File file;

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;

		Monitor monitor = MonitorFactory.start(COMMAND);

		// report service
		ActionReport report = (ActionReport) context.get(REPORT);
		context.put(VALIDATION_REPORT, new ValidationReport());
		String fileName = file.getName();
		FileInfo fileItem = new FileInfo(fileName,FILE_STATE.OK);
		context.put(FILE_NAME, fileName);
		report.getFiles().add(fileItem);

		try {
			log.info("parsing file : " + file.getAbsolutePath());

			Referential referential = (Referential) context.get(REFERENTIAL);
			if (referential != null) {
				referential.clear(true);
			}
			context.put(NETEX_REFERENTIAL, new NetexReferential());

			NetexImporter importer = (NetexImporter) context.get(IMPORTER);
			Document dom = importer.parseFileToDom(file);
			PublicationDeliveryStructure lineDeliveryStructure = importer.unmarshal(dom);

			context.put(NETEX_LINE_DATA_JAVA, lineDeliveryStructure);
			context.put(NETEX_LINE_DATA_DOM, dom);

			NetexParser netexParser = (NetexParser) ParserFactory.create(NetexParser.class.getName());
			//netexParser.initializeReferentials(context); // TODO enable this to validate

			// Profile validation
			NetexProfileValidator profileValidator = importer.getProfileValidator(context); 
			if (profileValidator != null) {
				context.put(NETEX_PROFILE_VALIDATOR, profileValidator);
				profileValidator.addCheckpoints(context);

				boolean validationOKWithNoErrors = profileValidator.validate(context);
				// TODO handle that validation errors occur

				// Parse (convert to chouette objects)
				if(validationOKWithNoErrors) {
					Parser parser = ParserFactory.create(PublicationDeliveryParser.class.getName());
					parser.parse(context);
					result = SUCCESS;
				}
			}

		} catch (Exception e) {
			// report service
			report.getFiles().add(fileItem);
			fileItem.addError(new FileError(FileError.CODE.INTERNAL_ERROR, e.toString()));
			log.error("parsing failed ", e);
		} finally {
			log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		}

		return result;
	}

/*
	private void preParseNetexModel(Referential referential, PublicationDeliveryStructure publicationDeliveryStruct) {
		// pre-parse routes
		List<JAXBElement<? extends LinkSequence_VersionStructure>> routesStructure = serviceFrame.getRoutes().getRoute_();
		for (JAXBElement<? extends LinkSequence_VersionStructure> jaxbElement : routesStructure) {
			Route route = (Route) jaxbElement.getValue();
			addRouteReference(referential, route);
		}
	}

	private void addRouteReference(Referential referential, Route route) {

	}
*/

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = new NetexLineParserCommand();
			return result;
		}
	}

	static {
		CommandFactory.factories.put(NetexLineParserCommand.class.getName(),
				new DefaultCommandFactory());
	}
}
