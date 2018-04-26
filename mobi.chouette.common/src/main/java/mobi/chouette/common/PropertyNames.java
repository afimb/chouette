package mobi.chouette.common;

public interface PropertyNames {
    public static final String ROOT_DIRECTORY = ".directory";
    public static final String ADMIN_KEY = ".admin.key";
    public static final String MAX_STARTED_JOBS = ".started.jobs.max";
	public static final String JOB_SHCEDULE_INTERVAL_MS = ".jobs.schedule.interval.ms";
    public static final String MAX_STARTED_TRANSFER_JOBS = ".started.transfer.jobs.max";
    public static final String MAX_COPY_BY_JOB = ".copy.by.import.max";

    public static final String RESCHEDULE_INTERRUPTED_JOBS = ".reschedule.interrupted.jobs";

    /** Whether ids are mapped using external stop place registry during import.
     * Disabling this will cause no ids to be mapped, regardless of input param. */
    public static final String STOP_PLACE_ID_MAPPING = ".stop.place.id.mapping";

    String REFERENTIAL_LOCK_MANAGER_IMPLEMENTATION = ".referential.lock.manager.impl";
    String KUBERNETES_ENABLED = ".kubernetes.enabled";
	String FILE_STORE_IMPLEMENTATION = ".file.store.impl";
    String GTFS_AGENCY_URL_DEFAULTS = "iev.gtfs.agency.url.defaults";
    String GTFS_AGENCY_PHONE_DEFAULTS = "iev.gtfs.agency.phone.defaults";
    String OSRM_ROUTE_SECTIONS_BASE = "iev.osrm.endpoint.";
}