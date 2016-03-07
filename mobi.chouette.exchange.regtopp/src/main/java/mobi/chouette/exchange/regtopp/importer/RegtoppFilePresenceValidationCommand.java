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
import mobi.chouette.exchange.regtopp.Constant;
import mobi.chouette.exchange.regtopp.model.RegtoppDayCodeDKO;
import mobi.chouette.exchange.regtopp.model.RegtoppDayCodeHeaderDKO;
import mobi.chouette.exchange.regtopp.model.RegtoppDestinationDST;
import mobi.chouette.exchange.regtopp.model.RegtoppInterchangeSAM;
import mobi.chouette.exchange.regtopp.model.RegtoppLineLIN;
import mobi.chouette.exchange.regtopp.model.RegtoppPathwayGAV;
import mobi.chouette.exchange.regtopp.model.RegtoppPeriodPER;
import mobi.chouette.exchange.regtopp.model.RegtoppFootnoteMRK;
import mobi.chouette.exchange.regtopp.model.RegtoppRoutePointRUT;
import mobi.chouette.exchange.regtopp.model.RegtoppStopHPL;
import mobi.chouette.exchange.regtopp.model.RegtoppTableVersionTAB;
import mobi.chouette.exchange.regtopp.model.RegtoppTripIndexTIX;
import mobi.chouette.exchange.regtopp.model.RegtoppRouteTMS;
import mobi.chouette.exchange.regtopp.model.RegtoppVehicleJourneyVLP;
import mobi.chouette.exchange.regtopp.model.RegtoppZoneSON;
import mobi.chouette.exchange.regtopp.model.importer.parser.ParseableFile;
import mobi.chouette.exchange.regtopp.model.importer.parser.RegtoppException;
import mobi.chouette.exchange.regtopp.model.importer.parser.RegtoppImporter;
import mobi.chouette.exchange.regtopp.model.importer.parser.filevalidator.DefaultEmptyFileContentValidator;
import mobi.chouette.exchange.regtopp.model.importer.parser.filevalidator.TripIndexValidator;
import mobi.chouette.exchange.regtopp.validation.RegtoppValidationReporter;
import mobi.chouette.exchange.report.ActionError;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.FileError;
import mobi.chouette.exchange.report.FileInfo;
import mobi.chouette.exchange.report.ActionError.CODE;
import mobi.chouette.exchange.report.FileInfo.FILE_STATE;

@Log4j
public class RegtoppFilePresenceValidationCommand implements Command, Constant {

	public static final String COMMAND = "RegtoppFilePresenceValidationCommand";

	private static final List<String> mandatoryFileExtensions = Arrays.asList(RegtoppTripIndexTIX.FILE_EXTENSION, RegtoppRouteTMS.FILE_EXTENSION,
			RegtoppStopHPL.FILE_EXTENSION, RegtoppDayCodeDKO.FILE_EXTENSION);

	private static final List<String> optionalFileExtensions = Arrays.asList(RegtoppDestinationDST.FILE_EXTENSION, RegtoppFootnoteMRK.FILE_EXTENSION,
			RegtoppPathwayGAV.FILE_EXTENSION, RegtoppInterchangeSAM.FILE_EXTENSION, RegtoppZoneSON.FILE_EXTENSION, RegtoppLineLIN.FILE_EXTENSION,
			RegtoppVehicleJourneyVLP.FILE_EXTENSION, RegtoppTableVersionTAB.FILE_EXTENSION, RegtoppPeriodPER.FILE_EXTENSION,
			RegtoppRoutePointRUT.FILE_EXTENSION);

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;

		Monitor monitor = MonitorFactory.start(COMMAND);

		ActionReport report = (ActionReport) context.get(REPORT);

		JobData jobData = (JobData) context.get(JOB_DATA);
		// check ignored files

		RegtoppImportParameters parameters = (RegtoppImportParameters) context.get(CONFIGURATION);
		// TODO read ie version from here

		RegtoppValidationReporter validationReporter = (RegtoppValidationReporter) context.get(REGTOPP_REPORTER);

		RegtoppImporter importer = (RegtoppImporter) context.get(PARSER);

		Set<String> validExtensions = new HashSet<String>();
		validExtensions.addAll(mandatoryFileExtensions);
		validExtensions.addAll(optionalFileExtensions);

		Set<String> prefixesFound = new HashSet<String>();
		Set<String> foundExtensions = new HashSet<String>();

