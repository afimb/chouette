package mobi.chouette.command;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.naming.InitialContext;

import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.JSONUtil;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.CommandLineProcessingCommands;
import mobi.chouette.exchange.CommandLineProcessingCommandsFactory;
import mobi.chouette.exchange.exporter.CompressCommand;
import mobi.chouette.exchange.gtfs.exporter.GtfsExportParameters;
import mobi.chouette.exchange.gtfs.importer.GtfsImportParameters;
import mobi.chouette.exchange.hub.exporter.HubExportParameters;
import mobi.chouette.exchange.importer.UncompressCommand;
import mobi.chouette.exchange.kml.exporter.KmlExportParameters;
import mobi.chouette.exchange.neptune.exporter.NeptuneExportParameters;
import mobi.chouette.exchange.neptune.importer.NeptuneImportParameters;
import mobi.chouette.exchange.netex.exporter.NetexExportParameters;
import mobi.chouette.exchange.netex.importer.NetexImportParameters;
import mobi.chouette.exchange.parameters.AbstractParameter;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.validation.ImportedLineValidatorCommand;
import mobi.chouette.exchange.validation.SharedDataValidatorCommand;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.model.Line;
import mobi.chouette.model.util.Referential;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

public class CommandManager implements Constant {

	private String[] args;

	private Options options = new Options();

	private String inputParametersFilename;

	private String outputParametersFilename;

	private String inputFileName;

	private String validationParametersFilename;

	private String outputFileName;

	private CommandJobData inputData;

	private CommandJobData outputData;

	private ValidationParameters validationParameters;

	private Context importContext;

	private Context exportContext;
	
	private String workingDirectory = "./work";

	public CommandManager(String[] args) {
		this.args = args;
		options.addOption("h", "help", false, "show help");
		options.addOption("d", "dir", true, "working directory (default = ./work)");
		options.addOption("i", "import", true, "import parameters (json)");
		options.addOption("e", "export", true, "export parameters  (json)");
		options.addOption("v", "validate", true, "validation parameters (json)");
		options.addOption("f", "file", true, "export file ");
	}

	public void parseArgs() {
		CommandLineParser parser = new BasicParser();
		CommandLine cmd = null;

		try {
			cmd = parser.parse(options, args);
			if (cmd.hasOption("h"))
				help();

			if (cmd.hasOption("i")) {
				inputParametersFilename = cmd.getOptionValue("i");
			} else {
				System.err.println("missing -i inputParameters.json ");
				help();
			}
			if (cmd.hasOption("v")) {
				validationParametersFilename = cmd.getOptionValue("v");
			}
			if (cmd.hasOption("d")) {
				workingDirectory = cmd.getOptionValue("d");
			}
			if (cmd.hasOption("e")) {
				outputParametersFilename = cmd.getOptionValue("e");
				if (cmd.hasOption("f")) {
					outputFileName = cmd.getOptionValue("f");
				} else {
					System.out.println("missing -f exportFile");
					help();
				}
			} else if (cmd.hasOption("f")) {
				System.err.println("unexpected -f option without -e option");
				help();
			}
			if (cmd.getArgList().size() == 1) {
				inputFileName = cmd.getArgList().get(0).toString();
			} else {
				System.err.println("missing inputFile ");
				help();
			}

		} catch (ParseException e) {
			System.err.println("Invalid syntax " + e.getMessage());
			help();
		}
		return;
	}

