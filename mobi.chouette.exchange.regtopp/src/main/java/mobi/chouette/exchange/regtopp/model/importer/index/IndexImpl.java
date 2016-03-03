package mobi.chouette.exchange.regtopp.model.importer.index;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.beanio.BeanReader;
import org.beanio.InvalidRecordException;
import org.beanio.RecordContext;
import org.beanio.StreamFactory;
import org.beanio.builder.FixedLengthParserBuilder;
import org.beanio.builder.StreamBuilder;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.regtopp.model.RegtoppObject;
import mobi.chouette.exchange.regtopp.model.RegtoppStopHPL;
import mobi.chouette.exchange.regtopp.model.importer.FileContentParser;
import mobi.chouette.exchange.regtopp.model.importer.RegtoppException;
import mobi.chouette.exchange.regtopp.model.importer.RegtoppImporter;
import mobi.chouette.exchange.regtopp.model.importer.RegtoppException.ERROR;

@Log4j
public abstract class IndexImpl<T> extends AbstractIndex<T> {
	// Cache class for entries read
	
	private int _total;
	private boolean _unique;
	private String _path;

	Map<String,T> _index = new HashMap<String,T>();
	FileContentParser _parser = null;

	public IndexImpl(FileContentParser fileParser) throws IOException {
		_parser = fileParser;

		index();
	}

	public boolean validate(RegtoppStopHPL bean, RegtoppImporter dao) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterator<T> iterator() {
		return (Iterator<T>) _index.values().iterator();
	}

	@Override
	public void dispose() {
		_index.clear();
		
	}

	@Override
	public Iterable<String> keys() {
		return (Iterable<String>) _index.keySet().iterator();
	}

	@Override
	public Iterable<T> values(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean containsKey(String key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public T getValue(String key) {
		// TODO Auto-generated method stub
		return null;
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

	@Override
	public Set<RegtoppException> getErrors() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<ERROR> getOkTests() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setWithValidation(boolean withValidation) {
		// TODO Auto-generated method stub
		
	}

}
