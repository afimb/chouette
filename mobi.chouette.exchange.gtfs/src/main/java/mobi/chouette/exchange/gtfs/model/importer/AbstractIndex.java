package mobi.chouette.exchange.gtfs.model.importer;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public abstract class AbstractIndex<T> implements Index<T> {

	public static final String IDS = "ids";

	protected abstract void initialize() throws IOException;

	protected abstract Set<String> getFieldIds();

	protected abstract void index() throws IOException;

	// protected abstract ByteBuffer getBuffer(String id, Context context);
	//
	// protected abstract ByteBuffer getBuffer(Token id, Context context);

	protected abstract T build(GtfsIterator reader, Context context);
	
	protected Set<GtfsException> _errors = new HashSet<>();
	
	@Getter
	protected Set<GtfsException> errors = new GtfsExceptionsHashSet<>();
	
	@Getter
	protected Set<GtfsException.ERROR> okTests = new GtfsErrorsHashSet<>();
	
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
