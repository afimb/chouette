package mobi.chouette.exchange.regtopp.importer.version;

import java.nio.file.Path;
import java.util.Arrays;

import mobi.chouette.exchange.regtopp.model.importer.parser.ParseableFile;
import mobi.chouette.exchange.regtopp.model.importer.parser.RegtoppImporter;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppDayCodeDKO;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppDayCodeHeaderDKO;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppDestinationDST;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppFootnoteMRK;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppLineLIN;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppPathwayGAV;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppRoutePointRUT;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppRouteTMS;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppStopHPL;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppTableVersionTAB;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppTripIndexTIX;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppVehicleJourneyVLP;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppZoneSON;
import mobi.chouette.exchange.report.FileInfo;

public class Regtopp12VersionHandler implements VersionHandler {

	@Override
	public void registerFileForIndex(RegtoppImporter importer, Path fileName, String extension, FileInfo file) {
		// TODO convert to switch case
		
		// TODO 2 use regtopp 1.1D version handler for files that can be handled there
		if ("TIX".equals(extension)) {
			ParseableFile parseableFile = new ParseableFile(fileName.toFile(), Arrays.asList(new Class[] { RegtoppTripIndexTIX.class }),
					file);
			importer.registerFileForIndex(RegtoppImporter.INDEX.TRIP_INDEX.name(), parseableFile);
			importer.registerFileForIndex(RegtoppImporter.INDEX.LINE_BY_TRIPS.name(), parseableFile);
		} else if ("TMS".equals(extension)) {
			ParseableFile parseableFile = new ParseableFile(fileName.toFile(), Arrays.asList(new Class[] { RegtoppRouteTMS.class }), file);
			importer.registerFileForIndex(RegtoppImporter.INDEX.ROUTE_INDEX.name(), parseableFile);
		} else if ("HPL".equals(extension)) {
			ParseableFile parseableFile = new ParseableFile(fileName.toFile(), Arrays.asList(new Class[] { RegtoppStopHPL.class }), file);
			importer.registerFileForIndex(RegtoppImporter.INDEX.STOP_BY_ID.name(), parseableFile);
		} else if ("DKO".equals(extension)) {
			ParseableFile parseableFile = new ParseableFile(fileName.toFile(),
					Arrays.asList(new Class[] { RegtoppDayCodeHeaderDKO.class, RegtoppDayCodeDKO.class }), file);
			importer.registerFileForIndex(RegtoppImporter.INDEX.DAYCODE_BY_ID.name(), parseableFile);
		} else if ("DST".equals(extension)) {
			ParseableFile parseableFile = new ParseableFile(fileName.toFile(), Arrays.asList(new Class[] { RegtoppDestinationDST.class }),
					file);
			importer.registerFileForIndex(RegtoppImporter.INDEX.DESTINATION_BY_ID.name(), parseableFile);
		} else if ("MRK".equals(extension)) {
			ParseableFile parseableFile = new ParseableFile(fileName.toFile(), Arrays.asList(new Class[] { RegtoppFootnoteMRK.class }),
					file);
			importer.registerFileForIndex(RegtoppImporter.INDEX.REMARK_BY_ID.name(), parseableFile);
		} else if ("GAV".equals(extension)) {
			ParseableFile parseableFile = new ParseableFile(fileName.toFile(), Arrays.asList(new Class[] { RegtoppPathwayGAV.class }),
					file);
			importer.registerFileForIndex(RegtoppImporter.INDEX.PATHWAY_FROM_STOP_ID.name(), parseableFile);
		} else if ("SAM".equals(extension)) {
			ParseableFile parseableFile = new ParseableFile(fileName.toFile(), Arrays.asList(new Class[] { RegtoppPathwayGAV.class }),
					file);
			importer.registerFileForIndex(RegtoppImporter.INDEX.INTERCHANGE.name(), parseableFile);
		} else if ("SON".equals(extension)) {
			ParseableFile parseableFile = new ParseableFile(fileName.toFile(), Arrays.asList(new Class[] { RegtoppZoneSON.class }), file);
			importer.registerFileForIndex(RegtoppImporter.INDEX.ZONE_BY_ID.name(), parseableFile);
		} else if ("LIN".equals(extension)) {
			ParseableFile parseableFile = new ParseableFile(fileName.toFile(), Arrays.asList(new Class[] { RegtoppLineLIN.class }), file);
			importer.registerFileForIndex(RegtoppImporter.INDEX.LINE_BY_ID.name(), parseableFile);
		} else if ("VLP".equals(extension)) {
			ParseableFile parseableFile = new ParseableFile(fileName.toFile(),
					Arrays.asList(new Class[] { RegtoppVehicleJourneyVLP.class }), file);
			importer.registerFileForIndex(RegtoppImporter.INDEX.VEHICLE_JOURNEY.name(), parseableFile);
		} else if ("TAB".equals(extension)) {
			ParseableFile parseableFile = new ParseableFile(fileName.toFile(), Arrays.asList(new Class[] { RegtoppTableVersionTAB.class }),
					file);
			importer.registerFileForIndex(RegtoppImporter.INDEX.TABLE_VERSION.name(), parseableFile);
		} else if ("RUT".equals(extension)) {
			ParseableFile parseableFile = new ParseableFile(fileName.toFile(), Arrays.asList(new Class[] { RegtoppRoutePointRUT.class }),
					file);
			importer.registerFileForIndex(RegtoppImporter.INDEX.ROUTE_POINT.name(), parseableFile);
		}

	}

	@Override
	public String createStopPointId(RegtoppRouteTMS tms) {
		return tms.getStopId();
	}
}
