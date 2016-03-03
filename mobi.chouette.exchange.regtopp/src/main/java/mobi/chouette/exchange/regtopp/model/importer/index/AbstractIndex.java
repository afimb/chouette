package mobi.chouette.exchange.regtopp.model.importer.index;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import mobi.chouette.exchange.regtopp.model.importer.RegtoppErrorsHashSet;
import mobi.chouette.exchange.regtopp.model.importer.RegtoppException;
import mobi.chouette.exchange.regtopp.model.importer.RegtoppExceptionsHashSet;
import mobi.chouette.exchange.regtopp.model.importer.RegtoppException.ERROR;

public abstract class AbstractIndex<T> implements Index<T> {

	public static final String IDS = "ids";

	protected abstract void index() throws IOException;

	// protected abstract ByteBuffer getBuffer(String id, Context context);
	//
	// protected abstract ByteBuffer getBuffer(Token id, Context context);

	protected Set<RegtoppException> _errors = new HashSet<>();
	
	@Getter
	protected Set<RegtoppException> errors = new RegtoppExceptionsHashSet<>();
	
	@Getter
	protected Set<RegtoppException.ERROR> okTests = new RegtoppErrorsHashSet<>();
	
	@Setter
	protected boolean withValidation = false;

	@ToString
	class Token {
		int offset = -1;
		int lenght = 0;
	}
	
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