		// TODO read FORMPAR.FRM to detect which version of Regtopp being used.
		// Currently 1.2 supported
		try {
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
						if ("TIX".equals(extension)) {
							ParseableFile parseableFile = new ParseableFile(fileName.toFile(), Arrays.asList(new Class[] { RegtoppTripIndexTIX.class }), file);
							importer.registerFileForIndex(RegtoppImporter.INDEX.TRIP_INDEX.name(), parseableFile, new TripIndexValidator());
							importer.registerFileForIndex(RegtoppImporter.INDEX.LINE_BY_TRIPS.name(), parseableFile, new TripIndexValidator());
						} else if ("TMS".equals(extension)) {
							ParseableFile parseableFile = new ParseableFile(fileName.toFile(), Arrays.asList(new Class[] { RegtoppRouteTMS.class }),
									file);
							importer.registerFileForIndex(RegtoppImporter.INDEX.ROUTE_BY_ID.name(), parseableFile, new DefaultEmptyFileContentValidator());
						} else if ("HPL".equals(extension)) {
							ParseableFile parseableFile = new ParseableFile(fileName.toFile(), Arrays.asList(new Class[] { RegtoppStopHPL.class }), file);
							importer.registerFileForIndex(RegtoppImporter.INDEX.STOP_BY_ID.name(), parseableFile, new DefaultEmptyFileContentValidator());
						} else if ("DKO".equals(extension)) {
							ParseableFile parseableFile = new ParseableFile(fileName.toFile(),
									Arrays.asList(new Class[] { RegtoppDayCodeHeaderDKO.class, RegtoppDayCodeDKO.class }), file);
							importer.registerFileForIndex(RegtoppImporter.INDEX.DAYCODE_BY_ID.name(), parseableFile, new DefaultEmptyFileContentValidator());
						} else if ("DST".equals(extension)) {
							ParseableFile parseableFile = new ParseableFile(fileName.toFile(), Arrays.asList(new Class[] { RegtoppDestinationDST.class }),
									file);
							importer.registerFileForIndex(RegtoppImporter.INDEX.DESTINATION_BY_ID.name(), parseableFile, new DefaultEmptyFileContentValidator());
						} else if ("MRK".equals(extension)) {
							ParseableFile parseableFile = new ParseableFile(fileName.toFile(), Arrays.asList(new Class[] { RegtoppFootnoteMRK.class }), file);
							importer.registerFileForIndex(RegtoppImporter.INDEX.REMARK_BY_ID.name(), parseableFile, new DefaultEmptyFileContentValidator());
						} else if ("GAV".equals(extension)) {
							ParseableFile parseableFile = new ParseableFile(fileName.toFile(), Arrays.asList(new Class[] { RegtoppPathwayGAV.class }), file);
							importer.registerFileForIndex(RegtoppImporter.INDEX.PATHWAY_FROM_STOP_ID.name(), parseableFile, new DefaultEmptyFileContentValidator());
						} else if ("SAM".equals(extension)) {
							ParseableFile parseableFile = new ParseableFile(fileName.toFile(), Arrays.asList(new Class[] { RegtoppPathwayGAV.class }), file);
							importer.registerFileForIndex(RegtoppImporter.INDEX.INTERCHANGE.name(), parseableFile, new DefaultEmptyFileContentValidator());
						} else if ("SON".equals(extension)) {
							ParseableFile parseableFile = new ParseableFile(fileName.toFile(), Arrays.asList(new Class[] { RegtoppZoneSON.class }), file);
							importer.registerFileForIndex(RegtoppImporter.INDEX.ZONE_BY_ID.name(), parseableFile, new DefaultEmptyFileContentValidator());
						} else if ("LIN".equals(extension)) {
							ParseableFile parseableFile = new ParseableFile(fileName.toFile(), Arrays.asList(new Class[] { RegtoppLineLIN.class }), file);
							importer.registerFileForIndex(RegtoppImporter.INDEX.LINE_BY_ID.name(), parseableFile, new DefaultEmptyFileContentValidator());
						} else if ("VLP".equals(extension)) {
							ParseableFile parseableFile = new ParseableFile(fileName.toFile(), Arrays.asList(new Class[] { RegtoppVehicleJourneyVLP.class }),
									file);
							importer.registerFileForIndex(RegtoppImporter.INDEX.VEHICLE_JOURNEY.name(), parseableFile, new DefaultEmptyFileContentValidator());
						} else if ("TAB".equals(extension)) {
							ParseableFile parseableFile = new ParseableFile(fileName.toFile(), Arrays.asList(new Class[] { RegtoppTableVersionTAB.class }),
									file);
							importer.registerFileForIndex(RegtoppImporter.INDEX.TABLE_VERSION.name(), parseableFile, new DefaultEmptyFileContentValidator());
						} else if ("RUT".equals(extension)) {
							ParseableFile parseableFile = new ParseableFile(fileName.toFile(), Arrays.asList(new Class[] { RegtoppRoutePointRUT.class }), file);
							importer.registerFileForIndex(RegtoppImporter.INDEX.ROUTE_POINT.name(), parseableFile, new DefaultEmptyFileContentValidator());
						}
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
								Arrays.asList(new FileError(FileError.CODE.FILE_NOT_FOUND, null)));
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
