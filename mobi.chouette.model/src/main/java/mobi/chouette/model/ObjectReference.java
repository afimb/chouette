package mobi.chouette.model;

/**
 * Reference to a NeptuneIdentifiedObject to be used for loosely coupled relations, typically across db schemas.
 *
 * Used to delay loading of related object until actually needed.
 *
 * @param <T>
 */
public interface ObjectReference <T extends NeptuneIdentifiedObject> {

	/**
	 * The objectId of the referenced object.
	 * @return
	 */
	String getObjectId();

	/**
	 * The referenced object.
	 *
	 * NB! Might be null even though objectId is set.
	 * @return referenced object
	 */
	T getObject();


	/**
	 * Whether or not the referenced object is loaded or not.
	 */
	boolean isLoaded();
}
