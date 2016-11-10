package mobi.chouette.exchange.netexprofile.importer;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.importer.validation.norway.AbstractValidator;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.model.util.Referential;

import javax.naming.InitialContext;
import java.io.IOException;

@Log4j
public class NetexValidationCommand implements Command, Constant {

    public static final String COMMAND = "NetexValidationCommand";

    @Override
    public boolean execute(Context context) throws Exception {
        boolean result = ERROR;
        Monitor monitor = MonitorFactory.start(COMMAND);
        ActionReporter reporter = ActionReporter.Factory.getInstance();
        String fileName = (String) context.get(FILE_NAME);

        try {
            Context validationContext = (Context) context.get(VALIDATION_CONTEXT);
            Referential referential = (Referential) context.get(REFERENTIAL);

            if (validationContext != null) {

            }

            // do we need stats in report?
/*
            result = !reporter.hasFileValidationErrors(context, fileName);
            if (result) {
                addStats(context, reporter, validationContext, referential);
            }
*/
        } catch (Exception e) {
            log.error("Netex validation failed ", e);
            throw e;
        } finally {
            AbstractValidator.resetContext(context);
            log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
        }

        // TODO: enable later when everything is set correctly
/*
        if (result == ERROR) {
            reporter.addFileErrorInReport(context, fileName,
                    ActionReporter.FILE_ERROR_CODE.INVALID_FORMAT, "Netex compliance failed");
        }
*/

        return result;
    }

    public static class DefaultCommandFactory extends CommandFactory {
        @Override
        protected Command create(InitialContext context) throws IOException {
            Command result = new NetexValidationCommand();
            return result;
        }
    }

    static {
        CommandFactory.factories.put(NetexValidationCommand.class.getName(),
                new NetexValidationCommand.DefaultCommandFactory());
    }

}
