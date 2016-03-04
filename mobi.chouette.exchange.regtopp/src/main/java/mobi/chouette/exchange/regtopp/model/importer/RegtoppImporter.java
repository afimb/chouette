package mobi.chouette.exchange.regtopp.model.importer;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.regtopp.model.RegtoppObject;
import mobi.chouette.exchange.regtopp.model.RegtoppStopHPL;
import mobi.chouette.exchange.regtopp.model.RegtoppTripIndexTIX;
import mobi.chouette.exchange.regtopp.model.importer.RegtoppException.ERROR;
import mobi.chouette.exchange.regtopp.model.importer.filevalidator.FileContentValidator;
import mobi.chouette.exchange.regtopp.model.importer.index.Index;
import mobi.chouette.exchange.regtopp.model.importer.index.IndexFactory;
import mobi.chouette.exchange.regtopp.model.importer.index.StopById;
import mobi.chouette.exchange.regtopp.model.importer.index.UniqueLinesByTripIndex;
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
		LINE_BY_TRIPS
	}

	private String _path;
	private Map<String, Index<RegtoppObject>> _indexMap = new HashMap<String, Index<RegtoppObject>>();
	private Map<String, FileContentParser> _fileContentMap = new HashMap<String, FileContentParser>();
	private Map<String, ParseableFile> _fileMap = new HashMap<String, ParseableFile>();
	private Map<String, FileContentValidator> _validatorMap = new HashMap<String, FileContentValidator>();

	private RegtoppValidationReporter _validationReporter;
	private Context _context;

	public RegtoppImporter(Context context, String path, RegtoppValidationReporter validationReporter) {
		_path = path;
		_validationReporter = validationReporter;
		_context = context;
	}

	public void registerFileForIndex(String indexName, ParseableFile parseableFile, FileContentValidator validator) {
		_fileMap.put(indexName, parseableFile);
		_validatorMap.put(parseableFile.getFile().getName(), validator);
		
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
					FileContentValidator validator = _validatorMap.get(parseableFile.getFile().getName());
					validator.validate(_context,parser);
				}

				index = IndexFactory.build(parser, clazz.getName());
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

	public boolean hasHPLImporter() {
		return hasImporter(RegtoppStopHPL.FILE_EXTENSION);
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

	@SuppressWarnings("unchecked")
	public Index<RegtoppStopHPL> getStopById() throws Exception {
		return getIndex(INDEX.STOP_BY_ID.name(), StopById.class);
	}

	public Index<RegtoppTripIndexTIX> getUniqueLinesByTripIndex() throws Exception {
		return getIndex(INDEX.LINE_BY_TRIPS.name(), UniqueLinesByTripIndex.class);
	}
}
