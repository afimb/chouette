package mobi.chouette.exchange.regtopp.importer.version;

import java.nio.file.Path;
import java.util.Arrays;

import mobi.chouette.exchange.regtopp.model.importer.parser.ParseableFile;
import mobi.chouette.exchange.regtopp.model.importer.parser.RegtoppImporter;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppDayCodeDKO;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppDayCodeHeaderDKO;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppDestinationDST;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppFootnoteMRK;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppLineLIN;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppPathwayGAV;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppRouteTDA;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppStopHPL;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppZoneSON;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppRouteTMS;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppTripIndexTIX;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppVehicleJourneyVLP;
import mobi.chouette.exchange.report.FileInfo;

public class Regtopp11DVersionHandler implements VersionHandler {

	@Override
	public void registerFileForIndex(RegtoppImporter importer, Path fileName, String extension, FileInfo file) {
		switch (extension) {

		case "TIX": {
			ParseableFile parseableFile = new ParseableFile(fileName.toFile(), Arrays.asList(new Class[] { RegtoppTripIndexTIX.class }), file);
			importer.registerFileForIndex(RegtoppImporter.INDEX.TRIP_INDEX.name(), parseableFile);
			importer.registerFileForIndex(RegtoppImporter.INDEX.LINE_BY_TRIPS.name(), parseableFile);
			break;
		}
		case "TDA": {
			
			// TODO index does not handle this file
			ParseableFile parseableFile = new ParseableFile(fileName.toFile(), Arrays.asList(new Class[] { RegtoppRouteTDA.class }), file);
			importer.registerFileForIndex(RegtoppImporter.INDEX.ROUTE_INDEX.name(), parseableFile);
			break;
		}
		case "HPL": {
			ParseableFile parseableFile = new ParseableFile(fileName.toFile(), Arrays.asList(new Class[] { RegtoppStopHPL.class }), file);
			importer.registerFileForIndex(RegtoppImporter.INDEX.STOP_BY_ID.name(), parseableFile);
			break;
		}
		case "DKO": {
			ParseableFile parseableFile = new ParseableFile(fileName.toFile(),
					Arrays.asList(new Class[] { RegtoppDayCodeHeaderDKO.class, RegtoppDayCodeDKO.class }), file);
			importer.registerFileForIndex(RegtoppImporter.INDEX.DAYCODE_BY_ID.name(), parseableFile);
			break;
		}
		case "DST": {
			ParseableFile parseableFile = new ParseableFile(fileName.toFile(), Arrays.asList(new Class[] { RegtoppDestinationDST.class }), file);
			importer.registerFileForIndex(RegtoppImporter.INDEX.DESTINATION_BY_ID.name(), parseableFile);
			break;
		}
		case "MRK": {
			ParseableFile parseableFile = new ParseableFile(fileName.toFile(), Arrays.asList(new Class[] { RegtoppFootnoteMRK.class }), file);
			importer.registerFileForIndex(RegtoppImporter.INDEX.REMARK_BY_ID.name(), parseableFile);
			break;
		}
		case "GAV": {
			ParseableFile parseableFile = new ParseableFile(fileName.toFile(), Arrays.asList(new Class[] { RegtoppPathwayGAV.class }), file);
			importer.registerFileForIndex(RegtoppImporter.INDEX.PATHWAY_FROM_STOP_ID.name(), parseableFile);
			break;
		}
		case "SAM": {
			ParseableFile parseableFile = new ParseableFile(fileName.toFile(), Arrays.asList(new Class[] { RegtoppPathwayGAV.class }), file);
			importer.registerFileForIndex(RegtoppImporter.INDEX.INTERCHANGE.name(), parseableFile);
			break;
		}
		case "SON": {
			ParseableFile parseableFile = new ParseableFile(fileName.toFile(), Arrays.asList(new Class[] { RegtoppZoneSON.class }), file);
			importer.registerFileForIndex(RegtoppImporter.INDEX.ZONE_BY_ID.name(), parseableFile);
			break;
		}
		case "LIN": {
			ParseableFile parseableFile = new ParseableFile(fileName.toFile(), Arrays.asList(new Class[] { RegtoppLineLIN.class }), file);
			importer.registerFileForIndex(RegtoppImporter.INDEX.LINE_BY_ID.name(), parseableFile);
			break;
		}
		case "VLP": {
			ParseableFile parseableFile = new ParseableFile(fileName.toFile(), Arrays.asList(new Class[] { RegtoppVehicleJourneyVLP.class }), file);
			importer.registerFileForIndex(RegtoppImporter.INDEX.VEHICLE_JOURNEY.name(), parseableFile);
			break;
		}
		}
	}

	@Override
	public String createStopPointId(RegtoppRouteTMS tms) {
		// TODO Auto-generated method stub
		return null;
	}
}
