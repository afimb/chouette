package mobi.chouette.exchange.regtopp.model.importer.parser;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.regtopp.model.RegtoppDayCodeDKO;
import mobi.chouette.exchange.regtopp.model.RegtoppDestinationDST;
import mobi.chouette.exchange.regtopp.model.RegtoppFootnoteMRK;
import mobi.chouette.exchange.regtopp.model.RegtoppObject;
import mobi.chouette.exchange.regtopp.model.RegtoppRouteTMS;
import mobi.chouette.exchange.regtopp.model.RegtoppStopHPL;
import mobi.chouette.exchange.regtopp.model.RegtoppTripIndexTIX;
import mobi.chouette.exchange.regtopp.model.importer.parser.RegtoppException.ERROR;
import mobi.chouette.exchange.regtopp.model.importer.parser.index.DaycodeById;
import mobi.chouette.exchange.regtopp.model.importer.parser.index.DestinationById;
import mobi.chouette.exchange.regtopp.model.importer.parser.index.FootnoteById;
import mobi.chouette.exchange.regtopp.model.importer.parser.index.Index;
import mobi.chouette.exchange.regtopp.model.importer.parser.index.IndexFactory;
import mobi.chouette.exchange.regtopp.model.importer.parser.index.RouteById;
import mobi.chouette.exchange.regtopp.model.importer.parser.index.StopById;
import mobi.chouette.exchange.regtopp.model.importer.parser.index.TripByIndexingKey;
import mobi.chouette.exchange.regtopp.model.importer.parser.index.UniqueLinesByTripIndex;
import mobi.chouette.exchange.regtopp.validation.RegtoppValidationReporter;

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
		LINE_BY_TRIPS, ROUTE_BY_ID
	}

	private String _path;
	private Map<String, Index<RegtoppObject>> _indexMap = new HashMap<String, Index<RegtoppObject>>();
	private Map<String, FileContentParser> _fileContentMap = new HashMap<String, FileContentParser>();
	private Map<String, ParseableFile> _fileMap = new HashMap<String, ParseableFile>();

	private RegtoppValidationReporter _validationReporter;
	private Context _context;

	public RegtoppImporter(Context context, String path, RegtoppValidationReporter validationReporter) {
		_path = path;
		_validationReporter = validationReporter;
		_context = context;
	}

	public void registerFileForIndex(String indexName, ParseableFile parseableFile) {
		_fileMap.put(indexName, parseableFile);
	}

	@SuppressWarnings("rawtypes")
	public void dispose() {
		for (Index importer : _indexMap.values()) {
			importer.dispose();
		}
		for (FileContentParser parser : _fileContentMap.values()) {
			parser.dispose();
		}

		_indexMap.clear();
		_indexMap = null;
		_fileContentMap.clear();
		_fileContentMap = null;

		_fileMap.clear();
		_fileMap = null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Index getIndex(String name, Class clazz) throws Exception {
		Index index = _indexMap.get(name);

		if (index == null) {
			try {
				ParseableFile parseableFile = _fileMap.get(name);
				if(parseableFile == null) {
					throw new RuntimeException("No such index "+name);
				}

				FileContentParser parser = _fileContentMap.get(parseableFile.getFile().getName());
				if (parser == null) {
					parser = new FileContentParser();
					_fileContentMap.put(parseableFile.getFile().getName(), parser);
					// Do actual parsing of file
					parser.parse(_context, parseableFile, _validationReporter);
				}

				index = IndexFactory.build(_validationReporter,parser, clazz.getName());
				_indexMap.put(name, index);
			} catch (ClassNotFoundException | IOException e) {
				FileParserValidationError context = new FileParserValidationError();
				context.put(FileParserValidationError.PATH, _path);
				context.put(FileParserValidationError.ERROR, ERROR.SYSTEM);
				throw new RegtoppException(context, e);
			}

		}
		return index;
	}

	@SuppressWarnings("unchecked")
	public Index<RegtoppStopHPL> getStopById() throws Exception {
		return getIndex(INDEX.STOP_BY_ID.name(), StopById.class);
	}

	public Index<RegtoppTripIndexTIX> getUniqueLinesByTripIndex() throws Exception {
		return getIndex(INDEX.LINE_BY_TRIPS.name(), UniqueLinesByTripIndex.class);
	}

	public Index<RegtoppTripIndexTIX> getTripIndex() throws Exception {
		return getIndex(INDEX.TRIP_INDEX.name(), TripByIndexingKey.class);
	}

	public boolean hasHPLImporter() {
		return hasImporter(RegtoppStopHPL.FILE_EXTENSION);
	}

	public boolean hasTIXImporter() {
		return hasImporter(RegtoppTripIndexTIX.FILE_EXTENSION);
	}

	private boolean hasImporter(final String pattern) {
		File folder = new File(_path);
		String[] matchingFiles = folder.list(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.toUpperCase().endsWith("."+pattern);
			}
		});
		return matchingFiles.length == 1;
	}

	public Index<RegtoppRouteTMS> getRouteById() throws Exception {
		return getIndex(INDEX.ROUTE_BY_ID.name(), RouteById.class);
	}

	public Index<RegtoppFootnoteMRK> getFootnoteById() throws Exception {
		return getIndex(INDEX.REMARK_BY_ID.name(), FootnoteById.class);
	}

	public Index<RegtoppDestinationDST> getDestinationById() throws Exception {
		return getIndex(INDEX.DESTINATION_BY_ID.name(), DestinationById.class);
	}

	public Index<RegtoppDayCodeDKO> getDayCodeById() throws Exception {
		return getIndex(INDEX.DAYCODE_BY_ID.name(), DaycodeById.class);
	}	

}
