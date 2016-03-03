package mobi.chouette.exchange.regtopp.parser;

import java.net.URL;
import java.util.TimeZone;

import org.apache.log4j.Logger;

import mobi.chouette.exchange.regtopp.model.importer.RegtoppException;
import mobi.chouette.exchange.report.FileError;
import mobi.chouette.exchange.report.FileInfo;

public abstract class AbstractConverter {

	/**
	 * @param source
	 * @return
	 */
	public static String getNonEmptyTrimedString(String source) {
		if (source == null)
			return null;
		String target = source.trim();
		return (target.length() == 0 ? null : target);
	}

	

	public static String composeObjectId(String prefix, String type, String id, Logger logger) {

		String[] tokens = id.split("\\.");
		if (tokens.length == 2) {
			// id should be produced by Chouette
			return tokens[0].trim().replaceAll("[^a-zA-Z_0-9]", "_") + ":" + type + ":"
					+ tokens[1].trim().replaceAll("[^a-zA-Z_0-9\\-]", "_");
		}
		return prefix + ":" + type + ":" + id.trim().replaceAll("[^a-zA-Z_0-9\\-]", "_");
	}

	public static String toString(URL url) {
		if (url == null)
			return null;
		return url.toString();
	}

	public static String toString(TimeZone tz) {
		if (tz == null)
			return null;
		return tz.getID();
	}

	
}
