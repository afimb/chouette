package mobi.chouette.exchange.regtopp.importer.parser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
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
import org.joda.time.Duration;
import org.joda.time.LocalDate;

import lombok.Getter;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.regtopp.beanio.DepartureTimeTypeHandler;
import mobi.chouette.exchange.regtopp.beanio.DrivingDurationTypeHandler;
import mobi.chouette.exchange.regtopp.beanio.LocalDateTypeHandler;
import mobi.chouette.exchange.regtopp.model.RegtoppObject;
import mobi.chouette.exchange.regtopp.validation.RegtoppException;
import mobi.chouette.exchange.regtopp.validation.RegtoppValidationReporter;
import mobi.chouette.exchange.report.FileError;
import mobi.chouette.exchange.report.FileError.CODE;
import mobi.chouette.exchange.report.FileInfo.FILE_STATE;

@Log4j
public class FileContentParser {
	public static final String REGTOPP_CHARSET = "ISO-8859-1";
	@Getter
	private List<Object> rawContent = new ArrayList<>();
	
	@Getter
	private ParseableFile parseableFile = null;

	public FileContentParser(ParseableFile parseableFile) {
		super();
		this.parseableFile = parseableFile;
	}

	public void parse(final Context context, final RegtoppValidationReporter validationReporter) throws Exception {
		StreamFactory factory = StreamFactory.newInstance();

		StreamBuilder builder = new StreamBuilder("regtopp");
		builder.format("fixedlength");
		builder.parser(new FixedLengthParserBuilder());
		builder.readOnly();

		builder.addTypeHandler("departureTime", Duration.class, new DepartureTimeTypeHandler());
		builder.addTypeHandler("drivingDuration", Duration.class, new DrivingDurationTypeHandler());
		builder.addTypeHandler("localDate", LocalDate.class, new LocalDateTypeHandler());

		for (Class<?> clazz : parseableFile.getRegtoppClasses()) {
			builder = builder.addRecord(clazz);
		}

		factory.define(builder);

		FileInputStream is = new FileInputStream(parseableFile.getFile());
		InputStreamReader isr = new InputStreamReader(is, REGTOPP_CHARSET);
		BufferedReader buffReader = new BufferedReader(isr);

		// TODO consider using error reporter instead if this continues parsing of the file
		BeanReader in = factory.createReader("regtopp", buffReader);

		final Set<RegtoppException> errors = new HashSet<RegtoppException>();
		final String fileName = parseableFile.getFile().getName();

		// TODO http://beanio.org/2.1/docs/reference/index.html#StreamValidation
		// Add custom validation error messages

		in.setErrorHandler(new BeanReaderErrorHandlerSupport() {
			@Override
			public void invalidRecord(InvalidRecordException ex) throws Exception {
				// if a bean object is mapped to a record group,
				// the exception may contain more than one record
				for (int i = 0, j = ex.getRecordCount(); i < j; i++) {
					RecordContext rContext = ex.getRecordContext(i);
					if (rContext.hasRecordErrors()) {
						for (String error : rContext.getRecordErrors()) {

							// TODO report this in a better fashion
							FileParserValidationError ctx = new FileParserValidationError(fileName, rContext.getLineNumber(), rContext.getRecordName(),
									rContext.getRecordText(), parseableFile.getInvalidFieldValue(), error);
							RegtoppException e = new RegtoppException(ctx, ex);
							errors.add(e);
							log.warn("Field error parsing record " + rContext.getRecordName() + " in file " + fileName + " at line " + rContext.getLineNumber() + ":" + error);
						}
					}
					if (rContext.hasFieldErrors()) {
						for (String field : rContext.getFieldErrors().keySet()) {
							for (String error : rContext.getFieldErrors(field)) {

								// TODO report this in a better fashion
								FileParserValidationError ctx = new FileParserValidationError(fileName, rContext.getLineNumber(), field,
										rContext.getFieldText(field), parseableFile.getInvalidFieldValue(), error);
								RegtoppException e = new RegtoppException(ctx, ex);
								errors.add(e);
								log.warn("Field error parsing field " + field + " in file " + fileName + " at line " + rContext.getLineNumber() + ":" + error);
							}
						}
					}
				}
				parseableFile.getFileInfo().addError(new FileError(CODE.INVALID_FORMAT, ex.getMessage()));

			}
		});

		Object record = null;

		try {
			while ((record = in.read()) != null) {
				((RegtoppObject) record).setRecordLineNumber(in.getLineNumber());
				rawContent.add(record);
			}
			log.info("Parsed file OK: " + parseableFile.getFile().getName());
			parseableFile.getFileInfo().setStatus(FILE_STATE.OK);
		} catch (InvalidRecordException ex) {
			log.error(ex);
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
