package mobi.chouette.exchange.regtopp.importer.index;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.regtopp.model.importer.parser.FileContentParser;
import mobi.chouette.exchange.regtopp.model.importer.parser.RegtoppImporter;
import mobi.chouette.exchange.regtopp.validation.RegtoppValidationReporter;

@Log4j
public abstract class IndexImpl<T> implements Index<T> {

	protected Map<String, T> index = new HashMap<String, T>();
	protected FileContentParser parser = null;
	protected RegtoppValidationReporter validationReporter = null;

	public IndexImpl(RegtoppValidationReporter validationReporter, FileContentParser parser) throws Exception {
		this.parser = parser;
		this.validationReporter = validationReporter;
		index();
	}

	@Override
	public Iterator<T> iterator() {
		return index.values().iterator();
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

	protected boolean isEmpty(String value)
	{
		return value == null || value.trim().length() == 0;
	}

	protected boolean isPresent(String value)
	{
		return !isEmpty(value);
	}

}
