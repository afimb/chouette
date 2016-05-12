package mobi.chouette.exchange.regtopp.importer.version;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.regtopp.importer.RegtoppImporter;
import mobi.chouette.exchange.regtopp.importer.parser.LineSpecificParser;
import mobi.chouette.exchange.regtopp.importer.parser.ParseableFile;
import mobi.chouette.exchange.regtopp.importer.parser.v12.RegtoppRouteParser;
import mobi.chouette.exchange.regtopp.importer.parser.v12.RegtoppTripParser;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppDayCodeDKO;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppStopHPL;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppRouteTMS;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppTripIndexTIX;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppVehicleJourneyVLP;
import mobi.chouette.exchange.regtopp.validation.RegtoppException;
import mobi.chouette.exchange.report.FileInfo;

public class Regtopp12VersionHandler extends Regtopp11DVersionHandler {

	@Override
	public LineSpecificParser createRouteParser() throws ClassNotFoundException, IOException {
		return (RegtoppRouteParser) ParserFactory.create(RegtoppRouteParser.class.getName());
	}

	@Override
	public LineSpecificParser createTripParser() throws ClassNotFoundException, IOException {
		return (RegtoppTripParser) ParserFactory.create(RegtoppTripParser.class.getName());
	}

	@Override
	public void registerFileForIndex(RegtoppImporter importer, Path fileName, String extension, FileInfo file) {
		switch (extension) {

		case "TIX": {
			ParseableFile parseableFile = new ParseableFile(fileName.toFile(), Arrays.asList(new Class[] { RegtoppTripIndexTIX.class }), RegtoppException.ERROR.TIX_INVALID_FIELD_VALUE, file);
			importer.registerFileForIndex(RegtoppImporter.INDEX.TRIP_INDEX.name(), parseableFile);
			importer.registerFileForIndex(RegtoppImporter.INDEX.LINE_BY_TRIPS.name(), parseableFile);
			break;
		}
		case "TMS": {
			ParseableFile parseableFile = new ParseableFile(fileName.toFile(), Arrays.asList(new Class[] { RegtoppRouteTMS.class }), RegtoppException.ERROR.TMS_INVALID_FIELD_VALUE, file);
			importer.registerFileForIndex(RegtoppImporter.INDEX.ROUTE_INDEX.name(), parseableFile);
			break;
		}

		case "VLP": {
			ParseableFile parseableFile = new ParseableFile(fileName.toFile(), Arrays.asList(new Class[] { RegtoppVehicleJourneyVLP.class }), RegtoppException.ERROR.VLP_INVALID_FIELD_VALUE, file);
			importer.registerFileForIndex(RegtoppImporter.INDEX.VEHICLE_JOURNEY.name(), parseableFile);
			break;
		}
//		case "TAB": {
//			ParseableFile parseableFile = new ParseableFile(fileName.toFile(), Arrays.asList(new Class[] { RegtoppTableVersionTAB.class }), file);
//			importer.registerFileForIndex(RegtoppImporter.INDEX.TABLE_VERSION.name(), parseableFile);
//			break;
//		}
//		case "RUT": {
//			ParseableFile parseableFile = new ParseableFile(fileName.toFile(), Arrays.asList(new Class[] { RegtoppRoutePointRUT.class }), file);
//			importer.registerFileForIndex(RegtoppImporter.INDEX.ROUTE_POINT.name(), parseableFile);
//			break;
//		}
		default:
			super.registerFileForIndex(importer, fileName, extension, file);
		}
	}

	@Override
	public String[] getMandatoryFileExtensions() {
		return new String[] {
				RegtoppTripIndexTIX.FILE_EXTENSION,
				RegtoppRouteTMS.FILE_EXTENSION,
				RegtoppStopHPL.FILE_EXTENSION,
				RegtoppDayCodeDKO.FILE_EXTENSION };
	}

}
