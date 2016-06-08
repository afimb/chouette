package mobi.chouette.exchange.regtopp.importer.index;

import java.util.Iterator;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.regtopp.importer.RegtoppImporter;
import mobi.chouette.exchange.regtopp.importer.parser.ParseableFile;

public interface Index<T> extends Iterable<T> {

	void dispose();

	Iterator<String> keys();

	Iterable<T> values(String key);

	boolean containsKey(String key);

	T getValue(String key);

	// Validate logical consistency (references to other files etc)
	boolean validate(T bean, RegtoppImporter importer) throws Exception;
	
	void validate(Context context,ParseableFile<T> parseableFile) throws Exception;

	int getLength();

	void index() throws Exception;

}
