package mobi.chouette.exchange.regtopp.importer.index;

import java.util.Iterator;

import mobi.chouette.exchange.regtopp.importer.RegtoppImporter;

public interface Index<T> extends Iterable<T> {

	void dispose();

	Iterator<String> keys();

	Iterable<T> values(String key);

	boolean containsKey(String key);

	T getValue(String key);

	// Validate logical consistency (references to other files etc)
	boolean validate(T bean, RegtoppImporter dao);

	int getLength();

	void index() throws Exception;

}
