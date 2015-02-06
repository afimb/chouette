package mobi.chouette.exchange.neptune.importer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.ToString;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.importer.report.FileItem;
import mobi.chouette.exchange.importer.report.Report;
import mobi.chouette.exchange.neptune.parser.ChouettePTNetworkParser;
import mobi.chouette.model.util.Referential;

import org.apache.commons.io.input.BOMInputStream;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Stateless(name = NeptuneParserCommand.COMMAND)
@ToString
@Log4j
public class NeptuneParserCommand implements Command, Constant {


	public static final String COMMAND = "NeptuneParserCommand";

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;
		Monitor monitor = MonitorFactory.start(COMMAND);
		Report report = (Report) context.get(REPORT);
		FileItem fileItem = new FileItem();
		fileItem.setName((String) context.get(FILE_URL));

		try{

			URL url = new URL((String) context.get(FILE_URL));
			log.info("[DSU] parsing file : " + url );

			InputStream input = new BOMInputStream(url.openStream());
			BufferedReader in = new BufferedReader(new InputStreamReader(input),
					8192 * 10);
			XmlPullParser xpp = XmlPullParserFactory.newInstance().newPullParser();
			xpp.setInput(in);
			context.put(PARSER, xpp);
			context.put(REFERENTIAL, new Referential());

			Parser parser = ParserFactory.create(ChouettePTNetworkParser.class
					.getName());
			parser.parse(context);

			log.info("[DSU] " + monitor.stop());
			report.getFiles().getFilesDetail().getOk().add(fileItem);
			result = SUCCESS;
			return result;
		}
		catch (Exception e)
		{
			report.getFiles().getFilesDetail().getError().add(fileItem);
			fileItem.getErrors().add(e.getMessage());
			throw e;
		}
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = null;
			try {
				String name = "java:app/mobi.chouette.exchange.neptune/"
						+ COMMAND;
				result = (Command) context.lookup(name);
			} catch (NamingException e) {
				log.error(e);
			}
			return result;
		}
	}

	static {
		CommandFactory factory = new DefaultCommandFactory();
		CommandFactory.factories.put(NeptuneParserCommand.class.getName(),
				factory);
	}
}
