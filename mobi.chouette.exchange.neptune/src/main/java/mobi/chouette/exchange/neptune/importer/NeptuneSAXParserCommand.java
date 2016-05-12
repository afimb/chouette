package mobi.chouette.exchange.neptune.importer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;

import javax.naming.InitialContext;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.FileError;
import mobi.chouette.exchange.report.FileInfo;
import mobi.chouette.exchange.report.FileInfo.FILE_STATE;

import org.apache.commons.io.FileUtils;
import org.xml.sax.SAXException;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
public class NeptuneSAXParserCommand implements Command, Constant {

	public static final String COMMAND = "NeptuneSAXParserCommand";

	public static final String SCHEMA_FILE = "/xsd/neptune.xsd";

	@Getter
	@Setter
	private String fileURL;

	@Override
	public boolean execute(Context context) throws Exception {

		boolean result = ERROR;

		Monitor monitor = MonitorFactory.start(COMMAND);

		ActionReport report = (ActionReport) context.get(REPORT);

		String fileName = new File(new URL(fileURL).toURI()).getName();
		FileInfo fileItem = new FileInfo(fileName,FILE_STATE.OK);

		Schema schema = (Schema) context.get(SCHEMA);
		if (schema == null) {
			SchemaFactory factory = SchemaFactory
					.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			schema = factory.newSchema(getClass().getResource(SCHEMA_FILE));
			context.put(SCHEMA, schema);
		}

		URL url = new URL(fileURL);


		NeptuneSAXErrorHandler errorHandler = new NeptuneSAXErrorHandler(
				context, fileURL);
		Reader reader = null;
		try {
			reader = new BufferedReader(CharSetChecker.getEncodedInputStreamReader(url.toString(), url.openStream()), 8192 * 10);
			StreamSource file = new StreamSource(reader);
			Validator validator = schema.newValidator();
			validator.setErrorHandler(errorHandler);
			// validator.reset();
			validator.validate(file);
			if (errorHandler.isHasErrors()) {
				report.getFiles().add(fileItem);
				fileItem.addError(new FileError(FileError.CODE.INVALID_FORMAT,"Xml errors"));
				return result;
			}
			result = SUCCESS;
		} catch (IOException | SAXException e) {

			if (!context.containsKey("REPLAY_VALIDATOR")
					&& e.getMessage().contains("ChouettePTNetwork")) {
				log.warn(e);
				if (reader != null ) reader.close();
				addNameSpace(url);
				context.put("REPLAY_VALIDATOR", Boolean.TRUE);
				boolean res = execute(context);
				context.remove("REPLAY_VALIDATOR");
				return res;
			}
			log.error(e);
			errorHandler.handleError(e);
			report.getFiles().add(fileItem);
			fileItem.addError(new FileError(FileError.CODE.INVALID_FORMAT,e.getMessage()));
		} catch (Exception e) {

			log.error(e);
			report.getFiles().add(fileItem);
			fileItem.addError(new FileError(FileError.CODE.INTERNAL_ERROR,e.getMessage()));

		} finally {
			if (reader != null ) reader.close();
			log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		}

		return result;
	}

	private void addNameSpace(URL url) {
		try {
			File tmp = File.createTempFile("netpuneImport", ".xml");
			FileUtils.copyInputStreamToFile(url.openStream(), tmp);
			
			InputStreamReader isr = CharSetChecker.getEncodedInputStreamReader(tmp.getName(), new FileInputStream(tmp));

			BufferedReader reader = new BufferedReader(isr, 8192);

			File f = new File(url.toURI());

			PrintWriter writer = new PrintWriter(f,isr.getEncoding());

			String l;
			while ((l = reader.readLine()) != null) {
				if (l.contains("<ChouettePTNetwork>")) {
					l = l.replace(
							"<ChouettePTNetwork>",
							"<ChouettePTNetwork xmlns=\"http://www.trident.org/schema/trident\" "
									+ "xmlns:acsb=\"http://www.ifopt.org.uk/acsb\" "
									+ "xmlns:siri=\"http://www.siri.org.uk/siri\">");
					log.info(" <ChouettePTNetwork> replaced :" + l);
				}
				writer.println(l);
			}
			reader.close();
			writer.close();
			tmp.delete();

		} catch (IOException | URISyntaxException e) {
	          log.error("fail to correct Neptune old fashion file "+e.getClass().getName()+" "+e.getMessage());
		}

	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = new NeptuneSAXParserCommand();
			return result;
		}
	}

	static {
		CommandFactory.factories.put(NeptuneSAXParserCommand.class.getName(),
				new DefaultCommandFactory());
	}
}
