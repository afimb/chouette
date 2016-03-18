package mobi.chouette.exchange.gtfs.model.importer;

import java.util.Set;

public interface Index<T> extends Iterable<T> {

	void dispose();

	Iterable<String> keys();

	Iterable<T> values(String key);

	boolean containsKey(String key);

	T getValue(String key);

	boolean validate(T bean, GtfsImporter dao);

	int getLength();
	
	Set<GtfsException> getErrors();

	Set<GtfsException.ERROR> getOkTests();
	
	void setWithValidation(boolean withValidation);

	String getPath();

	Integer getIndex(String name);
}
