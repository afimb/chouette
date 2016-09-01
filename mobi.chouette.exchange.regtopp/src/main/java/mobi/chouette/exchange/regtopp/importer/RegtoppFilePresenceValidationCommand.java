package mobi.chouette.exchange.regtopp.importer;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.naming.InitialContext;

import org.apache.commons.lang.StringUtils;

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
import mobi.chouette.exchange.regtopp.importer.version.Regtopp11DVersionHandler;
import mobi.chouette.exchange.regtopp.importer.version.Regtopp12NovusVersionHandler;
import mobi.chouette.exchange.regtopp.importer.version.Regtopp12VersionHandler;
import mobi.chouette.exchange.regtopp.importer.version.Regtopp13AVersionHandler;
import mobi.chouette.exchange.regtopp.importer.version.RegtoppVersion;
import mobi.chouette.exchange.regtopp.importer.version.VersionHandler;
import mobi.chouette.exchange.regtopp.validation.RegtoppException;
import mobi.chouette.exchange.report.ActionError;
import mobi.chouette.exchange.report.ActionError.CODE;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.FileError;
import mobi.chouette.exchange.report.FileInfo;
import mobi.chouette.exchange.report.FileInfo.FILE_STATE;

@Log4j
public class RegtoppFilePresenceValidationCommand implements Command {

	public static final String COMMAND = "RegtoppFilePresenceValidationCommand";

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;

		Monitor monitor = MonitorFactory.start(COMMAND);

		ActionReport report = (ActionReport) context.get(REPORT);

		JobData jobData = (JobData) context.get(JOB_DATA);
		// check ignored files

		RegtoppImportParameters parameters = (RegtoppImportParameters) context.get(CONFIGURATION);

		Path path = Paths.get(jobData.getPathName(), INPUT);
		RegtoppParameterGuesser guesser = new RegtoppParameterGuesser(path);

		RegtoppVersion declaredVersion = parameters.getVersion();
		RegtoppVersion detectedVersion = guesser.getDetectedVersion();
		RegtoppVersion parserVersion = null;

		String declaredEncoding = parameters.getCharsetEncoding();
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
					FileInfo file = new FileInfo(name, FILE_STATE.IGNORED);
					report.getFiles().add(file);
				} else {

					String prefix = name.substring(0, name.lastIndexOf("."));
					String extension = name.substring(name.lastIndexOf(".") + 1);

					if (!validExtensions.contains(extension)) {
						// Ignore unknown files
						FileInfo file = new FileInfo(name, FILE_STATE.IGNORED);
						report.getFiles().add(file);
					} else {
						// Valid file, add check that we do not include more than one admin code
						prefixesFound.add(prefix);
						foundExtensions.add(extension);

						// Add the file with status ERROR, parser will update to OK when parsed OK
						FileInfo file = new FileInfo(name, FILE_STATE.ERROR);
						report.getFiles().add(file);

						// Register file for parsing and necessary indexes

						versionHandler.registerFileForIndex(importer, fileName, extension, file);
					}
				}
			}

			if (prefixesFound.size() > 1) {
				// Multiple prefixes found, should all be the same (technically it is allowed, but too complicated for now)
				ActionError error = new ActionError(CODE.INVALID_DATA,
						"Multiple companies or versions found in zip file: " + StringUtils.join(prefixesFound, " "));
				report.setFailure(error);
			} else {
				if (!foundExtensions.containsAll(mandatoryFileExtensions)) {
					// Check that all 4 mandatory files found
					// Convert to set

					String prefix = prefixesFound.iterator().next();

					Set<String> missingFiles = new HashSet<String>();
					missingFiles.addAll(mandatoryFileExtensions);
					missingFiles.removeAll(foundExtensions);

					for (String missingExtension : missingFiles) {
						FileInfo fileInfo = new FileInfo(prefix + "." + missingExtension, FILE_STATE.ERROR,
								Arrays.asList(new FileError(FileError.CODE.FILE_NOT_FOUND, "Mandatory file missing")));
						report.getFiles().add(fileInfo);
					}

				} else {
					result = SUCCESS;
				}
			}
		} catch (RegtoppException e) {
			// log.error(e,e);
			if (e.getError().equals(RegtoppException.ERROR.SYSTEM))
				throw e;
			else
				report.setFailure(new ActionError(ActionError.CODE.INVALID_DATA, e.getError().name()));

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
