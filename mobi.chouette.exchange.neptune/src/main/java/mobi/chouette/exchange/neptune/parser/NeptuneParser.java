package mobi.chouette.exchange.neptune.parser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.ejb.Stateless;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.exchange.neptune.Constant;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

@Stateless
@Log4j
public class NeptuneParser implements Command, Constant {

	private static final String FILE = "/xsd/neptune.xsd";

	@Override
	public boolean execute(Context context) throws Exception {

		String path = (String) context.get(NEPTUNE_FILE);
		InputStream input = new FileInputStream(path);
		XmlPullParser xpp = XmlPullParserFactory.newInstance().newPullParser();
		BufferedReader in = new BufferedReader(new InputStreamReader(input),
				8192 * 10);
		xpp.setInput(in);

		return false;
	}

}
