package mobi.chouette.exchange.regtopp.model.importer.parser.index;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.regtopp.model.importer.parser.FileContentParser;
import mobi.chouette.exchange.regtopp.model.importer.parser.RegtoppImporter;
import mobi.chouette.exchange.regtopp.validation.RegtoppValidationReporter;

@Log4j
public abstract class IndexImpl<T> implements Index<T> {

	Map<String, T> index = new HashMap<String, T>();
	FileContentParser parser = null;
	RegtoppValidationReporter validationReporter = null;

	public IndexImpl(RegtoppValidationReporter validationReporter, FileContentParser parser) throws Exception {
		this.parser = parser;
		this.validationReporter = validationReporter;
		index();
	}

	@Override
	public Iterator<T> iterator() {
		return (Iterator<T>) index.values().iterator();
	}

	@Override
	public void dispose() {
		index.clear();
		index = null;
		parser = null;

	}

	@Override
	public Iterator<String> keys() {
		return index.keySet().iterator();
	}

	@Override
	public Iterable<T> values(String key) {
		return (Iterable<T>) index.get(key);
	}

	@Override
	public boolean containsKey(String key) {
		return index.containsKey(key);
	}

	@Override
	public T getValue(String key) {
		return index.get(key);
	}

	@Override
	public boolean validate(T bean, RegtoppImporter dao) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getLength() {
		return index.size();
	}

}
