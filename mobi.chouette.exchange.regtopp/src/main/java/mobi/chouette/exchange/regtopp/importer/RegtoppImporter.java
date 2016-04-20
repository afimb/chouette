package mobi.chouette.exchange.regtopp.importer;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.regtopp.importer.index.Index;
import mobi.chouette.exchange.regtopp.importer.index.IndexFactory;
import mobi.chouette.exchange.regtopp.importer.index.v11.DaycodeById;
import mobi.chouette.exchange.regtopp.importer.index.v11.DestinationById;
import mobi.chouette.exchange.regtopp.importer.index.v11.FootnoteById;
import mobi.chouette.exchange.regtopp.importer.index.v11.LineById;
import mobi.chouette.exchange.regtopp.importer.index.v11.RouteByLineNumber;
import mobi.chouette.exchange.regtopp.importer.index.v11.StopById;
import mobi.chouette.exchange.regtopp.importer.index.v11.TripByIndexingKey;
import mobi.chouette.exchange.regtopp.importer.index.v11.UniqueLinesByTripIndex;
import mobi.chouette.exchange.regtopp.importer.index.v12.RouteByIndexingKey;
import mobi.chouette.exchange.regtopp.importer.index.v12.RouteByRouteKey;
import mobi.chouette.exchange.regtopp.importer.parser.FileContentParser;
import mobi.chouette.exchange.regtopp.importer.parser.FileParserValidationError;
import mobi.chouette.exchange.regtopp.importer.parser.ParseableFile;
import mobi.chouette.exchange.regtopp.model.AbstractRegtoppTripIndexTIX;
import mobi.chouette.exchange.regtopp.model.RegtoppObject;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppDayCodeDKO;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppDestinationDST;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppFootnoteMRK;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppLineLIN;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppRouteTDA;
import mobi.chouette.exchange.regtopp.model.v11.RegtoppStopHPL;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppRouteTMS;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppTripIndexTIX;
import mobi.chouette.exchange.regtopp.validation.RegtoppException;
import mobi.chouette.exchange.regtopp.validation.RegtoppValidationReporter;
import mobi.chouette.exchange.regtopp.validation.RegtoppException.ERROR;

@Log4j
public class RegtoppImporter {
	public static enum INDEX {
		STOP_BY_ID,
		DAYCODE_BY_ID,
		DESTINATION_BY_ID,
		REMARK_BY_ID,
		PATHWAY_FROM_STOP_ID,
		TRIP_INDEX,
		TRIP_PATTERN,
		INTERCHANGE,
		ZONE_BY_ID,
		LINE_BY_ID,
		VEHICLE_JOURNEY,
		TABLE_VERSION,
		ROUTE_POINT,
		LINE_BY_TRIPS,
		ROUTE_INDEX,
		ROUTE_BY_ROUTE_KEY,
		ROUTE_BY_LINE_NUMBER
	}

	private String path;
	private Map<String, Index<RegtoppObject>> indexMap = new HashMap<String, Index<RegtoppObject>>();
	private Map<String, FileContentParser> fileContentMap = new HashMap<String, FileContentParser>();
	private Map<String, ParseableFile> fileMap = new HashMap<String, ParseableFile>();

	private RegtoppValidationReporter validationReporter;
	private Context context;

	public RegtoppImporter(Context context, String path, RegtoppValidationReporter validationReporter) {
		this.path = path;
		this.validationReporter = validationReporter;
		this.context = context;
	}

	public void registerFileForIndex(String indexName, ParseableFile parseableFile) {
		fileMap.put(indexName, parseableFile);
	}

	@SuppressWarnings("rawtypes")
	public void dispose() {
		for (Index importer : indexMap.values()) {
			importer.dispose();
		}
		for (FileContentParser parser : fileContentMap.values()) {
			parser.dispose();
		}

		indexMap.clear();
		indexMap = null;
		fileContentMap.clear();
		fileContentMap = null;

		fileMap.clear();
		fileMap = null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Index getIndex(String name, Class clazz) {
		Index index = indexMap.get(name);

		if (index == null) {
			try {
				ParseableFile parseableFile = fileMap.get(name);
				if (parseableFile == null) {
					throw new RuntimeException("No such index " + name);
				}

				FileContentParser parser = fileContentMap.get(parseableFile.getFile().getName());
				if (parser == null) {
					parser = new FileContentParser();
					fileContentMap.put(parseableFile.getFile().getName(), parser);
					// Do actual parsing of file
					parser.parse(context, parseableFile, validationReporter);
				}

				index = IndexFactory.build(validationReporter, parser, clazz.getName());
				indexMap.put(name, index);
			} catch (Exception e) {
				log.error(e);
				FileParserValidationError context = new FileParserValidationError();
				context.put(FileParserValidationError.PATH, path);
				context.put(FileParserValidationError.ERROR, ERROR.SYSTEM);
				throw new RegtoppException(context, e);
			}

		}
		return index;
	}

	@SuppressWarnings("unchecked")
	public Index<RegtoppStopHPL> getStopById() {
		return getIndex(INDEX.STOP_BY_ID.name(), StopById.class);
	}

	public Index<AbstractRegtoppTripIndexTIX> getUniqueLinesByTripIndex() {
		return getIndex(INDEX.LINE_BY_TRIPS.name(), UniqueLinesByTripIndex.class);
	}

	public Index<AbstractRegtoppTripIndexTIX> getTripIndex() {
		return getIndex(INDEX.TRIP_INDEX.name(), TripByIndexingKey.class);
	}

	public boolean hasTMSImporter() {
		return hasImporter(RegtoppRouteTMS.FILE_EXTENSION);
	}

	public boolean hasTIXImporter() {
		return hasImporter(RegtoppTripIndexTIX.FILE_EXTENSION);
	}

	public boolean hasLINImporter() {
		return hasImporter(RegtoppLineLIN.FILE_EXTENSION);
	}

	public boolean hasHPLImporter() {
		return hasImporter(RegtoppStopHPL.FILE_EXTENSION);
	}

	public boolean hasTDAImporter() {
		return hasImporter(RegtoppRouteTDA.FILE_EXTENSION);
	}

	private boolean hasImporter(final String pattern) {
		File folder = new File(path);
		String[] matchingFiles = folder.list(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.toUpperCase().endsWith("." + pattern);
			}
		});
		return matchingFiles.length == 1;
	}

	public Index<RegtoppFootnoteMRK> getFootnoteById() {
		return getIndex(INDEX.REMARK_BY_ID.name(), FootnoteById.class);
	}

	public Index<RegtoppDestinationDST> getDestinationById() {
		return getIndex(INDEX.DESTINATION_BY_ID.name(), DestinationById.class);
	}

	public Index<RegtoppDayCodeDKO> getDayCodeById() {
		return getIndex(INDEX.DAYCODE_BY_ID.name(), DaycodeById.class);
	}

	public Index<RegtoppLineLIN> getLineById() {
		return getIndex(INDEX.LINE_BY_ID.name(), LineById.class);

	}

	public Index<RegtoppRouteTMS> getRouteIndex() {
		return getIndex(INDEX.ROUTE_INDEX.name(), RouteByIndexingKey.class);
	}

	public Index<RegtoppRouteTMS> getRouteByRouteKey() {
		return getIndex(INDEX.ROUTE_BY_ROUTE_KEY.name(), RouteByRouteKey.class);
	}

	public Index<RegtoppRouteTDA> getRouteSegmentByLineNumber() {
		return getIndex(INDEX.ROUTE_BY_LINE_NUMBER.name(), RouteByLineNumber.class);
	}

}
