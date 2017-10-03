package mobi.chouette.model;

/**
 * Object reference to a concrete object.
 *
 * @param <T>
 */
public class SimpleObjectReference<T extends NeptuneIdentifiedObject> implements ObjectReference<T> {

	private T object;

	public SimpleObjectReference(T object) {
		this.object = object;
	}

	@Override
	public String getObjectId() {
		if (object != null) {
			return object.getObjectId();
		}
		return null;
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
