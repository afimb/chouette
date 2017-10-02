package mobi.chouette.model;

/**
 * Object reference to a concrete object.
 *
 * @param <T>
 */
public class SimpleObjectReference<T extends NeptuneIdentifiedObject> implements ObjectReference<T> {

	private String objectId;

	private T object;

	public SimpleObjectReference(T object) {
		this.object = object;
		if (object != null) {
			objectId = object.getObjectId();
		}
	}

	@Override
	public String getObjectId() {
		return objectId;
	}

	@Override
	public T getObject() {
		return object;
	}

	@Override
	public boolean isLoaded() {
		return true;
	}

}