	public void process() throws Exception {
		
		InitialContext initContext = new InitialContext();
		
		Command lineValidationCommand = CommandFactory.create(initContext, ImportedLineValidatorCommand.class.getName());
		Command sharedValidationCommand = CommandFactory.create(initContext, SharedDataValidatorCommand.class.getName());
		inputData = loadInputParameters();
		if (inputData == null)
			return; // invalid data

		if (withExport()) {
			outputData = loadOutputParameters();
			if (outputData == null)
				return; // invalid data
		}

		if (withValidation()) {
			validationParameters = loadValidationParameters();
			if (validationParameters == null)
				return; // invalid data
		}


		CommandLineProcessingCommands importProcessingCommands = null;
		CommandLineProcessingCommands exportProcessingCommands = null;
		importProcessingCommands = CommandLineProcessingCommandsFactory
				.create(buildCommandProcessingClassName(inputData));

		importContext = prepareImportContext();
		if (withExport()) {
			exportProcessingCommands = CommandLineProcessingCommandsFactory
					.create(buildCommandProcessingClassName(outputData));
			exportContext = prepareExportContext();
		}

		if (Files.exists(Paths.get(workingDirectory)))
			FileUtils.deleteDirectory(new File(workingDirectory));
		Files.createDirectories(Paths.get(workingDirectory));
		FileUtils.copyFileToDirectory(new File(inputFileName), new File(inputData.getPathName()));

		// initialize process
		// uncompress
		boolean result = SUCCESS;
		Command command = CommandFactory.create(initContext, UncompressCommand.class.getName());
		result = command.execute(importContext);
		if (!result) {
			System.err.println("fail to uncompress input file ; see import report for details ");
			return;
		}

		// input pre processing

		for (Command importCommand : importProcessingCommands.getPreProcessingCommands(importContext)) {
			result = importCommand.execute(importContext);
			if (!result) {
				System.err.println("fail to execute import command " + importCommand.getClass().getSimpleName()
						+ "; see import report for details ");
				return;
			}
		}

		// output pre processing
		if (withExport()) {
			for (Command exportCommand : exportProcessingCommands.getPreProcessingCommands(exportContext)) {
				result = exportCommand.execute(exportContext);
				if (!result) {
					System.err.println("fail to execute " + exportCommand.getClass().getSimpleName()
							+ "; see export report for details ");
					return;
				}
			}

		}

		// input & validation& output processing
		long id = 0;
		boolean exportFailed = false;
		for (Command importCommand : importProcessingCommands.getLineProcessingCommands(importContext)) {
			result = importCommand.execute(importContext);
			if (!result) {
				System.err.println("fail to execute " + importCommand.getClass().getName()
						+ "; see import report for details ");
				continue;
			}
			// execute line validation
			if (withValidation()) {
				lineValidationCommand.execute(importContext);
			}

			// execute export validation commands
			if (withExport()) {
				// - get line in import context
				Referential referential = (Referential) importContext.get(REFERENTIAL);
				if (referential.getLines().isEmpty())
					continue;
				Line line = referential.getLines().values().iterator().next();
				// some export uses Id as file name
				line.setId(++id);
				// - put line in export context
				exportContext.put(LINE, line);
				// execute commands
				for (Command exportCommand : exportProcessingCommands.getLineProcessingCommands(exportContext)) {
					result = exportCommand.execute(exportContext);
					if (!result) {
						exportFailed = true;
						System.err.println("fail to execute " + exportCommand.getClass().getName()
								+ "; see export report for details ");
						break;
					}
				}
			}
		}

		// input post processing
		for (Command importCommand : importProcessingCommands.getPostProcessingCommands(importContext)) {
			result = importCommand.execute(importContext);
			if (!result) {
				System.err.println("fail to execute " + importCommand.getClass().getName()
						+ "; see import report for details ");
				return;
			}
		}

		// validation post processing
		if (withValidation()) {
			sharedValidationCommand.execute(importContext);
		}

		// output post processing
		if (withExport() && !exportFailed) {
			for (Command exportCommand : exportProcessingCommands.getPostProcessingCommands(exportContext)) {
				result = exportCommand.execute(exportContext);
				if (!result) {
					System.err.println("fail to execute " + exportCommand.getClass().getName()
							+ "; see export report for details ");
					return;
				}
			}
			// compress result
			command = CommandFactory.create(initContext, CompressCommand.class.getName());
			result = command.execute(exportContext);
			if (!result) {
				System.err.println("fail to compress output file ; see export report for details ");
				return;
			}
			// transfer result
			FileUtils.copyFile(new File(outputData.getPathName(),outputData.getFilename()), new File(outputFileName));
		}

		return;
	}

