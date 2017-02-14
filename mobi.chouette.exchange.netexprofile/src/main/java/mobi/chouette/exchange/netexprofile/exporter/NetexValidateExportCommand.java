package mobi.chouette.exchange.netexprofile.exporter;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.FileUtil;
import mobi.chouette.common.JobData;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.importer.*;

import javax.naming.InitialContext;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Log4j
public class NetexValidateExportCommand implements Command, Constant {

    public static final String COMMAND = "NetexValidateExportCommand";

    @Override
    public boolean execute(Context context) throws Exception {
        boolean result = ERROR;

        Monitor monitor = MonitorFactory.start(COMMAND);

        try {
            Context validateContext = new Context();
            validateContext.putAll(context);

            NetexprofileImportParameters parameters = new NetexprofileImportParameters();
            NetexprofileExportParameters configuration = (NetexprofileExportParameters) context.get(CONFIGURATION);

            parameters.setOrganisationName(configuration.getOrganisationName());
            parameters.setUserName(configuration.getUserName());
            parameters.setName(configuration.getName());
            parameters.setNoSave(true);
            parameters.setReferentialName(configuration.getReferentialName());
            validateContext.put(CONFIGURATION, parameters);
            validateContext.put(REPORT, context.get(REPORT));

            JobData jobData = (JobData) context.get(JOB_DATA);
            String pathName = jobData.getPathName();
            File output = new File(pathName, OUTPUT);
            File input = new File(pathName, INPUT);

            if (!output.renameTo(input)) {
                log.error("rename failed");
            }

            output = new File(pathName, OUTPUT);

            InitialContext initialContext = (InitialContext) context.get(INITIAL_CONTEXT);

            try {
                Command init = CommandFactory.create(initialContext, NetexInitImportCommand.class.getName());
                init.execute(validateContext);

                Path path = Paths.get(jobData.getPathName(), INPUT);

                List<Path> filePaths = FileUtil.listFiles(path, "*.xml");
                context.put(NETEX_FILE_PATHS, filePaths);

                NetexSchemaValidationCommand schemaValidationCommand = (NetexSchemaValidationCommand)
                        CommandFactory.create(initialContext, NetexSchemaValidationCommand.class.getName());
                schemaValidationCommand.execute(context);

                for (Path file : filePaths) {
                    String url = file.toUri().toURL().toExternalForm();

                    NetexInitReferentialCommand initRefsCommand = (NetexInitReferentialCommand) CommandFactory.create(initialContext, NetexInitReferentialCommand.class.getName());
                    initRefsCommand.setFileURL(url);
                    initRefsCommand.execute(context);

                    NetexValidationCommand profileValidationCommand = (NetexValidationCommand) CommandFactory.create(initialContext, NetexValidationCommand.class.getName());
                    profileValidationCommand.execute(context);

                    NetexLineParserCommand parserCommand = (NetexLineParserCommand) CommandFactory.create(initialContext, NetexLineParserCommand.class.getName());
                    parserCommand.setFileURL(url);
                }
            } catch (Exception ex) {
                log.error("problem in validation" + ex);
            } finally {
                input.renameTo(output);
                Command disposeImportCommand = CommandFactory.create(initialContext, NetexDisposeImportCommand.class.getName());
                disposeImportCommand.execute(validateContext);
            }
            context.put(VALIDATION_REPORT, validateContext.get(VALIDATION_REPORT));
            result = SUCCESS;

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
            Command result = new NetexValidateExportCommand();
            return result;
        }
    }

    static {
        CommandFactory.factories.put(NetexValidateExportCommand.class.getName(), new NetexValidateExportCommand.DefaultCommandFactory());
    }

}
