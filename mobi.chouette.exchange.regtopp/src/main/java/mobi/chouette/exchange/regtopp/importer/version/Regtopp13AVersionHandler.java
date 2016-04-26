package mobi.chouette.exchange.regtopp.importer.version;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.regtopp.importer.RegtoppImporter;
import mobi.chouette.exchange.regtopp.importer.parser.LineSpecificParser;
import mobi.chouette.exchange.regtopp.importer.parser.ParseableFile;
import mobi.chouette.exchange.regtopp.importer.parser.v12novus.RegtoppRouteParser;
import mobi.chouette.exchange.regtopp.importer.parser.v13.RegtoppStopParser;
import mobi.chouette.exchange.regtopp.importer.parser.v13.RegtoppTripParser;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppDayCodeDKO;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppDestinationDST;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppFootnoteMRK;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppInterchangeSAM;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppLineLIN;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppZoneSON;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppPeriodPER;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppTableVersionTAB;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppVehicleJourneyVLP;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppPathwayGAV;
import mobi.chouette.exchange.regtopp.model.v13.RegtoppRoutePointRUT;
import mobi.chouette.exchange.regtopp.model.v13.RegtoppRouteTMS;
import mobi.chouette.exchange.regtopp.model.v13.RegtoppStopHPL;
import mobi.chouette.exchange.regtopp.model.v13.RegtoppStopPointSTP;
import mobi.chouette.exchange.regtopp.model.v13.RegtoppTripIndexTIX;
import mobi.chouette.exchange.report.FileInfo;

public class Regtopp13AVersionHandler extends Regtopp12VersionHandler {

	private static final List<String> mandatoryFileExtensions = Arrays.asList(RegtoppTripIndexTIX.FILE_EXTENSION, RegtoppRouteTMS.FILE_EXTENSION,
			RegtoppStopHPL.FILE_EXTENSION, RegtoppDayCodeDKO.FILE_EXTENSION, RegtoppStopPointSTP.FILE_EXTENSION);

	private static final List<String> optionalFileExtensions = Arrays.asList(RegtoppDestinationDST.FILE_EXTENSION, RegtoppFootnoteMRK.FILE_EXTENSION,
			RegtoppPathwayGAV.FILE_EXTENSION, RegtoppInterchangeSAM.FILE_EXTENSION, RegtoppZoneSON.FILE_EXTENSION, RegtoppLineLIN.FILE_EXTENSION,
			RegtoppVehicleJourneyVLP.FILE_EXTENSION, RegtoppTableVersionTAB.FILE_EXTENSION, RegtoppPeriodPER.FILE_EXTENSION,
			RegtoppRoutePointRUT.FILE_EXTENSION);

	@Override
	public LineSpecificParser createRouteParser() throws ClassNotFoundException, IOException {
		return (RegtoppRouteParser) ParserFactory.create(RegtoppRouteParser.class.getName());
	}

	@Override
	public LineSpecificParser createTripParser() throws ClassNotFoundException, IOException {
		return (RegtoppTripParser) ParserFactory.create(RegtoppTripParser.class.getName());
	}

	@Override
	public Parser createStopParser() throws ClassNotFoundException, IOException {
		return (RegtoppStopParser) ParserFactory.create(RegtoppStopParser.class.getName());
	}

	@Override
	public void registerFileForIndex(RegtoppImporter importer, Path fileName, String extension, FileInfo file) {
		switch (extension) {

		case "TIX": {
			ParseableFile parseableFile = new ParseableFile(fileName.toFile(), Arrays.asList(new Class[] { RegtoppTripIndexTIX.class }), file);
			importer.registerFileForIndex(RegtoppImporter.INDEX.TRIP_INDEX.name(), parseableFile);
			importer.registerFileForIndex(RegtoppImporter.INDEX.LINE_BY_TRIPS.name(), parseableFile);
			break;
		}
		case "HPL": {
			ParseableFile parseableFile = new ParseableFile(fileName.toFile(), Arrays.asList(new Class[] { RegtoppStopHPL.class }), file);
			importer.registerFileForIndex(RegtoppImporter.INDEX.STOP_BY_ID.name(), parseableFile);
			break;
		}
		case "STP": {
			ParseableFile parseableFile = new ParseableFile(fileName.toFile(), Arrays.asList(new Class[] { RegtoppStopPointSTP.class }), file);
			importer.registerFileForIndex(RegtoppImporter.INDEX.STOPPOINT_BY_ID.name(), parseableFile);
			importer.registerFileForIndex(RegtoppImporter.INDEX.STOPPOINT_BY_STOP_ID.name(), parseableFile);
			break;
		}
		case "TMS": {
			ParseableFile parseableFile = new ParseableFile(fileName.toFile(), Arrays.asList(new Class[] { RegtoppRouteTMS.class }), file);
			importer.registerFileForIndex(RegtoppImporter.INDEX.ROUTE_INDEX.name(), parseableFile);
			importer.registerFileForIndex(RegtoppImporter.INDEX.ROUTE_BY_ROUTE_KEY.name(), parseableFile);
			break;
		}
		case "GAV": {
			// WARNING WARNING! Ruter as the sole user of Regtopp1.3A sends GAV file according to 1.1D spec. Therefore this is used here.
			ParseableFile parseableFile = new ParseableFile(fileName.toFile(),
					Arrays.asList(new Class[] {
							mobi.chouette.exchange.regtopp.model.v11.RegtoppPathwayGAV.class,
							mobi.chouette.exchange.regtopp.model.v13.RegtoppPathwayGAV.class }),
					file);
			importer.registerFileForIndex(RegtoppImporter.INDEX.PATHWAY_BY_INDEXING_KEY.name(), parseableFile);
			break;
		}
		case "RUT": {
			ParseableFile parseableFile = new ParseableFile(fileName.toFile(), Arrays.asList(new Class[] { RegtoppRoutePointRUT.class }), file);
			importer.registerFileForIndex(RegtoppImporter.INDEX.ROUTE_POINT.name(), parseableFile);
			break;
		}
		default:
			super.registerFileForIndex(importer, fileName, extension, file);
		}
	}

	@Override
	public List<String> getMandatoryFileExtensions() {
		return mandatoryFileExtensions;
	}

	@Override
	public List<String> getOptionalFileExtensions() {
		return optionalFileExtensions;
	}
}