	private Context prepareImportContext() {
		Context context = new Context();
		context.put(Constant.REPORT, new ActionReport());
		context.put(Constant.JOB_DATA, inputData);
		context.put(CONFIGURATION, inputData.getConfiguration());
		context.put(VALIDATION, validationParameters);
		context.put(REPORT, new ActionReport());
		context.put(VALIDATION_REPORT, new ValidationReport());
		return context;
	}

	private Context prepareExportContext() {
		Context context = new Context();
		context.put(Constant.REPORT, new ActionReport());
		context.put(Constant.JOB_DATA, outputData);
		context.put(CONFIGURATION, outputData.getConfiguration());
		// context.put(VALIDATION, validationParameters);
		context.put(REPORT, new ActionReport());
		// context.put(MAIN_VALIDATION_REPORT, new ValidationReport());
		return context;
	}

	private boolean withExport() {
		return outputParametersFilename != null;
	}

	private boolean withValidation() {
		return validationParametersFilename != null;
	}

	private ValidationParameters loadValidationParameters() {
		try {
			return ParametersConverter.convertValidation(validationParametersFilename);
		} catch (Exception e) {
			System.err.println("error trying to read validation parameters file " + validationParametersFilename
					+ " : " + e.getMessage());
			return null;
		}
	}

	public void saveReports() throws Exception {
		ActionReport importReport = (ActionReport) importContext.get(REPORT);
		JSONUtil.toJSON(Paths.get(inputData.getPathName(), "importReport.json"), importReport);

		ValidationReport validationReport = (ValidationReport) importContext.get(VALIDATION_REPORT);
		JSONUtil.toJSON(Paths.get(inputData.getPathName(), VALIDATION_FILE), validationReport);
		if (withExport()) {
			ActionReport exportReport = (ActionReport) exportContext.get(REPORT);
			JSONUtil.toJSON(Paths.get(inputData.getPathName(), "exportReport.json"), exportReport);
		}
	}

	private CommandJobData loadInputParameters() {
		try {
			CommandJobData data = new CommandJobData();
			data.setFilename(new File(inputFileName).getName());
			data.setPathName(workingDirectory);
			data.setAction(IMPORTER);
			AbstractParameter configuration = ParametersConverter.convertConfiguration(inputParametersFilename);
			if (configuration instanceof NeptuneImportParameters) {
				data.setType("neptune");
			} else if (configuration instanceof NetexImportParameters) {
				data.setType("netex");
			} else if (configuration instanceof GtfsImportParameters) {
				data.setType("gtfs");
			} else {
				System.err.println("invalid input parameters type" + inputParametersFilename);
				return null;
			}
			data.setConfiguration(configuration);
			return data;

		} catch (Exception e) {
			System.err.println("error trying to read input parameters file " + inputParametersFilename + " : "
					+ e.getMessage());
			return null;
		}

	}

	private CommandJobData loadOutputParameters() {
		try {
			CommandJobData data = new CommandJobData();
			data.setPathName(workingDirectory);
			data.setAction(EXPORTER);
			AbstractParameter configuration = ParametersConverter.convertConfiguration(outputParametersFilename);
			if (configuration instanceof NeptuneExportParameters) {
				data.setType("neptune");
			} else if (configuration instanceof NetexExportParameters) {
				data.setType("netex");
			} else if (configuration instanceof GtfsExportParameters) {
				data.setType("gtfs");
			} else if (configuration instanceof HubExportParameters) {
				data.setType("hub");
			} else if (configuration instanceof KmlExportParameters) {
				data.setType("kml");
			} else {
				System.err.println("invalid input parameters type" + outputParametersFilename);
				return null;
			}
			data.setConfiguration(configuration);
			return data;

		} catch (Exception e) {
			System.err.println("error trying to read output parameters file " + outputParametersFilename + " : "
					+ e.getMessage());
			return null;
		}

	}

	private String buildCommandProcessingClassName(CommandJobData data) {

		return "mobi.chouette.exchange." + data.getType() + "." + data.getAction() + "."
				+ StringUtils.capitalize(data.getType()) + StringUtils.capitalize(data.getAction())
				+ "ProcessingCommands";
	}

	private void help() {
		// This prints out some help
		HelpFormatter formater = new HelpFormatter();

		formater.printHelp("command_iev [options] importFile", options);
		System.exit(0);
	}

}
