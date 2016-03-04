package mobi.chouette.exchange.regtopp.model.importer.index;

import java.io.IOException;

import mobi.chouette.exchange.regtopp.model.importer.RegtoppImporter;

public interface Index<T> extends Iterable<T> {

	void dispose();

	Iterable<String> keys();

	Iterable<T> values(String key);

	boolean containsKey(String key);

	T getValue(String key);

	boolean validate(T bean, RegtoppImporter dao);

	int getLength();

	void index() throws IOException;
	
}
