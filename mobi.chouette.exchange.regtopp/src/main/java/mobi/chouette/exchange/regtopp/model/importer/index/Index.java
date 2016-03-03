package mobi.chouette.exchange.regtopp.model.importer.index;

import java.util.Set;

import mobi.chouette.exchange.regtopp.model.importer.RegtoppException;
import mobi.chouette.exchange.regtopp.model.importer.RegtoppImporter;
import mobi.chouette.exchange.regtopp.model.importer.RegtoppException.ERROR;

public interface Index<T> extends Iterable<T> {

	void dispose();

	Iterable<String> keys();

	Iterable<T> values(String key);

	boolean containsKey(String key);

	T getValue(String key);

	boolean validate(T bean, RegtoppImporter dao);

	int getLength();
	
	Set<RegtoppException> getErrors();

	Set<RegtoppException.ERROR> getOkTests();
	
	void setWithValidation(boolean withValidation);
}
