package mobi.chouette.command;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
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
import mobi.chouette.exchange.validation.parameters.ValidationParameters;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;

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

	public CommandManager(String[] args) {
		this.args = args;
		options.addOption("h", "help", false, "show help");
		options.addOption("i", "input", true, "input parameters (json)");
		options.addOption("o", "output", true, "output parameters  (json)");
		options.addOption("v", "validate", true, "validation parameters (json)");
		options.addOption("f", "file", true, "output file ");
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
				System.out.println("missing -i inputParameters.json ");
				help();
			}
			if (cmd.hasOption("v")) {
				validationParametersFilename = cmd.getOptionValue("v");
			}
			if (cmd.hasOption("o")) {
				outputParametersFilename = cmd.getOptionValue("o");
				if (cmd.hasOption("f")) {
					outputFileName = cmd.getOptionValue("f");
				} else {
					System.out.println("missing -f outputFile");
					help();
				}
			} else if (cmd.hasOption("f")) {
				System.out.println("unexpected -f option without -o option");
				help();
			}
			if (cmd.getArgList().size() == 1) {
				inputFileName = cmd.getArgList().get(0).toString();
			} else {
				System.out.println("missing inputFile ");
				help();
			}

		} catch (ParseException e) {
			System.out.println("Invalid syntax " + e.getMessage());
			help();
		}
		return;
	}

	public void process() {
		inputData = loadInputParameters();
		if (inputData == null)
			return; // invalid data

		if (outputParametersFilename != null) {
			outputData = loadOutputParameters();
			if (outputData == null)
				return; // invalid data
		}

		if (validationParametersFilename != null) {
			validationParameters = loadValidationParameters();
			if (validationParameters == null)
				return; // invalid data
		}
		
		// may be useless
		InitialContext initContext = null;
		try {
			initContext = new InitialContext();
		} catch (NamingException e2) {
			e2.printStackTrace();
			return ;
		}
		
		
		Context context = new Context();
		context.put(Constant.REPORT, new ActionReport());
		CommandJobData data = new CommandJobData();
		context.put(Constant.JOB_DATA, data);
		data.setPathName("work");
		File input = new File(inputFileName);
		data.setFilename(input.getName());
		try {
			Files.createDirectories(Paths.get(".", "work"));
			FileUtils.copyFileToDirectory(new File(args[0]), new File(data.getPathName()));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			Command command = CommandFactory.create(initContext, UncompressCommand.class.getName());
			command.execute(context);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
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

	private CommandJobData loadInputParameters() {
		try {
			CommandJobData data = new CommandJobData();
			data.setFilename(inputFileName);
			data.setPathName("work");
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
			data.setFilename(outputFileName);
			data.setPathName("work");
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
				System.err.println("invalid input parameters type" + inputParametersFilename);
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

	private void help() {
		// This prints out some help
		HelpFormatter formater = new HelpFormatter();

		formater.printHelp("command_iev [options] inputFile", options);
		System.exit(0);
	}

}
