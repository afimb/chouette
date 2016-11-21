package mobi.chouette.exchange.regtopp.importer;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.FileUtil;
import mobi.chouette.common.JobData;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.regtopp.RegtoppConstant;
import mobi.chouette.exchange.regtopp.importer.version.*;
import mobi.chouette.exchange.regtopp.validation.RegtoppException;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.IO_TYPE;
import org.apache.commons.lang.StringUtils;

import javax.naming.InitialContext;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static mobi.chouette.exchange.report.ActionReporter.FILE_STATE.IGNORED;

@Log4j
public class RegtoppFilePresenceValidationCommand implements Command {

	public static final String COMMAND = "RegtoppFilePresenceValidationCommand";

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;

		Monitor monitor = MonitorFactory.start(COMMAND);

		ActionReporter actionReporter = ActionReporter.Factory.getInstance();

		JobData jobData = (JobData) context.get(JOB_DATA);
		// check ignored files

		RegtoppImportParameters parameters = (RegtoppImportParameters) context.get(CONFIGURATION);

		Path path = Paths.get(jobData.getPathName(), INPUT);
		RegtoppParameterGuesser guesser = new RegtoppParameterGuesser(path);

		RegtoppVersion declaredVersion = parameters.getVersion();
		RegtoppVersion detectedVersion = guesser.getDetectedVersion();
		RegtoppVersion parserVersion = null;

		String declaredEncoding = StringUtils.trimToNull(parameters.getCharsetEncoding());
		String detectedEncoding = guesser.getEncoding();
		String parserEncoding = null;
		
		// Validate encoding
		if(declaredEncoding != null) {
			if(declaredEncoding != detectedEncoding) {
				log.warn("Declared regtopp encoding is " + declaredEncoding + ", but detected encoding is " + detectedEncoding + ". Using declaredEncoding for parsing");
			}
			parserEncoding = declaredEncoding;
		} else 	if(declaredEncoding == null) {
			parserEncoding = detectedEncoding;
		}
		
		if(parserEncoding == null) {
			log.error("Unable to detect regtopp encoding and declaredEncoding is null. Aborting import");
			return ERROR;
		}
		
		context.put(RegtoppConstant.CHARSET,parserEncoding);

		// Validate version
		if(declaredVersion != null) {
			if(declaredVersion != detectedVersion) {
				log.warn("Declared regtopp version is " + declaredVersion + ", but detected version is " + detectedVersion + ". Using declaredVersion for parsing");
			}
			parserVersion = declaredVersion;
		} else 	if(declaredVersion == null) {
			parserVersion = detectedVersion;
			log.info("Detected regtopp version is " + detectedVersion + ".");
		}
		
		if(parserVersion == null) {
			log.error("Unable to detect regtopp version and declaredVersion is null. Aborting import");
			return ERROR;
		}
		
		log.info("Parsing Regtopp file=" + jobData.getInputFilename() + " referential=" + jobData.getReferential() + " declaredVersion=" + declaredVersion
				+ " detectedVersion=" + detectedVersion+ " declaredEncoding=" + declaredEncoding
				+ " detectedEncoding=" + detectedEncoding+  " calendarStrategy="+parameters.getCalendarStrategy());
		VersionHandler versionHandler = null;
		switch (parserVersion) {
		case R11D:
			versionHandler = new Regtopp11DVersionHandler();
			break;
		case R12:
			versionHandler = new Regtopp12VersionHandler();
			break;
		case R12N:
			versionHandler = new Regtopp12NovusVersionHandler();
			break;
		case R13A:
			versionHandler = new Regtopp13AVersionHandler();
			break;
		}

		context.put(RegtoppConstant.VERSION_HANDLER, versionHandler);

