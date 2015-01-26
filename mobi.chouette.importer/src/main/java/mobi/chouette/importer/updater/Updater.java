package mobi.chouette.importer.updater;

public interface Updater<T> {

	void update(T oldValue, T newValue) throws Exception;

}
