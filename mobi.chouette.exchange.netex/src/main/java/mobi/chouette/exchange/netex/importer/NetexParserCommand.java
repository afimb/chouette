package mobi.chouette.exchange.netex.importer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.netex.Constant;
import mobi.chouette.exchange.netex.parser.NetexParser;
import mobi.chouette.importer.Parser;
import mobi.chouette.importer.ParserFactory;
import mobi.chouette.model.util.Referential;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

@Stateless(name = NetexParserCommand.COMMAND)
@Log4j
public class NetexParserCommand implements Command, Constant {

	public static final String COMMAND = "NetexParserCommand";

	@Override
	public boolean execute(Context context) throws Exception {

		String path = (String) context.get(FILE);
		InputStream input = new FileInputStream(path);
		XmlPullParser xpp = XmlPullParserFactory.newInstance().newPullParser();
		BufferedReader in = new BufferedReader(new InputStreamReader(input),
				8192 * 10);
		xpp.setInput(in);
		context.put(XPP, xpp);
		context.put(REFERENTIAL, new Referential());

		Parser parser = ParserFactory.create(NetexParser.class
				.getName());
		parser.parse(context);

		return Constant.SUCCESS;
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = null;
			try {
				result = (Command) context.lookup(JAVA_MODULE + COMMAND);
			} catch (NamingException e) {
				log.error(e);
			}
			return result;
		}
	}

	static {
		CommandFactory factory = new DefaultCommandFactory();
		CommandFactory.factories.put(NetexParserCommand.class.getName(),
				factory);
	}
}
