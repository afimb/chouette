package mobi.chouette.exchange.regtopp.importer.index;

import static mobi.chouette.common.Constant.PARSER;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.regtopp.importer.RegtoppImporter;
import mobi.chouette.exchange.regtopp.importer.parser.FileContentParser;
import mobi.chouette.exchange.regtopp.importer.parser.FileParserValidationError;
import mobi.chouette.exchange.regtopp.importer.parser.ParseableFile;
import mobi.chouette.exchange.regtopp.model.RegtoppObject;
import mobi.chouette.exchange.regtopp.validation.RegtoppException;
import mobi.chouette.exchange.regtopp.validation.RegtoppValidationReporter;

@Log4j
public abstract class IndexImpl<T> implements Index<T> {

	protected Map<String, T> index = new HashMap<String, T>();
	protected FileContentParser parser = null;
	protected RegtoppValidationReporter validationReporter = null;

	private boolean validated = false;

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
	public int getLength() {
		return index.size();
	}

	@Override
	public void validate(Context context, ParseableFile parseableFile) throws Exception {
		if (!validated) {
			validated = true;
			RegtoppImporter importer = (RegtoppImporter) context.get(PARSER);

			String filename = parseableFile.getFile().getName().toUpperCase();
			String fileType = filename.substring(filename.lastIndexOf(".") + 1);

			validationReporter.reportSuccess(context, mobi.chouette.exchange.regtopp.validation.Constant.REGTOPP_FILE_POSTFIX + fileType, filename);

			if (getLength() == 0) {
				FileParserValidationError fileError = new FileParserValidationError(filename, 0, null, null, RegtoppException.ERROR.FILE_WITH_NO_ENTRY,
						"Empty file");
				validationReporter.reportError(context, new RegtoppException(fileError), filename);
			}

			for (T bean : index.values()) {
				if (bean != null) {
					try {
						// Call index validator
						validate(bean, importer);
					} catch (Exception ex) {
						log.error(ex, ex);
						if (ex instanceof RegtoppException) {
							validationReporter.reportError(context, (RegtoppException) ex, filename);
						} else {
							validationReporter.throwUnknownError(context, ex, filename);
						}
					}
					if (bean instanceof RegtoppObject) {
						// TODO some indices returns List<?> as bean
						validationReporter.reportErrors(context, ((RegtoppObject) bean).getErrors(), filename);
						validationReporter.validate(context, filename, ((RegtoppObject) bean).getOkTests());
					}
				}
			}
		}
	}

}
