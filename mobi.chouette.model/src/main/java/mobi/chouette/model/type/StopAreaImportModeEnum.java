package mobi.chouette.model.type;

/**
 * Types of behaviour for handling stop areas when importing data.
 */
public enum StopAreaImportModeEnum {
    /**
     * Read only. No changes to stop area repository are persisted during import.
     */
    READ_ONLY,
    /**
     * Create unknown stop areas referenced in import, but never update existing stop areas.
     */
    CREATE_NEW,
    /**
     * Create unknown stop areas and updated existing stop areas referenced in import
     */
    CREATE_OR_UPDATE;

    public boolean shouldCreateMissingStopAreas(){
        return !this.equals(READ_ONLY);
    }

    public boolean shouldUpdateStopAreas(){
        return this.equals(CREATE_OR_UPDATE);
    }
}
