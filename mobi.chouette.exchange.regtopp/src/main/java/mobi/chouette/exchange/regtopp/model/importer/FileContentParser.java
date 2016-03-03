package mobi.chouette.exchange.regtopp.model.importer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.beanio.BeanReader;
import org.beanio.BeanReaderErrorHandlerSupport;
import org.beanio.InvalidRecordException;
import org.beanio.RecordContext;
import org.beanio.StreamFactory;
import org.beanio.builder.FixedLengthParserBuilder;
import org.beanio.builder.StreamBuilder;

import lombok.Getter;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.regtopp.model.RegtoppObject;
import mobi.chouette.exchange.regtopp.model.importer.RegtoppException.ERROR;
import mobi.chouette.exchange.regtopp.validation.ValidationReporter;
import mobi.chouette.exchange.report.FileInfo.FILE_STATE;

@Log4j
public class FileContentParser {
	@Getter
	private List<Object> rawContent = new ArrayList<>();

	public void parse(final Context context, final ParseableFile parseableFile, final ValidationReporter validationReporter) throws Exception {
		StreamFactory factory = StreamFactory.newInstance();

		StreamBuilder builder = new StreamBuilder("regtopp").format("fixedlength").parser(new FixedLengthParserBuilder());

		for (Class<?> clazz : parseableFile.getRegtoppClasses()) {
			builder = builder.addRecord(clazz);

		}

		factory.define(builder);

		// TODO consider using error reporter instead if this continues parsing of the file
		BeanReader in = factory.createReader("regtopp", parseableFile.getFile());

		final Set<RegtoppException> errors = new HashSet<RegtoppException>();
		final String fileName = parseableFile.getFile().getName();

		in.setErrorHandler(new BeanReaderErrorHandlerSupport() {
			public void invalidRecord(InvalidRecordException ex) throws Exception {
				// if a bean object is mapped to a record group,
				// the exception may contain more than one record
				for (int i = 0, j = ex.getRecordCount(); i < j; i++) {
					RecordContext rContext = ex.getRecordContext(i);
					if (rContext.hasRecordErrors()) {
						for (String error : rContext.getRecordErrors()) {
							mobi.chouette.exchange.regtopp.model.importer.Context ctx = new mobi.chouette.exchange.regtopp.model.importer.Context(fileName,
									rContext.getLineNumber(), rContext.getRecordName(), rContext.getRecordText(), ERROR.INVALID_FIELD_VALUE, error);
							RegtoppException e = new RegtoppException(ctx, ex);
							errors.add(e);
						}
					}
					if (rContext.hasFieldErrors()) {
						for (String field : rContext.getFieldErrors().keySet()) {
							for (String error : rContext.getFieldErrors(field)) {

								mobi.chouette.exchange.regtopp.model.importer.Context ctx = new mobi.chouette.exchange.regtopp.model.importer.Context(fileName,
										rContext.getLineNumber(), field, rContext.getFieldText(field), ERROR.INVALID_FIELD_VALUE, error);
								RegtoppException e = new RegtoppException(ctx, ex);
								errors.add(e);
							}
						}
					}
				}
			}
		});

		Object record = null;

		try {
			while ((record = (RegtoppObject) in.read()) != null) {
				rawContent.add(record);
			}
			log.info("Parsed file OK: " + parseableFile.getFile().getName());
			parseableFile.getFileInfo().setStatus(FILE_STATE.OK);
		} catch (InvalidRecordException ex) {
		} finally {
			in.close();
		}
		if (errors.size() > 0) {
			validationReporter.reportErrors(context, errors, fileName);
		}

	}

	public void dispose() {
		rawContent.clear();
	}
}
