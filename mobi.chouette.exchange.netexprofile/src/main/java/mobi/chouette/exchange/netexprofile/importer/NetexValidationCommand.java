package mobi.chouette.exchange.netexprofile.importer;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.neptune.validation.*;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.importer.validation.NetexProfileValidator;
import mobi.chouette.exchange.netexprofile.importer.validation.norway.AbstractValidator;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.IO_TYPE;
import mobi.chouette.model.Line;
import mobi.chouette.model.util.NamingUtil;
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
                NetexProfileValidator validator = (NetexProfileValidator) context.get(NETEX_PROFILE_VALIDATOR);
                //validator.addCheckpoints(context); // needed? should probably be removed, as this is done in validators
                validator.validate(context);
            }

            result = !reporter.hasFileValidationErrors(context, fileName);

            // TODO consider need for stats in report
/*
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
        if (result == ERROR) {
            reporter.addFileErrorInReport(context, fileName,
                    ActionReporter.FILE_ERROR_CODE.INVALID_FORMAT, "Netex compliance failed");
        }
        return result;
    }

    private void addStats(Context context, ActionReporter reporter, Context validationContext, Referential referential) {
        // TODO: implement
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
