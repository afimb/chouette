package mobi.chouette.exchange.regtopp.importer;

import static mobi.chouette.exchange.regtopp.RegtoppConstant.REGTOPP_REPORTER;

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
import mobi.chouette.exchange.regtopp.importer.version.Regtopp12NovusVersionHandler;
import mobi.chouette.exchange.regtopp.importer.version.Regtopp12VersionHandler;
import mobi.chouette.exchange.regtopp.importer.version.VersionHandler;
import mobi.chouette.exchange.regtopp.model.importer.parser.RegtoppException;
import mobi.chouette.exchange.regtopp.model.importer.parser.RegtoppImporter;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppDayCodeDKO;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppDestinationDST;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppFootnoteMRK;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppInterchangeSAM;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppLineLIN;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppPathwayGAV;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppPeriodPER;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppRoutePointRUT;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppRouteTMS;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppStopHPL;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppTableVersionTAB;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppTripIndexTIX;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppVehicleJourneyVLP;
import mobi.chouette.exchange.regtopp.model.v12.RegtoppZoneSON;
import mobi.chouette.exchange.regtopp.validation.RegtoppValidationReporter;
import mobi.chouette.exchange.report.ActionError;
import mobi.chouette.exchange.report.ActionError.CODE;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.FileError;
import mobi.chouette.exchange.report.FileInfo;
import mobi.chouette.exchange.report.FileInfo.FILE_STATE;

@Log4j
public class RegtoppFilePresenceValidationCommand implements Command {

	public static final String COMMAND = "RegtoppFilePresenceValidationCommand";

	// TODO move to version handler
	private static final List<String> mandatoryFileExtensions = Arrays.asList(RegtoppTripIndexTIX.FILE_EXTENSION, RegtoppRouteTMS.FILE_EXTENSION,
			RegtoppStopHPL.FILE_EXTENSION, RegtoppDayCodeDKO.FILE_EXTENSION);

	// TODO move to version handler
	private static final List<String> optionalFileExtensions = Arrays.asList(RegtoppDestinationDST.FILE_EXTENSION, RegtoppFootnoteMRK.FILE_EXTENSION,
			RegtoppPathwayGAV.FILE_EXTENSION, RegtoppInterchangeSAM.FILE_EXTENSION, RegtoppZoneSON.FILE_EXTENSION, RegtoppLineLIN.FILE_EXTENSION,
			RegtoppVehicleJourneyVLP.FILE_EXTENSION, RegtoppTableVersionTAB.FILE_EXTENSION, RegtoppPeriodPER.FILE_EXTENSION,
			RegtoppRoutePointRUT.FILE_EXTENSION);

	private static final List<String> supportedVersions = Arrays.asList("1.2", "1.2Novus");

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;

		Monitor monitor = MonitorFactory.start(COMMAND);

		ActionReport report = (ActionReport) context.get(REPORT);

		JobData jobData = (JobData) context.get(JOB_DATA);
		// check ignored files

		RegtoppImportParameters parameters = (RegtoppImportParameters) context.get(CONFIGURATION);
		String declaredVersion = parameters.getVersion();
		if (!supportedVersions.contains(declaredVersion)) {
			ActionError error = new ActionError(CODE.INVALID_PARAMETERS,
					"Unsupported Regtopp version declared: " + declaredVersion + ". Supported versions are " + StringUtils.join(supportedVersions, " "));
			report.setFailure(error);
		} else {

			VersionHandler versionHandler = null;
			switch(declaredVersion) {
			case "1.2":
				versionHandler = new Regtopp12VersionHandler();
				break;
			case "1.2Novus":
				versionHandler = new Regtopp12NovusVersionHandler();
				break;
			}
			
			context.put(RegtoppConstant.VERSION_HANDLER, versionHandler);
			
			RegtoppValidationReporter validationReporter = (RegtoppValidationReporter) context.get(REGTOPP_REPORTER);

			// TODO read FORMPAR.FRM to detect which version of Regtopp being used.
			// Currently 1.2 supported
			try {
				RegtoppImporter importer = (RegtoppImporter) context.get(PARSER);

				Set<String> validExtensions = new HashSet<String>();
				validExtensions.addAll(mandatoryFileExtensions);
				validExtensions.addAll(optionalFileExtensions);

				Set<String> prefixesFound = new HashSet<String>();
				Set<String> foundExtensions = new HashSet<String>();

				Path path = Paths.get(jobData.getPathName(), INPUT);
				List<Path> list = FileUtil.listFiles(path, "*");

				for (Path fileName : list) {

					String name = fileName.getFileName().toString().toUpperCase();

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
				if (e instanceof RuntimeException)
					log.error(e, e);
				throw e;
			} finally {
				log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
			}

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
