package mobi.chouette.exchange.regtopp.parser;

import java.net.URL;
import java.sql.Time;
import java.util.TimeZone;

import mobi.chouette.exchange.regtopp.model.GtfsTime;
import mobi.chouette.exchange.regtopp.model.importer.RegtoppException;
import mobi.chouette.exchange.report.FileError;
import mobi.chouette.exchange.report.FileInfo;

import org.apache.log4j.Logger;

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

	/**
	 * @param gtfsTime
	 * @return
	 */
	public static Time getTime(GtfsTime gtfsTime) {
		if (gtfsTime == null)
			return null;

		Time time = gtfsTime.getTime();
		return time;
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

	private static void populateFileError(FileInfo file, RegtoppException ex) {
		FileError.CODE code = FileError.CODE.INTERNAL_ERROR;
		switch (ex.getError()) {
		case DUPLICATE_FIELD:
		case INVALID_FORMAT:
		case INVALID_FILE_FORMAT:
		case MISSING_FIELD:
		case MISSING_FOREIGN_KEY:
			code = FileError.CODE.INVALID_FORMAT;
			break;
		case SYSTEM:
			code = FileError.CODE.INTERNAL_ERROR;
			break;
		case MISSING_FILE:
			code = FileError.CODE.FILE_NOT_FOUND;
			break;
		}
		String message = ex.getMessage() != null? ex.getMessage() : ex.toString();
		file.addError(new FileError(code, message));
	}

	public static void populateFileError(FileInfo file, Exception ex) {

		if (ex instanceof RegtoppException) {
			populateFileError(file, (RegtoppException) ex);
		} else {
			String message = ex.getMessage() != null? ex.getMessage() : ex.getClass().getSimpleName();
			file.addError(new FileError(FileError.CODE.INTERNAL_ERROR, message));

		}
	}

}
