package mobi.chouette.exchange.regtopp;

public interface Constant extends mobi.chouette.common.Constant {
	public static final String REGTOPP_REPORTER = "regtopp_reporter";
	
	// Mandatory files Regtopp 1.1D
	public static final String REGTOPP_TRIPINDEX_FILE = ".*\\.TIX";
	public static final String REGTOPP_TRIPDATA_FILE = ".*\\.TMS";
	public static final String REGTOPP_STOPPLACE_FILE = ".*\\.HPL";
	public static final String REGTOPP_DAYCODE_FILE = ".*\\.DKO";
	
	
    // Optional files Regtopp 1.1D
	public static final String REGTOPP_DESTINATION_FILE = "*.DST";
	public static final String REGTOPP_REMARKS_FILE = "*.MRK";
	public static final String REGTOPP_PATHWAY_FILE = "*.GAV";
	public static final String REGTOPP_INTERCHANGE_FILE = "*.SAM";
	public static final String REGTOPP_ZONE_FILE = "*.SON";
	public static final String REGTOPP_LINE_FILE = "*.LIN";
	public static final String REGTOPP_VEHICHLE_JOURNEY_FILE = "*.VLP";

	
	public static final String GTFS_AGENCY_FILE = "agency.txt";
	public static final String GTFS_STOPS_FILE =   "stops.txt";
	public static final String GTFS_ROUTES_FILE =   "routes.txt";
	public static final String GTFS_SHAPES_FILE =   "shapes.txt";
	public static final String GTFS_TRIPS_FILE =   "trips.txt";
	public static final String GTFS_STOP_TIMES_FILE =   "stop_times.txt";
	public static final String GTFS_CALENDAR_FILE =   "calendar.txt";
	public static final String GTFS_CALENDAR_DATES_FILE =   "calendar_dates.txt";
	public static final String GTFS_FREQUENCIES_FILE =   "frequencies.txt";
	public static final String GTFS_TRANSFERS_FILE =   "transfers.txt";
	

}
