package mobi.chouette.exchange.regtopp.importer.parser;

import static mobi.chouette.exchange.regtopp.messages.RegtoppMessages.getMessage;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.beanio.BeanReader;
import org.beanio.BeanReaderErrorHandlerSupport;
import org.beanio.BeanReaderException;
import org.beanio.InvalidRecordException;
import org.beanio.MalformedRecordException;
import org.beanio.RecordContext;
import org.beanio.StreamFactory;
import org.beanio.UnexpectedRecordException;
import org.beanio.UnidentifiedRecordException;
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
import mobi.chouette.exchange.report.FileInfo.FILE_STATE;

@Log4j
public class FileContentParser<T> {
	public static final String REGTOPP_CHARSET = "ISO-8859-1";
	@Getter
	private List<Object> rawContent = new ArrayList<>();

	@Getter
	private ParseableFile<T> parseableFile = null;

	public FileContentParser(ParseableFile<T> parseableFile) {
		super();
		this.parseableFile = parseableFile;
	}

	public void parse(final Context context, final RegtoppValidationReporter validationReporter) throws Exception {
		log.info(this.hashCode() +" Starting to parse "+parseableFile);
		StreamFactory factory = StreamFactory.newInstance();

		String streamName = "regtopp-"+UUID.randomUUID().toString();
		StreamBuilder builder = new StreamBuilder(streamName);
		builder.resourceBundle("mobi.chouette.exchange.regtopp.customMessages");
		builder.format("fixedlength");
		builder.parser(new FixedLengthParserBuilder());
		builder.readOnly();

		builder.addTypeHandler("departureTime", Duration.class, new DepartureTimeTypeHandler());
		builder.addTypeHandler("drivingDuration", Duration.class, new DrivingDurationTypeHandler());
		builder.addTypeHandler("localDate", LocalDate.class, new LocalDateTypeHandler());

		for (Class<T> clazz : parseableFile.getRegtoppClasses()) {
			builder = builder.addRecord(clazz);
		}

		factory.define(builder);

		FileInputStream is = new FileInputStream(parseableFile.getFile());
		InputStreamReader isr = new InputStreamReader(is, REGTOPP_CHARSET);
		BufferedReader buffReader = new BufferedReader(isr);

		BeanReader in = factory.createReader(streamName, buffReader);

		final Set<RegtoppException> errors = new HashSet<RegtoppException>();
		final String fileName = parseableFile.getFile().getName();

		in.setErrorHandler(new BeanReaderErrorHandlerSupport() {
			@Override
			public void invalidRecord(InvalidRecordException ex) throws Exception {
				// if a bean object is mapped to a record group,
				// the exception may contain more than one record
				for (int i = 0, j = ex.getRecordCount(); i < j; i++) {
					RecordContext rContext = ex.getRecordContext(i);
					if (rContext.hasRecordErrors()) {
						for (String error : rContext.getRecordErrors()) {
							String key = "label." + rContext.getRecordName();
							String recordNameLabel = getMessage(key);
							if (recordNameLabel == null) {
								log.warn("Could not look up key '" + key + "', falling back to record name '" + rContext.getRecordName() + "'");
								recordNameLabel = rContext.getRecordName();
							}

							// TODO report this in a better fashion
							FileParserValidationError ctx = new FileParserValidationError(fileName, rContext.getLineNumber(), recordNameLabel,
									rContext.getRecordText(), parseableFile.getInvalidFieldValue(), error);
							RegtoppException e = new RegtoppException(ctx, ex);
							errors.add(e);
							log.warn(this.hashCode()+ " Field error parsing record " + recordNameLabel + " in file " + fileName + " at line " + rContext.getLineNumber() + ":"
									+ error);
						}
					}
					if (rContext.hasFieldErrors()) {
						for (String field : rContext.getFieldErrors().keySet()) {
							for (String error : rContext.getFieldErrors(field)) {
								String key = "label." + rContext.getRecordName() + "." + field;
								String fieldLabel = getMessage(key);
								if (fieldLabel == null) {
									log.warn("Could not look up key '" + key + "', falling back to field name '" + field + "'");
									fieldLabel = field;
								}

								// TODO report this in a better fashion
								FileParserValidationError ctx = new FileParserValidationError(fileName, rContext.getLineNumber(), fieldLabel,
										rContext.getFieldText(field), parseableFile.getInvalidFieldValue(), error);
								RegtoppException e = new RegtoppException(ctx, ex);
								errors.add(e);
								log.warn("Field error parsing field '" + fieldLabel + "' in file " + fileName + " at line " + rContext.getLineNumber() + ":"
										+ error);
							}
						}
					}
				}
			}

			// TODO Does not seem like this ever occurs
			@Override
			public void unexpectedRecord(UnexpectedRecordException ex) throws Exception {
				log.warn("Got UnexpectedRecordException.", ex);
			}

			@Override
			public void unidentifiedRecord(UnidentifiedRecordException ex) throws Exception {
				log.warn("Got UnidentifiedRecordException.", ex);
			}

			@Override
			public void malformedRecord(MalformedRecordException ex) throws Exception {
				log.warn("Got MalformedRecordException.", ex);
			}

			@Override
			public void fatalError(BeanReaderException ex) throws Exception {
				log.error("Got BeanReaderException.", ex);
			}

		});

		Object record;
		try {
			while ((record = in.read()) != null) {
				((RegtoppObject) record).setRecordLineNumber(in.getLineNumber());
				rawContent.add(record);
			}
			log.info(this.hashCode() +" Parsed file OK: " + parseableFile);
			parseableFile.getFileInfo().setStatus(FILE_STATE.OK);
		} catch (RuntimeException ex) {
			log.error("Unexpected error while parsing", ex);
		} finally {
			in.close();
		}
		if (errors.size() > 0) {
			validationReporter.reportErrors(context, errors, fileName);
		}
		log.info(this.hashCode() +" Finished parsing "+parseableFile);

	}

	public void dispose() {
		rawContent.clear();
	}

	@Override
	public String toString() {
		return "FileContentParser [parseableFile=" + parseableFile + "]";
	}

}
