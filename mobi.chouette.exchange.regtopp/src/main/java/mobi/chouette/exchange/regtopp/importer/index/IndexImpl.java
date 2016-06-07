package mobi.chouette.exchange.regtopp.importer.index;

import static mobi.chouette.common.Constant.PARSER;
import static mobi.chouette.exchange.regtopp.messages.RegtoppMessages.getMessage;

import java.util.*;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.regtopp.importer.RegtoppImporter;
import mobi.chouette.exchange.regtopp.importer.parser.FileContentParser;
import mobi.chouette.exchange.regtopp.importer.parser.FileParserValidationError;
import mobi.chouette.exchange.regtopp.importer.parser.ParseableFile;
import mobi.chouette.exchange.regtopp.model.RegtoppObject;
import mobi.chouette.exchange.regtopp.validation.Constant;
import mobi.chouette.exchange.regtopp.validation.RegtoppException;
import mobi.chouette.exchange.regtopp.validation.RegtoppValidationReporter;
import org.joda.time.Duration;

@Log4j
public abstract class IndexImpl<T> implements Index<T> {

	protected Map<String, T> index = new HashMap<String, T>();
	protected FileContentParser parser = null;
	protected RegtoppValidationReporter validationReporter = null;
	protected Context context = null;

	private boolean validated = false;
	
	private String filename = null;

	public IndexImpl(Context context, RegtoppValidationReporter validationReporter, FileContentParser parser) throws Exception {
		this.context = context;
		this.parser = parser;
		this.validationReporter = validationReporter;

		filename = parser.getParseableFile().getFile().getName().toUpperCase();

		
		index();
	}
	
	public String getUnderlyingFilename() {
		return filename;
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

			validationReporter.reportSuccess(context, mobi.chouette.exchange.regtopp.validation.Constant.REGTOPP_FILE_PREFIX + fileType + Constant.REGTOPP_FILE_POSTFIX, filename);

			if (getLength() == 0) {
				FileParserValidationError fileError = new FileParserValidationError(filename, 0, null, null, RegtoppException.ERROR.FILE_WITH_NO_ENTRY,
						getMessage("label.validation.emptyFile"));
				validationReporter.reportError(context, new RegtoppException(fileError), filename);
			}

			Set<String> invalidKeys = new HashSet<>();

			for (Map.Entry<String, T> entry : index.entrySet()) {
				T bean = entry.getValue();
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
						RegtoppObject regtoppObject = ((RegtoppObject) bean);
						if (regtoppObject.isInvalid()){
							log.warn("Removing " + regtoppObject.getClass() + " value with key '" + entry.getKey() + "' from index " + this.getClass() +
									" due to the following fatal error(s): " + getInvalidErrors(regtoppObject));
							invalidKeys.add(entry.getKey());
						}
						// TODO some indices returns List<?> as bean
						validationReporter.reportErrors(context, regtoppObject.getErrors(), filename);
						validationReporter.validate(context, ((RegtoppObject) bean).getOkTests());
					}
				}
			}
			removeKeys(invalidKeys);
		}
	}

	private String getInvalidErrors(RegtoppObject regtoppObject) {
		List<RegtoppException> invalidErrors = new ArrayList<>();
		for (RegtoppException regtoppException : regtoppObject.getErrors()) {
			if (regtoppException.isFatal()) {
				invalidErrors.add(regtoppException);
			}
		}
		return Arrays.toString(invalidErrors.toArray());
	}

	private void removeKeys(Set<String> invalidKeys) {
		for (String key : invalidKeys) {
			index.remove(key);
		}
	}

	protected boolean isNotNull(String string) {
		return !isNull(string);
	}
	
	protected boolean isNull(String string) {
		return string.matches("^[0]+$");
	}

	protected boolean isNotNull(Integer integer) {
		return integer != null;
	}

	protected boolean isNotNull(Duration duration) {
		return duration != null;
	}

}
