package mobi.chouette.exchange.neptune.parser;

import mobi.chouette.common.Context;

import org.xmlpull.v1.XmlPullParser;

public interface Parser {
	
	   public void parse(Context context) throws Exception;

}
