package mobi.chouette.exchange.regtopp.model.importer.parser.index;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.regtopp.model.RegtoppStopHPL;
import mobi.chouette.exchange.regtopp.model.importer.parser.FileContentParser;
import mobi.chouette.exchange.regtopp.model.importer.parser.RegtoppImporter;
import mobi.chouette.exchange.regtopp.validation.RegtoppValidationReporter;

@Log4j
public abstract class IndexImpl<T> implements Index<T> {

	Map<String,T> _index = new HashMap<String,T>();
	FileContentParser _parser = null;
	RegtoppValidationReporter _validationReporter = null;

	public IndexImpl(RegtoppValidationReporter validationReporter,FileContentParser fileParser) throws Exception {
		_parser = fileParser;
		_validationReporter = validationReporter;
		index();
	}

	@Override
	public Iterator<T> iterator() {
		return (Iterator<T>) _index.values().iterator();
	}

	@Override
	public void dispose() {
		_index.clear();
		_index = null;
		_parser = null;
		
	}

	@Override
	public Iterator<String> keys() {
		return _index.keySet().iterator();
	}

	@Override
	public Iterable<T> values(String key) {
		return (Iterable<T>) _index.get(key);
	}

	@Override
	public boolean containsKey(String key) {
		return _index.containsKey(key);
	}

	@Override
	public T getValue(String key) {
		return _index.get(key);
}

	@Override
	public boolean validate(T bean, RegtoppImporter dao) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getLength() {
		return _index.size();
	}

	

}
