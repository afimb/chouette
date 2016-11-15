package mobi.chouette.common;

public interface PropertyNames {
    public static final String ROOT_DIRECTORY = ".directory";
    public static final String ADMIN_KEY = ".admin.key";
    public static final String MAX_STARTED_JOBS = ".started.jobs.max";
    public static final String MAX_COPY_BY_JOB = ".copy.by.import.max";

    /** Update stop place registry when importing */
    public static final String STOP_PLACE_REGISTER_UPDATE = ".stop.place.register.update";

    /** URL to the NeTEx endpoint accepting publication deliveries */
    public static final String STOP_PLACE_REGISTER_URL = ".stop.place.register.url";

    public static final String RESCHEDULE_INTERRUPTED_JOBS = ".reschedule.interrupted.jobs";
}
