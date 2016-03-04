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
import mobi.chouette.exchange.regtopp.model.importer.index.Index;
import mobi.chouette.exchange.regtopp.model.importer.index.IndexFactory;
import mobi.chouette.exchange.regtopp.model.importer.index.StopById;
import mobi.chouette.exchange.regtopp.model.importer.index.TripIndex;
import mobi.chouette.exchange.regtopp.validation.ValidationReporter;

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
		ROUTE_POINT
	}

	private String _path;
	private Map<String, Index<RegtoppObject>> _indexMap = new HashMap<String, Index<RegtoppObject>>();
	private Map<String, FileContentParser> _fileContentMap = new HashMap<String, FileContentParser>();
	private ValidationReporter _validationReporter;

	private Map<String, ParseableFile> _fileMap = new HashMap<String, ParseableFile>();
	private Context _context;

	public RegtoppImporter(Context context, String path, ValidationReporter validationReporter) {
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

				FileContentParser parser = _fileContentMap.get(parseableFile.getFile().getName());
				if (parser == null) {
					parser = new FileContentParser();
					_fileContentMap.put(parseableFile.getFile().getName(), parser);
					// Do actual parsing of file
					parser.parse(_context, parseableFile, _validationReporter);
				}

				index = IndexFactory.build(parser, clazz.getName());
				_indexMap.put(name, index);
			} catch (ClassNotFoundException | IOException e) {
				mobi.chouette.exchange.regtopp.model.importer.FileParserValidationContext context = new mobi.chouette.exchange.regtopp.model.importer.FileParserValidationContext();
				context.put(mobi.chouette.exchange.regtopp.model.importer.FileParserValidationContext.PATH, _path);
				context.put(mobi.chouette.exchange.regtopp.model.importer.FileParserValidationContext.ERROR, ERROR.SYSTEM);
				throw new RegtoppException(context, e);
			}

		}
		return index;
	}

	// public boolean hasAgencyImporter() {
	// return hasImporter(AgencyById.FILENAME);
	// }
	//
	// public boolean hasCalendarImporter() {
	// return hasImporter(CalendarByService.FILENAME);
	// }
	//
	// public boolean hasCalendarDateImporter() {
	// return hasImporter(CalendarDateByService.FILENAME);
	// }
	//
	// public boolean hasFrequencyImporter() {
	// return hasImporter(FrequencyByTrip.FILENAME);
	// }

	public boolean hasStopImporter() {
		return hasImporter(StopById.FILETYPE);
	}

	private boolean hasImporter(final String pattern) {
		File folder = new File(_path);
		String[] matchingFiles = folder.list(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.toUpperCase().endsWith(pattern);
			}
		});
		return matchingFiles.length == 1;
	}

	// @SuppressWarnings("unchecked")
	// public Index<GtfsAgency> getAgencyById() {
	// return getImporter(INDEX.AGENCY_BY_ID.name(), AgencyById.FILENAME,
	// AgencyById.class);
	// }
	//
	// @SuppressWarnings("unchecked")
	// public Index<GtfsCalendar> getCalendarByService() {
	// return getImporter(INDEX.CALENDAR_BY_SERVICE.name(),
	// CalendarByService.FILENAME, CalendarByService.class);
	// }
	//
	// @SuppressWarnings("unchecked")
	// public Index<GtfsCalendarDate> getCalendarDateByService() {
	// return getImporter(INDEX.CALENDAR_DATE_BY_SERVICE.name(),
	// CalendarDateByService.FILENAME, CalendarDateByService.class);
	// }
	//
	// @SuppressWarnings("unchecked")
	// public Index<GtfsFrequency> getFrequencyByTrip() {
	// return getImporter(INDEX.FREQUENCY_BY_TRIP.name(),
	// FrequencyByTrip.FILENAME, FrequencyByTrip.class);
	// }

	@SuppressWarnings("unchecked")
	public Index<RegtoppStopHPL> getStopById() throws Exception {
		return getIndex(INDEX.STOP_BY_ID.name(), StopById.class);
	}

	public Index<RegtoppTripIndexTIX> getTripIndex() throws Exception {
		return getIndex(INDEX.TRIP_INDEX.name(), TripIndex.class);
	}
}
