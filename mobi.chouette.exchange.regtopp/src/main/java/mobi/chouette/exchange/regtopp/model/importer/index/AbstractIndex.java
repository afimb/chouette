package mobi.chouette.exchange.regtopp.model.importer.index;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import mobi.chouette.exchange.regtopp.model.importer.RegtoppErrorsHashSet;
import mobi.chouette.exchange.regtopp.model.importer.RegtoppException;
import mobi.chouette.exchange.regtopp.model.importer.RegtoppExceptionsHashSet;

public abstract class AbstractIndex<T> implements Index<T> {

	public static final String IDS = "ids";

	protected abstract void index() throws IOException;

	protected Set<RegtoppException> _errors = new HashSet<>();
	
	@Getter
	protected Set<RegtoppException> errors = new RegtoppExceptionsHashSet<>();
	
	@Getter
	protected Set<RegtoppException.ERROR> okTests = new RegtoppErrorsHashSet<>();
	
	@Setter
	protected boolean withValidation = false;

	@Override
	public void dispose() {
		_errors.clear();
		errors.clear();
		okTests.clear();
		_errors = null;
		errors = null;
		okTests = null;
	}

}
