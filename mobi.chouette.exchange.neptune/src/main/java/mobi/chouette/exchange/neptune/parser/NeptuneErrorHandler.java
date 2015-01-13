package mobi.chouette.exchange.neptune.parser;

import lombok.AllArgsConstructor;
import mobi.chouette.common.Context;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

@AllArgsConstructor
public class NeptuneErrorHandler implements ErrorHandler {

	private Context context;

	@Override
	public void warning(SAXParseException exception) throws SAXException {
		// TODO Auto-generated method stub

	}

	@Override
	public void error(SAXParseException exception) throws SAXException {
		// TODO Auto-generated method stub

	}

	@Override
	public void fatalError(SAXParseException exception) throws SAXException {
		// TODO Auto-generated method stub

	}

}
