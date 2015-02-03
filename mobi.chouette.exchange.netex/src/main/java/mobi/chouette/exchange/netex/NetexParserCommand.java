package mobi.chouette.exchange.netex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netex.parser.NetexParser;
import mobi.chouette.model.util.Referential;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

@Stateless(name = NetexParserCommand.COMMAND)
@Log4j
public class NetexParserCommand implements Command, Constant {

	public static final String COMMAND = "NetexParserCommand";

	@Override
	public boolean execute(Context context) throws Exception {

		URL url = new URL((String) context.get(FILE));
		InputStream input = url.openStream();
		XmlPullParser xpp = XmlPullParserFactory.newInstance().newPullParser();
		BufferedReader in = new BufferedReader(new InputStreamReader(input),
				8192 * 10);
		xpp.setInput(in);
		context.put(PARSER, xpp);
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
				String name = "java:app/mobi.chouette.exchange.netex/"
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
		CommandFactory.factories.put(NetexParserCommand.class.getName(),
				factory);
	}
}
