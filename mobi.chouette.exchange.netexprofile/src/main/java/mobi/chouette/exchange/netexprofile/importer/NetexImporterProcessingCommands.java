package mobi.chouette.exchange.netexprofile.importer;

import lombok.Data;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.FileUtil;
import mobi.chouette.common.JobData;
import mobi.chouette.common.chain.Chain;
import mobi.chouette.common.chain.ChainCommand;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.ProcessingCommands;
import mobi.chouette.exchange.ProcessingCommandsFactory;
import mobi.chouette.exchange.importer.CleanRepositoryCommand;
import mobi.chouette.exchange.importer.CopyCommand;
import mobi.chouette.exchange.importer.LineRegisterCommand;
import mobi.chouette.exchange.importer.UncompressCommand;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.IO_TYPE;
import mobi.chouette.exchange.validation.ImportedLineValidatorCommand;
import mobi.chouette.exchange.validation.SharedDataValidatorCommand;

import javax.naming.InitialContext;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static mobi.chouette.exchange.netexprofile.Constant.NETEX_FILE_PATHS;

@Data
@Log4j
public class NetexImporterProcessingCommands implements ProcessingCommands, Constant {

    public static class DefaultFactory extends ProcessingCommandsFactory {

        @Override
        protected ProcessingCommands create() throws IOException {
            ProcessingCommands result = new NetexImporterProcessingCommands();
            return result;
        }
    }

    static {
        ProcessingCommandsFactory.factories.put(NetexImporterProcessingCommands.class.getName(), new DefaultFactory());
    }

    @Override
    public List<? extends Command> getPreProcessingCommands(Context context, boolean withDao) {
        InitialContext initialContext = (InitialContext) context.get(INITIAL_CONTEXT);
        NetexprofileImportParameters parameters = (NetexprofileImportParameters) context.get(CONFIGURATION);
        List<Command> commands = new ArrayList<>();
        try {
            if (withDao && parameters.isCleanRepository()) {
                commands.add(CommandFactory.create(initialContext, CleanRepositoryCommand.class.getName()));
            }
            commands.add(CommandFactory.create(initialContext, UncompressCommand.class.getName()));
            commands.add(CommandFactory.create(initialContext, NetexInitImportCommand.class.getName()));
        } catch (Exception e) {
            log.error(e, e);
            throw new RuntimeException("unable to call factories");
        }

        return commands;
    }

    @Override
    public List<? extends Command> getLineProcessingCommands(Context context, boolean withDao) {
        InitialContext initialContext = (InitialContext) context.get(INITIAL_CONTEXT);
        NetexprofileImportParameters parameters = (NetexprofileImportParameters) context.get(CONFIGURATION);
		ActionReporter reporter = ActionReporter.Factory.getInstance();

        boolean level3validation = context.get(VALIDATION) != null;
        List<Command> commands = new ArrayList<>();
        JobData jobData = (JobData) context.get(JOB_DATA);
        Path path = Paths.get(jobData.getPathName(), INPUT);

        try {

            // Report any files that are not XML files
        	List<Path> excluded = FileUtil.listFiles(path, "*", "*.xml");

			if (!excluded.isEmpty()) {
				for (Path exclude : excluded) {
					reporter.setFileState(context, exclude.getFileName().toString(), IO_TYPE.INPUT, ActionReporter.FILE_STATE.IGNORED);
				}
			}

            // stream all file paths once
            List<Path> allFilePaths = FileUtil.listFiles(path, "*.xml");
            context.put(NETEX_FILE_PATHS, allFilePaths);

            // schema validation
 
            NetexSchemaValidationCommand schemaValidation = (NetexSchemaValidationCommand) CommandFactory.create(initialContext,
                    NetexSchemaValidationCommand.class.getName());
            commands.add(schemaValidation);

            // common file parsing

            List<Path> commonFilePaths = allFilePaths.stream()
                    .filter(filePath -> filePath.getFileName() != null && filePath.getFileSystem()
                            .getPathMatcher("glob:_*.xml").matches(filePath.getFileName()))
                    .collect(Collectors.toList());

            NetexCommonFilesParserCommand commonFilesParser = (NetexCommonFilesParserCommand) CommandFactory.create(initialContext, NetexCommonFilesParserCommand.class.getName());
            commonFilesParser.setFiles(commonFilePaths);
            commands.add(commonFilesParser);

            // line file processing

            List<Path> lineFilePaths = allFilePaths.stream()
                    .filter(filePath -> filePath.getFileName() != null && !filePath.getFileSystem()
                            .getPathMatcher("glob:_*.xml").matches(filePath.getFileName()))
                    .collect(Collectors.toList());

            for (Path file : lineFilePaths) {
                String url = file.toUri().toURL().toExternalForm();
                Chain chain = (Chain) CommandFactory.create(initialContext, ChainCommand.class.getName());
                commands.add(chain);

                // init referentials
                NetexInitReferentialCommand initializer = (NetexInitReferentialCommand) CommandFactory.create(initialContext, NetexInitReferentialCommand.class.getName());
                initializer.setFileURL(url);
                chain.add(initializer);

                // profile validation
                Command validator = CommandFactory.create(initialContext, NetexValidationCommand.class.getName());
                chain.add(validator);

                // parsing
                NetexLineParserCommand parser = (NetexLineParserCommand) CommandFactory.create(initialContext, NetexLineParserCommand.class.getName());
                parser.setFileURL(url);
                chain.add(parser);

                if (withDao && !parameters.isNoSave()) {

                    // register
                    Command register = CommandFactory.create(initialContext, LineRegisterCommand.class.getName());
                    chain.add(register);

                    Command copy = CommandFactory.create(initialContext, CopyCommand.class.getName());
                    chain.add(copy);
                }
                if (level3validation) {
                    // add validation
                    Command validate = CommandFactory.create(initialContext, ImportedLineValidatorCommand.class.getName());
                    chain.add(validate);
                }
            }

        } catch (Exception e) {
            // TODO: add exception handling here
        }

        return commands;
    }

    @Override
    public List<? extends Command> getPostProcessingCommands(Context context, boolean withDao) {
        InitialContext initialContext = (InitialContext) context.get(INITIAL_CONTEXT);
        boolean level3validation = context.get(VALIDATION) != null;

        List<Command> commands = new ArrayList<>();
        try {
            if (level3validation) {
                // add shared data validation
                commands.add(CommandFactory.create(initialContext, SharedDataValidatorCommand.class.getName()));
            }

        } catch (Exception e) {
            log.error(e, e);
            throw new RuntimeException("unable to call factories");
        }
        return commands;
    }

    @Override
    public List<? extends Command> getStopAreaProcessingCommands(Context context, boolean withDao) {
        return new ArrayList<>();
    }

    @Override
    public List<? extends Command> getDisposeCommands(Context context, boolean withDao) {
        List<Command> commands = new ArrayList<>();
        return commands;
    }

}
