package mobi.chouette.common;

public interface PropertyNames {
    public static final String ROOT_DIRECTORY = ".directory";
    public static final String ADMIN_KEY = ".admin.key";
    public static final String MAX_STARTED_JOBS = ".started.jobs.max";
    public static final String MAX_COPY_BY_JOB = ".copy.by.import.max";

    public static final String RESCHEDULE_INTERRUPTED_JOBS = ".reschedule.interrupted.jobs";

    /** Whether ids are mapped using external stop place registry during import.
     * Disabling this will cause no ids to be mapped, regardless of input param. */
    public static final String STOP_PLACE_ID_MAPPING = ".stop.place.id.mapping";
}
