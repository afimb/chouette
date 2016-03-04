package mobi.chouette.exchange.regtopp.parser;

import org.apache.log4j.Logger;

public abstract class AbstractConverter {


	public static String composeObjectId(String prefix, String type, String id, Logger logger) {

		String[] tokens = id.split("\\.");
		if (tokens.length == 2) {
			// id should be produced by Chouette
			return tokens[0].trim().replaceAll("[^a-zA-Z_0-9]", "_") + ":" + type + ":"
					+ tokens[1].trim().replaceAll("[^a-zA-Z_0-9\\-]", "_");
		}
		return prefix + ":" + type + ":" + id.trim().replaceAll("[^a-zA-Z_0-9\\-]", "_");
	}
	
}