		try {
			RegtoppImporter importer = (RegtoppImporter) context.get(PARSER);

			Set<String> validExtensions = new HashSet<String>();
			Set<String> mandatoryFileExtensions = new HashSet<String>();
			Set<String> optionalFileExtensions = new HashSet<String>();

			mandatoryFileExtensions.addAll(Arrays.asList(versionHandler.getMandatoryFileExtensions()));
			optionalFileExtensions.addAll(Arrays.asList(versionHandler.getOptionalFileExtensions()));

			validExtensions.addAll(mandatoryFileExtensions);
			validExtensions.addAll(optionalFileExtensions);

			Set<String> prefixesFound = new HashSet<String>();
			Set<String> foundExtensions = new HashSet<String>();

			List<Path> list = FileUtil.listFiles(path, "*");

			for (Path fileName : list) {

				String name = fileName.getFileName().toString().toUpperCase();

				if (name == null) {
					log.warn("Got a file with missing name. Unable to report error properly.");
					continue;
				}

				if (!name.matches("R[0-9]{4}\\.[A-Z]{3}")) {
					actionReporter.addFileReport(context, name, IO_TYPE.INPUT);
					actionReporter.setFileState(context, name, IO_TYPE.INPUT, IGNORED);
				} else {
					String prefix = name.substring(0, name.lastIndexOf("."));
					String extension = name.substring(name.lastIndexOf(".") + 1);

					if (!validExtensions.contains(extension)) {
						// Ignore unknown files
						actionReporter.addFileReport(context, name, IO_TYPE.INPUT);
						actionReporter.setFileState(context, name, IO_TYPE.INPUT, IGNORED);
					} else {
						// Valid file, add check that we do not include more than one admin code
						prefixesFound.add(prefix);
						foundExtensions.add(extension);

						// Add the file with status ERROR, parser will update to OK when parsed OK
						actionReporter.addFileReport(context, name, IO_TYPE.INPUT);
						actionReporter.setFileState(context, name, IO_TYPE.INPUT, ActionReporter.FILE_STATE.ERROR);

						// Register file for parsing and necessary indexes

						versionHandler.registerFileForIndex(importer, fileName, extension);
					}
				}
			}

			if (prefixesFound.size() > 1) {
				// Multiple prefixes found, should all be the same (technically it is allowed, but too complicated for now)
				actionReporter.addFileReport(context, jobData.getInputFilename(), IO_TYPE.INPUT);
				actionReporter.addFileErrorInReport(context, jobData.getInputFilename(), ActionReporter.FILE_ERROR_CODE.INVALID_FORMAT, "Multiple companies or versions found in zip file: " + StringUtils.join(prefixesFound, " "));
			} else {
				if (!foundExtensions.containsAll(mandatoryFileExtensions)) {
					// Check that all 4 mandatory files found
					// Convert to set

					String prefix = prefixesFound.iterator().next();

					Set<String> missingFiles = new HashSet<String>();
					missingFiles.addAll(mandatoryFileExtensions);
					missingFiles.removeAll(foundExtensions);

					for (String missingExtension : missingFiles) {
						actionReporter.addFileReport(context, prefix + "." + missingExtension, IO_TYPE.INPUT);
						actionReporter.addFileErrorInReport(context, prefix + "." + missingExtension, ActionReporter.FILE_ERROR_CODE.INVALID_FORMAT, "Mandatory file missing");
					}

				} else {
					result = SUCCESS;
				}
			}
		} catch (RegtoppException e) {
			// log.error(e,e);
			if (e.getError().equals(RegtoppException.ERROR.SYSTEM)) {
				throw e;
			} else {
				actionReporter.addFileReport(context, jobData.getInputFilename(), IO_TYPE.INPUT);
				actionReporter.addFileErrorInReport(context, jobData.getInputFilename(), ActionReporter.FILE_ERROR_CODE.INVALID_FORMAT, e.getError().name());
			}
		} catch (Exception e) {
			log.error(e, e);
			throw e;
		} finally {
			log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		}

		return result;
	}
	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = new RegtoppFilePresenceValidationCommand();
			return result;
		}
	}

	static {
		CommandFactory.factories.put(RegtoppFilePresenceValidationCommand.class.getName(), new DefaultCommandFactory());
	}
}
